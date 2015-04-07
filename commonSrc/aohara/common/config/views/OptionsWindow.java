package aohara.common.config.views;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import aohara.common.Dialogs;
import aohara.common.config.Config;
import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.selectorPanel.DecoratedComponent;

public class OptionsWindow implements DecoratedComponent<JPanel>{
	
	private final JPanel panel = new JPanel();
	private JDialog dialog;
	private final Config config;
	
	public OptionsWindow(Config config){
		this.config = config;
		
		Collection<OptionInput> optionInputs = config.getInputs();
		
		panel.setLayout(new GridLayout(optionInputs.size() + 1, 2));
		panel.setBorder(BorderFactory.createTitledBorder(config.getName()));
		
		for (OptionInput input : optionInputs){
			panel.add(new JLabel(String.format("<html><b>%s</b></html>", input.getName())));
			panel.add(input.getComponent());
			input.update(); // Get Value from option and place in component
			
		}
		panel.add(new JButton(new SubmitAction()));
		panel.add(new JButton(new CancelAction()));
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
	
	public JDialog toDialog(){
		if (dialog == null || !dialog.isActive()){
			dialog = new JDialog();
			dialog.setTitle(config.getName());
			dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
			dialog.add(getComponent());
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setVisible(true);
		}

		return dialog;
	}
	
	@SuppressWarnings("serial")
	private class SubmitAction extends AbstractAction {
		
		public SubmitAction(){
			super("Submit");
		}

		@Override
		public void actionPerformed(ActionEvent ev) {			
			try {				
				// Apply Values
				for (OptionInput input : config.getInputs()){
					input.apply();
				}
				config.save();
				
				// Only close dialog if no errors occurred
				if (dialog != null){
					dialog.setVisible(false);
				}
				
			} catch(InvalidInputException | IOException ex){
				config.rollback();  // Rollback changes to config
				Dialogs.errorDialog(getComponent(), ex);
			}
		}		
	}
	
	@SuppressWarnings("serial")
	private class CancelAction extends AbstractAction {

		public CancelAction(){
			super("Cancel");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (dialog != null){
				dialog.setVisible(false);
			}
		}	
	}
}
