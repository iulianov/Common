package aohara.common.selectorPanel;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;

import thirdParty.SortedListModel;

public class SelectorPanelBuilder<T extends Comparable<T>> {
	
	private final LinkedList<SelectorListListener<T>> selectorListListeners = new LinkedList<>();
	private final LinkedList<KeyListener> keyListeners = new LinkedList<>();
	
	private ControlPanel<T> leftControlPanel, rightControlPanel;	
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
	
	public void setLeftControlPanel(ControlPanel<T> controlPanel){
		leftControlPanel = controlPanel;
	}
	
	public void setRightControlPanel(ControlPanel<T> controlPanel){
		rightControlPanel = controlPanel;
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
		splitPane.setLeftComponent(createSidePanel(listScrollPane, leftControlPanel));
		splitPane.setRightComponent(createSidePanel(view.getComponent(), rightControlPanel));
		
		// Register views (so they can be updated when a new element is selected)
		LinkedList<SelectorView<T>> views = new LinkedList<>();
		if (leftControlPanel != null){
			views.add(leftControlPanel);
		}
		if (rightControlPanel != null){
			views.add(rightControlPanel);
		}
		views.add(view);
		
		// Assemble the components into the controller
		return new SelectorPanelController<>(splitPane, list, contextMenu, views, selectorListListeners);
	}
	
	// Helper Methods
	
	private JComponent createSidePanel(JComponent component, ControlPanel<T> controlPanel){
		// Nest the side-panel into a JScrollPane
		//JScrollPane scrollPane = new JScrollPane(component);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		//scrollPane.getViewport().setPreferredSize(component.getPreferredSize());
		
		// If not control panel, just return the JScrollPane
		if (controlPanel == null){
			//return scrollPane;
			return component;
		}
		
		// Otherwise, nest the JScrollPane and ControlPanel in a sidePanel
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		sidePanel.setPreferredSize(component.getPreferredSize());
		//sidePanel.add(scrollPane);
		sidePanel.add(component);
		sidePanel.add(controlPanel.getComponent());
		return sidePanel;
	}
}
