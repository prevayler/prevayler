// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler;

import java.io.Serializable;
import java.util.Date;

/** A clock that can be used in a PrevalentSystem for ALL its date/time related functions.
* The next version of AlarmClock will be able to wake up other objects in a similar way as javax.swing.Timer or java.util.Timer.
*/
public class AlarmClock implements Serializable {

    /** A newly created AlarmClock starts off paused.
    */
    public AlarmClock() {
        set(new Date(Long.MIN_VALUE));
    }

    /** Returns the time.
    * @return If the time is the same as the last call, the SAME Date object will be returned and not a new, equal one.
    */
    public synchronized Date time() {
        if (isPaused) return time;

        if (currentTimeMillis() != millis) {
            set(new Date(currentTimeMillis()));
        }

        return time;
    }

    /** Sets the time forward, recovering some of the time that was "lost" since the clock was paused. The clock must be paused. This method is called by the PrevaylerFactory when recovering Commands from the log file so that they can be re-executed in the "same" time as they had been originally.
    * @param newMillis cannot be earlier than time().getTime() and cannot be later than the current machine time (new Date().getTime() or System.currentTimeMillis()).
    */
    synchronized void recover(long newMillis) {
        if (!isPaused) throw new RuntimeException("AlarmClock must be paused for recovering.");

        if (newMillis == millis) return;
        if (newMillis < millis) throw new RuntimeException("AlarmClock's time cannot move forwards.");
        if (newMillis > currentTimeMillis()) throw new RuntimeException("AlarmClock's time cannot be set after the current time.");

        set(new Date(newMillis));
    }

    /** Causes time() to return always the same value as if the clock had stopped.
    * The clock does NOT STOP internally though. This method is called by Prevayler before each Command is executed so that it can be executed in a known moment in time.
    * @see resume()
    */
    synchronized void pause() {
        if (isPaused) throw new RuntimeException("AlarmClock was already paused.");

        time();           //Guarantees the time is up-to-date.
        isPaused = true;
    }

    /** Causes time() to return the actual current time again. This method is called by Prevayler after each Command is executed so that the clock can run again.
    * @see pause()
    */
    synchronized void resume() {
        if (!isPaused) throw new RuntimeException("AlarmClock was not paused.");

        isPaused = false;
    }

    /** Returns the current system time. Override this method if you want to use a different time source.
    */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void set(Date time) {
        this.time = time;
        this.millis = time.getTime();
    }

    private Date time;
    private long millis;
    private boolean isPaused = true;
}
