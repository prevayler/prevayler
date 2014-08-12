//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Transaction;

import java.util.Date;


class Appendix implements Transaction<AppendingSystem> {

  private static final long serialVersionUID = 7925676108189989759L;
  private final String appendix;

  public void executeOn(AppendingSystem prevalentSystem, Date ignored) {
    prevalentSystem.append(appendix);
  }

  Appendix(String appendix) {
    this.appendix = appendix;
  }
}