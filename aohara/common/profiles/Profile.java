package aohara.common.profiles;

import java.util.Map.Entry;
import java.util.Set;

public abstract class Profile {
	
	public static final String NAME = "name";
	public final String name;
	
	public Profile(String name){
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Profile){
			return ((Profile)o).entrySet().equals(entrySet());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return entrySet().hashCode() + name.hashCode();
	}
	
	@Override
	public String toString(){
		return "Profile : " + name;
	}
		
	public abstract String getProperty(String key);
	public abstract boolean hasProperty(String key);
	public abstract String putProperty(String key, String value);
	protected abstract Set<Entry<String, String>> entrySet();
}
