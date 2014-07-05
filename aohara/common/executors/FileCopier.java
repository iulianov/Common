package aohara.common.executors;

import java.net.MalformedURLException;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class FileCopier extends FileTransferExecutor {
	
	public FileCopier(){
		super(1, FileTransferExecutor.OVERWRITE);
	}
	
	public FileTransferContext copy(Path src, Path dest){
		try {
			return submit(src.toUri().toURL(), dest);
		} catch (MalformedURLException e) {
			notifyError(dest);
			return null;
		}
	}
}
