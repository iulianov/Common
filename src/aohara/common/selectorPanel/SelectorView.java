package aohara.common.selectorPanel;

import javax.swing.JComponent;

/**
 * Abstract Class for the Panel that will display the item selected in a
 * {@link aohara.common.selectorPanel.SelectorPanel}.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> The Type of element help in the JList.
 * @param <C> The Type of Component that will display the item info.
 */
public interface SelectorView<T, C extends JComponent> extends DecoratedComponent<C> {
	
	public void display(T element);
	public T getElement();

}
