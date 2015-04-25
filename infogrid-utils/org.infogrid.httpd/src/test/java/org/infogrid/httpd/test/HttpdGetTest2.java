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

package org.infogrid.httpd.test;

import org.infogrid.util.http.HTTP;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests multithreaded HTTP GETs.
 */
public class HttpdGetTest2
        extends
            AbstractHttpdTest
{
    @Test
    public void run()
            throws
                Exception
    {
        Thread [] t = new Thread[ THREADS ];
        for( int i=0 ; i<t.length ; ++i ) {
            t[i] = new Thread( new MyRunnable( i ));
            t[i].start();
        }

        while( true ) {
            Thread.sleep( 1000L );
            boolean cont = false;
            for( int i=0 ; i<t.length ; ++i ) {
                if( t[i].isAlive() ) {
                    cont = true;
                    break;
                }
            }
            if( !cont ) {
                break;
            }
        }
    }

    /**
     * The number of concurrent threads.
     */
    protected static final int THREADS = 20;

    /**
     * The Runnable for the Threads.
     */
    class MyRunnable
        implements
            Runnable
    {
        public MyRunnable(
                int threadIndex )
        {
            theThreadIndex = threadIndex;
        }

        public void run()
        {
            String url = "http://localhost:" + SERVER_PORT + "/";

            for( int i=0 ; i<100 ; ++i ) {
                log.info( "Performing test " + theThreadIndex + "/" + i );

                try {
                    HTTP.Response r = HTTP.http_get( url );

                    if( r != null ) {
                        Assert.assertEquals( "Wrong response code",   r.getResponseCode(), "200" );
                        Assert.assertEquals( "Wrong Location header", r.getLocation(),     null );
                        Assert.assertEquals( "Wrong MIME type",       r.getContentType(),  "text/plain" );
                    } else {
                        Assert.fail( "Null response" );
                    }
                } catch( Exception ex ) {
                    Assert.fail( "Thread " + theThreadIndex + " threw exception: " + ex );
                }
            }
            log.info( "Thread " + theThreadIndex + " is done." );
        }

        protected int theThreadIndex;
    }
}
