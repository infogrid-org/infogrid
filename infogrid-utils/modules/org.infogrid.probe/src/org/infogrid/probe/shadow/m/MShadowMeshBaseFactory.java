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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.shadow.m;

import org.infogrid.context.Context;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.net.NetMessageEndpointFactory;

import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.shadow.AbstractShadowMeshBaseFactory;
import org.infogrid.probe.shadow.ShadowMeshBase;

import org.infogrid.util.FactoryException;

/**
 * Knows how to instantiate MShadowMeshBases.
 */
public class MShadowMeshBaseFactory
        extends
            AbstractShadowMeshBaseFactory
{
    /**
     * Factory method.
     */
    public static MShadowMeshBaseFactory create(
            ModelBase                 modelBase,
            NetMessageEndpointFactory endpointFactory,
            ProbeDirectory            probeDirectory,
            long                      timeNotNeededTillExpires,
            Context                   c )
    {
        return new MShadowMeshBaseFactory( modelBase, endpointFactory, probeDirectory, timeNotNeededTillExpires, c );
    }

    /**
     * Constructor.
     */
    protected MShadowMeshBaseFactory(
            ModelBase                 modelBase,
            NetMessageEndpointFactory endpointFactory,
            ProbeDirectory            probeDirectory,
            long                      timeNotNeededTillExpires,
            Context                   c )
    {
        super( modelBase, endpointFactory, probeDirectory, timeNotNeededTillExpires, c );
    }

    /**
     * Factory method.
     *
     * @param key the key information required for object creation, if any
     * @param argument any information required for object creation, if any
     * @return the created object
     */
    public ShadowMeshBase obtainFor(
            NetMeshBaseIdentifier      key,
            CoherenceSpecification argument )
        throws
            FactoryException
    {
        MShadowMeshBase ret = MShadowMeshBase.create(
                key,
                theEndpointFactory,
                theModelBase,
                null,
                theProbeDirectory,
                theTimeNotNeededTillExpires,
                theMeshBaseContext );
        
        ret.setFactory( this );

        try {
            Long next = ret.doUpdateNow( argument );

        } catch( Throwable ex ) {
            throw new FactoryException( ex );
        }
        
        return ret;
    }
}
