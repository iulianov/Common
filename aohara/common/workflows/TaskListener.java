package aohara.common.workflows;

public interface TaskListener {
	
	public void taskStarted(WorkflowTask task, int targetProgress);
	public void taskProgress(WorkflowTask task, int increment);
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e);
	public void taskComplete(WorkflowTask task, boolean tasksRemaining);

}
