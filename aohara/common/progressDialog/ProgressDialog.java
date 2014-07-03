package aohara.common.progressDialog;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

import thirdParty.VerticalLayout;

public class ProgressDialog<T> implements ProgressListener<T> {
	
	private final JDialog dialog = new JDialog();
	private final HashMap<T, JProgressBar> bars = new HashMap<>();

	public ProgressDialog(String title){
		dialog.setLayout(new VerticalLayout());
		dialog.setTitle(title);
	}

	@Override
	public void progressStarted(T object, int target, int tasksRunning) {
		JProgressBar bar = new JProgressBar(0, target);
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(400, 50));
		dialog.add(bar);
		bars.put(object, bar);
		dialog.pack();

		dialog.setVisible(true);
		
		progressMade(object, 0);
	}
	
	@Override
	public void progressMade(T object, int current) {
		JProgressBar bar = bars.get(object);
		try {
			bar.setValue(current);
			bar.setString(String.format(
				"%s - %.2f%%",
				object,
				(current / (float) bar.getMaximum()) * 100
			));
		} catch (Throwable t){
			t.printStackTrace();
		}
	}

	@Override
	public void progressComplete(T object, int tasksRunning) {
		dialog.remove(bars.get(object));
		bars.remove(object);
		
		dialog.pack();
		dialog.setVisible(bars.size() > 0);
	}

	@Override
	public void progressError(T object, int tasksRunning) {
		progressComplete(object, tasksRunning);
	}
}
