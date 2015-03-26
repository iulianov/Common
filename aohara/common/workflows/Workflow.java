package aohara.common.workflows;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import aohara.common.Listenable;
import aohara.common.workflows.Workflow.WorkflowTask.TaskCompleteEvent;

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
					l.taskStarted(this, task, task.getTargetProgress());
				}
				TaskCompleteEvent event = task.call();
				if (!event.isTaskSuccessful()){
					keepGoing = false;
				}
				
				for (TaskListener l : getListeners()){
					l.taskComplete(this, task, !tasks.isEmpty() && keepGoing);
				}
			}
			status = Status.Finished;
		}
		catch (Exception e) { // Stop if exception occurs
			for (TaskListener l : getListeners()){
				l.taskError(this, task, false, e);
			}
			status = Status.Error;
		}
	}
	
	public void notifyProgress(WorkflowTask task, int increment){
		for (TaskListener l : getListeners()){
			l.taskProgress(this, task, increment);
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
	
	/**
	 * Abstract Base Class used to perform work for {@link aohara.common.workflows.Workflow}s.
	 * 
	 * The call() method is to return a boolean.  This boolean is used to decide
	 * whether the {@link aohara.common.workflows.Workflow} will continue executing.
	 * 
	 * @author Andrew O'Hara
	 */
	public abstract class WorkflowTask {
		
		private final TaskCallback[] callbacks;
		
		public WorkflowTask(TaskCallback... callbacks){
			this.callbacks = callbacks;
		}
		
		protected void progress(int increment){
			getWorkflow().notifyProgress(this, increment);
		}
		
		public abstract int getTargetProgress() throws IOException;
		public abstract String getTitle();
		public abstract TaskCompleteEvent call() throws Exception;
		
		@Override
		public String toString(){
			return getTitle();
		}
		
		public Workflow getWorkflow(){
			return Workflow.this;
		}
		
		public abstract class TaskEvent {
			
			private final String description;
			private final Boolean isRunning, isSuccessful;
			
			private TaskEvent(String description, boolean isRunning, Boolean isSuccessful){
				this.description = description;
				this.isRunning = isRunning;
				this.isSuccessful = isSuccessful;
			}
			
			public WorkflowTask getTask(){
				return WorkflowTask.this;
			}
			
			public String getDescription(){
				return String.format("Task: %s - %s", getTask().getTitle(), description);
			}
			
			public boolean isTaskRunning(){
				return isRunning;
			}
			
			public Boolean isTaskSuccessful(){
				return isSuccessful;
			}
			
		}
		
		public class TaskStartedEvent extends TaskEvent {
			
			protected TaskStartedEvent(){
				super("Started", true, null);
			}
		}
		
		public abstract class TaskCompleteEvent extends TaskEvent {
			
			private TaskCompleteEvent(String description, boolean isSuccessful){
				super(description, false, isSuccessful);
			}
			
		}
		
		public class TaskSuccessEvent extends TaskCompleteEvent {
			
			protected TaskSuccessEvent(){
				super("Success", true);
			}
		}
		
		public class TaskFailureEvent extends TaskCompleteEvent {
			
			protected TaskFailureEvent(String reason){
				super(String.format("Failure: %s", reason), false);
			}
		}
	}
}