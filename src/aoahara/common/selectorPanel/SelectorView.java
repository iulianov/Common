package aoahara.common.selectorPanel;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class SelectorView<T> extends JPanel {
	
	public abstract void display(T element);

}
