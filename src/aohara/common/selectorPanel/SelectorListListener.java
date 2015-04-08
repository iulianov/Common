package aohara.common.selectorPanel;

public interface SelectorListListener<T> {
	public void elementClicked(T element, int numTimes);
	public void elementSelected(T element);
}
