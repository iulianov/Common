package aohara.common;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> {
	
	private final Map<K, CacheEntry<V>> cache = new HashMap<>();
	private final long entryValidityMs;
	
	public SimpleCache(long validForMs){
		this.entryValidityMs = validForMs;
	}
	
	public void put(K key, V value){
		cache.put(
			key,
			new CacheEntry<>(value, System.currentTimeMillis() + entryValidityMs)
		);
	}
	
	public V get(K key){
		return containsKey(key) ? cache.get(key).value : null;
	}
	
	public boolean containsKey(K key){
		return cache.containsKey(key) && cache.get(key).expiryTime > System.currentTimeMillis();
	}
	
	public void clear(){
		cache.clear();
	}
	
	private static class CacheEntry<V> {
		
		private final V value;
		private final long expiryTime;
		
		private CacheEntry(V value, long expiryTime){
			this.value = value;
			this.expiryTime = expiryTime;
		}
	}
}
