package aohara.common.executors;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class TempDownloader extends Downloader {
	
	public TempDownloader(int numThreads, FileConflictResolver conflictStrategy){
		super(numThreads, conflictStrategy);
	}
	
	@Override
	protected FileTransferContext submit(URL input, Path dest){
		FileTransferContext context = new FileTransferContext(input, dest);
		try {
			int totalBytes = input.openConnection().getContentLength();
			if (totalBytes < 0){
				throw new IOException();
			}
			return (FileTransferContext) submit(new TempDownloadTask(
				context, Files.createTempFile("download", ".temp")
			));
		} catch (IOException e) {
			notifyError(context);
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
		public void execute(FileTransferContext context) throws Exception {
			// Download to temporary file, and then move over to destination
			transfer(context.getSource(), tempPath);
			notifySuccess();
			
			// Perform Move
			notifyStart((int) tempPath.toFile().length());
			this.transfer(tempPath.toUri().toURL(), context.getDest());
			tempPath.toFile().delete();
		}
	}

}
