package aohara.common.views;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs {
	
	public static void errorDialog(Component component, Throwable exception){
		exception.printStackTrace();
		JOptionPane.showMessageDialog(
			component,
			exception.getMessage(),
			exception.getClass().getSimpleName(),
			JOptionPane.ERROR_MESSAGE
		);
	}

}
