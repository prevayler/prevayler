package org.prevayler.demos.demo2.gui;

import org.prevayler.*;
import org.prevayler.demos.demo2.commands.AccountCreation;
import javax.swing.*;
import java.awt.Container;

class NewAccountFrame extends AccountFrame {
	
	NewAccountFrame(Prevayler prevayler, Container container) {
		super("New Account", prevayler, container);

		setBounds(50,50,240,114);
	}

	protected void addButtons(JPanel buttonPanel) {
		buttonPanel.add(new JButton(new OKAction()));
	}
	
	private class OKAction extends RobustAction {

		OKAction() {
			super("OK");
		}

		protected void action() throws Exception {
			prevayler.executeCommand(new AccountCreation(holderText()));
			dispose();
		}
	}

}