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

import java.util.Calendar;
import java.util.Iterator;

import junit.framework.TestCase;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.XStreamSerializer;

/**
 * @author Jacob Kjome [hoju@visi.com]
 */
public class AnotherTest extends TestCase {
    
    private Calendar date;
    
    public AnotherTest() {
        date = Calendar.getInstance();
        date.setTimeInMillis(23123);
    }
    
    public void testCampGuideSystem() throws Exception {
        CampGuide guide = (CampGuide)
        PrevaylerTransactionsFacade.create
            (CampGuide.class,
             PrevaylerFactory.createTransientPrevayler(new CampGuideImpl()),
             new SimpleTransactionTypeDeterminer(),
             new CampGuideTransactionHint());
        
        addSite(guide, "Hikers'delight", CampSite.ON_SITE, 300, 100, CampSite.NOT_AVAILABLE, 2, 3, 15, 7);
        addSite(guide, "Mountain view", CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, 3, 3, 30, 6);
        addSite(guide, "Middle of Nowhere", CampSite.ON_SITE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, 3, 1, 15, 4);
        addSite(guide, "Belle air", CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, 2, 3, 40, 4);
        addSite(guide, "Lakeview", CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, CampSite.NOT_AVAILABLE, 3, 3, 25, 6);
        addSite(guide, "Wild waters", CampSite.ON_SITE, 500, 0, CampSite.NOT_AVAILABLE, 2, 2, 55, 7);
        addSite(guide, "OpenAir Sports center", CampSite.ON_SITE, CampSite.ON_SITE, 0, 300, 1, 0, 90, 13);
        addSite(guide, "Central city campsite", 100, 100, 400, 100, 0, 1, 70, 10);
        addSite(guide, "Highhills outdoor resort", CampSite.ON_SITE, 200, 0, CampSite.NOT_AVAILABLE, 1, 3, 120, 12);
        addSite(guide, "Fred's Family Farm", CampSite.ON_SITE, 150, 0, 100, 2, 2, 120, 18);
        addSite(guide, "Holiday heaven", CampSite.ON_SITE, 200, 0, 0, 1, 2, 150, 18);
        addSite(guide, "Summer city", 200, CampSite.ON_SITE, 0, 0, 0, 1, 720, 29);
        addSite(guide, "Sunny sands", CampSite.ON_SITE, CampSite.ON_SITE, 0, 200, 1, 1, 300, 34);
        addSite(guide, "Surf-n-sleep", CampSite.ON_SITE, CampSite.ON_SITE, 0, 0, 0, 1, 550, 30);
        addSite(guide, "Dance and dream", 50, CampSite.ON_SITE, 0, 0, 1, 1, 640, 32);
        addSite(guide, "Sunset beach resort", CampSite.ON_SITE, CampSite.ON_SITE, 0, 0, 0, 1, 800, 35);
        
        assertEquals("Should have been 16 camp sites", new Integer(16), new Integer(guide.getCampSites().size()));
        
        int i = 16;
        for (Iterator iter = guide.getCampSites().iterator(); iter.hasNext();) {
            CampSite site = (CampSite) iter.next();
            //System.out.println(site);
            site.setDistanceToSupermarket(50);
            //System.out.println("updated: " + site.getUpdated().getTime());
            //for (int b=100000000; b > 0; b--) {}
            guide.updateCampSite(site);
            //System.out.println("updated: " + guide.getCampSite(site.objectCode()).getUpdated().getTime());
            assertEquals("object was updated, but created date should have stayed the same", new Long(site.getCreated().getTime()), new Long(guide.getCampSite(site.objectCode()).getCreated().getTime()));
            assertFalse("object was updated, so time should have been different", new Long(site.getUpdated().getTime()).equals(new Long(guide.getCampSite(site.objectCode()).getUpdated().getTime())));
            guide.removeCampSite(site);
            assertNull("camp site removed, so should have been null", guide.getCampSite(site.objectCode()));
            assertEquals("Should have been "+ --i +" camp sites", new Integer(i), new Integer(guide.getCampSites().size()));
        }
        assertEquals("Should have been 0", new Integer(0), new Integer(guide.getCampSites().size()));
        
        for (int j = 0; j < guide.getCampSites().size(); j++) {
            //System.out.println(((CampSite)guide.getCampSites().get(i)).getUpdated());
        }
        
    }

    public void testTheBaptismProblem() throws Exception {
        PrevaylerFactory factory = new PrevaylerFactory();
        factory.configureJournalSerializer(new XStreamSerializer());
        factory.configureSnapshotSerializer(new XStreamSerializer());
        factory.configureSnapshotSerializer(new JavaSerializer());
        factory.configurePrevalentSystem(new CampGuideImpl());
        Prevayler prevayler = factory.create();
        CampGuide guide = (CampGuide)
        PrevaylerTransactionsFacade.create
            (CampGuide.class,
             prevayler,
             new SimpleTransactionTypeDeterminer(),
             new CampGuideTransactionHint());
        
        if (guide.getCampSites().size() == 0) {
            System.out.println("adding single site for testing");
            addSite(guide, "Hikers'delight", CampSite.ON_SITE, 300, 100, CampSite.NOT_AVAILABLE, 2, 3, 15, 7);
        }
                
        for (Iterator iter = guide.getCampSites().iterator(); iter.hasNext();) {
            CampSite site = (CampSite) iter.next();
            assertTrue("Baptism issue! Transaction doesn't reflect upated value", date.getTimeInMillis() == site.getSomeDate().getTimeInMillis());
            System.out.println(site.getSomeDate().getTime());
            date.add(Calendar.DATE, 1); //move forward a day
            site.setSomeDate(date);
            guide.updateCampSite(site);
            System.out.println(site.getSomeDate().getTime());
            date.add(Calendar.DATE, -1); //move back to standard original date
            site.setSomeDate(date);
            guide.updateCampSite(site);
            System.out.println(site.getSomeDate().getTime());
        }

    }
    
    /**
     * Adds a camp site with the given features to the given camp guide
     */
    private void addSite(CampGuide guide, String name, 
        int playground, int supermarket, int swimmingPool, int disco, 
        int quiet, int beauty, int size, int price) {
        CampSite site = new CampSite(name);
        site.setDistanceToPlayground(playground);
        site.setDistanceToSupermarket(supermarket);
        site.setDistanceToSwimmingPool(swimmingPool);
        site.setDistanceToDisco(disco);
        site.setQuietIndication(quiet);
        site.setBeautyIndication(beauty);
        site.setSize(size);
        site.setPriceIndication(price);
        site.setSomeDate(date);
        guide.addCampSite(site);
        //System.out.println(site.getUpdated());
    }

}
