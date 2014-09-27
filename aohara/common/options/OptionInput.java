package aohara.common.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aohara.common.options.Constraint.InvalidInputException;
import aohara.common.selectorPanel.DecoratedComponent;

public abstract class OptionInput implements DecoratedComponent<JComponent> {
	
	protected final Option option;
	
	public OptionInput(Option option){
		this.option = option;
	}
	
	public String getName(){
		return option.name;
	}
	
	public void testValue() throws InvalidInputException {
		option.testValue(getValue());
	}
	
	public void apply() throws InvalidInputException{
		option.setValue(getValue());
	}
	
	protected abstract String getValue();
	
	// -- Default Implementations -----------------------------------------
	
	public static class TextFieldInput extends OptionInput {
		
		private final JTextField textField;

		public TextFieldInput(Option option) {
			super(option);
			textField = new JTextField(option.getValueString());
		}

		@Override
		public JComponent getComponent() {
			return textField;
		}

		@Override
		protected String getValue() {
			return textField.getText();
		}
	}
	
	public static class ComboBoxInput extends OptionInput {
		
		private final JComboBox<String> box;

		public ComboBoxInput(Option option, Collection<String> choices) {
			super(option);
			box = new JComboBox<String>(choices.toArray(new String[choices.size()]));
			box.setSelectedItem(option.getValue());
		}

		@Override
		public JComponent getComponent() {
			return box;
		}

		@Override
		protected String getValue() {
			return (String) box.getSelectedItem();
		}
	}
	
	public static class TrueFalseInput extends ComboBoxInput {

		public TrueFalseInput(Option option) {
			super(
				option,
				Arrays.asList(new String[]{Boolean.toString(true), Boolean.toString(false)})
			);
		}
		
	}
	
	public static class FileChooserInput extends OptionInput {
		
		private final JPanel panel;
		private final JTextField pathField;

		public FileChooserInput(Option option, int fileSelectionMode) {
			super(option);

			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			
			// Create Path Field
			pathField = new JTextField(option.getValue());
			panel.add(pathField, BorderLayout.CENTER);
			
			// Create File Chooser
			final JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File(option.getValue()));
			chooser.setDialogTitle("Choose path");
			chooser.setApproveButtonText("Select KSP Path");
			chooser.setFileSelectionMode(fileSelectionMode);
			
			// Add Button to open File Chooser
			JButton button = new JButton("...");
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION){
						pathField.setText(chooser.getSelectedFile().toString());
					}
				}
			});
			panel.add(button, BorderLayout.EAST);
		}

		@Override
		public JComponent getComponent() {
			return panel;
		}

		@Override
		protected String getValue() {
			return pathField.getText();
		}
	}

}
