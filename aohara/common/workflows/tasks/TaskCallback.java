package aohara.common.workflows.tasks;

import java.util.Arrays;
import java.util.Collection;

import aohara.common.workflows.Workflow.Status;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;


public abstract class TaskCallback {
	
	private final Collection<Status> acceptedStatuses;
	
	public TaskCallback(){
		this(Status.values());
	}
	
	public TaskCallback(Status... acceptedStatuses){
		this.acceptedStatuses = Arrays.asList(acceptedStatuses);
	}
	
	public void handleTaskEvent(TaskEvent event){
		if (acceptedStatuses.contains(event.getTask().getStatus())){
			processTaskEvent(event);
		}		
	}
	
	protected abstract void processTaskEvent(TaskEvent event);

}
