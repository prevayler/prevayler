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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


/**
 * Class CampSite describes a camp site
 *
 * @author Jacob Kjome [hoju@visi.com]
 */
public class CampSite implements Serializable {
    
    static final long serialVersionUID = 0L;
    
    /** Constant used to indicate that an item is not available */
    public static final int NOT_AVAILABLE = Integer.MAX_VALUE;

    /** Constant used to indicate that an item is available on the 
        camp site */
    public static final int ON_SITE = 0;
    
    /** The name of the camp site */
    private String name;

    /** The distance to the nearest playground from this camp site */
    private int playground;

    /** The distance to the nearest supermarket from this camp site */
    private int supermarket;

    /** The distance to the nearest swimming pool from this camp site */
    private int swimmingPool;

    /** The distance to the nearest discotheque from this camp site */
    private int disco;

    /** An indication of how quiet the campsite is. Value between 0
        (noisy) and 3 (completely quiet), both inclusive */
    private int quiet;

    /** An indication of how beatiful the view from the campsite is. 
        Value between 0 (nothing to see) and 3 (paradise on earth), 
        both inclusive */
    private int beauty;

    /** The size of the camp site, the number of locations */
    private int size;

    /** A price indication of this camp site */
    private int price;

    private long created;
    
    private long updated;

    private final String guid;
    
    private Calendar someDate;
    
    public String objectCode() {
        return this.guid;
    }
    
    private CampSite() {
        guid = new RandomGUID(true).toString();
    }
    
    /**
     * Create a new camp site, with the given name
     */
    public CampSite(String name) {
        this();
        this.name = name;
    }

    /** 
     * Returns the name of the camp site 
     */
    public String getName() {
        return name;
    }

    /** 
     * Sets the name of the camp site 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**  
     * Returns the distance to the nearest playground from this camp 
     * site. 
     */
    public int getDistanceToPlayground() {
        return playground;
    }

    /**  
     * Sets the distance to the nearest playground from this camp site. 
     */
    public void setDistanceToPlayground(int playground) {
        this.playground = playground;
    }

    /**  
     * Returns the distance to the nearest supermarket from this camp 
     * site 
     */
    public int getDistanceToSupermarket() {
        return supermarket;
    }

    /**  
     * Sets the distance to the nearest supermarket from this camp site 
     */
    public void setDistanceToSupermarket(int supermarket) {
        this.supermarket = supermarket;
    }

    /**  
     * Returns the distance to the nearest swimming pool from this camp 
     * site 
     */
    public int getDistanceToSwimmingPool() {
        return swimmingPool;
    }

    /**  
     * Sets the distance to the nearest swimming pool from this camp 
     * site 
     */
    public void setDistanceToSwimmingPool(int swimmingPool) {
        this.swimmingPool = swimmingPool;
    }

    /**  
     * Returns the distance to the nearest discotheque from this camp 
     * site 
     */
    public int getDistanceToDisco() {
        return disco;
    }

    /**  
     * Sets the distance to the nearest discotheque from this camp site 
     */
    public void setDistanceToDisco(int disco) {
        this.disco = disco;
    }

    /**  
     * Returns the indication of how quiet the campsite is. Value 
     * between 0 (extremely noisy) and 3 (completely quiet), both 
     * inclusive 
     */
    public int getQuietIndication() {
        return quiet;
    }

    /**  
     * Sets the indication of how quiet the campsite is. Value between 
     * 0 (extremely noisy) and 3 (completely quiet), both inclusive 
     */
    public void setQuietIndication(int quiet) {
        this.quiet = quiet;
    }

    /**  
     * Returns the indication of how beatiful the view from the campsite 
     * is. Value between 0 (nothing to see) and 3 (paradise on earth), 
     * both inclusive 
     */
    public int getBeautyIndication() {
        return beauty;
    }

    /**  
     * Sets the indication of how beatiful the view from the campsite 
     * is. Value between 0 (nothing to see) and 3 (paradise on earth), 
     * both inclusive 
     */
    public void setBeautyIndication(int beauty) {
        this.beauty = beauty;
    }

    /**  
     * Returns the size of the camp site, the number of locations 
     */
    public int getSize() {
        return size;
    }

    /**  
     * Sets the size of the camp site, the number of locations 
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**  
     * Returns a price indication of this camp site 
     */
    public int getPriceIndication() {
        return price;
    }

    /**  
     * Sets a price indication of this camp site 
     */
    public void setPriceIndication(int price) {
        this.price = price;
    }

