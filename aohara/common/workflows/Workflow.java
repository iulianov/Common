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
	
	// Tasks
	
	public void queueCopy(Path src, Path dest) throws MalformedURLException{
		addTask(new FileTransferTask(this, src.toUri().toURL(), dest));
	}
	
	public void queueDelete(Path path){
		addTask(new DeleteTask(this, path));
	}
	
	public void queueDownload(URL url, Path dest){
		addTask(new FileTransferTask(this,url, dest));
	}
	
	public void queueMove(Path src, Path dest) throws MalformedURLException{
		queueCopy(src, dest);
		queueDelete(src);;
	}
	
	public void queueTempDownload(URL url, Path dest) throws IOException{
		Path temp = Files.createTempFile("download", ".temp");
		queueDownload(url, temp);
		queueCopy(temp, dest);
		queueDelete(temp);
	}
}