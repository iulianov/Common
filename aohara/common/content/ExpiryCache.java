package aohara.common.content;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache where data has an expiry date.
 * 
 * @author Andrew O'Hara
 *
 * @param <K> type of Key
 * @param <V> type of Value
 */
public class ExpiryCache<K, V> {
	
	private final Map<K, CacheEntry<V>> cache = new HashMap<>();
	private final long entryValidityMs;
	
	public ExpiryCache(long validForMs){
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
