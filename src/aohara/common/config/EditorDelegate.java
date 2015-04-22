package aohara.common.config;

import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import aohara.common.views.FileField;

interface EditorDelegate {
	
	public Object getValue();
	public JComponent getComponent(boolean editMode);

	class TextFieldDelegate implements EditorDelegate {
		
		private final JTextField textField;
		private final JLabel label = new JLabel();
		
		TextFieldDelegate(String defaultValue){
			textField = new JTextField(defaultValue);
		}
	
		@Override
		public JComponent getComponent(boolean editMode) {
			if (editMode){
				return textField;
			}
			label.setText(textField.getText());
			return label;
		}
	
		@Override
		public Object getValue() {
			return textField.getText();
		}
		
	}
	
	class FileFieldDelegate implements EditorDelegate {
		
		private final FileField fileField;
		
		FileFieldDelegate(File defaultFile, int fileSelectionMode){
			fileField = new FileField(defaultFile, fileSelectionMode);
		}
	
		@Override
		public JComponent getComponent(boolean editMode) {
			return fileField;
		}
	
		@Override
		public Object getValue() {
			return fileField.getFile();
		}
	}
	
	class BooleanDelegate implements EditorDelegate {
	
		private final JCheckBox checkBox;
		
		BooleanDelegate(Boolean defaultValue){
			checkBox = new JCheckBox();
			checkBox.setSelected(defaultValue);
		}
		
		@Override
		public JComponent getComponent(boolean editMode) {
			return checkBox;
		}
	
		@Override
		public Object getValue() {
			return checkBox.isSelected();
		}
	}
}
