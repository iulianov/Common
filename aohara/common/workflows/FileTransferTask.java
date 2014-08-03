package aohara.common.workflows;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

public class FileTransferTask extends WorkflowTask {
	
	private final URL input;
	private final Path dest;
	
	public FileTransferTask(Workflow workflow, URL input, Path dest){
		super(workflow);
		this.input = input;
		this.dest = groomDestinationPath(input, dest);
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
			return input.openConnection().getContentLength();
		} catch (Exception e) {
			throw new InvalidContentException();
		}
	}
	
	public static Path groomDestinationPath(URL input, Path dest){
		// Check if destination is a folder
		if (dest.toFile().isDirectory()){
			String baseName = FilenameUtils.getBaseName(input.getPath());
	        String extension = FilenameUtils.getExtension(input.getPath());
	        dest = dest.resolve(String.format("%s.%s", baseName, extension));
		}
		
		// If dest parent does not exist, create it
		if (!dest.getParent().toFile().exists()){
			dest.getParent().toFile().mkdirs();
		}
		
		return dest;
	}
}
