package aohara.common.executors;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class TempDownloader extends Downloader {
	
	private final FileMover fileMover;
	
	public TempDownloader(int numThreads, FileMover fileMover){
		super(numThreads);
		this.fileMover = fileMover;
	}
	
	@Override
	protected FileTransferContext submit(URL input, Path dest){
		try {
			int totalBytes = input.openConnection().getContentLength();
			if (totalBytes < 0){
				throw new IOException();
			}
			FileTransferContext context = new FileTransferContext(input, dest);
			return (FileTransferContext) submit(new TempDownloadTask(
				context, Files.createTempFile("download", ".temp")
			));
		} catch (IOException e) {
			notifyError(dest);
			return null;
		}
	}
	
	public class TempDownloadTask extends FileTask {
		
		private final Path tempPath;
		
		public TempDownloadTask(FileTransferContext context, Path tempPath){
			super(context);
			this.tempPath = tempPath;
		}
		
		@Override
		public void execute() throws Exception {
			// Download to temporary file, and then move over to destination
			transfer(getSubject(), tempPath);
			
			// Move to destination, and wait on the copy
			fileMover.copy(tempPath, getResult()).join();
		}
	}

}
