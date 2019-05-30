//  Copyright (C) 2002 Refactor, Finland. http://www.refactor.fi/
//
//  This file is part of AddingMachine, an example program for Prevayler.
//
//  AddingMachine is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  AddingMachine is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with AddingMachine; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


/**
 * Utility class to display information on when snapshots are made.
 *
 * Leonard Norrgard <lkn@acm.org>
 * Refactor, Finland. http://www.refactor.fi/
 *
 * @version 1.0
 * @author Leonard Norrgard <lkn@acm.org>
 * @see SnapshotMaker
 */
package org.prevayler.util;

import org.prevayler.Prevayler;

public class SnapshotScheduleLogger<P> implements SnapshotListener<P> {
  public void snapshotStarted(Prevayler<P> prevayler, long prevaylerDate, long systemDate) {
    System.out.println("prevayler clock date when snapshot started: " + new java.util.Date(prevaylerDate));
    System.out.println("system clock date when snapshot started: " + new java.util.Date(systemDate));
  }

  public void snapshotTaken(Prevayler<P> prevayler, long prevaylerDate, long systemDate) {
    System.out.println("prevayler clock date when snapshot completed: " + new java.util.Date(prevaylerDate));
    System.out.println("system clock date when snapshot completed: " + new java.util.Date(systemDate));
  }

  public void snapshotException(Prevayler<P> prevayler, Exception exception, long prevaylerDate, long systemDate) {
    System.out.println("prevayler clock date when Exception occured: " + new java.util.Date(prevaylerDate));
    System.out.println("system clock when Exception occured: " + new java.util.Date(systemDate));
    System.out.println("the exception was: " + exception);
  }

  public void snapshotError(Prevayler<P> prevayler, Error error, long prevaylerDate, long systemDate) {
    System.out.println("prevayler clock date when Error occured: " + new java.util.Date(prevaylerDate));
    System.out.println("system clock date when Error occured: " + new java.util.Date(systemDate));
    System.out.println("the error was: " + error);
  }

  public void snapshotShutdown(Prevayler<P> prevayler, long prevaylerDate, long systemDate) {
    System.out.println("prevayler clock date when shutting down SnapshotMaker: " + new java.util.Date(prevaylerDate));
    System.out.println("system clock date when shutting down SnapshotMaker: " + new java.util.Date(systemDate));
  }
}
