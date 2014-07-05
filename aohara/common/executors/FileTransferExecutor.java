package aohara.common.executors;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import aohara.common.executors.context.FileTransferContext;

public abstract class FileTransferExecutor extends ProgressExecutor<URL, Path> {
	
	protected static final int OVERWRITE = 0, INCREMENT = 1, CANCEL = 2;
	
	private final int conflictStrategy;
	
	protected FileTransferExecutor(int numThreads, int conflictStrategy){
		super(numThreads);
		this.conflictStrategy = conflictStrategy;
	}
	
	protected FileTransferContext submit(URL input, Path dest){
		FileTask task = new FileTask(new FileTransferContext(input, dest));
		return (FileTransferContext) submit(task);
	}
	
	protected class FileTask extends ExecutorTask {
		
		public FileTask(FileTransferContext context){
			super(context);
		}
		
		private final Path incrementPath(){
			String baseName = FilenameUtils.getBaseName(getResult().toString());
	        String extension = FilenameUtils.getExtension(getResult().toString());
			
			Path path = null;
			for (int i=1; path == null || path.toFile().exists(); i++){
				path = getResult().getParent().resolve(String.format("%s (%d).%s", baseName, i, extension));
			}
			return path;
		}
		
		@Override
		protected void setUp() throws Exception {
			// Check if destination is a folder
			if (getResult().toFile().isDirectory()){
				String baseName = FilenameUtils.getBaseName(getSubject().getPath());
		        String extension = FilenameUtils.getExtension(getSubject().getPath());
		        setResult(getResult().resolve(String.format("%s.%s", baseName, extension)));
			}
			
			// If dest parent does not exist, create it
			if (!getResult().getParent().toFile().exists()){
				getResult().getParent().toFile().mkdirs();
			}
			
			// Check for file conflict
			if (getResult().toFile().isFile() && getResult().toFile().exists()){
				switch(conflictStrategy){
				case OVERWRITE: getResult().toFile().delete(); break;
				case INCREMENT: setResult(incrementPath()); break;
				case CANCEL: throw new Exception();
				}
			}
		}
		
		@Override
		protected void execute() throws Exception {
			transfer(getSubject(), getResult());		
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
