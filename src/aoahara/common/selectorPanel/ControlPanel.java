package aoahara.common.selectorPanel;

import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ControlPanel implements SelectorBasePanel {
	
	private final JPanel panel;

	public ControlPanel(){
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setBorder(BorderFactory.createEtchedBorder());
	}
	
	public JButton addButton(Action action){
		JButton button = new JButton(action);
		panel.add(button);
		return button;
	}
	
	public JButton addButton(JButton button){
		panel.add(button);
		return button;
	}
	
	public JComponent getComponent(){
		return panel;
	}

}
