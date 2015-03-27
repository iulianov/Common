package aohara.common.workflows.tasks;

import java.io.IOException;

import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.Status;

/**
 * Abstract Base Class used to perform work for {@link aohara.common.workflows.Workflow}s.
 * 
 * The call() method is to return a boolean.  This boolean is used to decide
 * whether the {@link aohara.common.workflows.Workflow} will continue executing.
 * 
 * @author Andrew O'Hara
 */
public abstract class WorkflowTask {
	
	public final String title;
	
	private static final float UPDATE_EVERY_PERCENT = 0.01f;
	private float lastUpdatedAtPercent = 0f;
	private int progress;
	private Integer targetProgress;
	private Status status = Status.Ready;
	private Workflow workflow;
	
	public WorkflowTask(String title){
		this.title = title != null ? title : "Unnamed Task";
	}
	
	protected void progress(int increment){
		progress += increment;
		
		// If update percentage threshold has been achieved, notify callbacks of progress
		float percentProgress = progress / (float) targetProgress;
		if ((percentProgress - lastUpdatedAtPercent) > UPDATE_EVERY_PERCENT){
			lastUpdatedAtPercent = percentProgress;
			workflow.notify(new TaskEvent("Progress"));
		}
	}
	
	public int getProgress(){
		return progress;
	}
	
	public final int getTargetProgress(){
		if (targetProgress == null){
			try {
				targetProgress = findTargetProgress();
			} catch (IOException e) {
				targetProgress = -1;
			}
		}
		return targetProgress;
	}
	
	public boolean call(Workflow workflow) {
		this.workflow = workflow;
		status = Status.Running;
		workflow.notify(new TaskEvent("Started"));
		
		try {
			boolean success = execute();
			status = success ? Status.Success : Status.Failure;
			workflow.notify(new TaskEvent(status.toString()));
			return success;
		} catch (Exception e){
			status = Status.Exception;
			workflow.notify(new TaskExceptionEvent(e));
			return false;
		}
	}
	
	@Override
	public String toString(){
		return title;
	}
	
	public Status getStatus(){
		return status;
	}
	
	public Workflow getWorkflow(){
		return workflow;
	}
	
	public abstract boolean execute() throws Exception;
	protected abstract int findTargetProgress() throws IOException;
	
	
	/**
	 * 
	 * @author Andrew O'Hara
	 *
	 */
	public class TaskEvent {
		
		public final String description;

		private TaskEvent(String description){
			this.description = description != null ? description : "";
		}
		
		public WorkflowTask getTask(){
			return WorkflowTask.this;
		}
		
		@Override
		public String toString(){
			return String.format("Task: %s - %s", getTask().title, description);
		}
		
		public boolean isWorkflowComplete(){
			return workflow.getTotalTasks() == workflow.getProgress();
		}
	}
	
	public class TaskExceptionEvent extends TaskEvent {
		
		public final Exception exception;
		
		private TaskExceptionEvent(Exception exception){
			super(exception.toString());
			this.exception = exception;
		}
	}
}