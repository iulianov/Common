package aohara.common.executors;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Future;

public class Downloader extends FileExecutor {

	public Downloader(int numThreads){
		super(numThreads, FileExecutor.INCREMENT);
	}

	public Future<Path> download(URL url, Path path) {
		return submit(url, path);
	}
}
