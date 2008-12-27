package org.prevayler.demos.demo2.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

abstract class RobustAction extends AbstractAction {
		
	RobustAction(String name) {
		super(name);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			action();
		} catch (Exception exception) {
			display(exception);
		}
	}
		
	protected abstract void action() throws Exception;
	
	static void display(Exception exception) {
		JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}
}
