package aohara.common.workflows;

import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import aohara.common.selectorPanel.DecoratedComponent;
import aohara.common.workflows.tasks.TaskCallback;
import aohara.common.workflows.tasks.WorkflowTask;
import aohara.common.workflows.tasks.WorkflowTask.TaskEvent;
import aohara.common.workflows.tasks.WorkflowTask.TaskExceptionEvent;

/**
 * Panel that is used to display the progress of various {@link aohara.common.workflows.Workflow}s.
 * 
 * Progress is shown with a Progress Bar and percentage progress.
 * The name of the task is shown before the percentage.
 * 
 * @author Andrew O'Hara
 */
public class ProgressPanel extends TaskCallback implements DecoratedComponent<JPanel>{
	
	private final JPanel panel = new JPanel();
	private final HashMap<Workflow, JProgressBar> bars = new HashMap<>();

	public ProgressPanel(){
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setPreferredSize(new Dimension(800, 30));
	}
	
	@Override
	protected void processTaskEvent(TaskEvent event) {
		switch(event.getTask().getStatus()){
		case Ready:
			break;  // Ignored
		case Running:
			taskProgress(event.getTask());
			break;
		case Exception:
			taskError(event.getTask(), ((TaskExceptionEvent)event).exception);
		case Success:
		case Failure:
			taskComplete(event.getTask(), event.isWorkflowComplete());
			break;
		}
	}
	
	public void taskProgress(final WorkflowTask task){
		Workflow workflow = task.getWorkflow();
		
		// Get Progress Bar
		final JProgressBar bar;
		if (bars.containsKey(workflow)){
			bar = bars.get(workflow);
		} else {
			bar = new JProgressBar();
			bar.setIndeterminate(true);
			bar.setStringPainted(true);
			bars.put(workflow, bar);
			
			panel.add(bar);
			panel.setVisible(true);
			panel.validate();
			
			// Launch thread to calculate target task progress, and update bar when ready
			SwingUtilities.invokeLater(new Runnable() {
                public void run() {
        			bar.setMaximum(task.getTargetProgress());  // Max set here, since setting in constructor may yield range exception
        			bar.setIndeterminate(task.getTargetProgress() < 1);
                }
            });
		}
		
		// Set Progress
		bar.setValue(task.getProgress());
		if (bar.isIndeterminate()){
			bar.setString(task.toString());
		} else {
			bar.setString(String.format(
				"(%d/%d) %s - %.2f%%",
				workflow.getProgress(), workflow.getTotalTasks(), workflow,
				(bar.getValue() / (float) bar.getMaximum()) * 100));
		}
	}
	
	public void taskComplete(WorkflowTask task, boolean workflowComplete){
		JProgressBar bar = bars.remove(task.getWorkflow());
		
		// Hide Progress Panel if no more tasks in progress
		// Should be done first for panel to notice change during component removal
		if (workflowComplete && bars.isEmpty()){
			panel.setVisible(false);
		}
		
		// Remove Progress Bar from Panel of bars
		if (bar != null){
			panel.remove(bar);
			panel.repaint();
		}
	}

	public void taskError(WorkflowTask task, Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(
			panel,
			"An error ocurred while processing:\n" + task.getWorkflow() + "\n\n" + e,
			"Error!",
			JOptionPane.ERROR_MESSAGE
		);
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
	
	public void toDialog(String title){
		final JDialog dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		dialog.add(getComponent());
		
		// Pack Dialog whenever progress bars added/removed
		getComponent().addContainerListener(new ContainerListener(){
			@Override
			public void componentAdded(ContainerEvent e) {
				update();
			}
			@Override
			public void componentRemoved(ContainerEvent e) {
				update();
			}
			private void update(){
				dialog.pack();
				dialog.setVisible(panel.isVisible());
			}
		});
	}
}
