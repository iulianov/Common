package aohara.common.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aohara.common.Listenable;
import aohara.common.executors.context.ExecutorContext;
import aohara.common.executors.progress.ProgressListener;

public class ProgressExecutor<C extends ExecutorContext> extends Listenable<ProgressListener> {
	
	private final ThreadPoolExecutor executor;
	private int running = 0;
	
	protected ProgressExecutor(int numThreads){
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
	}
	
	protected int getProcessing(){
		return executor.getQueue().size() + running;
	}
	
	protected ExecutorContext submit(ExecutorTask task){
		task.context.setFuture(executor.submit(task));
		return task.context;
	}
	
	protected void notifyError(C context){
		for (ProgressListener l : getListeners()) {
			l.progressError(context, getProcessing());
		}
	}
	
	// -- Task ------------------------------------------------------------
	
	protected abstract class ExecutorTask implements Callable<C> {
		
		private final C context;
		
		protected ExecutorTask(C context){
			this.context = context;
		}

		@Override
		public final C call() {
			try {
				setUp(context);
				
				// Start task
				running++;
				notifyStart(getTotalProgress(context));

				execute(context);
				
				// Notify of Success
				running--;
				context.setSuccesful();
				notifySuccess();
			} catch (Exception e){
				running--;
				notifyError(context);
				e.printStackTrace();
			}
			return context;
		}
		
		// -- Notifiers ------------------------------------------------
		
		protected void notifyStart(int totalProgress){
			for (ProgressListener l : getListeners()) {
				l.progressStarted(context, totalProgress, getProcessing());
			}
		}
		
		protected void notifySuccess(){
			for (ProgressListener l : getListeners()) {
				l.progressComplete(context, getProcessing());
			}
		}
		
		protected void progress(int progress){
			context.addProgress(progress);
			for (ProgressListener l : getListeners()) {
				l.progressMade(context, context.getProgress());
			}
		}
		
		// - Abstract Methods --------------------------------------------
		
		protected void setUp(C context) throws Exception {
			// No action
		}
		
		protected abstract void execute(C context) throws Exception;
		protected abstract int getTotalProgress(C context);
		
	}

}
