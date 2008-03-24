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

package org.infogrid.mesh.net.externalized;

import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.externalized.SimpleExternalizedMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.PropertyValue;

import org.infogrid.util.StringHelper;

/**
 *
 */
public class SimpleExternalizedNetMeshObject
        extends
            SimpleExternalizedMeshObject
        implements
            ExternalizedNetMeshObject
{
    /**
     * Factory method.
     */
    public static SimpleExternalizedNetMeshObject create(
            NetMeshObjectIdentifier    identifier,
            MeshTypeIdentifier []      typeNames,
            long                       timeCreated,
            long                       timeUpdated,
            long                       timeRead,
            long                       timeExpires,
            MeshTypeIdentifier []      propertyTypes,
            PropertyValue  []          propertyValues,
            NetMeshObjectIdentifier [] neighbors,
            MeshTypeIdentifier [][]    roleTypes,
            NetMeshObjectIdentifier [] equivalents,
            boolean                    giveUpLock,
            NetMeshBaseIdentifier []   proxyNames,
            int                        proxyTowardsHomeIndex,
            int                        proxyTowardsLockIndex )
    {
        // do some sanity checking
        if( identifier == null ) {
            throw new IllegalArgumentException( "null Identifier" );
        }
        if( typeNames != null ) {
            for( MeshTypeIdentifier current : typeNames ) {
                if( current == null ) {
                    throw new IllegalArgumentException( "null typeName" );
                }
            }
        } else {
            typeNames = new MeshTypeIdentifier[0];
        }
        if( propertyTypes != null ) {
            for( MeshTypeIdentifier current : propertyTypes ) {
                if( current == null ) {
                    throw new IllegalArgumentException( "null PropertyType" );
                }
            }
        } else {
            propertyTypes = new MeshTypeIdentifier[0];
        }
        if( propertyValues == null ) {
            propertyValues = new PropertyValue[0];
        }
        if( neighbors != null ) {
            for( MeshObjectIdentifier current : neighbors ) {
                if( current == null ) {
                    throw new IllegalArgumentException( "null neighbor" );
                }
            }
        } else {
            neighbors = new NetMeshObjectIdentifier[0];
            roleTypes = new MeshTypeIdentifier[0][];
        }
        
        if( equivalents != null ) {
            for( MeshObjectIdentifier current : equivalents ) {
                if( current == null ) {
                    throw new IllegalArgumentException( "null equivalent" );
                }
            }
        } else {
            equivalents = new NetMeshObjectIdentifier[0];
        }
        
        SimpleExternalizedNetMeshObject ret = new SimpleExternalizedNetMeshObject(
                identifier,
                typeNames,
                timeCreated,
                timeUpdated,
                timeRead,
                timeExpires,
                propertyTypes,
                propertyValues,
                neighbors,
                roleTypes,
                equivalents,
                giveUpLock,
                proxyNames,
                proxyTowardsHomeIndex,
                proxyTowardsLockIndex );

        return ret;
    }
    
    /**
     * Constructor.
     */
    protected SimpleExternalizedNetMeshObject(
            NetMeshObjectIdentifier    identifier,
            MeshTypeIdentifier []      typeNames,
            long                       timeCreated,
            long                       timeUpdated,
            long                       timeRead,
            long                       timeExpires,
            MeshTypeIdentifier []      propertyTypes,
            PropertyValue  []          propertyValues,
            NetMeshObjectIdentifier [] neighbors,
            MeshTypeIdentifier [][]    roleTypes,
            NetMeshObjectIdentifier [] equivalents,
            boolean                    giveUpLock,
            NetMeshBaseIdentifier[]    proxyNames,
            int                        proxyTowardsHomeIndex,
            int                        proxyTowardsLockIndex )
{
        super(  identifier,
                typeNames,
                timeCreated,
                timeUpdated,
                timeRead,
                timeExpires,
                propertyTypes,
                propertyValues,
                neighbors,
                roleTypes,
                equivalents );

        theGiveUpLock = giveUpLock;
        theProxyNames = proxyNames != null ? proxyNames : new NetMeshBaseIdentifier[0];

        theProxyTowardsHomeIndex = proxyTowardsHomeIndex;
        theProxyTowardsLockIndex = proxyTowardsLockIndex;
    }
    
    /**
     * Obtain the Identifier of the MeshObject.
     *
     * @return the Identifier of the MeshObject
     */
    @Override
    public NetMeshObjectIdentifier getIdentifier()
    {
        return (NetMeshObjectIdentifier) super.getIdentifier();
    }
    
    /**
     * Obtain the Identifiers of the neighbors of this MeshObject.
     * 
     * @return the Identifiers of the neighbors
     * @see #getTypes
     */
    @Override
    public NetMeshObjectIdentifier [] getNeighbors()
    {
        return (NetMeshObjectIdentifier []) super.getNeighbors();
    }

    /**
     * Get the MeshObject's equivalents, if any.
     *
     * @return the equivalents' Identifiers.
     */
    @Override
    public NetMeshObjectIdentifier[] getEquivalents()
    {
        return (NetMeshObjectIdentifier []) super.getEquivalents();
    }
    
    /**
     * Obtain the GiveUpLock property.
     *
     * @return the GiveUpLock property
     */
    public final boolean getGiveUpLock()
    {
        return theGiveUpLock;
    }

    /**
     * Obtain the NetworkIdentifiers of all Proxies.
     *
     * @return the NetworkIdentifiers, if any
     */
    public final NetMeshBaseIdentifier[] getProxyNames()
    {
        return theProxyNames;
    }

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the home replica.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public final NetMeshBaseIdentifier getProxyTowardsHomeNetworkIdentifier()
    {
        if( theProxyNames == null || theProxyTowardsHomeIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxyNames[ theProxyTowardsHomeIndex ];
        }
    }

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the replica with the lock.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public final NetMeshBaseIdentifier getProxyTowardsLockNetworkIdentifier()
    {
        if( theProxyNames == null || theProxyTowardsLockIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxyNames[ theProxyTowardsLockIndex ];
        }
    }
    
    /**
     * Convert to String representation, for debugging.
     *
     * @return String representation
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theIdentifier",
                    "theTypeNames",
                    "thePropertyTypes",
                    "thePropertyValues",
                    "theTimeCreated",
                    "theTimeUpdated",
                    "theTimeRead",
                    "theTimeExpires",
                    "theNeighbors",
                    "theRoleTypes",
                    "theEquivalents",
                    "theGiveUpLock",
                    "theProxyNames",
                    "theProxyTowardsHomeIndex",
                    "theProxyTowardsLockIndex"
                },
                new Object[] {
                    theIdentifier,
                    theTypeNames,
                    thePropertyTypes,
                    thePropertyValues,
                    theTimeCreated,
                    theTimeUpdated,
                    theTimeRead,
                    theTimeExpires,
                    theNeighbors,
                    theRoleTypes,
                    theEquivalents,
                    theGiveUpLock,
                    theProxyNames,
                    theProxyTowardsHomeIndex,
                    theProxyTowardsLockIndex
                },
                StringHelper.LOG_FLAGS.SHOW_NON_NULL | StringHelper.LOG_FLAGS.SHOW_NON_ZERO | StringHelper.LOG_FLAGS.SHOW_ZERO );
    }
    
    /**
     * The GiveUpLock property.
     */
    protected boolean theGiveUpLock;
    
    /**
     * NetworkIdentifiers for the Proxies to other NetworkedMeshBases that contain the replicas that are
     * closest in the replication graph. This may be null.
     */
    protected NetMeshBaseIdentifier[] theProxyNames;

    /**
     * The index into theProxies that represents our home Proxy. If this is HERE_CONSTANT, it
     * indicates that this is the home replica.
     */
    protected int theProxyTowardsHomeIndex = HERE_CONSTANT;
    
    /**
     * The index into theProxies that represents the Proxy towards the lock. If this
     * is HERE_CONSTANT, it indicates that this replica has the lock.
     */
    protected int theProxyTowardsLockIndex = HERE_CONSTANT;
    
    /** 
     * Special value indicating this replica (instead of another, reached through a Proxy).
     */
    protected static final int HERE_CONSTANT = -1;
}
