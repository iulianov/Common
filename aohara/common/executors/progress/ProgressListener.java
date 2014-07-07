package aohara.common.executors.progress;

import aohara.common.executors.context.ExecutorContext;

public interface ProgressListener {
	public void progressStarted(ExecutorContext context, int target, int tasksRunning);
	public void progressMade(ExecutorContext context, int current);
	public void progressComplete(ExecutorContext context, int tasksRunning);
	public void progressError(ExecutorContext context, int tasksRunning);
}
