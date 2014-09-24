package aohara.common.workflows.tasks.path;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class DefaultPathGen extends AbstractPathGen {
	
	private final Path path;
	
	public DefaultPathGen(Path path){
		this.path = path;
	}

	@Override
	public Path generatePath() {
		return path;
	}

	@Override
	public URL getUrl() throws MalformedURLException {
		return getPath().toUri().toURL();
	}
}
