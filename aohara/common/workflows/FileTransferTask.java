package aohara.common.workflows;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

public class FileTransferTask extends WorkflowTask {
	
	private final URL input;
	private final Path dest;
	
	public FileTransferTask(Workflow workflow, URL input, Path dest){
		super(workflow);
		this.input = input;
		this.dest = dest;
	}

	@Override
	public Boolean call() throws Exception {
		try (
			InputStream is = new BufferedInputStream(input.openStream());
			OutputStream os = new FileOutputStream(dest.toFile());
		){
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
				progress(bytesRead);
			}
		}
		return true;
	}

	@Override
	protected int getTargetProgress() throws InvalidContentException{
		try {
			int length = input.openConnection().getContentLength();
			if (length < 1){
				throw new Exception();
			}
			return length;
		} catch (Exception e) {
			throw new InvalidContentException();
		}
	}
}
