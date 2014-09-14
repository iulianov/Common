package aohara.common.workflows.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;

import aohara.common.workflows.Workflow;

public abstract class WorkflowTask implements Callable<Boolean> {
	
	public final Workflow workflow;
	
	public WorkflowTask(Workflow workflow){
		this.workflow = workflow;
	}
	
	protected void progress(int increment){
		workflow.notifyProgress(this, increment);
	}
	
	public abstract int getTargetProgress() throws IOException;
	public abstract String getTitle();
	
	@Override
	public String toString(){
		return getTitle();
	}
}
