package aohara.common;

import java.util.LinkedList;

public abstract class Listenable<T> {
	
	private final LinkedList<T> listeners = new LinkedList<>();
	
	public void addListener(T listener){
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
