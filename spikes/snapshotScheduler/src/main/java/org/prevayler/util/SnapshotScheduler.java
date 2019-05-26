// Prevayler(TM) - The Open-Source Prevalence Layer.
// This file is Copyright (C) 2002 Refactor, Finland. http://www.refactor.fi/
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import org.prevayler.Prevayler;

import java.util.*;

/**
 * SnapshotScheduler is a utility class to make snapshots of the given
 * prevalent system at regular intervals, as scheduled, with complete
 * logging capabilities. Using this class
 * is optional; you can make snapshots at any time by calling
 * prevayler.takeSnapshot(), for example at application shutdown time.
 * <p/>
 * The first snapshot can be scheduled in two ways:
 * <ul><li>At an absolute time given by a Date object (see example below).
 * <li>After a delay specified in milliseconds.
 * </ul>
 * <p/>
 * Subsequent snapshots are scheduled to occur at the given period.
 * See Java classes Timer and TimerTask for details. To make sure
 * snapshots are made at regular intervals, rather than
 * letting the start time drift for all subsequent snapshots,
 * the scheduling is made through the
 * {@link Timer#scheduleAtFixedRate(TimerTask, long, long) scheduleAtFixedRate} method.
 * <p/>
 * Example usage of SnapshotScheduler:
 * <p/>
 * <code><pre>
 *     // Make snapshots each night, starting next night at 03:00.00.000.
 *     GregorianCalendar first = new GregorianCalendar ();
 *     first.add(Calendar.DATE, 1);
 *     first.set(Calendar.HOUR_OF_DAY, 3);
 *     first.set(Calendar.MINUTE, 0);
 *     first.set(Calendar.SECOND, 0);
 *     first.set(Calendar.MILLISECOND, 0);
 * <p/>
 *     SnapshotScheduler scheduler = new SnapshotScheduler (
 *         prevayler,
 *         first.getTime(),
 *         1000*60*60*24,
 *         new SnapshotScheduler.Listener () {
 *             ... // Your implementation of the Listener
 *             ... // interface goes here.
 *         });
 * <p/>
 *     ... // Main work of the application goes here.
 * <p/>
 *     // At application shutdown time, make a call to cancel().
 *     scheduler.cancel ();
 * </pre></code>
 * <p/>
 * This class requires Java 1.3 or later.
 * <p/>
 * Possible features for a future version:
 * <ul><li>Provide optional information on file size used for a snapshot as well
 * as free storage space left when a snapshot has been made.
 * <li>If you have complex scheduling needs, you may want to create your own
 * variant of this class and integrate a real scheduler; for example Israel&#160;Olalla's Jcrontab
 * which is available at <a href="http://jcrontab.sourceforge.net">http://jcrontab.sourceforge.net</a>.
 * </ul>
 *
 * @author Leonard Norrgard &lt;lkn@acm.org&gt; Copyight &copy; 2002 <a href="http://www.refactor.fi/">Refactor</a>, Finland
 * @version 1.1alpha - 2002-11-13
 * @see Timer
 * @see TimerTask
 */
public class SnapshotScheduler<P> extends TimerTask {
  @SuppressWarnings("unused")
  private volatile boolean done = false;
  @SuppressWarnings("unused")
  private Object snapshotLock = new Object();
  private Prevayler<P> prevayler;
  private Timer timer;
  @SuppressWarnings("unused")
  private Object sync = new Object();

  private List<SnapshotListener<P>> listenerList = Collections.synchronizedList(new LinkedList<SnapshotListener<P>>());

  /**
   * Construct a SnapshotScheduler without a listener. One or more listeners can be added
   * by calling <a href="#addListener">addListener</a>. The first snapshot will
   * be made at firstTime, with subsequent snapshots every period milliseconds.
   *
   * @param prevayler the prevalent system to make snapshots of.
   * @param firstTime time when the first snapshot is to be made.
   * @param period    time in milliseconds between successive snapshots.
   */
  public SnapshotScheduler(Prevayler<P> prevayler, Date firstTime, long period) {
    this(prevayler, firstTime, period, null);
  }

  /**
   * Construct a SnapshotScheduler without a listener. One or more listeners can be added
   * by calling <a href="#addListener">addListener</a>. The first snapshot will
   * be made after delay milliseconds, with subsequent snapshots every
   * period milliseconds.
   *
   * @param prevayler the prevalent system to make snapshots of.
   * @param delay     delay in milliseconds before first snapshot is to be made.
   * @param period    time in milliseconds between successive snapshots.
   */
  public SnapshotScheduler(Prevayler<P> prevayler, long delay, long period) {
    this(prevayler, delay, period, null);
  }

