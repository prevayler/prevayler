// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler;

import java.io.Serializable;
import java.util.Date;

/** A clock used in the PrevalentSystem for ALL its date/time related functions.
* The next version of AlarmClock will be able to wake up other objects in a similar way as javax.swing.Timer or java.util.Timer.
*/
public interface AlarmClock extends Serializable {

    /** Returns the time.
    * @return A Date greater or equal to the one returned by the last call to this method. If the time is the same as the last call, the SAME Date object will be returned and not a new, equal one.
    */
    Date time();
}
