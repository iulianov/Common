package aoahara.common.selectorPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class SelectorPanel<T> implements ListSelectionListener, SelectorListener<T>, SelectorBasePanel {
	
	private final JSplitPane splitPane = new SelectorSplitPane();
	private final JList<T> list = new JList<T>(new DefaultListModel<T>());
	private final SelectorView<T> view;
	private Collection<T> elements = new LinkedList<>();

	public SelectorPanel(SelectorView<T> view){
		this.view = view;
		
		// Create Side Panels
		splitPane.setLeftComponent(new JPanel(new BorderLayout()));
		splitPane.setRightComponent(new JPanel(new BorderLayout()));
		
		// Add Main Components to Side Panels
		addComponent(true, BorderLayout.CENTER, list);
		addComponent(false, BorderLayout.CENTER, view.getComponent());
		
		splitPane.setContinuousLayout(true);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(150, 800));
		
		splitPane.setVisible(true);
		
		// Start Update Timer
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
	}
	
	private JComponent addComponent(boolean left, Object cons, JComponent comp){
		JPanel panel = (JPanel) (left ? splitPane.getLeftComponent() : splitPane.getRightComponent());
		panel.add(comp, cons);		
		return comp;
	}
	
	public JComponent addControlPanel(boolean left, ControlPanel panel){
		return addComponent(left, BorderLayout.SOUTH, panel.getComponent());
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

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (!arg0.getValueIsAdjusting()){
			view.display(list.getSelectedValue());
		}
	}
	
	public JComponent getComponent(){
		return splitPane;
	}
	
	// -- Internal Components -----------------------------------------
	
	private class SelectorSplitPane extends JSplitPane {
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
