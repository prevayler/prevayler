// Prevayler(TM) - The Open-Source Prevalence Layer.
// This file is Copyright (C) 2002 Refactor, Finland. http://www.refactor.fi/
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.util;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.prevayler.implementation.SnapshotPrevayler;

/**
 * SnapshotScheduler is a utility class to make snapshots of the given
 * prevalent system at regular intervals, as scheduled, with complete
 * logging capabilities. Using this class
 * is optional; you can make snapshots at any time by calling
 * prevayler.takeSnapshot(), for example at application shutdown time.
 * <p>
 * The first snapshot can be scheduled in two ways:
 * <ul><li>At an absolute time given by a Date object (see example below).
 * <li>After a delay specified in milliseconds.
 * </ul>
 * <p>
 * Subsequent snapshots are scheduled to occur at the given period.
 * See Java classes Timer and TimerTask for details. To make sure
 * snapshots are made at regular intervals, rather than
 * letting the start time drift for all subsequent snapshots,
 * the scheduling is made through the
 * {@link Timer#scheduleAtFixedRate(TimerTask, long, long) scheduleAtFixedRate} method.
 * <p>
 * Example usage of SnapshotScheduler:
 * <p>
 * <code><pre>
 *     // Make snapshots each night, starting next night at 03:00.00.000.
 *     GregorianCalendar first = new GregorianCalendar ();
 *     first.add(Calendar.DATE, 1);
 *     first.set(Calendar.HOUR_OF_DAY, 3);
 *     first.set(Calendar.MINUTE, 0);
 *     first.set(Calendar.SECOND, 0);
 *     first.set(Calendar.MILLISECOND, 0);
 * 
 *     SnapshotScheduler scheduler = new SnapshotScheduler (
 *         prevayler,
 *         first.getTime(),
 *         1000*60*60*24,
 *         new SnapshotScheduler.Listener () {
 *             ... // Your implementation of the Listener
 *             ... // interface goes here.
 *         });
 * 
 *     ... // Main work of the application goes here.
 * 
 *     // At application shutdown time, make a call to cancel().
 *     scheduler.cancel ();
 * </pre></code>
 * <p>
 * This class requires Java 1.3 or later.
 * <p>
 * Possible features for a future version:
 * <ul><li>Provide optional information on file size used for a snapshot as well
 * as free storage space left when a snapshot has been made.
 * <li>If you have complex scheduling needs, you may want to create your own
 * variant of this class and integrate a real scheduler; for example Israel&#160;Olalla's Jcrontab
 * which is available at <a href="http://jcrontab.sourceforge.net">http://jcrontab.sourceforge.net</a>.
 * </ul>
 * 
 * @version 1.1alpha - 2002-11-13
 * @author Leonard Norrgard &lt;lkn@acm.org&gt; Copyight &copy; 2002 <a href="http://www.refactor.fi/">Refactor</a>, Finland
 * @see Timer
 * @see TimerTask
 */
public class SnapshotScheduler extends TimerTask {
	private volatile boolean done = false;
	private Object snapshotLock = new Object ();
	private Prevayler prevayler;
	private Timer timer;
	private Object sync = new Object ();

	private List listenerList = Collections.synchronizedList(new LinkedList());

	/**
	 * Construct a SnapshotScheduler without a listener. One or more listeners can be added
	 * by calling <a href="#addListener">addListener</a>. The first snapshot will
	 * be made at firstTime, with subsequent snapshots every period milliseconds.
	 * 
	 * @param prevayler the prevalent system to make snapshots of.
	 * @param firstTime time when the first snapshot is to be made.
	 * @param period time in milliseconds between successive snapshots.
	 */	
	public SnapshotScheduler (Prevayler prevayler, Date firstTime, long period) {
		this (prevayler, firstTime, period, null);
	}

	/**
	 * Construct a SnapshotScheduler without a listener. One or more listeners can be added
	 * by calling <a href="#addListener">addListener</a>. The first snapshot will
	 * be made after delay milliseconds, with subsequent snapshots every
	 * period milliseconds.
	 * 
	 * @param prevayler the prevalent system to make snapshots of.
	 * @param delay delay in milliseconds before first snapshot is to be made.
	 * @param period time in milliseconds between successive snapshots.
	 */	
	public SnapshotScheduler (Prevayler prevayler, long delay, long period) {
		this (prevayler, delay, period, null);
	}

