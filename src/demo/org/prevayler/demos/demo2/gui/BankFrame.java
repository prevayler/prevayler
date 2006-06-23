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

import org.prevayler.ClockQuery;
import org.prevayler.Prevayler;
import org.prevayler.demos.demo2.business.Bank;
import org.prevayler.foundation.Cool;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BankFrame extends JFrame {

    private static final long serialVersionUID = 936051923275473259L;

    private final Prevayler<Bank> _prevayler;

    public BankFrame(Prevayler<Bank> prevayler) {
        super("Bank");
        _prevayler = prevayler;

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JDesktopPane desktop = new JDesktopPane();
        setContentPane(desktop);
        new AllAccountsFrame(prevayler, desktop);
        desktop.add(new RobustnessFrame());
        desktop.add(new MatchFrame(prevayler));
        setBounds(40, 40, 550, 420);
        setVisible(true);

        refreshClock();
    }

    private void refreshClock() {
        Thread clockRefresher = new Thread() {
            @Override public void run() {
                while (true) {
                    DateFormat format = new SimpleDateFormat("hh:mm:ss");
                    setTitle("Bank - " + format.format(_prevayler.execute(new ClockQuery())));
                    Cool.sleep(500);
                }
            }
        };
        clockRefresher.setDaemon(true);
        clockRefresher.start();
    }

    private static class RobustnessFrame extends JInternalFrame {

        private static final long serialVersionUID = -3548833894524605027L;

        RobustnessFrame() {
            super("Robustness Reminder", false, false, false, true);
            setContentPane(Box.createVerticalBox());

            addLine(" You can kill this process at any time. ");
            addLine(" When you run the application again, ");
            addLine(" you will see that nothing was lost. ");

            setBackground(new java.awt.Color(204, 204, 204));
            setBounds(300, 300, 235, 90);
            setVisible(true);
        }

        private void addLine(String line) {
            JLabel label = new JLabel(line);
            label.setAlignmentX(0.5f);
            getContentPane().add(label);
        }
    }

}
