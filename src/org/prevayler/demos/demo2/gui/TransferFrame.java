package org.prevayler.demos.demo2.gui;

import org.prevayler.demos.demo2.*;
import org.prevayler.demos.demo2.commands.*;
import org.prevayler.Prevayler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

class TransferFrame extends AccountFrame {
	
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
        sourceField.disable();
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
		
        OKAction() {
            super("OK");
		}

		public void action() throws Exception {
            long destinationNumber = parse(destinationField.getText());
            long amount = parse(amountField.getText());
            prevayler.executeCommand(new Transfer(account.number(), destinationNumber, amount));
            dispose();
		}
	}
}
