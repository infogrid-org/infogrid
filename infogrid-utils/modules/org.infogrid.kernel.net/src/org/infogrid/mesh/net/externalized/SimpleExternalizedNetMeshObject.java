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
 * This implementation of ExternalizedNetMeshObject is fully initialized in the
 * factory method.
 */
public class SimpleExternalizedNetMeshObject
        extends
            SimpleExternalizedMeshObject
        implements
            ExternalizedNetMeshObject
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Factory method.
     * 
     * @param identifier the NetMeshObjectIdentifier of the NetMeshObject
     * @param typeNames the MeshTypeIdentifier identifying the EntityTypes with which the NetMeshObject is currently blessed
     * @param timeCreated the time the NetMeshObject was created
     * @param timeUpdated the time the NetMeshObject was last updated
     * @param timeRead the time the NetMeshObject was last read
     * @param timeExpires the time the NetMeshObject will expire
     * @param propertyTypes the current PropertyTypes of the NetMeshObject, in the same sequence as propertyValues
     * @param propertyValues the current values of the PropertyTypes, in the same sequence as propertyTypes
     * @param neighbors the NetMeshObjectIdentifiers of the directly related NetMeshObjects
     * @param roleTypes the MeshTypeIdentifiers of the RoleTypes applicable to the neighbors, in the same sequence
     * @param equivalents the NetMeshObjectIdentifiers of the current left and right equivalent NetMeshObject, if any
     * @param giveUpHomeReplica if true, this replica will give up home replica status when asked
     * @param giveUpLock if true, this replica will give up the lock when asked
     * @param proxyIdentifiers the partner NetMeshBaseIdentifiers of all Proxies affected by this NetMeshObject
     * @param proxyTowardsHomeIndex index, into proxyIdentifiers, selecting the NetMeshBaseIdentifier of the Proxy in whose
     *        direction the home replica may be found
     * @param proxyTowardsLockIndex index, into proxyIdentifiers, selecting the NetMeshBaseIdentifier of the Proxy in whose
     *        direction the lock may be found
     * @return the created SimpleExternalizedNetMeshObject
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
            boolean                    giveUpHomeReplica,
            boolean                    giveUpLock,
            NetMeshBaseIdentifier []   proxyIdentifiers,
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
                giveUpHomeReplica,
                giveUpLock,
                proxyIdentifiers,
                proxyTowardsHomeIndex,
                proxyTowardsLockIndex );

        return ret;
    }
    
    /**
     * Constructor.
     * 
     * @param identifier the NetMeshObjectIdentifier of the NetMeshObject
     * @param typeNames the MeshTypeIdentifier identifying the EntityTypes with which the NetMeshObject is currently blessed
     * @param timeCreated the time the NetMeshObject was created
     * @param timeUpdated the time the NetMeshObject was last updated
     * @param timeRead the time the NetMeshObject was last read
     * @param timeExpires the time the NetMeshObject will expire
     * @param propertyTypes the current PropertyTypes of the NetMeshObject, in the same sequence as propertyValues
     * @param propertyValues the current values of the PropertyTypes, in the same sequence as propertyTypes
     * @param neighbors the NetMeshObjectIdentifiers of the directly related NetMeshObjects
     * @param roleTypes the MeshTypeIdentifiers of the RoleTypes applicable to the neighbors, in the same sequence
     * @param equivalents the NetMeshObjectIdentifiers of the current left and right equivalent NetMeshObject, if any
     * @param giveUpHomeReplica if true, this replica will give up home replica status when asked
     * @param giveUpLock if true, this replica will give up the lock when asked
     * @param proxyIdentifiers the partner NetMeshBaseIdentifiers of all Proxies affected by this NetMeshObject
     * @param proxyTowardsHomeIndex index, into proxyIdentifiers, selecting the NetMeshBaseIdentifier of the Proxy in whose
     *        direction the home replica may be found
     * @param proxyTowardsLockIndex index, into proxyIdentifiers, selecting the NetMeshBaseIdentifier of the Proxy in whose
     *        direction the lock may be found
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
            boolean                    giveUpHomeReplica,
            boolean                    giveUpLock,
            NetMeshBaseIdentifier[]    proxyIdentifiers,
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

        theGiveUpHomeReplica = giveUpHomeReplica;
        theGiveUpLock        = giveUpLock;

        theProxyIdentifiers = proxyIdentifiers != null ? proxyIdentifiers : new NetMeshBaseIdentifier[0];

        theProxyTowardsHomeIndex = proxyTowardsHomeIndex;
        theProxyTowardsLockIndex = proxyTowardsLockIndex;
    }
    
    /**
     * Obtain the NetMeshObjectIdentifier of the NetMeshObject.
     *
     * @return the NetMeshObjectIdentifier of the NetMeshObject
     */
    @Override
    public NetMeshObjectIdentifier getIdentifier()
    {
        return (NetMeshObjectIdentifier) super.getIdentifier();
    }
    
    /**
     * Obtain the NetMeshObjectIdentifiers of the neighbors of this NetMeshObject.
     *
     * @return the NetMeshObjectIdentifiers of the neighbors
     * @see #getRoleTypes
     */
    @Override
    public NetMeshObjectIdentifier [] getNeighbors()
    {
        return (NetMeshObjectIdentifier []) super.getNeighbors();
    }

    /**
     * Obtain the NetMeshObjectIdentifiers of the NetMeshObjects that participate in an equivalence
     * set with this NetMeshObject.
     *
     * @return the NetMeshObjectIdentifiers. May be null.
     */
    @Override
    public NetMeshObjectIdentifier[] getEquivalents()
    {
        return (NetMeshObjectIdentifier []) super.getEquivalents();
    }
    
    /**
     * Obtain the GiveUpHomeReplica property.
     * 
     * @return the GiveUpHomeReplica property
     */
    public final boolean getGiveUpHomeReplica()
    {
        return theGiveUpHomeReplica;
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
     * Obtain the NetMeshBaseIdentifiers of all Proxies affected by this NetMeshObject.
     *
     * @return the NetMeshBaseIdentifiers, if any
     */
    public final NetMeshBaseIdentifier[] getProxyIdentifiers()
    {
        return theProxyIdentifiers;
    }

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the home replica.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public final NetMeshBaseIdentifier getProxyTowardsHomeNetworkIdentifier()
    {
        if( theProxyIdentifiers == null || theProxyTowardsHomeIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxyIdentifiers[ theProxyTowardsHomeIndex ];
        }
    }

    /**
     * Obtain the NetMeshBaseIdentifier of the Proxy towards the replica with the lock.
     * 
     * @return the NetMeshBaseIdentifier, if any
     */
    public final NetMeshBaseIdentifier getProxyTowardsLockNetworkIdentifier()
    {
        if( theProxyIdentifiers == null || theProxyTowardsLockIndex == HERE_CONSTANT ) {
            return null;
        } else {
            return theProxyIdentifiers[ theProxyTowardsLockIndex ];
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
                    "theProxyIdentifiers",
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
                    theProxyIdentifiers,
                    theProxyTowardsHomeIndex,
                    theProxyTowardsLockIndex
                },
                StringHelper.LOG_FLAGS.SHOW_NON_NULL | StringHelper.LOG_FLAGS.SHOW_NON_ZERO | StringHelper.LOG_FLAGS.SHOW_ZERO );
    }
    
    /**
     * The GiveUpHomeReplica property.
     */
    protected boolean theGiveUpHomeReplica;
    
    /**
     * The GiveUpLock property.
     */
    protected boolean theGiveUpLock;
    
    /**
     * NetMeshBaseIdentifier for the Proxies to other NetworkedMeshBases that contain the replicas that are
     * closest in the replication graph. This may be null.
     */
    protected NetMeshBaseIdentifier [] theProxyIdentifiers;

    /**
     * The index into theProxyIdentifiers that represents our home Proxy. If this is HERE_CONSTANT, it
     * indicates that this is the home replica.
     */
    protected int theProxyTowardsHomeIndex = HERE_CONSTANT;
    
    /**
     * The index into theProxyIdentifiers that represents the Proxy towards the lock. If this
     * is HERE_CONSTANT, it indicates that this replica has the lock.
     */
    protected int theProxyTowardsLockIndex = HERE_CONSTANT;
    
    /** 
     * Special value indicating this replica (instead of another, reached through a Proxy).
     */
    protected static final int HERE_CONSTANT = -1;
}