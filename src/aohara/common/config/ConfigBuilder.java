package aohara.common.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.loader.ConfigLoader;
import aohara.common.config.loader.JsonConfigLoader;

public class ConfigBuilder {
	
	private final Map<EditableProperty, Object> defaults = new LinkedHashMap<>();
	
	private void addProperty(EditableProperty prop, Object defaultValue, boolean allowNone){
		// Add Constraints
		if (!allowNone){
			prop.addConstraint(new Constraints.NotNull(prop.name));
		}		
		defaults.put(prop, defaultValue);
	}
	
	public void addBooleanProperty(String name, Boolean defaultValue, boolean allowNone, boolean hidden){
		addProperty(new EditableProperty(name, hidden, Boolean.class), defaultValue, allowNone);
	}
	
	public void addPathProperty(String name, int fileSelectionMode, File defaultFile, boolean allowNone, boolean hidden){
		EditableProperty prop = new EditableProperty(name, hidden, File.class);
		addProperty(prop, defaultFile, allowNone);
		prop.addConstraint(new Constraints.EnsureIsFile(prop.name, fileSelectionMode));
	}
	
	public void addIntProperty(String name, Integer defaultValue, Integer minValue, Integer maxValue, boolean allowNone, boolean hidden){
		EditableProperty prop = new EditableProperty(name, hidden, Integer.class);
		addProperty(prop, defaultValue, allowNone);
		prop.addConstraint(new Constraints.EnsureInt(prop.name, minValue, maxValue));
	}
	
	public void addStringProperty(String name, String defaultValue, boolean allowNone, boolean hidden){
		addProperty(new EditableProperty(name, hidden, String.class), defaultValue, allowNone);
	}
	
	// --------------
	// -- Builders --
	// --------------
	
	public Config createConfig(String name, Path filePath){
		Config config = new Config(name, createLoader(filePath), defaults.keySet());
		setDefaults(config);
		return config;
	}
	
	public Config createConfigInDocuments(String name, String folderName, String fileName){
		return createConfig(
			name,
			Paths.get(System.getProperty("user.home"), "Documents", folderName, fileName)
		);
	}
	
	// -------------
	// -- Helpers --
	// -------------
	
	private ConfigLoader createLoader(Path filePath){
		return new JsonConfigLoader(filePath);
	}
	
	private void setDefaults(Config config){
		for (EditableProperty prop : defaults.keySet()){
			Object value = defaults.get(prop);
			
			if (value != null && !config.isPropertySet(prop.name)){
				try {
					prop.setValue(value);
				} catch (InvalidInputException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
