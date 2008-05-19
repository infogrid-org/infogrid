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

package org.infogrid.probe.shadow;

import org.infogrid.comm.MessageEndpoint;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.net.AbstractProxy;
import org.infogrid.meshbase.net.NetMeshBase;

import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.net.NetMessageEndpoint;

import org.infogrid.util.logging.Log;

/**
 * An AbstractProxy specifically for the use by ShadowMeshBases.
 */
public abstract class AbstractShadowProxy
        extends
            AbstractProxy
{
    private static final Log log = Log.getLogInstance( AbstractShadowProxy.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param ep the NetMessageEndpoint to use by this Proxy
     * @param mb the NetMeshBase that this Proxy belongs to
     */
    protected AbstractShadowProxy(
            NetMessageEndpoint ep,
            NetMeshBase        mb )
    {
        super( ep, mb );
    }

    /**
      * Indicates that a Transaction has been committed. Overriding this makes for easier debugging.
      *
      * @param theTransaction the Transaction that was committed
      */
    @Override
    public void transactionCommitted(
            Transaction theTransaction )
    {
        super.transactionCommitted( theTransaction );
    }

    /**
     * Internal implementation method for messageReceived. Overriding this makes
     * debugging easier.
     *
     * @param incoming the incoming message
     */
    @Override
    protected void internalMessageReceived(
            MessageEndpoint<XprisoMessage> endpoint,
            XprisoMessage                  incoming )
    {
        // This implementation must do everything that a regular NetMeshBase does. (Otherwise the
        // main NetMeshBase would have changes that weren't reflected in the ShadowMeshBase where
        // they were supposedly from.
        // It also needs to keep track of what it did.

        ChangeSet queued = ChangeSet.create();
        
        // deal with conveyed objects whose home replica has been pushed here
        if( incoming.getConveyedMeshObjects() != null && incoming.getPushHomeReplicas() != null ) {
            ExternalizedNetMeshObject [] conveyed = incoming.getConveyedMeshObjects();
            NetMeshObjectIdentifier   [] pushed   = incoming.getPushHomeReplicas();
            
            for( ExternalizedNetMeshObject current : conveyed ) {
                for( NetMeshObjectIdentifier id : pushed ) {
                    if( id.equals( current.getIdentifier() )) {
                        NetMeshObjectCreatedEvent event = new NetMeshObjectCreatedEvent(
                                null,
                                theEndpoint.getNetworkIdentifierOfPartner(),
                                current,
                                theEndpoint.getNetworkIdentifierOfPartner());
                        
                        event.setResolver( theMeshBase );
                        queued.addChange( event );
                        break;
                    }
                }
            }
        }

        // deal with type changes
        if( incoming.getTypeAdditions() != null ) {
            NetMeshObjectTypeAddedEvent [] typeChanges = incoming.getTypeAdditions();

            for( NetMeshObjectTypeAddedEvent current : typeChanges ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }
        if( incoming.getTypeRemovals() != null ) {
            NetMeshObjectTypeRemovedEvent [] typeChanges = incoming.getTypeRemovals();

            for( NetMeshObjectTypeRemovedEvent current : typeChanges ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }

        // deal with neighbor changes
        if( incoming.getNeighborAdditions() != null ) {
            NetMeshObjectNeighborAddedEvent [] neighborAdditions = incoming.getNeighborAdditions();

            for( NetMeshObjectNeighborAddedEvent current : neighborAdditions ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }
        if( incoming.getNeighborRemovals() != null ) {
            NetMeshObjectNeighborRemovedEvent [] neighborRemovals = incoming.getNeighborRemovals();

            for( NetMeshObjectNeighborRemovedEvent current : neighborRemovals ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }
        if( incoming.getRoleAdditions() != null ) {
            NetMeshObjectRoleAddedEvent [] roleChanges = incoming.getRoleAdditions();

            for( NetMeshObjectRoleAddedEvent current : roleChanges ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }
        if( incoming.getRoleRemovals() != null ) {
            NetMeshObjectRoleRemovedEvent [] roleChanges = incoming.getRoleRemovals();

            for( NetMeshObjectRoleRemovedEvent current : roleChanges ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }

        // deal with property changes
        if( incoming.getPropertyChanges() != null ) {
            NetMeshObjectPropertyChangeEvent [] propertyChanges = incoming.getPropertyChanges();

            for( NetMeshObjectPropertyChangeEvent current : propertyChanges ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }

        // deal with deleted objects
        if( incoming.getDeletions() != null ) {
            NetMeshObjectDeletedEvent [] deletions = incoming.getDeletions();

            for( NetMeshObjectDeletedEvent current : deletions ) {
                current.setResolver( theMeshBase );
                queued.addChange( current );
            }
        }

        if( !queued.isEmpty() ) {
            ((ShadowMeshBase)theMeshBase).queueNewChanges( queued );
        }        

        super.internalMessageReceived( endpoint, incoming );
    }
}
