package aohara.common.selectorPanel;

import javax.swing.JComponent;

public interface DecoratedComponent<C extends JComponent> {
	public C getComponent();
}
