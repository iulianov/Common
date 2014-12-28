package aohara.common.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.loader.ConfigLoader;
import aohara.common.config.loader.JsonConfigLoader;

public class ConfigBuilder {
	
	private final Map<Option, OptionInput> options = new LinkedHashMap<>();
	private final Map<Option, String> defaults = new LinkedHashMap<>();
	
	private void addProperty(Option option, OptionInput input, String defaultValue, boolean allowNone, Constraint... constraints){
		// Add Constraints
		if (!allowNone){
			option.addConstraint(new Constraints.NotNull(option));
		}
		for (Constraint c : constraints){
			option.addConstraint(c);
		}
		
		options.put(option, input);
		defaults.put(option, defaultValue);
	}
	
	public void addMultiProperty(String name, Collection<String> choices, String defaultValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		addProperty(
			option,
			new OptionInput.ComboBoxInput(option, choices),
			defaultValue,
			allowNone
		);
	}
	
	public void addTrueFalseProperty(String name, Boolean defaultValue, boolean allowNone, boolean hidden){
		addMultiProperty(
			name,
			Arrays.asList(new String[]{Boolean.TRUE.toString(), Boolean.FALSE.toString()}),
			defaultValue != null ? defaultValue.toString() : null,
			allowNone,
			hidden
		);
	}
	
	public void addPathProperty(String name, int fileSelectionMode, Path defaultPath, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);		
		addProperty(
			option,
			new OptionInput.FileChooserInput(option, fileSelectionMode),
			defaultPath != null ? defaultPath.toString() : null,
			allowNone
		);
	}
	
	public void addIntProperty(String name, Integer defaultValue, Integer minValue, Integer maxValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		addProperty(
			option,
			new OptionInput.TextFieldInput(option),
			Integer.toString(defaultValue),
			allowNone,
			new Constraints.EnsureInt(option, minValue, maxValue)
		);
	}
	
	public void addTextProperty(String name, String defaultValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		addProperty(option, new OptionInput.TextFieldInput(option), defaultValue, allowNone);
	}
	
	// --------------
	// -- Builders --
	// --------------
	
	public Config createConfig(String name, Path filePath){
		Config config = new Config(name, createLoader(filePath), options.keySet());
		setDefaults(config);
		return config;
	}
	
	public Config createConfigInDocuments(String name, String folderName, String fileName){
		return createConfig(name, Paths.get(
			System.getProperty("user.home"), "Documents",
			folderName, fileName
		));
	}
	
	public GuiConfig createGuiConfig(String name, Path filePath){
		GuiConfig config = new GuiConfig(name, createLoader(filePath), options);
		setDefaults(config);
		return config;
	}
	
	public GuiConfig createGuiConfigInDocuments(String name, String folderName, String fileName){
		return createGuiConfig(name, Paths.get(
			System.getProperty("user.home"), "Documents",
			folderName, fileName
		));
	}
	
	// -------------
	// -- Helpers --
	// -------------
	
	private ConfigLoader createLoader(Path filePath){
		return new JsonConfigLoader(filePath);
	}
	
	private void setDefaults(Config config){
		for (Entry<Option, String> entry : defaults.entrySet()){
			Option option = entry.getKey();
			String value = entry.getValue();
			if (value != null && config.getProperty(option.name) == null){
				try {
					option.setValue(value);
				} catch (InvalidInputException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
