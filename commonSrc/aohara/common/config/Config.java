package aohara.common.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.loader.ConfigLoader;
import aohara.common.config.views.OptionInput;
import aohara.common.config.views.OptionsWindow;

/**
 * Abstract Configuration Manager which stores data in a properties file.
 * 
 * @author Andrew O'Hara
 */
public class Config {
	
	private final Map<String, OptionInput> options = new LinkedHashMap<>();
	private final String name;
	private final ConfigLoader loader;
	private boolean loaded = false;
	
	Config(String name, ConfigLoader loader, Collection<OptionInput> optionInputs){
		this.name = name;
		this.loader = loader;
		
		for (OptionInput input : optionInputs){
			options.put(input.option.name, input);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public Path getFolder(){
		return loader.filePath.getParent();
	}
	
	public void setProperty(String key, String value) throws InvalidInputException{
		options.get(key).option.setValue(value);
	}
	
	public void save() throws IOException{
		loader.save(this);
	}
	
	public void rollback(){
		loaded = false;
	}
	
	public boolean isPropertySet(String key){
		ensureLoaded();
		return options.get(key).option.getValue() != null;
	}
	
	public String getProperty(String key){
		ensureLoaded();
		
		OptionInput input = options.get(key);
		Option option = input.option;
		
		try {
			option.testValue(option.getValue());
			return option.getValue();
		} catch (InvalidInputException ex){
			new OptionsWindow(this).toDialog();
			try {
				option.testValue(option.getValue());
				return option.getValue();
			} catch (InvalidInputException e) {
				System.exit(1);
				return null;
			}
		}
	}

	public boolean hasProperty(String key){
		return options.containsKey(key);
	}
	
	public Collection<String> keySet(){
		return options.keySet();
	}
	
	public Collection<OptionInput> getInputs(){
		Collection<OptionInput> inputs = new LinkedList<>();
		for (OptionInput input : options.values()){
			if (!input.option.hidden){
				inputs.add(input);
			}
		}
		return inputs;
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