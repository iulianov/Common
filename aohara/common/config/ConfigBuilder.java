package aohara.common.config;

import java.nio.file.Path;

public class ConfigBuilder {
	
	public Config createConfig(Path configPath){
		return null;
	}
	
	public Config createConfigInDocuments(String folderName, String configName){
		return null;
	}
	
	public void addTrueFalseProperty(String name, Boolean defaultValue, boolean allowNone){
		
	}
	
	public void addPathProperty(String name, int fileChooserType, Path defaultPath, boolean allowNone){
		
	}
	
	public void addIntProperty(String name, Integer defaultValue, Integer minValue, Integer maxValue){
		
	}
}
