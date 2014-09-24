package aohara.common.workflows.tasks.path;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public interface PathGen {
	
	public Path getPath();
	public File getFile();
	public URL getUrl() throws MalformedURLException;
	
}
