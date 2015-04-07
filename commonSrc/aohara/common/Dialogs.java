package aohara.common;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs {
	
	public static void errorDialog(Component component, Throwable exception){
		JOptionPane.showMessageDialog(
			component,
			exception.getClass().getSimpleName(),
			exception.getMessage(),
			JOptionPane.ERROR_MESSAGE
		);
	}

}
