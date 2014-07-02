package aohara.common.selectorPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import aohara.common.Listenable;

@SuppressWarnings("serial")
public class SelectorPanel<T> extends Listenable<ElementClickListener<T>>
		implements MouseListener, ListSelectionListener, SelectorListener<T>,
		DecoratedComponent<JComponent> {
	
	private final JSplitPane splitPane = new SelectorSplitPane();
	private final JList<T> list = new JList<T>(new DefaultListModel<T>());
	private Collection<T> elements = new LinkedList<>();
	private final Collection<SelectorView<T, JPanel>> views = new ArrayList<>(); 
	private JScrollPane scrollPane;

	public SelectorPanel(SelectorView<T, JPanel> view){
		views.add(view);
		
		// Configure List
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.addMouseListener(this);
		
		// Add Componenents to JSplitPane
		splitPane.setLeftComponent(createSidePanel(list, new Dimension(150, 800)));
		splitPane.setRightComponent(createSidePanel(view.getComponent(), null));
		
		// Start List Model Update Timer
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
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
	
	@Override
	public void setDataSource(Collection<T> newElements){
		synchronized(list){
			elements = newElements;
		}
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
			
			for (SelectorView<T, JPanel> view : views){
				view.display(list.getSelectedValue());
				view.getComponent().updateUI();
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
        for (ElementClickListener<T> l : getListeners()){
        	l.elementClicked(list.getSelectedValue(), evt.getClickCount());
        }
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
	
	// -- ListModel Update Task ---------------------------------------
	
	private class UpdateTask extends TimerTask {

		@Override
		public void run() {
			synchronized(list){
				DefaultListModel<T> model = (DefaultListModel<T>) list.getModel();
				
				T selected = list.getSelectedValue();
				
				if (model.getSize() != elements.size()){
					model.removeAllElements();
					for (T ele : elements){
						model.addElement(ele);
					}
					
					if (selected == null && !elements.isEmpty()){
						selectIndex(0);
					} else {
						selectElement(selected);
					}
				}
			}
		}
	}
}