    /**
     * Sets both created and updated fields as required.  There is no need
     * to call this method outside of a Transaction in a Prevalent system,
     * especially since it will simply be overwritten by the Transaction.
     * The prevalent system, which should be in the same package, should set
     * this.  It is package protected, so that's the only way it would work
     * anyway.
     *
     * @param timestamp the current Date
     */
    void setTimestamp(long timestamp) {
        if (this.created == 0) this.created = timestamp;
        this.updated = timestamp;
    }
    
    /**
     * The date the current object was initially prevayled
     * (see {@link #setTimestamp}).  Returned objects are defensively copied to
     * avoid accidental mutation.
     *
     * @return a new Date object or null if not yet set
     */
    public Date getCreated() {
        return (this.created != 0) ? new Date(this.created) : null;
    }

    /**
     * The date the current object was updated.  Same as created date if not
     * actually updated at a later time.
     *
     * @return a new Date object or null if not yet set
     * @see #getCreated
     */
    public Date getUpdated() {
        return (this.updated != 0) ? new Date(this.updated) : null;
    }
    
	private String getDistanceString(int distance) {
		if (distance == NOT_AVAILABLE) {
			return "-";
		} else {
			return Integer.toString(distance);
		}
	}

    /**
     * @param someDate The someDate to set.
     */
    public void setSomeDate(Calendar someDate) {
        this.someDate = someDate;
    }

    /**
     * @return Returns the someDate.
     */
    public Calendar getSomeDate() {
        return someDate;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CampSite:");
        buffer.append(" serialVersionUID: ");
        buffer.append(serialVersionUID);
        buffer.append(" NOT_AVAILABLE: ");
        buffer.append(NOT_AVAILABLE);
        buffer.append(" ON_SITE: ");
        buffer.append(ON_SITE);
        buffer.append(" name: ");
        buffer.append(name);
        buffer.append(" playground: ");
        buffer.append(playground);
        buffer.append(" supermarket: ");
        buffer.append(supermarket);
        buffer.append(" swimmingPool: ");
        buffer.append(swimmingPool);
        buffer.append(" disco: ");
        buffer.append(disco);
        buffer.append(" quiet: ");
        buffer.append(quiet);
        buffer.append(" beauty: ");
        buffer.append(beauty);
        buffer.append(" size: ");
        buffer.append(size);
        buffer.append(" price: ");
        buffer.append(price);
        buffer.append(" created: ");
        buffer.append(created);
        buffer.append(" updated: ");
        buffer.append(updated);
        buffer.append(" guid: ");
        buffer.append(guid);
        buffer.append(" someDate: ");
        buffer.append(someDate);
        buffer.append("]");
        return buffer.toString();
    }
    /**
     * Returns <code>true</code> if this <code>CampSite</code> is the same as the o argument.
     *
     * @return <code>true</code> if this <code>CampSite</code> is the same as the o argument.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        CampSite castedObj = (CampSite) o;
        return ((this.name == null ? castedObj.name == null : this.name
            .equals(castedObj.name))
            && (this.playground == castedObj.playground)
            && (this.supermarket == castedObj.supermarket)
            && (this.swimmingPool == castedObj.swimmingPool)
            && (this.disco == castedObj.disco)
            && (this.quiet == castedObj.quiet)
            && (this.beauty == castedObj.beauty)
            && (this.size == castedObj.size)
            && (this.price == castedObj.price)
            && (this.created == castedObj.created)
            && (this.updated == castedObj.updated)
            && (this.guid == null ? castedObj.guid == null : this.guid
                .equals(castedObj.guid)) && (this.someDate == null
            ? castedObj.someDate == null
            : this.someDate.equals(castedObj.someDate)));
    }
    /**
     * Override hashCode.
     *
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31
            * hashCode
            + (int) (+serialVersionUID ^ (serialVersionUID >>> 32));
        hashCode = 31 * hashCode + NOT_AVAILABLE;
        hashCode = 31 * hashCode + ON_SITE;
        hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = 31 * hashCode + playground;
        hashCode = 31 * hashCode + supermarket;
        hashCode = 31 * hashCode + swimmingPool;
        hashCode = 31 * hashCode + disco;
        hashCode = 31 * hashCode + quiet;
        hashCode = 31 * hashCode + beauty;
        hashCode = 31 * hashCode + size;
        hashCode = 31 * hashCode + price;
        hashCode = 31 * hashCode + (int) (+created ^ (created >>> 32));
        hashCode = 31 * hashCode + (int) (+updated ^ (updated >>> 32));
        hashCode = 31 * hashCode + (guid == null ? 0 : guid.hashCode());
        hashCode = 31 * hashCode + (someDate == null ? 0 : someDate.hashCode());
        return hashCode;
    }
}
