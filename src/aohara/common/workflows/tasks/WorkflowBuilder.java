package aohara.common.workflows.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;

import aohara.common.workflows.Workflow;

/**
 * Class with methods for adding standard tasks to a Workflow
 */
public class WorkflowBuilder {
	
	protected final Object context;
	private final Queue<WorkflowTask> tasks = new LinkedList<>();
	private final Collection<TaskCallback> listeners = new LinkedList<>();
	
	public WorkflowBuilder(Object context){
		this.context = context;
	}
	
	public void copy(Path src, Path dest) {
		addTask(new FileTransferTask(src.toUri(), dest));
	}
	
	public void download(URL src, Path dest) {
		try {
			addTask(new FileTransferTask(src.toURI(), dest));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Path path){
		addTask(new DeleteTask(path));
	}
	
	public void move(Path src, Path dest) {
		copy(src, dest);
		delete(src);
	}
	
	public void tempDownload(URL url, Path dest) throws IOException {
		Path temp = downloadToTemp(url);
		copy(temp, dest);
	}
	
	public Path downloadToTemp(URL url) throws IOException {
		Path temp = Files.createTempFile("download", ".tempDownload");
		temp.toFile().deleteOnExit();
		download(url, temp);
		return temp;
	}
	
	public void browserGoTo(URL dest){
		addTask(new BrowserGoToTask(dest));
	}
	
	public void addTask(WorkflowTask task){
		tasks.add(task);
	}
	
	public void addListener(TaskCallback listener){
		listeners.add(listener);
	}
	
	public Workflow buildWorkflow(){
		return new Workflow(context, tasks, listeners);
	}
	
	public void execute(Executor executor){
		executor.execute(buildWorkflow());
	}
	
	public void unzip(Path zipPath, Map<Path, ZipEntry> zipEntries, Path destFolder){
		addTask(new UnzipTask(zipEntries, zipPath, destFolder));
	}
}