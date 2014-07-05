package aohara.common.executors.context;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class FileTransferContext extends ExecutorContext<URL, Path> {

	public FileTransferContext(URL subject, Path result) {
		super(subject, result);
	}

	@Override
	public int getTotalProgress() {
		try {
			return getSubject().openConnection().getContentLength();
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public String toString() {
		return getSubject().getFile();
	}

}
