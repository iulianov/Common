package aohara.common.selectorPanel;

import javax.swing.JComponent;

/**
 * Decorator for a JComponent.
 * 
 * Allows an extending class to use a JComponent and have a client class get
 * that component.  Essentially allows for a Component to be constructed without
 * extending the component.
 * 
 * @author Andrew O'Hara
 *
 * @param <C> The Type of Component to be Decorated
 */
public interface DecoratedComponent<C extends JComponent> {
	public C getComponent();
}
