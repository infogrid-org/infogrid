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

package org.infogrid.probe.manager;

import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.ShadowMeshBaseFactory;

import org.infogrid.util.CachingMap;
import org.infogrid.util.MNameServer;
import org.infogrid.util.NameServer;
import org.infogrid.util.PatientSmartFactory;
import org.infogrid.util.logging.Log;

/**
 *
 */
public abstract class AbstractProbeManager
        extends
            PatientSmartFactory<NetMeshBaseIdentifier, ShadowMeshBase, CoherenceSpecification>
        implements
            ProbeManager
{
    private static final Log log = Log.getLogInstance( AbstractProbeManager.class ); // our own, private logger

    /**
     * Constructor.
     */
    protected AbstractProbeManager(
            ShadowMeshBaseFactory                            delegate,
            CachingMap<NetMeshBaseIdentifier,ShadowMeshBase> storage )
    {
        super( delegate, storage );
    }

    /**
     * Set the main MeshBase for which this ProbeManager manages the Probes.
     *
     * @param main the main MeshBase.
     */
    public void setMainNetMeshBase(
            NetMeshBase main )
    {
        if( theNameServer != null ) {
            if( theMainNetMeshBase != null ) {
                theNameServer.remove( theMainNetMeshBase.getIdentifier() );
            }        
            if( main != null ) {
                theNameServer.put( main.getIdentifier(), main );
            }
        }
        theMainNetMeshBase = main;
    }


    /**
     * Obtain a NameServer that contains all ShadowMeshBases and the main NetMeshBase.
     * 
     * @return the NameServer
     */
    public synchronized NameServer<NetMeshBaseIdentifier,NetMeshBase> getNetMeshBaseNameServer()
    {
        if( theNameServer == null ) {
            theNameServer = MNameServer.<NetMeshBaseIdentifier,NetMeshBase>create( this );
            if( theMainNetMeshBase != null ) {
                theNameServer.put( theMainNetMeshBase.getIdentifier(), theMainNetMeshBase );
            }
        }
        return theNameServer;
    }

    /**
     * The main NetMeshBase.
     */
    protected NetMeshBase theMainNetMeshBase;
    
    /**
     * The NameServer, allocated as needed.
     */
    protected MNameServer<NetMeshBaseIdentifier,NetMeshBase> theNameServer;
}