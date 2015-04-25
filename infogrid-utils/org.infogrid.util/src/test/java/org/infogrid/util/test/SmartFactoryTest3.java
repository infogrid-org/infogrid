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
 * Tests the WeakReference behavior of the MSmartFactory.
 */
public class SmartFactoryTest3
        extends
            AbstractTest
{
    @Test
    public void run()
        throws
            Exception
    {
        Factory<String,Foo,Integer> delegateFactory = new AbstractFactory<String,Foo,Integer>() {
            public Foo obtainFor(
                    String  key,
                    Integer argument )
            {
                int value = argument.intValue();
                return new Foo( value*value );
            }
        };
        
        MSmartFactory<String,Foo,Integer> testFactory = MSmartFactory.createWeakReference( delegateFactory );

        SwappingHashMapTestListener listener = null;
        listener = new SwappingHashMapTestListener(); // on separate line so it can be commented out easily

        if( listener != null ) {
            testFactory.getStorage().addDirectCachingMapListener( listener );
        }

        Assert.assertTrue("testFactory is not empty", testFactory.isEmpty() );

        //
        
        log.info( "Creating a few objects and keeping references to them" );
        
        int n1=10;
        Foo [] results1 = new Foo[ n1 ];

        for( int i=0 ; i<n1 ; ++i ) {
            results1[i] = testFactory.obtainFor( "key-obtainFor-" + String.valueOf( i ), i );
        }
        
        collectGarbage();

        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               n1);
        if( listener != null ) {
            Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),   n1);
            Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),  0);
            Assert.assertEquals("wrong number of expired events", listener.theExpiredEvents.size(),  0);
            listener.clear();
        }

        //
        
        log.info( "Removing the reference to a few" );
        
        results1[3] = null;
        results1[7] = null;
        
        collectGarbage();

        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               n1-2);
        if( listener != null ) {
            Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),    0);
            Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),  0);
            Assert.assertEquals("wrong number of expired events", listener.theExpiredEvents.size(),  2);
            listener.clear();
        }

        //
        
        log.info( "Setting all results to null" );
        results1 = null;
        
        collectGarbage();
        
        Assert.assertEquals("wrong size of testFactory", testFactory.size(),               0);
        if( listener != null ) {
            Assert.assertEquals("wrong number of added events", listener.theAddedEvents.size(),      0);
            Assert.assertEquals("wrong number of removed events", listener.theRemovedEvents.size(),    0);
            Assert.assertEquals("wrong number of expired events", listener.theExpiredEvents.size(), n1-2);
            listener.clear();
        }
    }

    private static final Log log = Log.getLogInstance( SmartFactoryTest3.class ); // our own, private logger
    
    /**
     * Test class. Like Integer.
     */
    public static class Foo
    {
        /**
         * Constructor.
         *
         * @param payload the payload
         */
        public Foo(
                int payload )
        {
            thePayload = payload;
        }
        
        /**
         * Obtain the payload
         *
         * @return the payload
         */
        public int intValue()
        {
            return thePayload;
        }
        
        /**
         * Override finalize for easier debugging.
         */
        @Override
        public void finalize()
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Foo( " + thePayload + " ) was just finalized." );
            }
        }
        /**
         * The payload.
         */
        protected int thePayload;
    }
}
