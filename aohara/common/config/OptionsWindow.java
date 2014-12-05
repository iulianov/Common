package aohara.common.config;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.selectorPanel.DecoratedComponent;

public class OptionsWindow implements DecoratedComponent<JPanel>{
	
	private final JPanel panel = new JPanel();
	private JDialog dialog;
	private final Collection<OptionInput> optionInputs;
	private final boolean restartOnSuccess, exitOnCancel;
	private final Config config;
	
	public OptionsWindow(Config config, Collection<OptionInput> optionInputs){
		this(config, optionInputs, false, false);
	}
	
	public OptionsWindow(Config config, Collection<OptionInput> optionInputs, boolean restartOnSuccess, boolean exitOnCancel){
		this.config = config;
		this.optionInputs = optionInputs;
		this.restartOnSuccess = restartOnSuccess;
		this.exitOnCancel = exitOnCancel;
		
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
			dialog.setModal(true);
			dialog.add(getComponent());
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
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
				for (OptionInput input : optionInputs){
					input.apply();
				}
				config.save();
				
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
				config.rollback();  // Rollback changes to config
				
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
