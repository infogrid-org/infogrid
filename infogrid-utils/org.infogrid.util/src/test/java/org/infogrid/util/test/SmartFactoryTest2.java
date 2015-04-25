//
// This file is part of InfoGrid(tm). You may not use this file except in
// compliance with the InfoGrid license. The InfoGrid license and important
// disclaimers are contained in the file LICENSE.InfoGrid.txt that you should
// have received with InfoGrid. If you have not received LICENSE.InfoGrid.txt
// or you do not consent to all aspects of the license and the disclaimers,
// no license is granted; do not use this file.
// 
// For more information about InfoGrid go to http://infogrid.org/
//
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.test;

import org.infogrid.util.AbstractFactory;
import org.infogrid.util.Factory;
import org.infogrid.util.MSmartFactory;
import org.infogrid.util.logging.Log;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the event generation of the MSmartFactory.
 */
public class SmartFactoryTest2
        extends
            AbstractTest
{
    @Test
    public void run()
        throws
            Exception
    {
        Factory<String,Integer,Integer> delegateFactory = new AbstractFactory<String,Integer,Integer>() {
            public Integer obtainFor(
                    String  key,
                    Integer argument )
            {
                int value = argument.intValue();
                return value*value;
            }
        };
        
        MSmartFactory<String,Integer,Integer> testFactory = MSmartFactory.createDirect( delegateFactory );
        
        SwappingHashMapTestListener listener = new SwappingHashMapTestListener();
        testFactory.getStorage().addDirectCachingMapListener( listener );
        
        Assert.assertTrue("testFactory is not empty", testFactory.isEmpty() );

        //
        
        log.info( "Creating a few objects" );
        
        int n1=10;
        
        for( int i=0 ; i<n1 ; ++i ) {
            int value = testFactory.obtainFor( "key-obtainFor-" + String.valueOf( i ), i );
        }

        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               n1);
        Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),   n1);
        Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),  0);
        listener.clear();

        //
        
        log.info( "Inserting objects directly" );
        
        int n2 = 20;
        
        for( int i=0 ; i<n2 ; ++i ) {
            testFactory.put( "key-put-" + String.valueOf( i ), i );
        }
        
        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               n1+n2);
        Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),      n2);
        Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),     0);
        listener.clear();

        Assert.assertTrue("testFactory is empty", !testFactory.isEmpty() );
 
        //
        
        log.info( "Removing some objects" );
        
        int n3 = 5;
        
        for( int i=0 ; i<n3 ; ++i ) {
            testFactory.remove( "key-obtainFor-" + String.valueOf( i ));
            testFactory.remove( "key-put-" + String.valueOf( i+5 ));
        }
        
        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               n1 + n2 - 2*n3);
        Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),                0);
        Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),           2*n3);
        listener.clear();
        
    }

    private static final Log log = Log.getLogInstance( SmartFactoryTest2.class ); // our own, private logger
}
