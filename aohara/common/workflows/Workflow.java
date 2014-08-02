package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import aohara.common.Listenable;

public class Workflow extends Listenable<TaskListener> implements Runnable {

	private final Queue<WorkflowTask> tasks = new LinkedList<>();
	public static enum Status {Ready, Running, Finished, Error};
	private Status status = Status.Ready;
	private int totalTasks = 0;
	private final String name;
	
	public Workflow(String name){
		this.name = name;
	}
	
	public void addTask(WorkflowTask task){
		if (status != Status.Ready){
			throw new IllegalArgumentException("Can only add tasks before workflow has started.");
		}
		tasks.add(task);
		totalTasks++;
	}
	
	@Override
	public void run(){
		status = Status.Running;
		
		// Run Workflow
		boolean result = true;
		while(!tasks.isEmpty() && result){
			WorkflowTask task = tasks.poll();
			try {
				for (TaskListener l : getListeners()){
					l.taskStarted(task, task.getTargetProgress());
				}
				result = task.call();
			} catch (Exception e) {
				for (TaskListener l : getListeners()){
					l.taskError(task, !tasks.isEmpty(), e);
				}
				result = false;
			}
			
			if (result){
				for (TaskListener l : getListeners()){
					l.taskComplete(task, !tasks.isEmpty());
				}
			}
		}
		
		status = (result && tasks.isEmpty() ? Status.Finished : Status.Error);
	}
	
	public void notifyProgress(WorkflowTask task, int increment){
		for (TaskListener l : getListeners()){
			l.taskProgress(task, increment);
		}
	}
	
	public Status getStatus(){
		return status;
	}
	
	public int getProgress(){
		return totalTasks - tasks.size();
	}
	
	public int getTotalTasks(){
		return totalTasks;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	// Task Methods
	
	public static Workflow copy(Path src, Path dest) throws MalformedURLException{
		Workflow workflow = new Workflow(String.format("Copying %s to %s", src.getFileName(), dest));
		workflow.addTask(new FileTransferTask(workflow, src.toUri().toURL(), dest));
		return workflow;
	}
	
	public static Workflow download(URL url, Path dest){
		Workflow workflow = new Workflow(String.format("Downloading %s", dest.getFileName()));
		workflow.addTask(new FileTransferTask(workflow, url, dest));
		return workflow;
	}
	
	public static Workflow download(String url, Path dest) throws MalformedURLException{
		return download(new URL(url), dest);
	}
	
	public static Workflow delete(Path path){
		Workflow workflow = new Workflow(String.format("Deleting %s", path));
		workflow.addTask(new DeleteTask(workflow, path));
		return workflow;
	}
	
	public static Workflow move(Path src, Path dest) throws MalformedURLException{
		Workflow workflow = new Workflow(String.format("Moving %s to %", src.getFileName(), dest));
		workflow.addTask(new FileTransferTask(workflow, src.toUri().toURL(), dest));
		workflow.addTask(new DeleteTask(workflow, src));
		return workflow;
	}
	
	public static Workflow tempDownload(URL url, Path dest) throws IOException{
		Workflow workflow = new Workflow(String.format("Downloading %s", dest.getFileName()));
		Path temp = Files.createTempFile("download", ".temp");
		workflow.addTask(new FileTransferTask(workflow, url, temp));
		workflow.addTask(new FileTransferTask(workflow, temp.toUri().toURL(), dest));
		workflow.addTask(new DeleteTask(workflow, temp));
		return workflow;
	}
	
	public static Workflow tempDownload(String url, Path dest) throws IOException {
		return tempDownload(new URL(url), dest);
	}
}