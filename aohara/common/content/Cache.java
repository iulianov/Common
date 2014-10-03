package aohara.common.content;

import java.util.HashMap;
import java.util.Map;

public class Cache<V> {

	private final Map<Object[], V> cache = new HashMap<>();
	
	public V get(Object... keys){		
		return cache.get(keys);
	}
	
	public void put(V value, Object... keys){
		cache.put(keys, value);
	}
}