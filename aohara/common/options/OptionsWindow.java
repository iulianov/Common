package aohara.common.options;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aohara.common.options.Constraint.InvalidInputException;
import aohara.common.selectorPanel.DecoratedComponent;

public class OptionsWindow implements DecoratedComponent<JPanel>{
	
	private final JPanel panel = new JPanel();
	private JDialog dialog;
	private final Set<OptionInput> optionInputs;
	private final String title;
	private final boolean restartOnSuccess, exitOnCancel;
	
	public OptionsWindow(String title, Set<OptionInput> optionInputs){
		this(title, optionInputs, false, false);
	}
	
	public OptionsWindow(String title, Set<OptionInput> optionInputs, boolean restartOnSuccess, boolean exitOnCancel){
		this.title = title;
		this.optionInputs = optionInputs;
		this.restartOnSuccess = restartOnSuccess;
		this.exitOnCancel = exitOnCancel;
		
		panel.setLayout(new GridLayout(optionInputs.size() + 1, 2));
		panel.setBorder(BorderFactory.createTitledBorder(title));
		
		for (OptionInput input : optionInputs){
			panel.add(new JLabel(String.format("<html><b>%s</b></html>", input.getName())));
			panel.add(input.getComponent());
			
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
			dialog.setTitle(title);
			dialog.setModal(true);
			dialog.add(getComponent());
			
			dialog.pack();
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
				// Test Values before applying
				for (OptionInput input : optionInputs){
					input.testValue();
				}
				
				// Apply Values
				for (OptionInput input : optionInputs){
					input.apply();
				}
				
				if (dialog != null){
					dialog.setVisible(false);
					
					// Restart if required
					if (restartOnSuccess){
						JOptionPane.showMessageDialog(
							getComponent(),
							"A restart is required",
							"Restart",
							JOptionPane.WARNING_MESSAGE
						);
						System.exit(0);
					}
				}
				
			} catch(InvalidInputException ex){
				JOptionPane.showMessageDialog(
					getComponent(),
					ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE
				);
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
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				// Exit if Required
				if (exitOnCancel){
					System.exit(1);
				}
			}
		}
		
		
	}

}
