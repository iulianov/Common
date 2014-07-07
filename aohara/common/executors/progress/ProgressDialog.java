package aohara.common.executors.progress;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import aohara.common.executors.context.ExecutorContext;
import thirdParty.VerticalLayout;

public class ProgressDialog implements ProgressListener {
	
	private final JDialog dialog = new JDialog();
	private final HashMap<ExecutorContext, JProgressBar> bars = new HashMap<>();
	
	public ProgressDialog(){
		this("Progress");
	}

	public ProgressDialog(String title){
		dialog.setLayout(new VerticalLayout());
		dialog.setTitle(title);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void progressStarted(ExecutorContext object, int target, int tasksRunning) {
		JProgressBar bar = new JProgressBar();
		bar.setMaximum(target > 0 ? target : 0);
		bar.setIndeterminate(target < 0);
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(400, 50));
		dialog.add(bar);
		bars.put(object, bar);
		dialog.pack();

		dialog.setVisible(true);
		
		progressMade(object, 0);
	}
	
	@Override
	public void progressMade(ExecutorContext object, int current) {		
		JProgressBar bar = bars.get(object);
		try {
			bar.setValue(current);
			if (bar.isIndeterminate()){
				bar.setString(object.toString());
			} else {
				bar.setString(String.format(
					"%s - %.2f%%", object,
					(current / (float) bar.getMaximum()) * 100));
			}
		} catch (Throwable t){
			t.printStackTrace();
		}
	}

	@Override
	public void progressComplete(ExecutorContext object, int tasksRunning) {
		JProgressBar bar = bars.get(object);
		
		if (bar != null){
			dialog.remove(bar);
			bars.remove(object);
			
			dialog.pack();
			dialog.setVisible(tasksRunning > 0);
		}
	}

	@Override
	public void progressError(ExecutorContext object, int tasksRunning) {
		JOptionPane.showMessageDialog(
			dialog,
			"An error ocurred while processing:\n" + object,
			"Error!",
			JOptionPane.ERROR_MESSAGE
		);
		progressComplete(object, tasksRunning);
	}
}
