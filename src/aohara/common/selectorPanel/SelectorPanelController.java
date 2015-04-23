package aohara.common.selectorPanel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import aohara.common.selectorPanel.DecoratedComponent;
import aohara.common.selectorPanel.SelectorListListener;
import aohara.common.selectorPanel.SelectorView;
import thirdParty.SortedListModel;

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
public class SelectorPanelController<T extends Comparable<T>> implements DecoratedComponent<JSplitPane> {
	
	private final Collection<SelectorListListener<T>> listeners;
	private final Collection<SelectorView<T>> views;
	
	private final JSplitPane splitPane;
	private final JList<T> list;
	private final JPopupMenu contextMenu;

	SelectorPanelController(
			JSplitPane splitPane,
			JList<T> list,
			JPopupMenu contextMenu,
			Collection<SelectorView<T>> views,
			Collection<SelectorListListener<T>> listeners
	) {
		this.splitPane = splitPane;
		this.list = list;
		this.contextMenu = contextMenu;
		this.views = views;
		this.listeners = listeners;
		
		// When the list selection changes, update views to reflect it
		InnerListener innerListener = new InnerListener();
		list.addListSelectionListener(innerListener);
		list.addMouseListener(innerListener);
	}
	
	// -- Interface -----------------------------------------------------
	
	public int selectElement(T element) {
		list.setSelectedValue(element, true);
		return list.getSelectedIndex();
	}

	public T selectIndex(int index) {
		list.setSelectedIndex(index);
		return list.getSelectedValue();
	}

	public void setData(final Collection<T> data) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				SortedListModel<T> model = (SortedListModel<T>) list.getModel();
				model.clear();
				for (T element : data){
					model.add(element);
				}
			}
			
		});
	}
	
	@Override
	public JSplitPane getComponent() {
		return splitPane;
	}
	
	public JList<T> getList(){
		return list;
	}
	
	// -- Listeners --------------------------------------------------
	
	private class InnerListener extends MouseAdapter implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()){
				
				T element = list.getSelectedValue();
				
				for (SelectorListListener<T> l : listeners){
					l.elementSelected(element);
				}
				
				// Update all views for selected element
				for (SelectorView<T> view : views){
					view.display(element);
					view.getComponent().updateUI();
				}
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent evt) {
			for (SelectorListListener<T> l : listeners){
				l.elementClicked(list.getSelectedValue(), evt.getClickCount());
			}
			
			if (SwingUtilities.isRightMouseButton(evt)){
				list.setSelectedIndex(list.locationToIndex(evt.getPoint()));
				 
				 // Open Popup Menu
				 if (contextMenu != null){
					 contextMenu.show((Component) evt.getSource(), evt.getX(), evt.getY());
				 }
			}
	    }
	}
}
