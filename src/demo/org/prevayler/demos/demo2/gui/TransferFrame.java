// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.gui;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.demos.demo2.business.transactions.Transfer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Container;

class TransferFrame extends AccountFrame {

    private static final long serialVersionUID = -3037927508242313124L;

    private final String accountNumber;

    private JTextField sourceField;

    private JTextField destinationField;

    private JTextField amountField;

    TransferFrame(String accountNumber, Prevayler<Bank> prevayler, Container container) {
        super("Transfer", prevayler, container);

        this.accountNumber = accountNumber;
        sourceField.setText(accountNumber);

        setBounds(50, 50, 200, 194);
    }

    @Override protected void addFields(Box fieldBox) {
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

    @Override protected void addButtons(JPanel buttonPanel) {
        buttonPanel.add(new JButton(new OKAction()));
    }

    private class OKAction extends RobustAction {

        private static final long serialVersionUID = 7431901563750307402L;

        OKAction() {
            super("OK");
        }

        @Override public void action() throws Exception {
            String destinationNumber = destinationField.getText();
            long amount = parse(amountField.getText());
            _prevayler.execute(new Transfer(accountNumber, destinationNumber, amount));
            dispose();
        }

    }

}
