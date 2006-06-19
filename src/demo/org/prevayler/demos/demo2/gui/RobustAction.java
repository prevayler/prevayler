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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;

abstract class RobustAction extends AbstractAction {

    RobustAction(String name) {
        super(name);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            action();
        } catch (Exception exception) {
            display(exception);
        }
    }

    protected abstract void action() throws Exception;

    static void display(Exception exception) {
        JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
