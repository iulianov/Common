package aohara.common.config;

import java.util.Collection;
import java.util.Map;

import aohara.common.config.loader.ConfigLoader;

public class GuiConfig extends Config{
	
	private final Collection<OptionInput> inputs;

	public GuiConfig(String name, ConfigLoader loader, Map<Option, OptionInput> options) {
		super(name, loader, options.keySet());
		inputs = options.values();
	}
	
	public void openOptionsWindow(boolean exitOnCancel){
		for (OptionInput input : inputs){
			input.update();
		}
		
		new OptionsWindow(this, inputs, exitOnCancel).toDialog();
	}

}
