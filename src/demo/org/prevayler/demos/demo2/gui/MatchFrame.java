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

import net.sourceforge.javamatch.engine.MatchException;

import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Bank;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

    private Prevayler<Bank> prevayler;

    private JButton refreshButton;

    private JTable matchTable;

    private DefaultTableModel matchTableModel;

    /**
     * Creates a new MatchFrame, that uses the given Prevayler persistent
     * storage mechanism, and puts itself in the given parent container.
     */
    /*
     * MatchFrame(Prevayler prevayler, Container container) { super("Interesting
     * accounts"); this.prevayler = prevayler; initUI(); refreshTable();
     * container.add(this); setVisible(true); }
     */

    MatchFrame(Prevayler<Bank> prevayler) {
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
        setBounds(100, 70, 400, 250);
        getContentPane().setLayout(new BorderLayout(0, 0));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(@SuppressWarnings("unused") ActionEvent event) {
                refreshTable();
            }
        });
        buttonPanel.add(refreshButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        matchTableModel = new DefaultTableModel();
        matchTableModel.setColumnIdentifiers(new String[] { "Match", "Account", "Balance", "#Entries" });
        matchTable = new JTable(matchTableModel);
        getContentPane().add(new JScrollPane(matchTable), BorderLayout.CENTER);
    }

    /**
     * Refreshes the table that shows the top-10 matching results
     */
    private void refreshTable() {
        matchTableModel.setRowCount(0);

        try {
            List<Object[]> results = prevayler.execute(new JavaMatchQuery());
            for (Object[] result : results) {
                matchTableModel.addRow(result);
            }
        } catch (MatchException me) {
            System.out.println(me);
        }
    }

}
