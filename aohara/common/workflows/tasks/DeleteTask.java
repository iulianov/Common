package aohara.common.workflows.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.gen.PathGen;

/**
 * WorkflowTask to delete the given file.
 * 
 * @author Andrew O'Hara
 */
public class DeleteTask extends WorkflowTask {

	private final PathGen pathGen;

	public DeleteTask(PathGen pathGen) {
		this.pathGen = pathGen;
	}

	@Override
	public Boolean call() throws FileNotFoundException {
		Path path = pathGen.getPath();
		for (File file : getFiles(path.toFile())){
			int size = (int) file.length();
			if (file.exists() && !file.delete()) {
				throw new FileNotFoundException("Failed to delete file: " + path.toFile());
			}
			progress(size);
		}
		return true;
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
	public int getTargetProgress() throws IOException {
		int progress = 0;
		for (File file : getFiles(pathGen.getPath().toFile())){
			progress += file.length();
		}
		return progress;
	}

	@Override
	public String getTitle() {
		return String.format("Deleting %s", pathGen.getPath().toFile());
	}
}