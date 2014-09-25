package aohara.common.workflows.tasks.gen;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class GenFactory {
	
	public static PathGen fromPath(final Path path){
		return new PathGen(){
			@Override
			public Path getPath() {
				return path;
			}

			@Override
			public URI getURI() throws URISyntaxException{
				return getPath().toUri();
			}
		};
	}
	
	public static URLGen fromUrl(final URL url){
		return new URLGen(){
			@Override
			public URL getURL() {
				return url;
			}

			@Override
			public URI getURI() throws URISyntaxException{
				return getURL().toURI();
			}
		};
	}

}
