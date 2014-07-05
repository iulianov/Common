package aohara.common.executors;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

public class TempDownloader extends Downloader {
	
	private final FileMover fileMover;
	
	public TempDownloader(int numThreads, FileMover fileMover){
		super(numThreads);
		this.fileMover = fileMover;
	}
	
	@Override
	protected Future<Path> submit(URL input, Path dest){
		System.err.println("foo");
		try {
			int totalBytes = input.openConnection().getContentLength();
			return executor.submit(new TempDownloadTask(
				input, dest, Files.createTempFile("download", ".temp"),
				totalBytes));
		} catch (IOException e) {
			notifyError(dest);
			return null;
		}
	}
	
	private class TempDownloadTask extends FileTask {
		
		private final Path tempPath;
		
		private TempDownloadTask(URL input, Path dest, Path tempPath, int totalBytes){
			super(input, dest, totalBytes);
			this.tempPath = tempPath;
		}
		
		@Override
		public void execute() throws Exception {
			// Download to temporary file, and then move over to destination
			transfer(input, tempPath);
			
			// Move to destination, and wait on the copy
			fileMover.copy(tempPath, subject).get();
		}
	}

}
