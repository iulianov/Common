package thirdParty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class SortedListModel<T> extends AbstractListModel<T> {
	SortedSet<T> model;

	public SortedListModel(Comparator<T> comparator) {
		model = new TreeSet<>(comparator);
	}

	public int getSize() {
		return model.size();
	}

	@SuppressWarnings("unchecked")
	public T getElementAt(int index) {
		return (T) model.toArray()[index];
	}
	
	public Set<T> getElements(){
		return new TreeSet<T>(model);
	}

	public void add(T element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(T elements[]) {
		Collection<T> c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public T firstElement() {
		return model.first();
	}

	public Iterator<T> iterator() {
		return model.iterator();
	}

	public T lastElement() {
		return model.last();
	}

	public boolean removeElement(T element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}