package aohara.common.workflows.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;

/**
 * WorkflowTask to transfer the file from the given URL to the given path location.
 * 
 * @author Andrew O'Hara
 */
public class FileTransferTask extends WorkflowTask {
	
	public static final float REPORT_PER_PERCENT = (float) 0.01;
	private final URI src;
	private final Path dest;
	
	public FileTransferTask(URI src, Path dest){
		this.src = src;
		this.dest = dest;
	}

	@Override
	public boolean call(Workflow workflow) throws IOException, URISyntaxException {
		// Do not download if no input was specified
		if (src  == null){
			return true;
		}
		
		File destFile = dest.toFile();
		
		// Check if destination is a folder
		if (destFile.isDirectory()){
			String baseName = FilenameUtils.getBaseName(src.getPath());
	        String extension = FilenameUtils.getExtension(src.getPath());
	        destFile = new File(destFile, String.format("%s.%s", baseName, extension));
		}
		
		// If dest parent does not exist, create it
		if (!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		} 
		// Check for file conflict
		else if (destFile.isFile() && destFile.exists()){
			destFile.delete();
		}
		
		// Perform Transfer
		try (
			InputStream is = new BufferedInputStream(src.toURL().openStream());
			OutputStream os = new FileOutputStream(destFile);
		){
			float contentLength = getTargetProgress();
			byte[] buf = new byte[1024];
			int bytesRead, currentBunch = 0;
			
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
				currentBunch += bytesRead;
				if (currentBunch / contentLength >= REPORT_PER_PERCENT){
					progress(workflow, currentBunch);
					currentBunch = 0;
				}
			}
		}
		return true;
	}

	@Override
	public String getTitle() {		
		return String.format("Transferring to %s", dest);
	}

	@Override
	public int getTargetProgress() throws IOException {
		return src != null ? src.toURL().openConnection().getContentLength() : -1;
	}
}
