package aohara.common.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aohara.common.selectorPanel.DecoratedComponent;

public class FileChooserField implements DecoratedComponent<JPanel> {
	
	private final JFileChooser chooser = new JFileChooser();
	private final JPanel panel = new JPanel(new BorderLayout());
	private final JTextField pathField = new JTextField(30);
	
	public FileChooserField(int fileSelectionMode){
	
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
	
	public File getFile(){
		return new File(pathField.getText());
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}

}
