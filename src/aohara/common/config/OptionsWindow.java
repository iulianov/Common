package aohara.common.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.selectorPanel.DecoratedComponent;
import aohara.common.views.Dialogs;
import aohara.tinkertime.resources.Icons;

public class OptionsWindow implements DecoratedComponent<JPanel>{
	
	private final JPanel panel;
	private final JTable table;
	private JDialog dialog;
	private final Config config;
	private boolean success = false;
	private final OptionsCellEditor editor;
	
	public OptionsWindow(final Config config){
		this.config = config;
		
		table = new JTable(new OptionsTableModel());
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		
		// Set Cell Renderer and Editor
		editor = new OptionsCellEditor();
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(1).setCellEditor(editor);
		columnModel.getColumn(1).setCellRenderer(editor);
		
		columnModel.getColumn(0).setPreferredWidth(200);
		columnModel.getColumn(1).setPreferredWidth(200);
		table.setRowHeight(25);
		
		// Create OK Button for Control Panel
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				success = true;
				
				try {
					// Set New Properties (fail fast if illegal argument)
					for (int row=0; row<table.getRowCount(); row++){
						String propertyName = table.getModel().getValueAt(row, 0).toString();
						Object value = table.getModel().getValueAt(row, 1);
						config.setProperty(propertyName, value);
					}
					config.save();
					
					// Only close dialog if no errors occurred
					if (dialog != null){
						dialog.setVisible(false);
					}
				} catch (InvalidInputException | IOException ex) {
					config.rollback();  // Rollback changes to config
					Dialogs.errorDialog(getComponent(), ex);
				}
			}
		});
		
		// Create Cancel Button for Control Panel
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dialog != null){
					dialog.setVisible(false);
				}
			}
		});
		
		// Create Control Panel (Ok, Cancel)
		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(okButton);
		controlPanel.add(cancelButton);
		
		panel = new JPanel(new BorderLayout());
		panel.add(table, BorderLayout.CENTER);
		panel.add(controlPanel, BorderLayout.SOUTH);
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
	
	public boolean toDialog(){
		if (dialog == null || !dialog.isActive()){
			dialog = new JDialog();
			dialog.setTitle(config.getName());
			dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
			dialog.add(getComponent());
			dialog.setIconImages(Icons.getAppIcons());
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}

		return success;
	}
	
	private class OptionsTableModel extends AbstractTableModel {
		
		@Override
		public int getRowCount() {
			return config.getNonHiddenProperties().size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0){
				return config.getNonHiddenProperties().get(rowIndex).getName();
			} else {
				return editor.getDelegate(rowIndex, columnIndex).getValue();
			}
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
		    return columnIndex == 1;
		}
	}
	
	private class OptionsCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
		
		private EditorDelegate activeDelegate;
		private Map<Point, EditorDelegate> components = new HashMap<>();
		
		public EditorDelegate getDelegate(int row, int column){
			Point key = new Point(row, column);

			// If there is no editor delegate for this cell yet, create one
			if (!components.containsKey(key)){
				Property prop = config.getNonHiddenProperties().get(row);
				EditorDelegate c;
				if (prop.type.isAssignableFrom(File.class)){
					int fileSelectionMode;
					try{
						fileSelectionMode = ((Constraints.EnsureIsFile) prop.findConstraint(Constraints.EnsureIsFile.class)).fileSelectionMode;
					} catch (IllegalArgumentException e){
						fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
					}
					c = new EditorDelegate.FileFieldDelegate(prop.getValueAsFile(), fileSelectionMode);
				} else if (prop.type.isAssignableFrom(Boolean.class)){
					c=  new EditorDelegate.BooleanDelegate(prop.getValueAsBool());
				} else {
					c = new EditorDelegate.TextFieldDelegate(prop.getValueAsString());
				}
				components.put(key, c);
			}
			return activeDelegate = components.get(key);
		}
		
		@Override
		public Object getCellEditorValue() {
			return activeDelegate.getValue();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			table.setRowSelectionInterval(row, row);
			return getDelegate(row, column).getComponent(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			return getDelegate(row, column).getComponent(false);
		}
	}
}
