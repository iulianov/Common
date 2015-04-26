package aohara.common.workflows.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * WorkflowTask to delete the given file.
 * 
 * @author Andrew O'Hara
 */
class DeleteTask extends WorkflowTask {

	private final Path path;

	DeleteTask(Path path) {
		super(String.format("Deleting %s", path));
		this.path = path;
	}
	
	private List<File> getFiles(File file){
		List<File> files = new LinkedList<>();
		
		if (file.isDirectory()) {
			for (File c : file.listFiles()) {
				files.addAll(getFiles(c));
			}
		}
		files.add(file);
		
		return files;
	}

	@Override
	protected int findTargetProgress() throws IOException {
		int progress = 0;
		if (path != null){
			for (File file : getFiles(path.toFile())){
				progress += file.length();
			}
		}
		return progress;
	}

	@Override
	public boolean execute() throws Exception {
		if (path != null){
			for (File file : getFiles(path.toFile())){
				int size = (int) file.length();
				if (file.exists() && !file.delete()) {
					throw new FileNotFoundException("Failed to delete file: " + path.toFile());
				}
				progress(size);
			}
		}
		return true;
	}
}