package aoahara.common.selectorPanel;

import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ControlPanel<T> implements SelectorView<T> {
	
	protected final JPanel panel;
	protected T element;

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

	@Override
	public void display(T element) {
		this.element = element;
	}

	@Override
	public T getElement() {
		return element;
	}

}
