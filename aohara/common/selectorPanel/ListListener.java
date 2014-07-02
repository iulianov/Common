package aohara.common.selectorPanel;

public interface ListListener<T> {
	public void elementClicked(T element, int numTimes);
	public void elementSelected(T element);
}
