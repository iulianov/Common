package aohara.common.config.loader;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import aohara.common.config.Config;

public abstract class ConfigLoader {
	
	public final Path filePath;
	
	public ConfigLoader(Path filePath){
		this.filePath = filePath;
	}
	
	public abstract Map<String, String> loadProperties(Config config) throws IOException;
	public abstract void save(Config config) throws IOException;
}
