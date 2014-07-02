package aohara.common.selectorPanel;

import java.util.Collection;

public interface SelectorListener<T> {
	
	public void setDataSource(Collection<T> elements);
	public int selectElement(T element);
	public T selectIndex(int index);

}
