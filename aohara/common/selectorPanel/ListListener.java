package aohara.common.selectorPanel;

import java.awt.event.MouseEvent;

public interface ListListener<T> {
	public void elementClicked(T element, int numTimes) throws Exception;
	public void elementRightClicked(MouseEvent evt, T element) throws Exception;
	public void elementSelected(T element) throws Exception;
}
