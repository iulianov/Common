package aohara.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import aohara.common.config.Constraint.InvalidInputException;

/**
 * Abstract Configuration Manager which stores data in a properties file.
 * 
 * @author Andrew O'Hara
 */
public class Config {
	
	private final Map<Option, OptionInput> options;
	private final Path filePath;
	
	public Config(Path filePath, Map<Option, OptionInput> options){
		if (!filePath.toFile().isFile()){
			throw new IllegalArgumentException("filePath must be a file");
		}
		
		// Bind Options to this config
		for (Option option : options.keySet()){
			option.setConfig(this);
		}
		
		this.filePath = filePath;
		this.options = options;
		load();
	}
	
	public Path getFolder(){
		return filePath.getParent();
	}
	
	public void setProperty(String key, String value){
		Properties props = load();
		props.setProperty(key, value);

		// Save Properties to File
		filePath.toFile().getParentFile().mkdirs();		
		try(FileOutputStream os = new FileOutputStream(filePath.toFile())){
			props.store(os, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key){
		return load().getProperty(key);
	}
	
	public boolean hasProperty(String key){
		return load().containsKey(key);
	}
	
	public Collection<String> keySet(){
		Collection<String> keys = new LinkedList<>();
		for (Option option : options.keySet()){
			keys.add(option.name);
		}
		return keys;
	}
	
	private Properties load(){
		Properties props = new Properties();
		try(FileInputStream is = new FileInputStream(filePath.toFile())){
			props.load(is);
		} catch (FileNotFoundException ex){
			// No Action
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	public boolean isValid(){
		try {
			for (Option option : options.keySet()){
				option.testValue(option.getValue());
			}
			return true;
		} catch (InvalidInputException e) {
			return false;
		}
	}
	
	public void openOptionsWindow(boolean restartOnSuccess, boolean exitOnCancel){
		new OptionsWindow("Options", options.values(), restartOnSuccess, exitOnCancel).toDialog();
	}
}