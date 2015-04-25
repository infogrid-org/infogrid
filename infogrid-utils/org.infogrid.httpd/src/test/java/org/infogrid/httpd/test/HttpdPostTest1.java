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

import java.util.Iterator;
import org.infogrid.util.ArrayMap;
import org.infogrid.util.http.HTTP;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests HTTP POSTs.
 */
public class HttpdPostTest1
        extends
            AbstractHttpdTest
{
    @Test
    public void run()
            throws
                Exception
    {
        Thread.sleep( 1000L );

        String url = "http://localhost:" + SERVER_PORT + "/";

        ArrayMap<String,String> pars = new ArrayMap<String,String>();
        pars.put( "abc", "def" );

        int MAX = 1000;

        for( int i=0 ; i<MAX ; ++i ) {
            log.info( "Performing test " + i );

            pars.put( "index", String.valueOf( i ));

            HTTP.Response r = HTTP.http_post( url, pars, false );

            if( r != null ) {
                Assert.assertEquals( "Wrong response code",   r.getResponseCode(), "200" );
                Assert.assertEquals( "Wrong Location header", r.getLocation(),     null );
                Assert.assertEquals( "Wrong MIME type",       r.getContentType(),  "text/plain" );

                StringBuilder correctResponse = new StringBuilder();
                String   sep = "";
                Iterator<String> iter = pars.keySet().iterator();
                while( iter.hasNext() ) {
                    String key   = iter.next();
                    String value = pars.get( key );
                    correctResponse.append( sep ).append( key ).append( "=" ).append( value );
                    sep = "&";
                }
                Assert.assertEquals( "Wrong response", r.getContentAsString(), correctResponse.toString() );
            } else {
                Assert.fail( "Null response" );
            }
        }
    }
}
