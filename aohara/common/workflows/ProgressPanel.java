package aohara.common.workflows;

import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import thirdParty.VerticalLayout;
import aohara.common.selectorPanel.DecoratedComponent;
import aohara.common.workflows.tasks.WorkflowTask;

public class ProgressPanel implements DecoratedComponent<JPanel>, TaskListener{
	
	private final JPanel panel = new JPanel();
	private final HashMap<Workflow, JProgressBar> bars = new HashMap<>();

	public ProgressPanel(){
		panel.setLayout(new VerticalLayout());
	}

	@Override
	public void taskStarted(WorkflowTask task, int target) {
		JProgressBar bar = new JProgressBar();
		bar.setMaximum(target > 0 ? target : 0);
		bar.setIndeterminate(target < 0);
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(400, 50));
		panel.add(bar);
		bars.put(task.workflow, bar);

		panel.setVisible(true);
		
		taskProgress(task, 0);
	}
	
	@Override
	public void taskProgress(WorkflowTask task, int increment) {	
		Workflow workflow = task.workflow;
		
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
	public void taskComplete(WorkflowTask task, boolean tasksRemaining){
		JProgressBar bar = bars.remove(task.workflow);
		
		// Should be done first for panel to notice change during component removal
		if (!tasksRemaining){
			panel.setVisible(false);
		}
		
		if (bar != null){
			panel.remove(bar);
		}
	}

	@Override
	public void taskError(WorkflowTask task, boolean tasksRemaining, Exception e) {
		JOptionPane.showMessageDialog(
			panel,
			"An error ocurred while processing:\n" + task.workflow + "\n\n" + e,
			"Error!",
			JOptionPane.ERROR_MESSAGE
		);
		taskComplete(task, tasksRemaining);
	}

	@Override
	public JPanel getComponent() {
		return panel;
	}
	
	public void toDialog(String title){
		final JDialog dialog = new JDialog();
		dialog.setTitle(title);
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
