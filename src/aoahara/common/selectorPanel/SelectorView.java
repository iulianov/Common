package aoahara.common.selectorPanel;

public interface SelectorView<T> extends SelectorBasePanel {
	
	public void display(T element);
	public T getElement();

}
