package aohara.common.executors;

import java.net.MalformedURLException;
import java.nio.file.Path;

import aohara.common.executors.context.FileTransferContext;

public class FileCopier extends FileTransferExecutor {
	
	public FileCopier(FileConflictResolver fileConflictResolver){
		super(1, fileConflictResolver);
	}
	
	public FileTransferContext copy(Path src, Path dest){
		try {
			return transfer(src.toUri().toURL(), dest);
		} catch (MalformedURLException e) {
			notifyError(null);
			return null;
		}
	}
}
