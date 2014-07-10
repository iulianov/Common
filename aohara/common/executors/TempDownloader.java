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
	public FileTransferContext submit(FileTransferContext context){
		try {
			submit(new TempDownloadTask(
					context, Files.createTempFile("download", ".temp")));
			return context;
		} catch (IOException e) {
			notifyError(context);
			return null;
		}
	}
	
	@Override
	public FileTransferContext transfer(URL input, Path dest){
		return submit(new FileTransferContext(input, dest));
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
			
			// Perform Move
			transfer(tempPath.toUri().toURL(), context.getDest());
			tempPath.toFile().delete();
		}
		
		@Override
		protected int getTotalProgress(FileTransferContext context) {
			try {
				return context.getSource().openConnection().getContentLength() * 2;
			} catch (IOException e) {
				return -1;
			}
		}
	}

}
