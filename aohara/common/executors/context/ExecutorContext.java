package aohara.common.executors.context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class ExecutorContext {
	
	private int progress = 0;
	private Future<?> future;
	private boolean success = false;
	
	public void addProgress(int moreProgress){
		progress += moreProgress;
	}
	
	public int getProgress(){
		return progress;
	}
	
	public boolean hasFuture(){
		return future != null;
	}
	
	public Future<?> setFuture(Future<?> future){
		this.future = future;
		return future;
	}
	
	public boolean isFinished(){
		return future.isDone();
	}
	
	public void join() throws InterruptedException, ExecutionException{
		future.get();
	}
	
	public void setSuccesful(){
		success = true;
	}
	
	public boolean isSuccessful(){
		return success;
	}
	
	@Override
	public abstract String toString();
	public abstract int getTotalProgress();

}
