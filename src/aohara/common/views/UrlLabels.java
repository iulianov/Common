package aohara.common.views;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import aohara.common.Util;
import aohara.common.selectorPanel.DecoratedComponent;

public abstract class UrlLabels implements DecoratedComponent<JLabel> {
	
	private final JLabel label = new JLabel();
	private boolean configured = false;
	
	public UrlLabels(final URL url){
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Util.goToHyperlink(url);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(
			        	label, "Could not open hyperlink:\n" + url);
				}
			}
		});
	}
	
	public abstract void configureLabel(JLabel label);
	
	@Override
	public JLabel getComponent() {
		if (!configured){
			configureLabel(label);
		}
		return label;
	}
	
	
	
	public static class UrlIcon extends UrlLabels {
		
		private final Icon icon;
		
		public UrlIcon(Icon icon, URL url){
			super(url);
			this.icon = icon;
		}

		@Override
		public void configureLabel(JLabel label) {
			label.setIcon(icon);
		}
		
	}
	
	public static class UrlLink extends UrlLabels {
		
		private final String text;

		public UrlLink(String text, URL url) {
			super(url);
			this.text = text;
		}

		@Override
		public void configureLabel(JLabel label) {
			label.setText(String.format("<html><a href=''>%s</a></html>", text));
		}
		
	}
	

}
