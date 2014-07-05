package aohara.common.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aohara.common.Listenable;
import aohara.common.executors.context.ExecutorContext;
import aohara.common.progressDialog.ProgressListener;

public class ProgressExecutor<A, B> extends Listenable<ProgressListener<B>>{
	
	private final ThreadPoolExecutor executor;
	private int running = 0;
	
	protected ProgressExecutor(int numThreads){
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
	}
	
	protected int getProcessing(){
		return executor.getQueue().size() + running;
	}
	
	protected void notifyError(B result){
		for (ProgressListener<B> l : getListeners()) {
			l.progressError(result, getProcessing());
		}
	}
	
	protected ExecutorContext<A, B> submit(ExecutorTask task){
		task.context.setFuture(executor.submit(task));
		return task.context;
	}
	
	protected abstract class ExecutorTask implements Callable<B> {
		
		private final ExecutorContext<A, B> context;
		
		protected ExecutorTask(ExecutorContext<A, B> context){
			this.context = context;
		}

		@Override
		public final B call() {
			// Try to perform setup
			try {
				setUp();
			} catch (Exception e1) {
				notifyError(context.getResult());
			}
			
			// Start Task
			running++;
			for (ProgressListener<B> l : getListeners()) {
				l.progressStarted(context.getResult(), context.getTotalProgress(),getProcessing());
			}
			
			try {
				execute();
				// Notify of Success
				running--;
				for (ProgressListener<B> l : getListeners()) {
					l.progressComplete(context.getResult(), getProcessing());
				}
				return getResult();
			} catch (Exception e){
				running--;
				notifyError(context.getResult());
			}
			
			return null;
		}
		
		protected void progress(int progress){
			context.addProgress(progress);
			for (ProgressListener<B> l : getListeners()) {
				l.progressMade(context.getResult(), context.getProgress());
			}
		}
		
		protected A getSubject(){
			return context.getSubject();
		}
		
		protected B getResult(){
			return context.getResult();
		}
		
		protected void setResult(B result){
			context.setResult(result);
		}
		
		protected void setUp() throws Exception {
			// No action
		}
		
		protected abstract void execute() throws Exception;
		
	}

}
