package aohara.common.profiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapProfile extends Profile {
	
	private final Map<String, String> data = new HashMap<>();
	
	public MapProfile(String name) {
		super(name);
	}
	
	public String getProperty(String key){
		return data.get(key);
	}
	
	public boolean hasProperty(String key){
		return data.containsKey(key);
	}
	
	public String putProperty(String key, String value){
		return data.put(key, value);
	}

	@Override
	protected Set<Entry<String, String>> entrySet() {
		return data.entrySet();
	}

}
