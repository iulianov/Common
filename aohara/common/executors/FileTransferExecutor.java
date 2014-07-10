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

public abstract class FileTransferExecutor extends ProgressExecutor<FileTransferContext> {
	
	public static enum FileConflictResolver { Overwrite, Increment, Cancel };
	
	protected final FileConflictResolver conflictStrategy;
	
	protected FileTransferExecutor(int numThreads, FileConflictResolver conflictStrategy){
		super(numThreads);
		this.conflictStrategy = conflictStrategy;
	}
	
	public FileTransferContext submit(FileTransferContext context){
		return (FileTransferContext) submit(new FileTask(context));
	}
	
	protected FileTransferContext transfer(URL input, Path dest){
		FileTask task = new FileTask(new FileTransferContext(input, dest));
		return (FileTransferContext) submit(task);
	}
	
	public class FileTask extends ExecutorTask {
		
		public FileTask(FileTransferContext context){
			super(context);
		}
		
		private final Path incrementPath(FileTransferContext context){
			String baseName = FilenameUtils.getBaseName(context.getDest().toString());
	        String extension = FilenameUtils.getExtension(context.getDest().toString());
			
			Path path = null;
			for (int i=1; path == null || path.toFile().exists(); i++){
				path = context.getDest().getParent().resolve(String.format("%s (%d).%s", baseName, i, extension));
			}
			return path;
		}
		
		@Override
		protected void setUp(FileTransferContext context) throws Exception {
			// Check if destination is a folder
			if (context.getDest().toFile().isDirectory()){
				String baseName = FilenameUtils.getBaseName(context.getSource().getPath());
		        String extension = FilenameUtils.getExtension(context.getSource().getPath());
		        context.setDest(context.getDest().resolve(String.format("%s.%s", baseName, extension)));
			}
			
			// If dest parent does not exist, create it
			if (!context.getDest().getParent().toFile().exists()){
				context.getDest().getParent().toFile().mkdirs();
			}
			
			// Check for file conflict
			if (context.getDest().toFile().isFile() && context.getDest().toFile().exists()){
				switch(conflictStrategy){
				case Overwrite: context.getDest().toFile().delete(); break;
				case Increment: context.setDest(incrementPath(context)); break;
				case Cancel: throw new Exception();
				}
			}
		}
		
		@Override
		protected void execute(FileTransferContext context) throws Exception {
			transfer(context.getSource(), context.getDest());
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

		@Override
		protected int getTotalProgress(FileTransferContext context) {
			try {
				return context.getSource().openConnection().getContentLength();
			} catch (IOException e) {
				return -1;
			}
		}
	}
}
