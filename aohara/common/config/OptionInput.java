package aohara.common.config;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aohara.common.config.Constraint.InvalidInputException;
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
	public abstract void update();
	
	// -- Default Implementations -----------------------------------------
	
	public static class TextFieldInput extends OptionInput {
		
		private final JTextField textField = new JTextField();

		public TextFieldInput(Option option) {
			super(option);
		}

		@Override
		public JComponent getComponent() {
			return textField;
		}

		@Override
		protected String getValue() {
			return textField.getText();
		}

		@Override
		public void update() {
			textField.setText(option.getValue());
		}
	}
	
	public static class ComboBoxInput extends OptionInput {
		
		private final JComboBox<String> box;

		public ComboBoxInput(Option option, Collection<String> choices) {
			super(option);
			box = new JComboBox<String>(choices.toArray(new String[choices.size()]));
		}

		@Override
		public JComponent getComponent() {
			return box;
		}

		@Override
		protected String getValue() {
			return (String) box.getSelectedItem();
		}

		@Override
		public void update() {
			box.setSelectedItem(option.getValue());
		}
	}
	
	public static class FileChooserInput extends OptionInput {
		
		private final JFileChooser chooser = new JFileChooser();
		private final JPanel panel = new JPanel(new BorderLayout());
		private final JTextField pathField = new JTextField(option.getValue());

		public FileChooserInput(Option option, int fileSelectionMode) {
			super(option);

			panel.add(pathField, BorderLayout.CENTER);
			
			// init File Chooser
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

		@Override
		public void update() {
			String value = option.getValue();
			chooser.setSelectedFile(value != null ? new File(value) : null);
			pathField.setText(value);
		}
	}

}
