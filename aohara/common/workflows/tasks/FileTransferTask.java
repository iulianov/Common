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
	
	public static final float REPORT_PER_PERCENT = (float) 0.01;
	private final URL input;
	private final Path dest;	
	
	public FileTransferTask(Workflow workflow, URL input, Path dest){
		super(workflow);
		this.input = input;
		this.dest = groomDestinationPath(input, dest);
	}

	@Override
	public Boolean call() throws Exception {
		transferFile(this, input, dest);
		return true;
	}
	
	public static void transferFile(WorkflowTask task, URL input, Path dest) throws IOException{
		// Check for file conflict
		if (dest.toFile().isFile() && dest.toFile().exists()){
			dest.toFile().delete();
		} else if (!dest.getParent().toFile().exists()){
			dest.getParent().toFile().mkdirs();
		}
		
		try (
				InputStream is = new BufferedInputStream(input.openStream());
				OutputStream os = new FileOutputStream(dest.toFile());
			){
				float contentLength = task.getTargetProgress();
				byte[] buf = new byte[1024];
				int bytesRead, currentBunch = 0;
				
				while ((bytesRead = is.read(buf)) > 0) {
					os.write(buf, 0, bytesRead);
					currentBunch += bytesRead;
					if (currentBunch / contentLength >= REPORT_PER_PERCENT){
						task.progress(currentBunch);
						currentBunch = 0;
					}
				}
			}
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
		if (dest.getParent() != null && !dest.getParent().toFile().exists()){
			dest.getParent().toFile().mkdirs();
		}
		
		return dest;
	}

	@Override
	public String getTitle() {
		return String.format("Transferring to %s", dest);
	}
}
