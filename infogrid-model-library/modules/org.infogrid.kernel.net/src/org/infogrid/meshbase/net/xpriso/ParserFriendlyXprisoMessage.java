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

package org.infogrid.meshbase.net.xpriso;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;

import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;


import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.StringHelper;

import java.util.ArrayList;

/**
 * An XprisoMessage that is suitable for parsers that pick up one item at a time, instead of
 * all of them at the same time.
 */
public class ParserFriendlyXprisoMessage
        extends
            AbstractXprisoMessage
{
    /**
     * Factory method.
     *
     * @param requestId the request id of the message
     * @param responseId the response id of the message
     * @param sender identifies the sender of this message
     * @param receiver identifies the receiver of this message
     * @return the created ParserFriendlyXprisoMessage
     */
    public static ParserFriendlyXprisoMessage create(
            long                  requestId,
            long                  responseId,
            NetMeshBaseIdentifier sender,
            NetMeshBaseIdentifier receiver )
    {
        ParserFriendlyXprisoMessage ret = new ParserFriendlyXprisoMessage( requestId, responseId, sender, receiver );
        return ret;
    }

    /**
     * Constructor.
     * 
     * @param requestId the request id of the message
     * @param responseId the response id of the message
     * @param sender identifies the sender of this message
     * @param receiver identifies the receiver of this message
     */
    protected ParserFriendlyXprisoMessage(
            long                  requestId,
            long                  responseId,
            NetMeshBaseIdentifier sender,
            NetMeshBaseIdentifier receiver )
    {
        theRequestId  = requestId;
        theResponseId = responseId;

        theSenderIdentifier   = sender;
        theReceiverIdentifier = receiver;
    }

    /**
     * Determine whether this message contains any valid payload or is empty.
     *
     * @return true if it is empty
     */
    public boolean isEmpty()
    {
        // This is listed in alphabetical sequence to make it easier in IDEs to check whether we have all of them

        if( theCeaseCommunications ) {
            return false;
        }
        if( theConveyedMeshObjects.size() > 0 ) {
            return false;
        }
        if( theCreations.size() > 0 ) {
            return false;
        }
        if( theDeleteChanges.size() > 0 ) {
            return false;
        }
        if( theNeighborAdditions.size() > 0 ) {
            return false;
        }
        if( theNeighborRemovals.size() > 0 ) {
            return false;
        }
        if( thePropertyChanges.size() > 0 ) {
            return false;
        }
        if( thePushHomeReplicas.size() > 0 ) {
            return false;
        }
        if( thePushLockObjects.size() > 0 ) {
            return false;
        }
        // ignore theReceiverIdentifier;
        if( theReclaimedLockObjects.size() > 0 ) {
            return false;
        }
        // ignore theRequestId;
        if( theRequestedCanceledObjects.size() > 0 ) {
            return false;
        }
        if( theRequestedFirstTimeObjects.size() > 0 ) {
            return false;
        }
        if( theRequestedHomeReplicas.size() > 0 ) {
            return false;
        }
        if( theRequestedLockObjects.size() > 0 ) {
            return false;
        }
        if( theRequestedResynchronizeDependentReplicas.size() > 0 ) {
            return false;
        }
        // ignore theResponseId
        if( theResynchronizedDependentReplicas.size() > 0 ) {
            return false;
        }
        if( theRoleAdditions.size() > 0 ) {
            return false;
        }
        if( theRoleRemovals.size() > 0 ) {
            return false;
        }
        // ignore theSenderIdentifier;        
        if( theTypeAdditions.size() > 0 ) {
            return false;
        }
        if( theTypeRemovals.size() > 0 ) {
            return false;
        }
        return true;
    }
    
    /**
     * Set the request ID.
     *
     * @param id the request ID
     */
    public void setRequestId(
            long id )
    {
        theRequestId = id;
    }
    
    /**
     * Set the response ID.
     *
     * @param id the response ID
     */
    public void setResponseId(
            long id )
    {
        theResponseId = id;
    }
    
    /**
     * Set the NetMeshBaseIdentifier of the sender.
     * 
     * @param newValue the NNetMeshBaseIdentifierof the sender
     */
    public void setSenderIdentifier(
            NetMeshBaseIdentifier newValue )
    {
        theSenderIdentifier = newValue;
    }

    /**
     * Set the NetMeshBaseIdentifier of the receiver.
     * 
     * @param newValue the NNetMeshBaseIdentifierof the receiver
     */
    public void setReceiverIdentifier(
            NetMeshBaseIdentifier newValue )
    {
        theReceiverIdentifier = newValue;
    }

    /**
     * Set the NetworkPaths to the MeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @param newValue the NetworkPaths for the MeshObjects
     */
    public void addRequestedFirstTimeObject(
            NetMeshObjectAccessSpecification newValue )
    {
        theRequestedFirstTimeObjects.add( newValue );
    }
    
    /**
     * Obtain the NetworkPaths to the MeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @return the NetworkPaths for the MeshObjects
     */
    public NetMeshObjectAccessSpecification[] getRequestedFirstTimeObjects()
    {
        NetMeshObjectAccessSpecification [] ret = ArrayHelper.copyIntoNewArray( theRequestedFirstTimeObjects, NetMeshObjectAccessSpecification.class );
        return ret;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @param newValue the identifiers for the MeshObjects
     */
    public void addRequestedCanceledObject(
            NetMeshObjectIdentifier newValue )
    {
        theRequestedCanceledObjects.add( newValue );
    }
    
    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @return the identifiers for the MeshObjects
     */
    public NetMeshObjectIdentifier [] getRequestedCanceledObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedCanceledObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Set the creation events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the creation events
     */
    public void addCreation(
            NetMeshObjectCreatedEvent newValue )
    {
        theCreations.add( newValue );
    }
    
    /**
     * Obtain the creation events that the sender needs to convey to the
     * receiver
     *
     * @return the creation events
     */
    public NetMeshObjectCreatedEvent [] getCreations()
    {
        NetMeshObjectCreatedEvent [] ret = ArrayHelper.copyIntoNewArray( theCreations, NetMeshObjectCreatedEvent.class );
        return ret;
    }

    /**
     * Set the identifiers for the MeshObjects that the sender has deleted
     * semantically, and of whose deletion the receivers needs to be notified.
     *
     * @param newValue the identifiers for the MeshObjects
     */
    public void addDeleteChange(
            NetMeshObjectDeletedEvent newValue )
    {
        theDeleteChanges.add( newValue );
    }

    /**
     * Obtain the identifiers for the MeshObjects that the sender has deleted
     * semantically, and of whose deletion the receivers needs to be notified.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public NetMeshObjectDeletedEvent [] getDeletions()
    {
        NetMeshObjectDeletedEvent [] ret = ArrayHelper.copyIntoNewArray( theDeleteChanges, NetMeshObjectDeletedEvent.class );
        return ret;
    }

    /**
     * Set the externalized representation of the MeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @param newValue the MeshObjects
     */
    public void addConveyedMeshObject(
            ExternalizedNetMeshObject newValue )
    {
        theConveyedMeshObjects.add( newValue );
    }

    /**
     * Obtain the externalized representation of the MeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject[] getConveyedMeshObjects()
    {
        ExternalizedNetMeshObject [] ret = ArrayHelper.copyIntoNewArray( theConveyedMeshObjects, ExternalizedNetMeshObject.class );
        return ret;
    }

    /**
     * Set the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the neighbor change events
     */
    public void addNeighborAddition(
            NetMeshObjectNeighborAddedEvent newValue )
    {
        theNeighborAdditions.add( newValue );
    }

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborAddedEvent [] getNeighborAdditions()
    {
        NetMeshObjectNeighborAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theNeighborAdditions, NetMeshObjectNeighborAddedEvent.class );
        return ret;
    }

    /**
     * Set the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the neighbor change events
     */
    public void addNeighborRemoval(
            NetMeshObjectNeighborRemovedEvent newValue )
    {
        theNeighborRemovals.add( newValue );
    }

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborRemovedEvent [] getNeighborRemovals()
    {
        NetMeshObjectNeighborRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theNeighborRemovals, NetMeshObjectNeighborRemovedEvent.class );
        return ret;
    }

    /**
     * Set the property change events that the sender needs to convey to the
     * received.
     *
     * @param newValue the property change events
     */
    public void addPropertyChange(
            NetMeshObjectPropertyChangeEvent newValue )
    {
        thePropertyChanges.add( newValue );
    }

    /**
     * Obtain the property change events that the sender needs to convey to the
     * receiver.
     *
     * @return the property change events
     */
    public NetMeshObjectPropertyChangeEvent [] getPropertyChanges()
    {
        NetMeshObjectPropertyChangeEvent [] ret = ArrayHelper.copyIntoNewArray( thePropertyChanges, NetMeshObjectPropertyChangeEvent.class );
        return ret;
    }

    /**
     * Set the role change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the role change events
     */
    public void addRoleAdditions(
            NetMeshObjectRoleAddedEvent newValue )
    {
        theRoleAdditions.add( newValue );
    }

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleAddedEvent [] getRoleAdditions()
    {
        NetMeshObjectRoleAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theRoleAdditions, NetMeshObjectRoleAddedEvent.class );
        return ret;
    }

    /**
     * Set the role change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the role change events
     */
    public void addRoleRemoval(
            NetMeshObjectRoleRemovedEvent newValue )
    {
        theRoleRemovals.add( newValue );
    }

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleRemovedEvent [] getRoleRemovals()
    {
        NetMeshObjectRoleRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theRoleRemovals, NetMeshObjectRoleRemovedEvent.class );
        return ret;
    }

    /**
     * Set the type added events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the type addition events
     */
    public void addTypeAddition(
            NetMeshObjectTypeAddedEvent newValue )
    {
        theTypeAdditions.add( newValue );
    }

    /**
     * Obtain the type addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the type addition events
     */
    public NetMeshObjectTypeAddedEvent [] getTypeAdditions()
    {
        NetMeshObjectTypeAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theTypeAdditions, NetMeshObjectTypeAddedEvent.class );
        return ret;
    }

    /**
     * Set the type removed events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the type removed events
     */
    public void addTypeRemoval(
            NetMeshObjectTypeRemovedEvent newValue )
    {
        theTypeRemovals.add( newValue );
    }

    /**
     * Obtain the type addition removed that the sender needs to convey to the
     * receiver.
     *
     * @return the type removed events
     */
    public NetMeshObjectTypeRemovedEvent [] getTypeRemovals()
    {
        NetMeshObjectTypeRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theTypeRemovals, NetMeshObjectTypeRemovedEvent.class );
        return ret;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     * 
     * @param newValue the MeshObjectIdentifier for the MeshObject
     */
    public void addRequestedLockObjects(
            NetMeshObjectIdentifier newValue )
    {
        theRequestedLockObjects.add( newValue );
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @return the identifiers for the MeshObjects
     */
    public NetMeshObjectIdentifier [] getRequestedLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     * 
     * @param newValue the MeshObjectIdentifier for the MeshObject
     */
    public void addPushLockObject(
            NetMeshObjectIdentifier newValue )
    {
        thePushLockObjects.add( newValue );
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @return the identifiers for the MeshObjects
     */
    public NetMeshObjectIdentifier [] getPushLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( thePushLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }
    
    /**
     * Set the identifiers for the MeshObjects for which the sender has forcefully
     * reclaimed the lock.
     * 
     * @param newValue the MeshObjectIdentifier for the MeshObject
     */
    public void addReclaimedLockObject(
            NetMeshObjectIdentifier newValue )
    {
        theReclaimedLockObjects.add( newValue );
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @return the identifiers for the MeshObjects
     */
    public NetMeshObjectIdentifier [] getReclaimedLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theReclaimedLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }
    
    /**
     * Set the identifiers for the NetMeshObjects for which the sender requests
     * home replica status.
     * 
     * @param newValue the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addRequestedHomeReplica(
            NetMeshObjectIdentifier newValue )
    {
        theRequestedHomeReplicas.add( newValue );
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender requests
     * home replica status.
     * 
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public NetMeshObjectIdentifier [] getRequestedHomeReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedHomeReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Set the identifiers for the NetMeshObjects for which the sender surrenders
     * the home replica status to the receiver.
     * 
     * @param newValue the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addPushHomeReplica(
            NetMeshObjectIdentifier newValue )
    {
        thePushHomeReplicas.add( newValue );
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender surrenders
     * the home replica status to the receiver.
     * 
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public NetMeshObjectIdentifier [] getPushHomeReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( thePushHomeReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }
    
    /**
     * Set the identifiers for the MeshObjects for which the sender has a replica
     * that it wishes to resynchronize as a dependent replica.
     * 
     * @param newValue the MeshObjectIdentifier for the MeshObject
     */
    public void addRequestedResynchronizeDependentReplica(
            NetMeshObjectIdentifier newValue )
    {
        theRequestedResynchronizeDependentReplicas.add( newValue );
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has a replica
     * that it wishes to resynchronize as a dependent replica.
     *
     * @return the identifiers for the MeshObjects
     */
    public NetMeshObjectIdentifier [] getRequestedResynchronizeDependentReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedResynchronizeDependentReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Set the externalized representation of the MeshObjects that are sent
     * by the sender to the receiver in response to a resynchronizeDependent request.
     *
     * @param newValue the MeshObjects
     */
    public void addResynchronizeDependentReplica(
            ExternalizedNetMeshObject newValue )
    {
        theResynchronizedDependentReplicas.add( newValue );
    }

    /**
     * Obtain the externalized representation of the MeshObjects that are sent
     * by the sender to the receiver in response to a resynchronizeDependent request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject [] getResynchronizeDependentReplicas()
    {
        ExternalizedNetMeshObject [] ret = ArrayHelper.copyIntoNewArray( theResynchronizedDependentReplicas, ExternalizedNetMeshObject.class );
        return ret;
    }

    /**
     * Set whether or not to cease communications after this message.
     *
     * @param newValue the new value
     */
    public void setCeaseCommunications(
            boolean newValue )
    {
        theCeaseCommunications = newValue;
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
                    "theRequestId",
                    "theResponseId",
                    "theSenderIdentifier",
                    "theReceiverIdentifier",
                    "theConveyedMeshObjects",
                    "thePropertyChanges",
                    "theTypeAdditions",
                    "theTypeRemovals",
                    "theCreations",
                    "theDeleteChanges",
                    "theNeighborAdditions",
                    "theNeighborRemovals",
                    "theRoleAdditions",
                    "theRoleRemovals",
                    "thePushLockObjects",
                    "theRequestedFirstTimeObjects",
                    "theRequestedLockObjects",
                    "theReclaimedLockObjects",
                    "theRequestedCanceledObjects",
                    "theRequestedResynchronizeDependentReplicas",
                    "theResynchronizedDependentReplicas",
                    "theCeaseCommunications"
                },
                new Object[] {
                    theRequestId,
                    theResponseId,
                    theSenderIdentifier,
                    theReceiverIdentifier,
                    theConveyedMeshObjects,
                    thePropertyChanges,
                    theTypeAdditions,
                    theTypeRemovals,
                    theCreations,
                    theDeleteChanges,
                    theNeighborAdditions,
                    theNeighborRemovals,
                    theRoleAdditions,
                    theRoleRemovals,
                    thePushLockObjects,
                    theRequestedFirstTimeObjects,
                    theRequestedLockObjects,
                    theReclaimedLockObjects,
                    theRequestedCanceledObjects,
                    theRequestedResynchronizeDependentReplicas,
                    theResynchronizedDependentReplicas,
                    theCeaseCommunications
                },
                StringHelper.LOG_FLAGS.SHOW_NON_NULL | StringHelper.LOG_FLAGS.SHOW_NON_ZERO );
    }

    /**
     * The set of MeshObjects, identified by a NetMeshObjectAccessSpecification, for which the
     * sender would like to obtain a first-time lease.
     */
    protected ArrayList<NetMeshObjectAccessSpecification> theRequestedFirstTimeObjects = new ArrayList<NetMeshObjectAccessSpecification>();
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, for which the
     * sender currently as a lease, but whose lease the sender would like to
     * cancel.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedCanceledObjects = new ArrayList<NetMeshObjectIdentifier>();
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, that have been
     * deleted semantically by the sender, and of whose deletion the receiver
     * needs to be notified.
     */
    protected ArrayList<NetMeshObjectDeletedEvent> theDeleteChanges = new ArrayList<NetMeshObjectDeletedEvent>();
    
    /**
     * The set of MeshObjectCreatedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectCreatedEvent> theCreations = new ArrayList<NetMeshObjectCreatedEvent>();
    
    /**
     * The set of MeshObjects that is being conveyed by the sender to the receiver,
     * e.g. in response to a first-time lease requested.
     */
    protected ArrayList<ExternalizedNetMeshObject> theConveyedMeshObjects = new ArrayList<ExternalizedNetMeshObject>();

    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectNeighborAddedEvent> theNeighborAdditions = new ArrayList<NetMeshObjectNeighborAddedEvent>();
    
    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectNeighborRemovedEvent> theNeighborRemovals = new ArrayList<NetMeshObjectNeighborRemovedEvent>();
    
    /**
     * The set of PropertyChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectPropertyChangeEvent> thePropertyChanges = new ArrayList<NetMeshObjectPropertyChangeEvent>();
    
    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectRoleAddedEvent> theRoleAdditions = new ArrayList<NetMeshObjectRoleAddedEvent>();
    
    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectRoleRemovedEvent> theRoleRemovals = new ArrayList<NetMeshObjectRoleRemovedEvent>();
    
    /**
     * The set of TypeAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectTypeAddedEvent> theTypeAdditions = new ArrayList<NetMeshObjectTypeAddedEvent>();
    
    /**
     * The set of TypeRemovedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectTypeRemovedEvent> theTypeRemovals = new ArrayList<NetMeshObjectTypeRemovedEvent>();
    
    /**
     * The set of MeshObjects, identified by their IdentifierValues, for which the
     * sender requests the lock.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedLockObjects = new ArrayList<NetMeshObjectIdentifier>();
    
    /**
     * The set of MeshObjects, identified by their IdentifierValues, whose lock
     * the sender surrenders to the receiver.
     */
    protected ArrayList<NetMeshObjectIdentifier> thePushLockObjects = new ArrayList<NetMeshObjectIdentifier>();
    
    /**
     * The set of MeshObjects, identified by their IdentifierValues, whose lock
     * the sender has forcefully reclaimed.
     */
    protected ArrayList<NetMeshObjectIdentifier> theReclaimedLockObjects = new ArrayList<NetMeshObjectIdentifier>();
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, whose which the
     * sender requests the homeReplica status.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedHomeReplicas = new ArrayList<NetMeshObjectIdentifier>();

    /**
     * The set of MeshObjects, identified by the MeshObjectIdentifier, whose homeReplica status
     * the sender surrenders to the receiver.
     */
    protected ArrayList<NetMeshObjectIdentifier> thePushHomeReplicas = new ArrayList<NetMeshObjectIdentifier>();
    
    /**
     * The set of MeshObjects, identified by their IdentifierValues, that the sender
     * wishes to resynchronize as dependent replicas.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedResynchronizeDependentReplicas = new ArrayList<NetMeshObjectIdentifier>();

    /**
     * The set of MeshObjects that are being sent by the sender to the receiver in
     * response to a synchronizedDependent request.
     */
    protected ArrayList<ExternalizedNetMeshObject> theResynchronizedDependentReplicas = new ArrayList<ExternalizedNetMeshObject>();
}
