package aohara.common.executors;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import aohara.common.Listenable;
import aohara.common.progressDialog.ProgressListener;

public abstract class FileExecutor extends Listenable<ProgressListener<Path>>{
	
	protected static final int OVERWRITE = 0, INCREMENT = 1, CANCEL = 2;
	
	private final ThreadPoolExecutor executor;
	private final int conflictStrategy;
	private final boolean useTempFile;
	private int running = 0;
	
	protected FileExecutor(int numThreads, int conflictStrategy, boolean useTempFile){
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
		this.conflictStrategy = conflictStrategy;
		this.useTempFile = useTempFile;
	}
	
	protected Future<Path> submit(URL input, Path dest){
		try {
			int totalBytes = input.openConnection().getContentLength();
			return executor.submit(new FileTask(input, dest, totalBytes));
		} catch (IOException e) {
			notifyError(dest);
			return null;
		}
	}
	
	private int getProcessing(){
		return executor.getQueue().size() + running;
	}
	
	protected void notifyError(Path path){
		for (ProgressListener<Path> l : getListeners()) {
			l.progressError(path, getProcessing());
		}
	}
	
	private class FileTask implements Callable<Path> {
		
		private final int totalBytes;
		private Path dest;
		private final URL input;
		private int currentBytes = 0;
		
		private FileTask(URL input, Path dest, int totalBytes){
			this.input = input;
			this.dest = dest;
			this.totalBytes = totalBytes; 
		}
		
		private final Path incrementPath(){
			//String fileName = original.getFileName().toString();
			String baseName = FilenameUtils.getBaseName(dest.toString());
	        String extension = FilenameUtils.getExtension(dest.toString());
			
			Path path = null;
			for (int i=1; path == null || path.toFile().exists(); i++){
				path = dest.getParent().resolve(String.format("%s (%d).%s", baseName, i, extension));
			}
			return path;
		}
		
		protected void progress(int progress){
			currentBytes += progress;
			for (ProgressListener<Path> l : getListeners()) {
				l.progressMade(dest, currentBytes);
			}
		}
		
		@Override
		public final Path call(){	
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
			
			// Check for file conflict
			if (dest.toFile().isFile() && dest.toFile().exists()){
				switch(conflictStrategy){
				case OVERWRITE: dest.toFile().delete(); break;
				case INCREMENT: dest = incrementPath(); break;
				case CANCEL: return null;
				}
			}
			
			// Start Task
			running++;
			for (ProgressListener<Path> l : getListeners()) {
				l.progressStarted(dest, totalBytes, getProcessing());
			}
			
			try{
				if (useTempFile) {
					// Download to temporary file, and then move over to dest
					Path tempPath = Files.createTempFile("download", ".temp");
					transfer(input, tempPath);
					FileUtils.moveFile(tempPath.toFile(), dest.toFile());
				} else {
					// Download directly to destination
					transfer(input, dest);
				}
				
				// Notify of Success
				running--;
				for (ProgressListener<Path> l : getListeners()) {
					l.progressComplete(dest, getProcessing());
				}
				
				return dest;
			} catch (IOException e){
				running--;
				notifyError(dest);
				return null;
			}
		}
		
		private final void transfer(URL input, Path dest) throws IOException {
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
		}
	}
}
