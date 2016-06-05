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

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.DefaultNetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.util.logging.Log;
import org.junit.Test;

/**
 * Tests NetMeshObjectAccessSpecifications.
 */
public class NetSerializerTest5
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
        NetMeshBaseIdentifier test1 = theMeshBaseIdentifierFactory.fromExternalForm( "http://example.com/somewhere" );
        NetMeshBaseIdentifier test2 = theMeshBaseIdentifierFactory.fromExternalForm( "https://example.net/" );
        NetMeshBaseIdentifier test3 = theMeshBaseIdentifierFactory.fromExternalForm( "http://example.org/x/y/z" );

        DefaultNetMeshObjectAccessSpecificationFactory fact1 = DefaultNetMeshObjectAccessSpecificationFactory.create( test1 );

        NetMeshObjectIdentifier id1 = fact1.getNetMeshObjectIdentifierFactory().fromExternalForm( "#id1" );

        NetMeshObjectAccessSpecification spec1  = fact1.obtain( test1 );
        NetMeshObjectAccessSpecification spec2  = fact1.obtain( test2 );
        NetMeshObjectAccessSpecification spec12 = fact1.obtain(
                new NetMeshBaseIdentifier[] { test1, test2 } );

        NetMeshObjectAccessSpecification spec11 = fact1.obtain( test1, id1 );
        NetMeshObjectAccessSpecification spec21 = fact1.obtain( test2, id1 );
        NetMeshObjectAccessSpecification spec321 = fact1.obtain(
                new NetMeshBaseIdentifier[] { test3, test2 },
                id1 );

        //
        
        checkEquals( spec1.toExternalForm(),   "http://example.com/somewhere",                                                   "wrong spec1" );
        checkEquals( spec2.toExternalForm(),   "https://example.net/",                                                           "wrong spec2" );
        checkEquals( spec12.toExternalForm(),  "http://example.com/somewhere!https://example.net/!https://example.net/",         "wrong spec12" );
        checkEquals( spec11.toExternalForm(),  "http://example.com/somewhere#id1",                                               "wrong spec11" );
        checkEquals( spec21.toExternalForm(),  "https://example.net/!http://example.com/somewhere#id1",                          "wrong spec21" );
        checkEquals( spec321.toExternalForm(), "http://example.org/x/y/z!https://example.net/!http://example.com/somewhere#id1", "wrong spec321" );

        // FIXME: also test scope and coherence parameters
    }

    // Our Logger
    private static Log log = Log.getLogInstance( NetSerializerTest5.class );
}
