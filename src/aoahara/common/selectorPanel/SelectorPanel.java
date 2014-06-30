package aoahara.common.selectorPanel;

import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class SelectorPanel<T> extends JSplitPane
		implements ListSelectionListener, SelectorListener<T> {
	
	private final JList<T> list;
	private final SelectorView<T> view;
	private Collection<T> elements = new LinkedList<>();

	public SelectorPanel(SelectorView<T> view){
		setLeftComponent(list = new JList<T>(new DefaultListModel<T>()));
		setRightComponent(this.view = view);
		setContinuousLayout(true);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(150, 800));
		
		setVisible(true);
		
		// Start Update Timer
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new UpdateTask(), 0, 1000);
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
			view.updateUI();
		}
	}
	
	@Override
	public void paintComponent(java.awt.Graphics g){
		synchronized(list){
			super.paintComponent(g);
		}
	}
	
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
