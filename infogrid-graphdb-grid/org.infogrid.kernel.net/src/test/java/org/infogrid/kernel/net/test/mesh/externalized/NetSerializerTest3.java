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

package org.infogrid.kernel.net.test.mesh.externalized;

import java.text.ParseException;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * NetMeshBaseIdentifiers can only use protocols that have been specified in the factory.
 */
public class NetSerializerTest3
        extends
            AbstractNetSerializerTest
{
    /**
     * Run the test.
     * 
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        String [] testData = {
            "ftp://foo.com/",
            "abc://bar.com"
        };
        
        for( int i=0 ; i<testData.length ; ++i ) {
            log.info( "Test " + i );

            NetMeshBaseIdentifier found = null;
            try {
                found = theMeshBaseIdentifierFactory.fromExternalForm( testData[i] );
                
                reportError( "No exception thrown", testData[i] );

            } catch( ParseException ex ) {
                // ok
            }
        }
    }
    // Our Logger
    private static Log log = Log.getLogInstance( NetSerializerTest3.class );    
}
