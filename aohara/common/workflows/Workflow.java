package aohara.common.workflows;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;

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
public final class Workflow implements Runnable {

	public static enum Status {Ready, Running, Success, Failure, Exception};
	
	private final Queue<WorkflowTask> tasks = new LinkedList<>();
	private final String name;
	private final Collection<TaskCallback> callbacks;
	private final int totalTasks;
	
	private Status status = Status.Ready;
	
	public Workflow(String name, Queue<WorkflowTask> tasks, Collection<TaskCallback> callbacks){
		this.name = name;
		this.tasks.addAll(tasks);
		this.callbacks = callbacks;
		totalTasks = tasks.size();
	}
	
	@Override
	public void run(){
		status = Status.Running;
		
		// Run Workflow
		while(!tasks.isEmpty()){
			if (!tasks.poll().call(this)){
				status = Status.Failure;
				return;
			}
		}
		
		status = Status.Success;
	}
	
	public void addCallback(TaskCallback callback){
		callbacks.add(callback);
	}
	
	public void notify(TaskEvent event){
		for (TaskCallback callback : callbacks){
			callback.handleTaskEvent(event);
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
}