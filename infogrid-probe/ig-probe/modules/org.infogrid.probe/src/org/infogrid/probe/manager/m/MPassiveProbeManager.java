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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.probe.manager.m;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.probe.ProbeDirectory;
import org.infogrid.probe.manager.AbstractProbeManager;
import org.infogrid.probe.manager.PassiveProbeManager;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.ShadowMeshBaseFactory;
import org.infogrid.util.CachingMap;
import org.infogrid.util.MCachingHashMap;

/**
 * In-memory implementation of PassiveProbeManager.
 */
public class MPassiveProbeManager
        extends
            AbstractProbeManager
        implements
            PassiveProbeManager
{
    /**
     * Factory method.
     *
     * @param delegateFactory the delegate ShadowMeshBaseFactory that knows how to instantiate ShadowMeshBases
     * @param dir the ProbeDirectory to use
     * @return the created MPassiveProbeManager
     */
    public static MPassiveProbeManager create(
            ShadowMeshBaseFactory delegateFactory,
            ProbeDirectory        dir )
    {
        CachingMap<NetMeshBaseIdentifier,ShadowMeshBase> storage = MCachingHashMap.create();

        return new MPassiveProbeManager( delegateFactory, storage, dir );
    }

    /**
     * Constructor.
     * 
     * @param delegateFactory the delegate ShadowMeshBaseFactory that knows how to instantiate ShadowMeshBases
     * @param storage the storage to use
     * @param dir the ProbeDirectory to use
     */
    protected MPassiveProbeManager(
            ShadowMeshBaseFactory                            delegateFactory,
            CachingMap<NetMeshBaseIdentifier,ShadowMeshBase> storage,
            ProbeDirectory                                   dir )
    {
        super( delegateFactory, storage, dir );
    }

    /**
     * We are not needed any more.
     *
     * @param isPermanent true if this is permanent
     */
    public void die(
            boolean isPermanent )
    {
        // no op
    }
}
