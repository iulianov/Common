package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.BrowserGoToTask;
import aohara.common.workflows.tasks.DeleteTask;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.gen.GenFactory;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.common.workflows.tasks.gen.URIGen;
import aohara.common.workflows.tasks.gen.URLGen;

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
	
	public void copy(URIGen src, PathGen path) throws MalformedURLException{
		download(src, path);
	}
	
	public void download(URIGen src, PathGen path){
		addTask(new FileTransferTask(src, path));
	}

	public void delete(PathGen pathGen){
		addTask(new DeleteTask(pathGen));
	}
	
	public void move(PathGen src, PathGen dest) throws MalformedURLException{
		copy(src, dest);
		delete(src);
	}
	
	public void tempDownload(URLGen url, PathGen destGen) throws IOException{
		Path temp = downloadToTemp(url);
		copy(GenFactory.fromPath(temp), destGen);
	}
	
	public Path downloadToTemp(URLGen url) throws IOException {
		Path temp = Files.createTempFile("download", ".tempDownload");
		temp.toFile().deleteOnExit();
		download(url, GenFactory.fromPath(temp));
		return temp;
	}
	
	public void browserGoTo(URLGen dest){
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