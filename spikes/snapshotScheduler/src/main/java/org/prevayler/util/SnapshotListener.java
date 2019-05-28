package org.prevayler.util;

import org.prevayler.Prevayler;

/**
 * To get informed when a Prevayler snapshot is made, implement this interface
 * and add your listener either using the SnapshotScheduler constructor or using
 * {@link SnapshotScheduler#addListener(SnapshotListener) addListener}.
 */
public interface SnapshotListener<P> {
  /**
   * SnapshotScheduler will start making a snapshot immediately
   * after having informed all listeners.
   *
   * @param prevayler     the Prevayler system that this SnapshotScheduler is working on.
   * @param prevaylerDate the date of the prevayler clock in milliseconds when the snapshot begins.
   * @param systemDate    the date of the system clock in milliseconds when the snapshot begins.
   */
  public void snapshotStarted(Prevayler<P> prevayler, long prevaylerDate, long systemDate);

  /**
   * SnapshotScheduler has completed making a snapshot.
   *
   * @param prevayler     the Prevayler system that this SnapshotScheduler is working on.
   * @param prevaylerDate the date of the prevayler clock in milliseconds when the snapshot was completed.
   * @param systemDate    the date of the system clock in milliseconds when the snapshot was completed.
   */
  public void snapshotTaken(Prevayler<P> prevayler, long prevaylerDate, long systemDate);

  /**
   * An Exception has occured while making the snapshot.
   *
   * @param prevayler     the Prevayler system that this SnapshotScheduler is working on.
   * @param exception     the Exception. Note that since Java 1.4, this can contain a chain
   *                      of linked causes, see Throwable.getCause() for more information.
   * @param prevaylerDate the date of the prevayler clock in milliseconds when the Exception occured.
   * @param systemDate    the date of the system clock in milliseconds when the Exception occured.
   * @see java.lang.Exception
   */
  public void snapshotException(Prevayler<P> prevayler, Exception exception, long prevaylerDate, long systemDate);

  /**
   * An Error has occured while making the snapshot.
   * Quoting from the documentation from Java class Error:
   * "An Error is a subclass of Throwable that indicates serious problems
   * that a reasonable application should not try to catch.
   * Most such errors are abnormal conditions. [...]"
   * <p/>
   * SnapshotScheduler will quit making snapshots whenever an Error occurs and die
   * after having informed all listeners through this method.
   *
   * @param prevayler     the Prevayler system that this SnapshotScheduler is working on.
   * @param error         the Error. Note that since Java 1.4, this can contain a chain
   *                      of linked causes, see Throwable.getCause() for more information.
   * @param prevaylerDate the date of the prevayler clock in milliseconds when the Error occured.
   * @param systemDate    the date of the system clock in milliseconds when the Error occured.
   * @see java.lang.Error
   */
  public void snapshotError(Prevayler<P> prevayler, Error error, long prevaylerDate, long systemDate);

  /**
   * SnapshotScheduler is shutting down after a call to cancel().
   *
   * @param prevayler     the Prevayler system that this SnapshotScheduler is working on.
   * @param prevaylerDate the date of the prevayler clock in milliseconds when shutting down SnapshotScheduler.
   * @param systemDate    the date of the system clock in milliseconds when shutting down SnapshotScheduler.
   */
  public void snapshotShutdown(Prevayler<P> prevayler, long prevaylerDate, long systemDate);
}