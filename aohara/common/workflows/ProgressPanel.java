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

import aohara.common.selectorPanel.DecoratedComponent;
import aohara.common.workflows.Workflow.WorkflowTask;

/**
 * Panel that is used to display the progress of various {@link aohara.common.workflows.Workflow}s.
 * 
 * Progress is shown with a Progress Bar and percentage progress.
 * The name of the task is shown before the percentage.
 * 
 * @author Andrew O'Hara
 */
public class ProgressPanel implements DecoratedComponent<JPanel>, TaskListener{
	
	private final JPanel panel = new JPanel();
	private final HashMap<Workflow, JProgressBar> bars = new HashMap<>();

	public ProgressPanel(){
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setPreferredSize(new Dimension(800, 30));
	}

	@Override
	public void taskStarted(Workflow workflow, WorkflowTask task, int target) {
		JProgressBar bar = new JProgressBar();
		bar.setMaximum(target > 0 ? target : 0);
		bar.setIndeterminate(target < 1);
		bar.setStringPainted(true);
		panel.add(bar);
		bars.put(workflow, bar);

		panel.setVisible(true);
		panel.validate();
		
		taskProgress(workflow, task, 0);
	}
	
	@Override
	public void taskProgress(Workflow workflow, WorkflowTask task, int increment) {			
		JProgressBar bar = bars.get(workflow);
		bar.setValue(bar.getValue() + increment);
		if (bar.isIndeterminate()){
			bar.setString(task.toString());
		} else {
			bar.setString(String.format(
				"(%d/%d) %s - %.2f%%",
				workflow.getProgress(), workflow.getTotalTasks(), workflow,
				(bar.getValue() / (float) bar.getMaximum()) * 100));
		}
	}
	
	@Override
	public void taskComplete(Workflow workflow, WorkflowTask task, boolean tasksRemaining){
		JProgressBar bar = bars.remove(workflow);
		
		// Should be done first for panel to notice change during component removal
		if (!tasksRemaining){
			panel.setVisible(false);
		}
		
		if (bar != null){
			panel.remove(bar);
		}
	}

	@Override
	public void taskError(Workflow workflow, WorkflowTask task, boolean tasksRemaining, Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(
			panel,
			"An error ocurred while processing:\n" + workflow + "\n\n" + e,
			"Error!",
			JOptionPane.ERROR_MESSAGE
		);
		taskComplete(workflow, task, tasksRemaining);
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
