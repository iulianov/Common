package aohara.common.executors;

import java.net.URL;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class Downloader extends FileTransferExecutor {
	
	public Downloader(int numThreads, FileConflictResolver fileConflictResolver){
		super(numThreads, fileConflictResolver);
	}

	public FileTransferContext download(URL url, Path path) {
		return submit(url, path);
	}
}
