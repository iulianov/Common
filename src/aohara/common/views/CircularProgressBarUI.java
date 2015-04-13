package aohara.common.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

class CircularProgressBarUI extends BasicProgressBarUI {
	
	@Override
	public Dimension getPreferredSize(JComponent c){
		return new Dimension(40, 30);
	}
	
	private Dimension getBarRect(){
		Insets b = progressBar.getInsets(); // area for border
		int barRectWidth = progressBar.getWidth() - b.right - b.left;
		int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
		return new Dimension(barRectWidth, barRectHeight);
	}

	@Override
	public void paintDeterminate(Graphics g, JComponent c) {
		Dimension barRect = getBarRect();
		if (barRect.width <= 0 || barRect.height <= 0){
			return;
		}
		
		double degree = 360 * progressBar.getPercentComplete();
		drawProgress(g, barRect, 90, degree);
		
		// Deal with possible text painting
        if (progressBar.isStringPainted()) {
        	Insets b = progressBar.getInsets(); // area for border
            paintString(g, b.left + Math.round(barRect.width * .3f), b.top, barRect.width, barRect.height, 0, b);
        }
	}
	
	@Override
	public void paintIndeterminate(Graphics g, JComponent c) {
		Dimension barRect = getBarRect();
		if (barRect.width <= 0 || barRect.height <= 0){
			return;
		}
		
		double percentProgress = getAnimationIndex() / (float) getFrameCount();
		percentProgress = percentProgress * 4 % 1;  // Increase speed by 4
		drawProgress(g, barRect, percentProgress * 360, 90);
	}
	
	private void drawProgress(Graphics g, Dimension barRect, double startDegree, double degreeDelta){
		Insets b = progressBar.getInsets(); // area for border
		
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(progressBar.getForeground());
		
		double sz = Math.min(barRect.width, barRect.height);
		double cx = b.left + barRect.width * .3;
		double cy = b.top + barRect.height * .5;
		double or = sz * .5;
		double ir = or * .5; // or - 20;
		Shape inner = new Ellipse2D.Double(
				cx - ir, cy - ir, ir * 2, ir * 2
		);
		Shape outer = new Arc2D.Double(
				cx - or, cy - or, sz, sz,
				startDegree, degreeDelta, Arc2D.PIE
		);
		Area area = new Area(outer);
		area.subtract(new Area(inner));
		g2.fill(area);
		g2.dispose();
	}
}
