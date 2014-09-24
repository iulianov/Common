package aohara.common.workflows.tasks.path;

import java.io.File;
import java.nio.file.Path;

public abstract class AbstractPathGen implements PathGen {
	
	private Path cachedPath;
	
	public abstract Path generatePath();
	
	@Override
	public Path getPath(){
		if (cachedPath == null){
			cachedPath = generatePath();
		}
		return cachedPath;
	}
	
	@Override
	public File getFile(){
		return getPath().toFile();
	}
	
}
