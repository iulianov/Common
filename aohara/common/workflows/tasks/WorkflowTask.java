package aohara.common.workflows.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;

import aohara.common.workflows.Workflow;

/**
 * Abstract Base Class used to perform work for {@link aohara.common.workflows.Workflow}s.
 * 
 * The call() method is to return a boolean.  This boolean is used to decide
 * whether the {@link aohara.common.workflows.Workflow} will continue executing.
 * 
 * @author Andrew O'Hara
 */
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
