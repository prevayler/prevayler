/*
 * Copyright (c) 2003 Jay Sachs. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name "Prevayler" nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.prevayler.contrib.facade;

import java.util.*;

/**
 * Class CampGuide maintains a list of camp sites
 *
 * @author Jacob Kjome [hoju@visi.com]
 */
public class CampGuideImpl implements java.io.Serializable, CampGuide {
    
    static final long serialVersionUID = 0L;
    
    /** The list of camp sites */
    private Map campSites;
    private transient long transaction_time;

    /**
     * Creates a new camp guide
     */
    public CampGuideImpl() {
        campSites = new HashMap();
    }

    /**
     * Adds the given camp site to this camp guide
     */
    public void addCampSite(CampSite site) {
        stampCampSite(site);
        campSites.put(site.objectCode(), site);
    }

    public void updateCampSite(CampSite site) {
        if (!campSites.containsKey(site.objectCode())) throw new RuntimeException("Can't update non-existent camp site!  Try adding it first.");
        addCampSite(site);
    }
    
    /**
     * Removes the given cam site from this camp guide
     */
    public void removeCampSite(CampSite site) {
        campSites.remove(site.objectCode());
    }

    public CampSite getCampSite(String objectCode) {
        return (CampSite) campSites.get(objectCode);
    }
    
    /**
     * Returns all camp sites in this camp guide
     * 
     * @return an unmodifiable set of camp sites
     */
    public Set getCampSites() {
        return Collections.unmodifiableSet(new HashSet(campSites.values()));
    }
    
    /**
     * Set the current timestamp here in order for
     * objects to get stamped as they are added to the
     * prevalent system.  To work properly, this should be
     * called from within a transaction, before the object
     * is added to the system.  The value is not only
     * transient, but also is reset to zero immediately
     * after usage, so there should be no expectation of
     * persisting this timestamp across transactions.  The
     * value is for internal use anyway as there is no
     * accessor method.
     * 
     * @param timestamp
     */
    public void setTransactionTime(Date timestamp) {
        if (timestamp != null) {
            transaction_time = timestamp.getTime();
        }
    }
    
    private void stampCampSite(CampSite site) {
        if (this.transaction_time != 0) {
            site.setTimestamp(this.transaction_time);
            this.transaction_time = 0; //reset to zero after operation is done
        }
    }
}
