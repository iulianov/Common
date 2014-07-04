package aohara.common.executors;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.concurrent.Future;

public class FileCopier extends FileExecutor {
	
	public FileCopier(){
		super(1, FileExecutor.OVERWRITE, false);
	}
	
	public Future<Path> copy(Path src, Path dest){
		try {
			return submit(src.toUri().toURL(), dest);
		} catch (MalformedURLException e) {
			notifyError(dest);
			return null;
		}
	}
}
