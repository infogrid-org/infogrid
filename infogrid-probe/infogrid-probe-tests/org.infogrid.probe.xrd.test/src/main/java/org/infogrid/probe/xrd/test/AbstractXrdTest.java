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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.xrd.test;

import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.logging.Log;

/**
 * Factors out common functionality for XRD tests.
 */
public abstract class AbstractXrdTest
        extends
            AbstractTest
{
    /**
     * Constructor.
     *
     * @param testClass the class to test
     */
    protected AbstractXrdTest(
            Class<?> testClass )
    {
        super( localFileName( testClass, "/ResourceHelper" ));
    }

    /**
     * Dump the content of a MeshBase to log.debug().
     *
     * @param mb the MeshBase whose content we want to dump
     * @param prefix a string to prepend
     * @param mylog the Log to dump to
     * @throws Exception all sorts of things may go wrong during a test
     */
    protected final void dumpMeshBase(
            IterableMeshBase mb,
            String           prefix,
            Log              mylog )
        throws
            Exception
    {
        if( mylog.isDebugEnabled() ) {
            StringBuilder buf = new StringBuilder( prefix );
            for( MeshObject current : mb ) {
                buf.append( "\n  " );
                buf.append( current.getIdentifier() );
                buf.append( " (created: " );
                buf.append( current.getTimeCreated() );
                buf.append( " updated: " );
                buf.append( current.getTimeUpdated() );
                buf.append( " read: " );
                buf.append( current.getTimeRead() );
                buf.append( ")" );

                if( true ) {
                    buf.append( "\n    Types:" );
                    for( EntityType et : current.getTypes() ) {
                        buf.append( "\n        " );
                        buf.append( et.getName().value() );
                    }
                }
                if( true ) {
                    buf.append( "\n    Properties:" );
                    for( PropertyType pt : current.getAllPropertyTypes() ) {
                        buf.append( "\n        " );
                        buf.append( pt.getName().value() );
                        buf.append( ": " );
                        buf.append( current.getPropertyValue( pt ));
                    }
                }
            }
            mylog.debug( buf.toString() );
        }
    }
}

