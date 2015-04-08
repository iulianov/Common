package aohara.common.selectorPanel;

import javax.swing.JComponent;

/**
 * Abstract Class for the Panel that will display the item selected in a
 * {@link aohara.common.selectorPanel.SelectorPanelController}.
 * 
 * @author Andrew O'Hara
 *
 * @param <T> The Type of element help in the JList.
 * @param <C> The Type of Component that will display the item info.
 */
public interface SelectorView<T> extends DecoratedComponent<JComponent> {
	
	public void display(T element);
	public T getElement();
	
	public abstract class AbstractSelectorView<T> implements SelectorView<T>{
		
		private T element;
		
		public final void display(T element){
			this.element = element;
			onElementChanged(element);
		}
		
		public final T getElement(){
			return element;
		}
		
		protected abstract void onElementChanged(T element);
	}

}
