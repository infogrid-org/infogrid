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
 * Tests the basic behavior of the MSimpleSmartFactory.
 */
public class SmartFactoryTest1
        extends
            AbstractTest
{
    @Test
    public void run()
        throws
            Exception
    {
        Factory<String,Integer,Float> delegateFactory = new AbstractFactory<String,Integer,Float>() {
            public Integer obtainFor(
                    String key,
                    Float  argument )
            {
                int value = argument.intValue();
                return value;
            }
        };
        
        MSmartFactory<String,Integer,Float> testFactory = MSmartFactory.createDirect( delegateFactory );
        
        String [] testKeys1 = {
            "abc",
            "def",
            "ghiklm"
        };
        Float [] testArgs1 = {
            1.f,
            2.f,
            3.4f
        };
        
        //
        
        log.info( "Creating a few objects" );

        Integer [] values1 = new Integer[ testKeys1.length ];
        for( int i=0 ; i<testKeys1.length ; ++i ) {
            values1[i] = testFactory.obtainFor( testKeys1[i], testArgs1[i] );
            
            Assert.assertEquals("Not the same", testArgs1[i].intValue(), values1[i].intValue());
        }
        Assert.assertEquals("wrong number of objects in factory", testFactory.size(), testKeys1.length);

        //
        
        log.info( "Adding a few objects manually" );
        
        String [] testKeys2 = {
            "zztop",
            "aabottom",
            testKeys1[2]
        };
        Integer [] testValues2 = {
            5,
            6,
            7
        };
        Integer [] testValues2Return = {
            null,
            null,
            testArgs1[2].intValue()
        };
        Integer [] testValues2Actual = new Integer[ testValues2Return.length ];

        for( int i=0 ; i<testKeys2.length ; ++i ) {
            testValues2Actual[i] = testFactory.put( testKeys2[i], testValues2[i] );
            
            Assert.assertEquals("Not the same", testValues2Return[i], testValues2Actual[i]);
        }
        Assert.assertEquals("wrong number of objects in factory", testFactory.size(), testKeys1.length + testKeys2.length - 1);
        
        //
        
        log.info( "looking up objects" );
        
        for( int i=0 ; i<testKeys1.length-1 /* skip the last one */ ; ++i ) {
            Integer ret = testFactory.get( testKeys1[i] );
        
            Assert.assertEquals("not the same", ret, values1[i]);
        }
        for( int i=0 ; i<testKeys2.length ; ++i ) {
            Integer ret = testFactory.get( testKeys2[i] );
        
            Assert.assertEquals("not the same", ret, testValues2[i]);
        }
        
        //

        log.info( "removing a few" );
        
        Integer ret = testFactory.remove( testKeys1[1] );
        Assert.assertEquals("not the same", ret, values1[1]);
        
        ret = testFactory.remove( testKeys2[1] );
        Assert.assertEquals("not the same", ret, testValues2[1]);
        Assert.assertEquals("wrong number of objects in factory", testFactory.size(), testKeys1.length + testKeys2.length - 1 -2);
    }

    private static final Log log = Log.getLogInstance( SmartFactoryTest1.class ); // our own, private logger
}
