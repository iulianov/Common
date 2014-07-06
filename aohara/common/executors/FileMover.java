package aohara.common.executors;

import java.net.MalformedURLException;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class FileMover extends FileCopier {
	
	public FileMover(FileConflictResolver fileConflictResolver){
		super(fileConflictResolver);
	}
	
	public FileTransferContext move(Path src, Path dest){
		try {
			FileTask task = new MoveTask(new FileTransferContext(
					src.toUri().toURL(), dest));
			return (FileTransferContext) submit(task);
		} catch (MalformedURLException e) {
			notifyError(null);
			return null;
		}
		
	}
	
	protected class MoveTask extends FileTask {

		protected MoveTask(FileTransferContext context) {
			super(context);
		}
		
		@Override
		protected FileTransferContext execute() throws Exception {
			FileTransferContext context = super.execute();
			getDest().toFile().delete();	
			return context;
		}
	}
}
