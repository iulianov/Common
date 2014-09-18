package aohara.common.workflows.tasks;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.Workflow;

/**
 * WorkflowTask to transfer the file from the given URL to the given path location.
 * 
 * @author Andrew O'Hara
 */
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
		// Check for file conflict
		if (dest.toFile().isFile() && dest.toFile().exists()){
			dest.toFile().delete();
		}
		
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
	public int getTargetProgress() throws IOException {
		return input.openConnection().getContentLength();
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

	@Override
	public String getTitle() {
		return String.format("Transferring to %s", dest);
	}
}
