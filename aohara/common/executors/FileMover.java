package aohara.common.executors;

import java.net.URL;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class FileMover extends FileCopier {
	
	@Override
	protected FileTransferContext submit(URL input, Path dest){
		FileTask task = new MoveTask(new FileTransferContext(input, dest));
		return (FileTransferContext) submit(task);
	}
	
	protected class MoveTask extends FileTask {

		protected MoveTask(FileTransferContext context) {
			super(context);
		}
		
		@Override
		protected void execute() throws Exception {
			super.execute();
			getResult().toFile().delete();	
		}
		
	}

}
