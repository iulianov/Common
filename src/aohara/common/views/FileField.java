package aohara.common.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileField extends JPanel {
	
	private final JFileChooser chooser = new JFileChooser();
	private final JTextField pathField = new JTextField();
	
	public FileField(File file, int fileSelectionMode){
		this(fileSelectionMode);
		setFile(file);
	}
	
	public FileField(int fileSelectionMode){
		super(new BorderLayout());
		
		// Add Text Field showing Path
		add(pathField, BorderLayout.CENTER);
		
		// init File Chooser
		chooser.setDialogTitle("Choose path");
		chooser.setApproveButtonText("Select KSP Path");
		chooser.setFileSelectionMode(fileSelectionMode);
		
		// Add Button to open File Chooser
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chooser.showSaveDialog(FileField.this) == JFileChooser.APPROVE_OPTION){
					pathField.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		add(button, BorderLayout.EAST);
	}
	
	public void setFile(File file){
		pathField.setText(file != null ? file.toString() : null);
		chooser.setSelectedFile(file);
	}
	
	public File getFile(){
		return new File(pathField.getText());
	}
}
