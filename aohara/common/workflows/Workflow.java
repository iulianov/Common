package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import aohara.common.Listenable;
import aohara.common.workflows.tasks.DeleteTask;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.WorkflowTask;

/**
 * Queues Tasks for Asynchronous Execution at a later date.
 * 
 * Prepare by adding {aohara.common.workflows.tasks.WorkflowTask}s to it.
 * Once all tasks are added, the workflow can be queued for execution using a
 * built-in executor.
 * 
 * The workflow itself does no work.  The WorkflowTasks added to it are responsible
 * for performaing all the work, and they are the ones that report their progress.
 * 
 * @author Andrew O'Hara
 *
 */
public class Workflow extends Listenable<TaskListener> implements Runnable {

	public static enum Status {Ready, Running, Finished, Error};
	
	private final Queue<WorkflowTask> tasks = new LinkedList<>();
	private final String name;
	
	private Status status = Status.Ready;
	private int totalTasks = 0;
	
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
		WorkflowTask task = null;
		boolean keepGoing = true;
		try {
			while(!tasks.isEmpty() && keepGoing){
				task = tasks.poll();
				for (TaskListener l : getListeners()){
					l.taskStarted(task, task.getTargetProgress());
				}
				keepGoing = task.call();
				for (TaskListener l : getListeners()){
					l.taskComplete(task, !tasks.isEmpty() && keepGoing);
				}
			}
			status = Status.Finished;
		}
		catch (Exception e) { // Stop if exception occurs
			for (TaskListener l : getListeners()){
				l.taskError(task, false, e);
			}
			status = Status.Error;
		}
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
		temp.toFile().deleteOnExit();
		queueDownload(url, temp);
		queueCopy(temp, dest);
	}
}