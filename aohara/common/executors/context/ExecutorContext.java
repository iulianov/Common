package aohara.common.executors.context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class ExecutorContext<A, B> {
	
	private final A subject;
	private B result;
	private int progress = 0;
	private Future<B> future;
	
	public ExecutorContext(A subject){
		this(subject, null);
	}
	
	public ExecutorContext(A subject, B result){
		this.subject = subject;
		this.result = result;
	}
	
	public boolean hasResult(){
		return result != null;
	}
	
	public B getResult(){
		return result;
	}
	
	public B setResult(B result){
		this.result = result;
		return result;
	}
	
	public A getSubject(){
		return subject;
	}
	
	public void addProgress(int moreProgress){
		progress += moreProgress;
	}
	
	public int getProgress(){
		return progress;
	}
	
	public abstract int getTotalProgress();
	
	@Deprecated
	public Future<B> getFuture(){
		return future;
	}
	
	public boolean hasFuture(){
		return future != null;
	}
	
	public Future<B> setFuture(Future<B> future){
		this.future = future;
		return future;
	}
	
	public boolean isFinished(){
		return getFuture().isDone();
	}
	
	public void join() throws InterruptedException, ExecutionException{
		getFuture().get();
	}
	
	@Override
	public abstract String toString();

}
