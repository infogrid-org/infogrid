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
 * Tests HTTP GETs.
 */
public class HttpdGetTest1
        extends
            AbstractHttpdTest
{
    @Test
    public void run()
            throws
                Exception
    {
        String url = "http://localhost:" + SERVER_PORT + "/";

        for( int i=0 ; i<100 ; ++i ) {
            log.info( "Performing test " + i );

            HTTP.Response r = HTTP.http_get( url );

            if( r != null ) {
                Assert.assertEquals( "Wrong response code",   r.getResponseCode(),    "200" );
                Assert.assertEquals( "Wrong Location header", r.getLocation(),        null );
                Assert.assertEquals( "Wrong MIME type",       r.getContentType(),     "text/plain" );
                Assert.assertEquals( "Wrong response",        r.getContentAsString(), String.valueOf( i ) );
            } else {
                Assert.fail( "Null response" );
            }
        }
    }
}
