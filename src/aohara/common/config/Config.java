package aohara.common.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
	
	private final Map<String, EditableProperty> options = new LinkedHashMap<>();
	private final String name;
	private final ConfigLoader loader;
	private boolean loaded = false;
	
	Config(String name, ConfigLoader loader, Collection<EditableProperty> options){
		this.name = name;
		this.loader = loader;
		
		for (EditableProperty option : options){
			this.options.put(option.name, option);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public Path getFolder(){
		return loader.filePath.getParent();
	}
	
	public void setProperty(String key, Object value) throws InvalidInputException{
		options.get(key).setValue(value);
	}
	
	public void save() throws IOException{
		loader.save(this);
	}
	
	public void rollback(){
		loaded = false;
	}
	
	public boolean isPropertySet(String key){
		ensureLoaded();
		return options.get(key).getValue() != null;
	}
	
	public Property getProperty(String key){
		ensureLoaded();
		
		EditableProperty prop = options.get(key);
		
		try {
			prop.test();
			return prop;
		} catch (InvalidInputException ex){
			OptionsWindow window = new OptionsWindow(this);
			if (window.toDialog()){
				return getProperty(key);
			} else {
				System.exit(1);
				return null;
			}
		}
	}
	
	public List<Property> getNonHiddenProperties(){
		List<Property> nonHidden = new LinkedList<>();
		for (EditableProperty prop : options.values()){
			if (!prop.hidden){
				nonHidden.add(prop);
			}
		}
		return nonHidden;
	}

	public boolean hasProperty(String key){
		return options.containsKey(key);
	}
	
	public Collection<String> keySet(){
		return options.keySet();
	}
	
	private void ensureLoaded(){
		if (!loaded){
			try {
				for (Entry<String, String> entry : loader.loadProperties(this).entrySet()){
					Class<?> type = options.get(entry.getKey()).type;
					Object value = entry.getValue();
					if (type.isAssignableFrom(java.io.File.class)){
						value = new java.io.File(value.toString());
					} else if (type.isAssignableFrom(Boolean.class)){
						value = Boolean.parseBoolean(value.toString());
					}
					setProperty(entry.getKey(), value);
				}
			} catch (IOException | InvalidInputException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
	}
}