	/**
	 * Construct a SnapshotScheduler with the given listener. More listeners can be added
	 * by calling <a href="#addListener">addListerner</a>.
	 * 
	 * @param prevayler the prevalent system to make snapshots of.
	 * @param firstTime time when the first snapshot is to be made.
	 * @param period time in milliseconds between successive snapshots.
	 * @param listener If non-null, the listener will be called each time a snapshot has been made.
	 */	
	public SnapshotScheduler (Prevayler prevayler, Date firstTime, long period, Listener listener) {
		this.prevayler = prevayler;
		if (listener != null) {
			listenerList.add (listener);
		}
		timer = new Timer (false); // Don't make this a daemon thread!
		timer.scheduleAtFixedRate (this, firstTime, period);
	}

	/**
	 * Construct a SnapshotScheduler with the given listener. More listeners can be added
	 * by calling <a href="#addListener">addListerner</a>. The first snapshot will
	 * be made after delay milliseconds, with subsequent snapshots every
	 * period milliseconds.
	 * 
	 * @param prevayler the prevalent system to make snapshots of.
	 * @param delay delay in milliseconds before first snapshot is to be made.
	 * @param period time in milliseconds between successive snapshots.
	 * @param listener If non-null, the listener will be called each time a snapshot has been made.
	 */	
	public SnapshotScheduler (SnapshotPrevayler prevayler, long delay, long period, Listener listener) {
		this.prevayler = prevayler;
		if (listener != null) {
			listenerList.add (listener);
		}
		timer = new Timer (false); // Don't make this a daemon thread!
		timer.scheduleAtFixedRate (this, delay, period);
	}

	/** Make a snapshot. */
	public void run () {
		snapshotStarted ();

		try {
			
	         	prevayler.takeSnapshot();

		} catch (Exception e) {

			// This is likely a temporary problem, so keep running.
			snapshotException (e);

		} catch (Error e) {

			snapshotError (e);

			// This is likely fatal, so reassert. See Java documentation for classes Error and Throwable.
			throw e;
		}

		snapshotTaken ();
	}

	/**
	 * Stop making snapshots. If a snapshot is being made at the time of this call
	 * it will be completed, but no further snapshots will be made.
	 */
	public boolean cancel () {
		snapshotShutdown ();
		return super.cancel ();
	}

	/**
	 * To get informed when a Prevayler snapshot is made, implement this interface
	 * and add your listener either using the SnapshotScheduler constructor or using
	 * {@link SnapshotScheduler#addListener(SnapshotScheduler.Listener) addListener}.
	 */
	public interface Listener {
		/**
		 * SnapshotScheduler will start making a snapshot immediately
		 * after having informed all listeners.
		 * 
		 * @param prevayler the Prevayler system that this SnapshotScheduler is working on.
		 * @param prevaylerDate the date of the prevayler clock in milliseconds when the snapshot begins.
		 * @param systemDate the date of the system clock in milliseconds when the snapshot begins.
		 */
		public void snapshotStarted (Prevayler prevayler, long prevaylerDate, long systemDate);

		/**
		 * SnapshotScheduler has completed making a snapshot.
		 * 
		 * @param prevayler the Prevayler system that this SnapshotScheduler is working on.
		 * @param prevaylerDate the date of the prevayler clock in milliseconds when the snapshot was completed.
		 * @param systemDate the date of the system clock in milliseconds when the snapshot was completed.
		 */
		public void snapshotTaken (Prevayler prevayler, long prevaylerDate, long systemDate);

		/**
		 * An Exception has occured while making the snapshot.
		 * 
		 * @param prevayler the Prevayler system that this SnapshotScheduler is working on.
		 * @param exception the Exception. Note that since Java 1.4, this can contain a chain
		 * of linked causes, see Throwable.getCause() for more information.
		 * @param prevaylerDate the date of the prevayler clock in milliseconds when the Exception occured.
		 * @param systemDate the date of the system clock in milliseconds when the Exception occured.
		 * @see java.lang.Exception
		 */
		public void snapshotException (Prevayler prevayler, Exception exception, long prevaylerDate, long systemDate);

