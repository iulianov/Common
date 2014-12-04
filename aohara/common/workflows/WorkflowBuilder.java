package aohara.common.workflows;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.common.workflows.tasks.DeleteTask;
import aohara.common.workflows.tasks.FileTransferTask;

/**
 * Class with methods for adding standard tasks to a Workflow
 */
public class WorkflowBuilder {
	
	private final String workflowName;
	private final List<WorkflowTask> tasks = new LinkedList<>();
	private final List<TaskListener> listeners = new LinkedList<>();
	
	public WorkflowBuilder(String workflowName){
		this.workflowName = workflowName;
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
	
	public void addListener(TaskListener listener){
		listeners.add(listener);
	}
	
	public Workflow buildWorkflow(){
		Workflow workflow = new Workflow(workflowName);
		for (WorkflowTask task : tasks){
			workflow.addTask(task);
		}
		for (TaskListener l : listeners){
			workflow.addListener(l);
		}
		return workflow;
	}
	
	public void execute(Executor executor){
		executor.execute(buildWorkflow());
	}
}