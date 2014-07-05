package aohara.common.executors;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Future;

import org.apache.commons.io.FilenameUtils;

public abstract class FileTransferExecutor extends ProgressExecutor<Path> {
	
	protected static final int OVERWRITE = 0, INCREMENT = 1, CANCEL = 2;
	
	private final int conflictStrategy;
	
	protected FileTransferExecutor(int numThreads, int conflictStrategy){
		super(numThreads);
		this.conflictStrategy = conflictStrategy;
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
	
	protected class FileTask extends ExecutorTask {
		
		protected final URL input;
		
		protected FileTask(URL input, Path dest, int totalBytes){
			super(dest, totalBytes);
			this.input = input;
		}
		
		private final Path incrementPath(){
			String baseName = FilenameUtils.getBaseName(subject.toString());
	        String extension = FilenameUtils.getExtension(subject.toString());
			
			Path path = null;
			for (int i=1; path == null || path.toFile().exists(); i++){
				path = subject.getParent().resolve(String.format("%s (%d).%s", baseName, i, extension));
			}
			return path;
		}
		
		@Override
		protected void setUp() throws Exception {
			// Check if destination is a folder
			if (subject.toFile().isDirectory()){
				String baseName = FilenameUtils.getBaseName(input.getPath());
		        String extension = FilenameUtils.getExtension(input.getPath());
		        subject = subject.resolve(String.format("%s.%s", baseName, extension));
			}
			
			// If dest parent does not exist, create it
			if (!subject.getParent().toFile().exists()){
				subject.getParent().toFile().mkdirs();
			}
			
			// Check for file conflict
			if (subject.toFile().isFile() && subject.toFile().exists()){
				switch(conflictStrategy){
				case OVERWRITE: subject.toFile().delete(); break;
				case INCREMENT: subject = incrementPath(); break;
				case CANCEL: throw new Exception();
				}
			}
		}
		
		@Override
		protected void execute() throws Exception {
			transfer(input, subject);		
		}
		
		protected final void transfer(URL input, Path dest) throws IOException {
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
