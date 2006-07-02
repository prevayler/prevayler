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

import org.prevayler.Listener;
import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.demos.demo2.business.transactions.Deposit;
import org.prevayler.demos.demo2.business.transactions.HolderChange;
import org.prevayler.demos.demo2.business.transactions.Withdrawal;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class AccountEditFrame extends AccountFrame implements Listener<AccountEvent> {

    private static final long serialVersionUID = -1881757771183108518L;

    private final String accountNumber;

    private JTextField balanceField;

    private JList historyList;

    AccountEditFrame(String accountNumber, Prevayler<Bank> prevayler, Container container) throws Bank.AccountNotFound {
        super("Account " + accountNumber, prevayler, container);

        this.accountNumber = accountNumber;
        prevayler.register(AccountEvent.class, this);
        prevayler.execute(new AccountPing(accountNumber));

        holderField.addFocusListener(new HolderListener());

        setBounds(50, 50, 306, 300);
    }

    @Override protected void addFields(Box fieldBox) {
        super.addFields(fieldBox);

        fieldBox.add(gap());
        fieldBox.add(labelContainer("Transaction History"));
        historyList = new JList();
        historyList.setEnabled(false);
        fieldBox.add(new JScrollPane(historyList));

        fieldBox.add(gap());
        fieldBox.add(labelContainer("Balance"));
        balanceField = new JTextField();
        balanceField.setEnabled(false);
        fieldBox.add(balanceField);
    }

    @Override protected void addButtons(JPanel buttonPanel) {
        buttonPanel.add(new JButton(new DepositAction()));
        buttonPanel.add(new JButton(new WithdrawAction()));
        buttonPanel.add(new JButton(new TransferAction()));
    }

    private class DepositAction extends RobustAction {

        private static final long serialVersionUID = 405949418924180386L;

        DepositAction() {
            super("Deposit...");
        }

        @Override public void action() throws Exception {
            Number amount = enterAmount("Deposit");
            if (amount == null)
                return;
            _prevayler.execute(new Deposit(accountNumber, amount.longValue()));
        }
    }

    private class WithdrawAction extends RobustAction {

        private static final long serialVersionUID = -3457941349394635348L;

        WithdrawAction() {
            super("Withdraw...");
        }

        @Override public void action() throws Exception {
            Number amount = enterAmount("Withdrawal");
            if (amount == null)
                return;
            _prevayler.execute(new Withdrawal(accountNumber, amount.longValue()));
        }
    }

    private Number enterAmount(String operation) throws Exception {
        String amountText = JOptionPane.showInputDialog(null, "Enter amount", operation, JOptionPane.PLAIN_MESSAGE);
        if (amountText == null)
            return null;
        return new Long(parse(amountText));
    }

    private class TransferAction extends RobustAction {

        private static final long serialVersionUID = -5062849913422257492L;

        TransferAction() {
            super("Transfer...");
        }

        @Override public void action() {
            new TransferFrame(accountNumber, _prevayler, getDesktopPane());
        }
    }

    public void handle(AccountEvent event) {
        if (event.getNumber().equals(accountNumber)) {
            holderField.setText(event.getHolder());
            historyList.setListData(event.getHistory());
            balanceField.setText(event.getBalance());
        }
    }

    private class HolderListener extends FocusAdapter {
        @Override public void focusLost(@SuppressWarnings("unused") FocusEvent e) {
            try {
                _prevayler.execute(new HolderChange(accountNumber, holderText()));
            } catch (Exception exception) {
                RobustAction.display(exception);
            }
        }
    }

}
