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
import org.prevayler.demos.demo2.business.transactions.AccountCreation;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Container;

class NewAccountFrame extends AccountFrame {

    private static final long serialVersionUID = -7766047243601388692L;

    NewAccountFrame(Prevayler<Bank> prevayler, Container container) {
        super("New Account", prevayler, container);

        setBounds(50, 50, 240, 114);
    }

    @Override protected void addButtons(JPanel buttonPanel) {
        buttonPanel.add(new JButton(new OKAction()));
    }

    private class OKAction extends RobustAction {

        private static final long serialVersionUID = 728880919739535517L;

        OKAction() {
            super("OK");
        }

        @Override protected void action() throws Exception {
            _prevayler.execute(new AccountCreation(holderText()));
            dispose();
        }
    }

}
