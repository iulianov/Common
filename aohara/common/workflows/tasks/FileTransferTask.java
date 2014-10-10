package aohara.common.workflows.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.common.workflows.tasks.gen.URIGen;

/**
 * WorkflowTask to transfer the file from the given URL to the given path location.
 * 
 * @author Andrew O'Hara
 */
public class FileTransferTask extends WorkflowTask {
	
	public static final float REPORT_PER_PERCENT = (float) 0.01;
	private final URIGen srcGen, destGen;
	
	public FileTransferTask(URIGen src, PathGen dest){
		this.srcGen = src;
		this.destGen = dest;
	}

	@Override
	public Boolean call() throws Exception {
		File destFile = new File(destGen.getURI());
		
		// Check if destination is a folder
		if (destFile.isDirectory()){
			String baseName = FilenameUtils.getBaseName(srcGen.getURI().getPath());
	        String extension = FilenameUtils.getExtension(srcGen.getURI().getPath());
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
			InputStream is = new BufferedInputStream(srcGen.getURI().toURL().openStream());
			OutputStream os = new FileOutputStream(new File(destGen.getURI()));
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
		return true;
	}

	@Override
	public String getTitle() {
		String dest = "null";
		try {
			dest = destGen.getURI().getPath();
		} catch (URISyntaxException e) {
		}
		
		return String.format("Transferring to %s", dest);
	}

	@Override
	public int getTargetProgress() throws IOException {
		try {
			return srcGen.getURI().toURL().openConnection().getContentLength();
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
}
