package aohara.common.selectorPanel;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;

import thirdParty.SortedListModel;

public class SelectorPanelBuilder<T extends Comparable<T>> {
	
	private final LinkedList<SelectorListListener<T>> selectorListListeners = new LinkedList<>();
	private final LinkedList<KeyListener> keyListeners = new LinkedList<>();
		
	private ListCellRenderer<T> renderer;
	private JPopupMenu contextMenu;
	
	public void addListContextMenu(JPopupMenu contextMenu){
		this.contextMenu = contextMenu;
	}
	
	public void setListCellRenderer(ListCellRenderer<T> renderer){
		this.renderer = renderer;
	}
	
	public void addKeyListener(KeyListener listener){
		keyListeners.add(listener);
	}
	
	public void addSelectionListener(SelectorListListener<T> listener){
		if (listener == null){
			throw new IllegalArgumentException("listener cannot be null");
		}
		selectorListListeners.add(listener);
	}
	
	public void setContextMenu(JPopupMenu contextMenu){
		this.contextMenu = contextMenu;
	}
	
	public SelectorPanelController<T> createSelectorPanel(SelectorView<T> view){
		if (view == null){
			throw new IllegalArgumentException("View must not be null");
		}
		
		// Create the List
		JList<T> list = new JList<>(new SortedListModel<T>());	
		list.setPreferredSize(new Dimension(200, 600));
		list.setCellRenderer(renderer);
		for (KeyListener keyListener : keyListeners){
			list.addKeyListener(keyListener);
		}
		
		// Add the list to a JScrollPane
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		listScrollPane.getViewport().setPreferredSize(list.getPreferredSize());
		
		// Create the split pane with the list and view
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listScrollPane);
		splitPane.setRightComponent(view.getComponent());
		
		// Register views (so they can be updated when a new element is selected)
		LinkedList<SelectorView<T>> views = new LinkedList<>();
		views.add(view);
		
		// Assemble the components into the controller
		return new SelectorPanelController<>(splitPane, list, contextMenu, views, selectorListListeners);
	}
}
