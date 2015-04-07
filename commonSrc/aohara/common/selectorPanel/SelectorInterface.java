package aohara.common.selectorPanel;

import java.util.Collection;


public interface SelectorInterface<T> {
	
	public int selectElement(T element);
	public T selectIndex(int index);
	public void setData(Collection<T> data);

}
