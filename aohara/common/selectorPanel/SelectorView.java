package aohara.common.selectorPanel;

import javax.swing.JComponent;

public interface SelectorView<T, C extends JComponent> extends DecoratedComponent<C> {
	
	public void display(T element);
	public T getElement();

}
