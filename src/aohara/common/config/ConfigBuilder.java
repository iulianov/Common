package aohara.common.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import aohara.common.config.Constraint.InvalidInputException;
import aohara.common.config.loader.ConfigLoader;
import aohara.common.config.loader.JsonConfigLoader;
import aohara.common.config.views.OptionInput;

public class ConfigBuilder {
	
	private final Collection<OptionInput> inputs = new LinkedList<>();
	private final Map<Option, String> defaults = new LinkedHashMap<>();
	
	private OptionInput addProperty(OptionInput input, String defaultValue, boolean allowNone, Constraint... constraints){
		// Add Constraints
		if (!allowNone){
			input.option.addConstraint(new Constraints.NotNull(input.option));
		}
		for (Constraint c : constraints){
			input.option.addConstraint(c);
		}
		
		inputs.add(input);
		defaults.put(input.option, defaultValue);
		return input;
	}
	
	public OptionInput addMultiProperty(String name, Collection<String> choices, String defaultValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		return addProperty(
			new OptionInput.ComboBoxInput(option, choices),
			defaultValue,
			allowNone
		);
	}
	
	public OptionInput addTrueFalseProperty(String name, Boolean defaultValue, boolean allowNone, boolean hidden){
		return addMultiProperty(
			name,
			Arrays.asList(new String[]{Boolean.TRUE.toString(), Boolean.FALSE.toString()}),
			defaultValue != null ? defaultValue.toString() : null,
			allowNone,
			hidden
		);
	}
	
	public OptionInput addPathProperty(String name, int fileSelectionMode, Path defaultPath, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);		
		return addProperty(
			new OptionInput.FileChooserInput(option, fileSelectionMode),
			defaultPath != null ? defaultPath.toString() : null,
			allowNone
		);
	}
	
	public OptionInput addIntProperty(String name, Integer defaultValue, Integer minValue, Integer maxValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		return addProperty(
			new OptionInput.TextFieldInput(option),
			Integer.toString(defaultValue),
			allowNone,
			new Constraints.EnsureInt(option, minValue, maxValue)
		);
	}
	
	public OptionInput addTextProperty(String name, String defaultValue, boolean allowNone, boolean hidden){
		Option option = new Option(name, hidden);
		return addProperty(new OptionInput.TextFieldInput(option), defaultValue, allowNone);
	}
	
	// --------------
	// -- Builders --
	// --------------
	
	public Config createConfig(String name, Path filePath){
		Config config = new Config(name, createLoader(filePath), inputs);
		setDefaults(config);
		return config;
	}
	
	public Config createConfigInDocuments(String name, String folderName, String fileName){
		return createConfig(
			name,
			Paths.get(
				System.getProperty("user.home"), "Documents",
				folderName, fileName
			)
		);
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
			
			if (value != null && !config.isPropertySet(option.name)){
				try {
					option.setValue(value);
				} catch (InvalidInputException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
