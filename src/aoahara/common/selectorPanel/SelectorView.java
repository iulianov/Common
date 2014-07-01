package aoahara.common.selectorPanel;

import javax.swing.JPanel;

public interface SelectorView<T, C extends JPanel> extends DecoratedComponent<C> {
	
	public void display(T element);
	public T getElement();

}
