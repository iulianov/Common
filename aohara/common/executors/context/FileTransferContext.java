package aohara.common.executors.context;

import java.net.URL;
import java.nio.file.Path;

public class FileTransferContext extends ExecutorContext {

	private final URL source;
	private Path dest;
	
	public FileTransferContext(URL subject, Path result) {
		this.source = subject;
		this.dest = result;
	}

	@Override
	public String toString() {
		return getDest().getFileName().toString();
	}
	
	public Path getDest(){
		return dest;
	}
	
	public Path setDest(Path result){
		this.dest = result;
		return result;
	}
	
	public URL getSource(){
		return source;
	}
}
