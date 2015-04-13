package aohara.common.workflows.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

/**
 * WorkflowTask to transfer the file from the given URL to the given path location.
 * 
 * @author Andrew O'Hara
 */
public class FileTransferTask extends WorkflowTask {
	
	public static final float REPORT_PER_PERCENT = (float) 0.01;
	private final URI src;
	private final Path dest;
	
	protected FileTransferTask(URI src, Path dest){
		super("Downloading File");
		this.src = src;
		this.dest = dest;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return src != null ? src.toURL().openConnection().getContentLength() : -1;
	}

	@Override
	public boolean execute() throws Exception {
		transfer(src, dest);
		return true;
	}
	
	protected void transfer(URL url, Path dest) throws NullSourceException, MalformedURLException, IOException, URISyntaxException {
		if (url == null){
			throw new NullSourceException();
		}
		transfer(url.toURI(), dest);
	}
	
	protected void transfer(URI src, Path dest) throws MalformedURLException, IOException, NullSourceException {
		// Do not download if no input was specified
		if (src  == null){
			throw new NullSourceException();
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
			if (!destFile.delete()){
				throw new IOException("File conflict.  Could not delete existing file.");
			}
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
					progress(currentBunch);
					currentBunch = 0;
				}
			}
		}
	}
	
	protected static class NullSourceException extends Exception {} 
}
