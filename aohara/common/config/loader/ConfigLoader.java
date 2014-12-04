package aohara.common.config.loader;


import java.nio.file.Path;

import aohara.common.config.Config;

public abstract class ConfigLoader {
	
	public final Path filePath;
	
	public ConfigLoader(Path filePath){
		this.filePath = filePath;
	}
	
	public abstract void load(Config config);
	public abstract void save(Config config);
}
