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

import org.prevayler.demos.demo2.business.Account;

public class AccountInfo {

    private final String number;

    private final String display;

    public AccountInfo(Account account) {
        this.number = account.numberString();
        this.display = account.toString();
    }

    public String getNumber() {
        return number;
    }

    public String getDisplay() {
        return display;
    }

    @Override public String toString() {
        return display;
    }

}
