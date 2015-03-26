package aohara.common.workflows;

import aohara.common.workflows.Workflow.WorkflowTask.TaskCompleteEvent;

public interface TaskCallback {
	
	public void handleTaskEvent(TaskCompleteEvent event);

}
