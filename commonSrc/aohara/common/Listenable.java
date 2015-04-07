package aohara.common;

import java.util.LinkedList;

/**
 * Alternative to {@link java.util.Observable} which supports generics.
 * 
 * This allows the listenable class to invoke concrete methods included in the
 * interface specified by the generic.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> The type of listener that can listen to this class.
 */
public abstract class Listenable<T> {
	
	private final LinkedList<T> listeners = new LinkedList<>();
	
	public void addSelectionListener(T listener){
		listeners.add(listener);
	}
	
	public void clearListeners(){
		listeners.clear();
	}
	
	public void removeListener(T listener){
		listeners.remove(listener);
	}
	
	protected java.util.Collection<T> getListeners(){
		return new LinkedList<T>(listeners);
	}

}
