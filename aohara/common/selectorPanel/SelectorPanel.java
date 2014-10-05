package aohara.common.selectorPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import thirdParty.SortedListModel;
import aohara.common.Listenable;

/**
 * Generic Panel which includes a JList on the left, and Item View on the right.
 * 
 * The JList supports only Single Selection.
 * 
 * This Panel allows {@link aohara.common.selectorPanel.ListListener} objects to listen
 * for list events.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> The Type that is held in the JList.
 */
@SuppressWarnings("serial")
public class SelectorPanel<T> extends Listenable<ListListener<T>>
		implements MouseListener, ListSelectionListener, SelectorInterface<T>,
		DecoratedComponent<JComponent> {
	
	private final JSplitPane splitPane = new SelectorSplitPane();
	private final JList<T> list;
	private final Collection<SelectorView<T, JPanel>> views = new ArrayList<>(); 
	private JScrollPane scrollPane;
	private JPopupMenu popupMenu;

	public SelectorPanel(SelectorView<T, JPanel> view, Comparator<T> comparator){
		list = new JList<T>(new SortedListModel<T>(comparator));
		
		views.add(view);
		
		// Configure List
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.addMouseListener(this);
		
		// Add Componenents to JSplitPane
		splitPane.setLeftComponent(createSidePanel(list, new Dimension(200, 700)));
		splitPane.setRightComponent(createSidePanel(view.getComponent(), new Dimension(620, 700)));
	}
	
	private JPanel createSidePanel(JComponent comp, Dimension preferredSize){
		scrollPane = new JScrollPane(comp);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		JPanel sidePanel = new JPanel(new BorderLayout());
		sidePanel.setPreferredSize(preferredSize);
		sidePanel.add(scrollPane, BorderLayout.CENTER);
		
		return sidePanel;
	}
	
	public JComponent addControlPanel(boolean left, ControlPanel<T> controlPanel){
		JPanel sidePanel = (JPanel) (left ? splitPane.getLeftComponent() : splitPane.getRightComponent());
		sidePanel.add(controlPanel.getComponent(), BorderLayout.SOUTH);
		views.add(controlPanel);
		return sidePanel;
	}
	
	public void addPopupMenu(JPopupMenu popupMenu){
		this.popupMenu = popupMenu;
	}
	
	private SortedListModel<T> getModel(){
		return (SortedListModel<T>) list.getModel();
	}
	
	@Override
	public void addElement(T element) {
		getModel().add(element);
	}

	@Override
	public void removeElement(T element) {
		getModel().removeElement(element);
	}
	
	@Override
	public void clear() {
		getModel().clear();
	}
	
	@Override
	public int selectElement(T element){
		list.setSelectedValue(element, true);
		return list.getSelectedIndex();
	}
	
	@Override
	public T selectIndex(int index){
		list.setSelectedIndex(index);
		return list.getSelectedValue();
	}
	
	public void setListCellRenderer(ListCellRenderer <T> renderer){
		list.setCellRenderer(renderer);
	}
	
	@Override
	public JComponent getComponent(){
		return splitPane;
	}
	
	// -- Listeners ---------------------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (!arg0.getValueIsAdjusting()){
			
			T element = list.getSelectedValue();
			
			// Update all views for selected element
			for (SelectorView<T, JPanel> view : views){
				view.display(element);
				view.getComponent().updateUI();
			}
			
			// Notify listeners of selection
			for (ListListener<T> l : getListeners()){
				try {
					l.elementSelected(element);
				} catch (Exception e) {
					errorMessage(e.getMessage());
				}
			}
			
			
			// Reset right scroll bar to top
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
					scrollBar.setValue(scrollBar.getMinimum());
				}
			});
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent evt) {
		for (ListListener<T> l : getListeners()){
			try {
				l.elementClicked(list.getSelectedValue(), evt.getClickCount());
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage(e.getMessage());
			}
		}
		
		if (SwingUtilities.isRightMouseButton(evt)){
			selectIndex(list.locationToIndex(evt.getPoint()));
			 
			 // Open Popup Menu
			 if (popupMenu != null){
				 popupMenu.show((Component) evt.getSource(), evt.getX(), evt.getY());
			 }
		}
    }
	
	private void errorMessage(String message){
		JOptionPane.showMessageDialog(
				splitPane, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void mousePressed(MouseEvent e) { /* N/A */}
	@Override
	public void mouseReleased(MouseEvent e) { /* N/A */ }
	@Override
	public void mouseEntered(MouseEvent e) { /* N/A */ }
	@Override
	public void mouseExited(MouseEvent e) { /* N/A */ }
	
	// -- Internal Components -----------------------------------------
	
	private class SelectorSplitPane extends JSplitPane {
		
		public SelectorSplitPane(){
			setContinuousLayout(true);
		}
		
		@Override
		public void paintComponent(java.awt.Graphics g){
			/**Overridden to prevent view update during model update **/
			synchronized(list){
				super.paintComponent(g);
			}
		}
	}
}