		/**
		 * An Error has occured while making the snapshot.
		 * Quoting from the documentation from Java class Error:
		 * "An Error is a subclass of Throwable that indicates serious problems
		 * that a reasonable application should not try to catch.
		 * Most such errors are abnormal conditions. [...]"
		 * 
		 * SnapshotScheduler will quit making snapshots whenever an Error occurs and die
		 * after having informed all listeners through this method.
		 *
		 * @param prevayler the Prevayler system that this SnapshotScheduler is working on.
		 * @param error the Error. Note that since Java 1.4, this can contain a chain
		 * of linked causes, see Throwable.getCause() for more information.
		 * @param prevaylerDate the date of the prevayler clock in milliseconds when the Error occured.
		 * @param systemDate the date of the system clock in milliseconds when the Error occured.
		 * @see java.lang.Error
		 */
		public void snapshotError (Prevayler prevayler, Error error, long prevaylerDate, long systemDate);

		/**
		 * SnapshotScheduler is shutting down after a call to cancel().
		 * 
		 * @param prevayler the Prevayler system that this SnapshotScheduler is working on.
		 * @param prevaylerDate the date of the prevayler clock in milliseconds when shutting down SnapshotScheduler.
		 * @param systemDate the date of the system clock in milliseconds when shutting down SnapshotScheduler.
		 * */
		public void snapshotShutdown (Prevayler prevayler, long prevaylerDate, long systemDate);
	}

	/**
	 * Remove the given listener from this SnapshotScheduler, no more
	 * notifications will occur to the given listener.
	 * 
	 * @param listener listener to remove.
	 */		
	public void removeListener (Listener listener) {
		listenerList.remove (listener);
	}

	/**
	 * Add the given listener to this SnapshotScheduler; the listener will be notified
	 * on the progress of making snapshots.
	 *
	 * @param listener listener to add.
	 */
	public void addListener (Listener listener) {
		listenerList.add (listener);
	}

	/** Inform all listeners that a new snapshot is about to be made immediately. */
	private void snapshotStarted () {
		long prevaylerDate = prevayler.system().clock().time().getTime();
		long systemDate = System.currentTimeMillis();
		
		Iterator i = listenerList.iterator();
		while (i.hasNext ()) {
			((Listener) i.next ()).snapshotStarted(prevayler, prevaylerDate, systemDate);
		}
	}

	/** Inform all interested listeners that a new snapshot has been made. */
	private void snapshotTaken () {
		long prevaylerDate = prevayler.system().clock().time().getTime();
		long systemDate = System.currentTimeMillis();

		Iterator i = listenerList.iterator();
		while (i.hasNext ()) {
			((Listener) i.next ()).snapshotTaken(prevayler, prevaylerDate, systemDate);
		}
	}

	/** Inform all listeners that an exception occured while making the snapshot. */
	private void snapshotException (Exception e) {
		long prevaylerDate = prevayler.system().clock().time().getTime();
		long systemDate = System.currentTimeMillis();

		Iterator i = listenerList.iterator();
		while (i.hasNext ()) {
			((Listener) i.next ()).snapshotException(prevayler, e, prevaylerDate, systemDate);
		}
	}
	
	/** Inform all listeners that an exception occured while making the snapshot. */
	private void snapshotError (Error e) {
		long prevaylerDate = prevayler.system().clock().time().getTime();
		long systemDate = System.currentTimeMillis();

		Iterator i = listenerList.iterator();
		while (i.hasNext ()) {
			((Listener) i.next ()).snapshotError(prevayler, e, prevaylerDate, systemDate);
		}
	}

	/** Inform all listeners that SnapshotScheduler is shutting down.  */
	private void snapshotShutdown () {
		long prevaylerDate = prevayler.system().clock().time().getTime();
		long systemDate = System.currentTimeMillis();

		Iterator i = listenerList.iterator();
		while (i.hasNext ()) {
			((Listener) i.next ()).snapshotShutdown(prevayler, prevaylerDate, systemDate);
		}
	}
}
