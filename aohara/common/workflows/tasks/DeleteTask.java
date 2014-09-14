package aohara.common.workflows.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import aohara.common.workflows.Workflow;

public class DeleteTask extends WorkflowTask {

	private final Path path;

	public DeleteTask(Workflow workflow, Path path) {
		super(workflow);
		this.path = path;
	}

	@Override
	public Boolean call() throws FileNotFoundException {
		for (File file : getFiles(path.toFile())){
			int size = (int) file.length();
			if (!file.delete()) {
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
		for (File file : getFiles(path.toFile())){
			progress += file.length();
		}
		return progress;
	}

	@Override
	public String getTitle() {
		return String.format("Deleting %s", path);
	}
}