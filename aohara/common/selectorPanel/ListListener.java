package aohara.common.selectorPanel;

public interface ListListener<T> {
	public void elementClicked(T element, int numTimes) throws Exception;
	public void elementSelected(T element) throws Exception;
}
