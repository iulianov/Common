package aohara.common.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.loader.ConfigLoader;

/**
 * Abstract Configuration Manager which stores data in a properties file.
 * 
 * @author Andrew O'Hara
 */
public class Config {
	
	private final Map<String, Option> options = new HashMap<>();
	private final String name;
	private final ConfigLoader loader;
	private boolean loaded = false;
	
	public Config(String name, ConfigLoader loader, Collection<Option> options){
		this.name = name;
		this.loader = loader;
		
		for (Option option : options){
			this.options.put(option.name, option);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public Path getFolder(){
		return loader.filePath.getParent();
	}
	
	public void setProperty(String key, String value) throws InvalidInputException{
		options.get(key).setValue(value);
	}
	
	public void save() throws IOException{
		loader.save(this);
	}
	
	public void rollback(){
		loaded = false;
	}
	
	public String getProperty(String key){
		ensureLoaded();
		return options.get(key).getValue();
	}
	
	public boolean hasProperty(String key){
		return options.containsKey(key);
	}
	
	public Collection<String> keySet(){
		return options.keySet();
	}
	
	public boolean isValid(){
		ensureLoaded();
		
		try {
			for (Option option : options.values()){
				option.testValue(option.getValue());
			}
			return true;
		} catch (InvalidInputException e) {
			return false;
		}
	}
	
	private void ensureLoaded(){
		if (!loaded){
			try {
				for (Entry<String, String> entry : loader.loadProperties(this).entrySet()){
					setProperty(entry.getKey(), entry.getValue());
				}
			} catch (IOException | InvalidInputException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
	}
}