package aohara.common.workflows;

import aohara.common.workflows.Workflow.WorkflowTask;


/**
 * Listens to events from {aohara.common.workflows.tasks.WorkflowTask}s.
 * 
 * Add the implementing listener to the {aohara.common.workflows.Workflow} to receive
 * events from all the tasks it holds.
 * 
 * @author Andrew O'Hara
 */
public interface TaskListener {
	
	public void taskStarted(WorkflowTask task, int targetProgress);
	public void taskProgress(WorkflowTask task, int increment);
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e);
	public void taskComplete(WorkflowTask task, boolean tasksRemaining);

}
