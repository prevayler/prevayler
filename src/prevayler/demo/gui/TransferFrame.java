package prevayler.demo.gui;

import prevayler.demo.*;
import prevayler.demo.commands.*;
import prevayler.Prevayler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

class TransferFrame extends AccountFrame {
	
	private final Account account;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField ammountField;
	
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
        fieldBox.add(labelContainer("Ammount"));
        ammountField = new JTextField();
        fieldBox.add(ammountField);
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
            long ammount = parse(ammountField.getText());
            prevayler.executeCommand(new Transfer(account.number(), destinationNumber, ammount));
            dispose();
		}
	}
}
