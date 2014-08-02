package aohara.common.workflows;

import java.util.concurrent.Callable;

public abstract class WorkflowTask implements Callable<Boolean> {
	
	public final Workflow workflow;
	
	protected WorkflowTask(Workflow workflow){
		this.workflow = workflow;
	}
	
	protected void progress(int increment){
		workflow.notifyProgress(this, increment);
	}
	
	protected abstract int getTargetProgress() throws InvalidContentException;
	
	@SuppressWarnings("serial")
	public class InvalidContentException extends Exception {}

}
