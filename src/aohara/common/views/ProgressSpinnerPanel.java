package aohara.common.views;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressSpinnerPanel extends JPanel {
	
	private final JLabel label = new JLabel();
	private final JProgressBar progressWidget;
	
	ProgressSpinnerPanel(JProgressBar progressWidget){
		super(new BorderLayout());
		this.progressWidget = progressWidget;
		progressWidget.setVisible(false);
		
		add(label, BorderLayout.CENTER);
		add(progressWidget, BorderLayout.EAST);
	}
	
	public static ProgressSpinnerPanel create(){
		JProgressBar bar = new JProgressBar(){
			@Override
			public boolean isDisplayable(){
				return true;
			}
		};
		bar.setUI(new CircularProgressBarUI());
		bar.setStringPainted(true);

		return new ProgressSpinnerPanel(bar);
	}
	
	public void start(){
		progressWidget.setIndeterminate(true);
		progressWidget.setVisible(true);
	}
	
	public void setProgress(int progress){
		progressWidget.setValue(progress);
	}
	
	public void setMaxProgress(int maxProgress){
		progressWidget.setMaximum(maxProgress);
		progressWidget.setIndeterminate(maxProgress <= 0);
	}
	
	public void reset(){
		progressWidget.setIndeterminate(false);
		progressWidget.setVisible(false);
	}
	
	public void setIcon(Icon icon){
		label.setIcon(icon);
	}
	
	public void setText(String text){
		label.setText(text);
	}
	
	public boolean isRunning(){
		return progressWidget.isVisible();
	}
}