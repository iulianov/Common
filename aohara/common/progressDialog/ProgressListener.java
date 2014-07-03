package aohara.common.progressDialog;

public interface ProgressListener<T> {
	public void progressStarted(T object, int target, int tasksRunning);
	public void progressMade(T object, int current);
	public void progressComplete(T object, int tasksRunning);
	public void progressError(T object, int tasksRunning);
}