  /**
   * Construct a SnapshotScheduler with the given listener. More listeners can be added
   * by calling <a href="#addListener">addListerner</a>.
   *
   * @param prevayler the prevalent system to make snapshots of.
   * @param firstTime time when the first snapshot is to be made.
   * @param period    time in milliseconds between successive snapshots.
   * @param listener  If non-null, the listener will be called each time a snapshot has been made.
   */
  public SnapshotScheduler(Prevayler<P> prevayler, Date firstTime, long period, SnapshotListener<P> listener) {
    this.prevayler = prevayler;
    if (listener != null) {
      listenerList.add(listener);
    }
    timer = new Timer(false); // Don't make this a daemon thread!
    timer.scheduleAtFixedRate(this, firstTime, period);
  }

  /**
   * Construct a SnapshotScheduler with the given listener. More listeners can be added
   * by calling <a href="#addListener">addListerner</a>. The first snapshot will
   * be made after delay milliseconds, with subsequent snapshots every
   * period milliseconds.
   *
   * @param prevayler the prevalent system to make snapshots of.
   * @param delay     delay in milliseconds before first snapshot is to be made.
   * @param period    time in milliseconds between successive snapshots.
   * @param listener  If non-null, the listener will be called each time a snapshot has been made.
   */
  public SnapshotScheduler(Prevayler<P> prevayler, long delay, long period, SnapshotListener<P> listener) {
    this.prevayler = prevayler;
    if (listener != null) {
      listenerList.add(listener);
    }
    timer = new Timer(false); // Don't make this a daemon thread!
    timer.scheduleAtFixedRate(this, delay, period);
  }

  /**
   * Make a snapshot.
   */
  public void run() {
    snapshotStarted();

    try {

      prevayler.takeSnapshot();

    } catch (Exception e) {

      // This is likely a temporary problem, so keep running.
      snapshotException(e);

    } catch (Error e) {

      snapshotError(e);

      // This is likely fatal, so reassert. See Java documentation for classes Error and Throwable.
      throw e;
    }

    snapshotTaken();
  }

  /**
   * Stop making snapshots. If a snapshot is being made at the time of this call
   * it will be completed, but no further snapshots will be made.
   */
  public boolean cancel() {
    snapshotShutdown();
    return super.cancel();
  }

  /**
   * Remove the given listener from this SnapshotScheduler, no more
   * notifications will occur to the given listener.
   *
   * @param listener listener to remove.
   */
  public void removeListener(SnapshotListener<P> listener) {
    listenerList.remove(listener);
  }

  /**
   * Add the given listener to this SnapshotScheduler; the listener will be notified
   * on the progress of making snapshots.
   *
   * @param listener listener to add.
   */
  public void addListener(SnapshotListener<P> listener) {
    listenerList.add(listener);
  }

  /**
   * Inform all listeners that a new snapshot is about to be made immediately.
   */
  private void snapshotStarted() {
    long prevaylerDate = prevayler.clock().time().getTime();
    long systemDate = System.currentTimeMillis();

    Iterator<SnapshotListener<P>> i = listenerList.iterator();
    while (i.hasNext()) {
      i.next().snapshotStarted(prevayler, prevaylerDate, systemDate);
    }
  }

  /**
   * Inform all interested listeners that a new snapshot has been made.
   */
  private void snapshotTaken() {
    long prevaylerDate = prevayler.clock().time().getTime();
    long systemDate = System.currentTimeMillis();

    Iterator<SnapshotListener<P>> i = listenerList.iterator();
    while (i.hasNext()) {
      i.next().snapshotTaken(prevayler, prevaylerDate, systemDate);
    }
  }

  /**
   * Inform all listeners that an exception occured while making the snapshot.
   */
  private void snapshotException(Exception e) {
    long prevaylerDate = prevayler.clock().time().getTime();
    long systemDate = System.currentTimeMillis();

    Iterator<SnapshotListener<P>> i = listenerList.iterator();
    while (i.hasNext()) {
      i.next().snapshotException(prevayler, e, prevaylerDate, systemDate);
    }
  }

  /**
   * Inform all listeners that an exception occured while making the snapshot.
   */
  private void snapshotError(Error e) {
    long prevaylerDate = prevayler.clock().time().getTime();
    long systemDate = System.currentTimeMillis();

    Iterator<SnapshotListener<P>> i = listenerList.iterator();
    while (i.hasNext()) {
      i.next().snapshotError(prevayler, e, prevaylerDate, systemDate);
    }
  }

  /**
   * Inform all listeners that SnapshotScheduler is shutting down.
   */
  private void snapshotShutdown() {
    long prevaylerDate = prevayler.clock().time().getTime();
    long systemDate = System.currentTimeMillis();

    Iterator<SnapshotListener<P>> i = listenerList.iterator();
    while (i.hasNext()) {
      i.next().snapshotShutdown(prevayler, prevaylerDate, systemDate);
    }
  }
}
