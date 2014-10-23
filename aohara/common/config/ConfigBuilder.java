package aohara.common.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import aohara.common.config.Constraint.InvalidInputException;

public class ConfigBuilder {
	
	private final Map<Option, OptionInput> options = new LinkedHashMap<>();
	private final Map<Option, String> defaults = new LinkedHashMap<>();
	
	private void addProperty(Option option, OptionInput input, String defaultValue, Constraint... constraints){
		options.put(option, input);
		defaults.put(option, defaultValue);
		
		for (Constraint c : constraints){
			option.addConstraint(c);
		}
	}
	
	public void addMultiProperty(String name, Collection<String> choices, String defaultValue, boolean allowNone){
		Option option = new Option(name);
		if (!allowNone){
			option.addConstraint(new Constraints.NotNull(option));
		}
		
		addProperty(
			option,
			new OptionInput.ComboBoxInput(option, choices),
			defaultValue
		);
	}
	
	public void addTrueFalseProperty(String name, Boolean defaultValue, boolean allowNone){
		addMultiProperty(
			name,
			Arrays.asList(new String[]{Boolean.TRUE.toString(), Boolean.FALSE.toString()}),
			defaultValue != null ? defaultValue.toString() : null,
			allowNone
		);
	}
	
	public void addPathProperty(String name, int fileSelectionMode, Path defaultPath, boolean allowNone){
		Option option = new Option(name);
		addProperty(
			option,
			new OptionInput.FileChooserInput(option, fileSelectionMode),
			defaultPath != null ? defaultPath.toString() : null
		);
	}
	
	public void addIntProperty(String name, Integer defaultValue, Integer minValue, Integer maxValue){
		Option option = new Option(name);
		addProperty(
			option,
			new OptionInput.TextFieldInput(option),
			Integer.toString(defaultValue),
			new Constraints.EnsureInt(option, minValue, maxValue)
		);
	}
	
	public void addTextProperty(String name, String defaultValue){
		Option option = new Option(name);
		addProperty(option, new OptionInput.TextFieldInput(option), defaultValue);
	}
	
	public Config createConfig(Path configPath){
		Config config = new Config(configPath, options);
		
		for (Entry<Option, String> entry : defaults.entrySet()){
			Option option = entry.getKey();
			String value = entry.getValue();
			if (value != null){
				try {
					option.setValue(value);
				} catch (InvalidInputException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return config;
	}
	
	public Config createConfigInDocuments(String folderName, String configName){
		return createConfig(Paths.get(
			System.getProperty("user.home"), "Documents",
			folderName, configName
		));
	}
}
