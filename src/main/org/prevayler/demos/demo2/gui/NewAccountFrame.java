package org.prevayler.demos.demo2.gui;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.transactions.AccountCreation;
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
			_prevayler.execute(new AccountCreation(holderText()));
			dispose();
		}
	}

}