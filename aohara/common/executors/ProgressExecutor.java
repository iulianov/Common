package aohara.common.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aohara.common.Listenable;
import aohara.common.progressDialog.ProgressListener;

public class ProgressExecutor<T> extends Listenable<ProgressListener<T>>{
	
	protected final ThreadPoolExecutor executor;
	private int running = 0;
	
	protected ProgressExecutor(int numThreads){
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
	}
	
	protected int getProcessing(){
		return executor.getQueue().size() + running;
	}
	
	protected void notifyError(T object){
		for (ProgressListener<T> l : getListeners()) {
			l.progressError(object, getProcessing());
		}
	}
	
	protected abstract class ExecutorTask implements Callable<T> {
		
		protected T subject;
		private final int target;
		private int current = 0;
		
		protected ExecutorTask(T subject, int target){
			this.subject = subject;
			this.target = target;
		}

		@Override
		public final T call() {
			// Try to perform setup
			try {
				setUp();
			} catch (Exception e1) {
				notifyError(subject);
			}
			
			// Start Task
			running++;
			for (ProgressListener<T> l : getListeners()) {
				l.progressStarted(subject, target, getProcessing());
			}
			
			try {
				execute();
				// Notify of Success
				running--;
				for (ProgressListener<T> l : getListeners()) {
					l.progressComplete(subject, getProcessing());
				}
			} catch (Exception e){
				running--;
				notifyError(subject);
			}
			
			return null;
		}
		
		protected void progress(int progress){
			this.current += progress;
			for (ProgressListener<T> l : getListeners()) {
				l.progressMade(subject, current);
			}
		}
		
		protected void setUp() throws Exception {
			// No action
		}
		
		protected abstract void execute() throws Exception;
		
	}

}
