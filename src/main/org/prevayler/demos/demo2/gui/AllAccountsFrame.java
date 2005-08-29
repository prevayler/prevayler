package org.prevayler.demos.demo2.gui;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.*;
import org.prevayler.demos.demo2.business.transactions.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

class AllAccountsFrame extends JInternalFrame implements BankListener, AccountListener {

	private static final long serialVersionUID = -9182376858708585231L;
	private final Prevayler _prevayler;
	private final JList accountList;

	AllAccountsFrame(Prevayler prevayler, Container container) {
    	super("All Accounts", true);  //true means resizable.
		_prevayler = prevayler;
		
		accountList = new JList();
		accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bank().setBankListener(this);
		listenToAccounts();
		refreshAccounts();
		
		container.add(this);
    		getContentPane().add(new JScrollPane(accountList), BorderLayout.CENTER);
		getContentPane().add(accountButtons(), BorderLayout.SOUTH);

		setBounds(10,10,330,240);
		show();
	}
  
	private void listenToAccounts() {
		java.util.Iterator it = accounts().iterator();
		while (it.hasNext()) {
			((Account)it.next()).addAccountListener(this);
		}
	}

	private void refreshAccounts() {
		accountList.setListData(accounts().toArray());
	}
	
	private java.util.List accounts() {
		return bank().accounts();
	}
	
	private Bank bank() {
		return (Bank)_prevayler.prevalentSystem();
	}
	
	public void accountCreated(Account a) { //Implements BankListener.
		a.addAccountListener(this);
		refreshAccounts();
	}

	public void accountDeleted(Account a) { //Implements BankListener.
		a.removeAccountListener(this);
		refreshAccounts();
	}

	public void accountChanged() { //Implements AccountListener.
		refreshAccounts();
	}
  
	private JPanel accountButtons() {
		JPanel result = new JPanel();
   
		result.add(new JButton(new AccountCreation()));
		result.add(new JButton(new AccountEditAction()));
		result.add(new JButton(new AccountDeleteAction()));
    
		return result;
	}

	class AccountCreation extends AbstractAction {

		private static final long serialVersionUID = 9182061237235826622L;

		AccountCreation() {
			super("Create");
		}

		public void actionPerformed(ActionEvent e) {
			new NewAccountFrame(_prevayler, getDesktopPane());
		}
		
	}
	
	abstract class SelectedAccountAction extends RobustAction implements ListSelectionListener {

		SelectedAccountAction(String name) {
			super(name);
			refreshEnabled();
			accountList.addListSelectionListener(this);
		}
  		
		private void refreshEnabled() {
			this.setEnabled(accountList.getSelectedValue() != null);
		}

		public void valueChanged(ListSelectionEvent event) {
			refreshEnabled();
		}

		protected void action() throws Exception {
			action((Account)accountList.getSelectedValue());
		}

		abstract void action(Account account) throws Exception;
	}

	class AccountEditAction extends SelectedAccountAction {

		private static final long serialVersionUID = 5901968097767524191L;

		AccountEditAction() {
			super("Edit");
		}
		
		void action(Account account) {
			new AccountEditFrame(account, _prevayler, getDesktopPane());
		}
	}

	class AccountDeleteAction extends SelectedAccountAction {

		private static final long serialVersionUID = -1320330350155595965L;

		AccountDeleteAction() {
			super("Delete");
		}
		
		void action(Account account) throws Exception {
			int option = JOptionPane.showConfirmDialog(null, "Delete selected account?", "Account Deletion", JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION) return;
			
			_prevayler.execute(new AccountDeletion(account));
		}
		  	
	}

}