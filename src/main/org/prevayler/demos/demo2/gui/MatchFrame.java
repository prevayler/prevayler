package org.prevayler.demos.demo2.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import net.sourceforge.javamatch.engine.*;
import net.sourceforge.javamatch.query.*;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.*;

/**
 * Class MatchFrame shows the results of matching the data in the bank demo 
 * application
 *
 * @see http://javamatch.sourceforge.net/integration.html#prevayler
 * @author Walter van Iterson
 * @author Jacob Kjome
 */
class MatchFrame extends JInternalFrame {

	private static final long serialVersionUID = 2988308712345594693L;
	private Prevayler prevayler;
    private JButton refreshButton;
    private JTable matchTable;
    private DefaultTableModel matchTableModel;

    /**
     * Creates a new MatchFrame, that uses the given Prevayler persistent storage
     * mechanism, and puts itself in the given parent container.
     */
    /*MatchFrame(Prevayler prevayler, Container container) {
        super("Interesting accounts");
        this.prevayler = prevayler;
        initUI();
        refreshTable();
        container.add(this);
        setVisible(true);
    }*/

    MatchFrame(Prevayler prevayler) {
        super("Interesting accounts");
        this.prevayler = prevayler;
        initUI();
        refreshTable();
        setVisible(true);
    }

    /**
     * Initializes the user interface
     */
    private void initUI() {
        setBounds(100,70,400,250);
        getContentPane().setLayout(new BorderLayout(0, 0));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                refreshTable();
            }
        });
        buttonPanel.add(refreshButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        matchTableModel= new DefaultTableModel();
        matchTableModel.setColumnIdentifiers(new String[] {"Match", "Account", "Balance", "#Entries"});
        matchTable = new JTable(matchTableModel);
        getContentPane().add(new JScrollPane(matchTable), BorderLayout.CENTER);
    }

    /**
     * Refreshes the table that shows the top-10 matching results
     */
    private void refreshTable() {
        matchTableModel.setRowCount(0);
        Bank bank = (Bank)prevayler.prevalentSystem();
        java.util.List accounts = bank.accounts();

        try {
            // create the match engine
            MatchEngine matchEngine = new MatchEngine();

            // create the match query
            QuerySet query = new QuerySet();
            query.addPreferred(new Maximum("balance()"));
            query.addPreferred(new Maximum("transactionHistory().size()"));

            // execute the match query
            MatchResult matchResult = matchEngine.executeQuery(query, accounts);

            // retrieve matching results
            for (Iterator resultIterator = matchResult.getResultIterator(); resultIterator.hasNext();) {
                ResultItem curResultItem = (ResultItem)resultIterator.next();
                Account matchedAccount = (Account)curResultItem.getMatchedObject();
                // create a row in the table that displays the matching results
                Object[] rowData = new Object[4];
                rowData[0] = new Float(curResultItem.getMatchValue());
                rowData[1] = matchedAccount;
                rowData[2] = new Long(matchedAccount.balance());
                rowData[3] = new Integer(matchedAccount.transactionHistory().size());
                matchTableModel.addRow(rowData);
            }
        } catch (MatchException me) {
            System.out.println(me);
        }
    }

}