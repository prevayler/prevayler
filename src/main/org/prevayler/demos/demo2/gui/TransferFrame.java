package org.prevayler.demos.demo2.gui;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Account;
import org.prevayler.demos.demo2.business.transactions.Transfer;

class TransferFrame extends AccountFrame {

	private static final long serialVersionUID = -3037927508242313124L;
	private final Account account;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField amountField;
	
    TransferFrame(Account account, Prevayler prevayler, Container container) {
        super("Transfer", prevayler, container);

		this.account = account;
        sourceField.setText(account.numberString());
    
        setBounds(50,50,200,194);
	}

	protected void addFields(Box fieldBox) {
        fieldBox.add(labelContainer("From Account"));
        sourceField = new JTextField();
        sourceField.setEnabled(false);
        fieldBox.add(sourceField);
		
		fieldBox.add(gap());
        fieldBox.add(labelContainer("To Account"));
        destinationField = new JTextField();
        fieldBox.add(destinationField);

		fieldBox.add(gap());
        fieldBox.add(labelContainer("Amount"));
        amountField = new JTextField();
        fieldBox.add(amountField);
	}

	protected void addButtons(JPanel buttonPanel) {
        buttonPanel.add(new JButton(new OKAction()));
	}
	
    private class OKAction extends RobustAction {

		private static final long serialVersionUID = 7431901563750307402L;

		OKAction() {
            super("OK");
		}

		public void action() throws Exception {
            long destinationNumber = parse(destinationField.getText());
            long amount = parse(amountField.getText());
			_prevayler.execute(new Transfer(account.number(), destinationNumber, amount));
            dispose();
		}
	}
}
