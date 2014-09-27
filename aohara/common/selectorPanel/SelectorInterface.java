package aohara.common.selectorPanel;


public interface SelectorInterface<T> {
	
	public void addElement(T element);
	public void removeElement(T element);
	public int selectElement(T element);
	public T selectIndex(int index);
	public void clear();

}
