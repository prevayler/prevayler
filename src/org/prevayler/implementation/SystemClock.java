// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import org.prevayler.*;
import java.util.Date;

/**
 * An AlarmClock that uses the local system clock as its current-time source.
 * This class can be extended so that other time sources can be used.
 * @see #currentTimeMillis()
 */
public class SystemClock implements AlarmClock {

  private Date time;
  private long millis;
  private boolean isPaused = true;


  /** 
   * A newly created SystemClock starts off paused with time() equal to new Date(Long.MIN_VALUE).
   */
  public SystemClock() {
    set(new Date(Long.MIN_VALUE));
  }


  public synchronized Date time() {
    if (isPaused) return time;

    long currentMillis = currentTimeMillis();
    if (currentMillis != millis) {
      set(new Date(currentMillis));
    }

    return time;
  }


  /**
   * Causes time() to return always the same value as if the clock had stopped.
   * The clock does NOT STOP internally though. This method is called by Prevayler before each Command is executed so that it can be executed in a known moment in time.
   * @see #resume()
   */
  synchronized void pause() {
    if (isPaused) throw new IllegalStateException("AlarmClock was already paused.");

    time();           //Guarantees the time is up-to-date.
    isPaused = true;
  }


  /**
   * Causes time() to return the current time again. This method is called by Prevayler after each Command is executed so that the clock can start running again.
   * @see #pause()
   */
  synchronized void resume() {
    if (!isPaused) throw new IllegalStateException("AlarmClock was not paused.");

    isPaused = false;
  }


  /**
   * Sets the time forward, recovering some of the time that was "lost" since the clock was paused. The clock must be paused. This method is called by Prevayler when recovering commands from the commandLog file so that they can be re-executed in the "same" time as they had been originally.
   * @param newMillis the new time in milliseconds. Cannot be earlier than time().getTime() and cannot be later than currentTimeMillis().
   */
  synchronized void recover(long newMillis) {
    if (!isPaused) throw new IllegalStateException("AlarmClock must be paused for recovering.");

    if (newMillis == millis) return;
    if (newMillis < millis) throw new IllegalArgumentException("AlarmClock's time cannot be set backwards.");
    if (newMillis > currentTimeMillis()) throw new IllegalArgumentException("AlarmClock's time cannot be set after the current time.");

    set(new Date(newMillis));
  }


  /**
   * Returns System.currentTimeMillis(). Override this method if you want to use a different time source for your system.
   */
  protected long currentTimeMillis() {
    return System.currentTimeMillis();
  }


  private void set(Date time) {
    this.time = time;
    this.millis = time.getTime();
  }

}
