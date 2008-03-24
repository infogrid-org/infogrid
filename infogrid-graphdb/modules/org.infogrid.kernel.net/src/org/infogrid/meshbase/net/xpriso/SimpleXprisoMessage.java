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

import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;

import org.infogrid.util.StringHelper;

import java.io.Serializable;

/**
 * This captures the information exchanged between Proxies.
 */
public class SimpleXprisoMessage
        extends
            AbstractXprisoMessage
        implements
            XprisoMessage,
            Serializable
{
    /**
     * Create a blank SimpleXprisoMessage.
     */
    public static SimpleXprisoMessage create()
    {
        return new SimpleXprisoMessage();
    }

    /**
     * Constructor.
     */
    protected SimpleXprisoMessage()
    {
        // no op
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
        if( theConveyedMeshObjects != null && theConveyedMeshObjects.length > 0 ) {
            return false;
        }
        if( theCreations != null && theCreations.length > 0 ) {
            return false;
        }
        if( theDeleteChanges != null && theDeleteChanges.length > 0 ) {
            return false;
        }
        if( theNeighborAdditions != null && theNeighborAdditions.length > 0 ) {
            return false;
        }
        if( theNeighborRemovals != null && theNeighborRemovals.length > 0 ) {
            return false;
        }
        if( thePropertyChanges != null && thePropertyChanges.length > 0 ) {
            return false;
        }
        if( thePushLockObjects != null && thePushLockObjects.length > 0 ) {
            return false;
        }
        // ignore theReceiverIdentifier;
        if( theReclaimedLockObjects != null && theReclaimedLockObjects.length > 0 ) {
            return false;
        }
        // ignore theRequestId;
        if( theRequestedCanceledObjects != null && theRequestedCanceledObjects.length > 0 ) {
            return false;
        }
        if( theRequestedFirstTimeObjects != null && theRequestedFirstTimeObjects.length > 0 ) {
            return false;
        }
        if( theRequestedLockObjects != null && theRequestedLockObjects.length > 0 ) {
            return false;
        }
        if( theRequestedResynchronizeDependentReplicas != null && theRequestedResynchronizeDependentReplicas.length > 0 ) {
            return false;
        }
        // ignore theResponseId
        if( theResynchronizedDependentReplicas != null && theResynchronizedDependentReplicas.length > 0 ) {
            return false;
        }
        if( theRoleAdditions != null && theRoleAdditions.length > 0 ) {
            return false;
        }
        if( theRoleRemovals != null && theRoleRemovals.length > 0 ) {
            return false;
        }
        // ignore theSenderIdentifier;        
        if( theTypeAdditions != null && theTypeAdditions.length > 0 ) {
            return false;
        }
        if( theTypeRemovals != null && theTypeRemovals.length > 0 ) {
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
    public void setRequestedFirstTimeObjects(
            NetMeshObjectAccessSpecification[] newValue )
    {
        theRequestedFirstTimeObjects = newValue;
    }
    
    /**
     * Obtain the NetworkPaths to the MeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @return the NetworkPaths for the MeshObjects
     */
    public NetMeshObjectAccessSpecification[] getRequestedFirstTimeObjects()
    {
        return theRequestedFirstTimeObjects;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @param newValue the identifiers for the MeshObjects
     */
    public void setRequestedCanceledObjects(
            MeshObjectIdentifier[] newValue )
    {
        theRequestedCanceledObjects = newValue;
    }
    
    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedCanceledObjects()
    {
        return theRequestedCanceledObjects;
    }

    /**
     * Set the creation events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the creation events
     */
    public void setCreations(
            NetMeshObjectCreatedEvent [] newValue )
    {
        theCreations = newValue;
    }
    
    /**
     * Obtain the creation events that the sender needs to convey to the
     * receiver
     *
     * @return the creation events
     */
    public NetMeshObjectCreatedEvent [] getCreations()
    {
        return theCreations;
    }

    /**
     * Set the identifiers for the MeshObjects that the sender has deleted
     * semantically, and of whose deletion the receivers needs to be notified.
     *
     * @param newValue the identifiers for the MeshObjects
     */
    public void setDeleteChanges(
            NetMeshObjectDeletedEvent [] newValue )
    {
        theDeleteChanges = newValue;
    }

    /**
     * Obtain the identifiers for the MeshObjects that the sender has deleted
     * semantically, and of whose deletion the receivers needs to be notified.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public NetMeshObjectDeletedEvent [] getDeleteChanges()
    {
        return theDeleteChanges;
    }

    /**
     * Set the externalized representation of the MeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @param newValue the MeshObjects
     */
    public void setConveyedMeshObjects(
            ExternalizedNetMeshObject [] newValue )
    {
        theConveyedMeshObjects = newValue;
    }

    /**
     * Obtain the externalized representation of the MeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject[] getConveyedMeshObjects()
    {
        return theConveyedMeshObjects;
    }

    /**
     * Set the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the neighbor change events
     */
    public void setNeighborAdditions(
            NetMeshObjectNeighborAddedEvent [] newValue )
    {
        theNeighborAdditions = newValue;
    }

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborAddedEvent [] getNeighborAdditions()
    {
        return theNeighborAdditions;
    }

    /**
     * Set the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the neighbor change events
     */
    public void setNeighborRemovals(
            NetMeshObjectNeighborRemovedEvent [] newValue )
    {
        theNeighborRemovals = newValue;
    }

    /**
     * Obtain the neighbor change events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor change events
     */
    public NetMeshObjectNeighborRemovedEvent [] getNeighborRemovals()
    {
        return theNeighborRemovals;
    }

    /**
     * Set the property change events that the sender needs to convey to the
     * received.
     *
     * @param newValue the property change events
     */
    public void setPropertyChanges(
            NetMeshObjectPropertyChangeEvent [] newValue )
    {
        thePropertyChanges = newValue;
    }

    /**
     * Obtain the property change events that the sender needs to convey to the
     * receiver.
     *
     * @return the property change events
     */
    public NetMeshObjectPropertyChangeEvent [] getPropertyChanges()
    {
        return thePropertyChanges;
    }

    /**
     * Set the role change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the role change events
     */
    public void setRoleAdditions(
            NetMeshObjectRoleAddedEvent [] newValue )
    {
        theRoleAdditions = newValue;
    }

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleAddedEvent [] getRoleAdditions()
    {
        return theRoleAdditions;
    }

    /**
     * Set the role change events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the role change events
     */
    public void setRoleRemovals(
            NetMeshObjectRoleRemovedEvent [] newValue )
    {
        theRoleRemovals = newValue;
    }

    /**
     * Obtain the role change events that the sender needs to convey to the
     * receiver.
     *
     * @return the role change events
     */
    public NetMeshObjectRoleRemovedEvent [] getRoleRemovals()
    {
        return theRoleRemovals;
    }

    /**
     * Set the type added events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the type addition events
     */
    public void setTypeAdditions(
            NetMeshObjectTypeAddedEvent [] newValue )
    {
        theTypeAdditions = newValue;
    }

    /**
     * Obtain the type addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the type addition events
     */
    public NetMeshObjectTypeAddedEvent [] getTypeAdditions()
    {
        return theTypeAdditions;
    }

    /**
     * Set the type removed events that the sender needs to convey to the
     * receiver.
     *
     * @param newValue the type removed events
     */
    public void setTypeRemovals(
            NetMeshObjectTypeRemovedEvent [] newValue )
    {
        theTypeRemovals = newValue;
    }

    /**
     * Obtain the type addition removed that the sender needs to convey to the
     * receiver.
     *
     * @return the type removed events
     */
    public NetMeshObjectTypeRemovedEvent [] getTypeRemovals()
    {
        return theTypeRemovals;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @param newValue the IdentifierValues for the MeshObjects
     */
    public void setRequestedLockObjects(
            MeshObjectIdentifier[] newValue )
    {
        theRequestedLockObjects = newValue;
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedLockObjects()
    {
        return theRequestedLockObjects;
    }

    /**
     * Set the identifiers for the MeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @param newValue the IdentifierValues for the MeshObjects
     */
    public void setPushLockObjects(
            MeshObjectIdentifier[] newValue )
    {
        thePushLockObjects = newValue;
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getPushLockObjects()
    {
        return thePushLockObjects;
    }
    
    /**
     * Set the identifiers for the MeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @param newValue the IdentifierValues for the MeshObjects
     */
    public void setReclaimedLockObjects(
            MeshObjectIdentifier[] newValue )
    {
        theReclaimedLockObjects = newValue;
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getReclaimedLockObjects()
    {
        return theReclaimedLockObjects;
    }
    
    /**
     * Set the identifiers for the MeshObjects for which the sender has a replica
     * that it wishes to resynchronize as a dependent replica.
     *
     * @param newValue the IdentifierValues for the MeshObjects
     */
    public void setRequestedResynchronizeDependentReplicas(
            MeshObjectIdentifier[] newValue )
    {
        theRequestedResynchronizeDependentReplicas = newValue;
    }

    /**
     * Obtain the identifiers for the MeshObjects for which the sender has a replica
     * that it wishes to resynchronize as a dependent replica.
     *
     * @return the IdentifierValues for the MeshObjects
     */
    public MeshObjectIdentifier[] getRequestedResynchronizeDependentReplicas()
    {
        return theRequestedResynchronizeDependentReplicas;
    }

    /**
     * Set the externalized representation of the MeshObjects that are sent
     * by the sender to the receiver in response to a resynchronizeDependent request.
     *
     * @param newValue the MeshObjects
     */
    public void setResynchronizeDependentReplicas(
            ExternalizedNetMeshObject [] newValue )
    {
        theResynchronizedDependentReplicas = newValue;
    }

    /**
     * Obtain the externalized representation of the MeshObjects that are sent
     * by the sender to the receiver in response to a resynchronizeDependent request.
     *
     * @return the MeshObjects
     */
    public ExternalizedNetMeshObject [] getResynchronizeDependentReplicas()
    {
        return theResynchronizedDependentReplicas;
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
    protected NetMeshObjectAccessSpecification[] theRequestedFirstTimeObjects = NetMeshObjectAccessSpecification.EMPTY_ARRAY;
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, for which the
     * sender currently as a lease, but whose lease the sender would like to
     * cancel.
     */
    protected MeshObjectIdentifier[] theRequestedCanceledObjects = MeshObjectIdentifier.EMPTY_ARRAY;
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, that have been
     * deleted semantically by the sender, and of whose deletion the receiver
     * needs to be notified.
     */
    protected NetMeshObjectDeletedEvent [] theDeleteChanges = {};
    
    /**
     * The set of MeshObjectCreatedEvents that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectCreatedEvent [] theCreations = {};
    
    /**
     * The set of MeshObjects that is being conveyed by the sender to the receiver,
     * e.g. in response to a first-time lease requested.
     */
    protected ExternalizedNetMeshObject[] theConveyedMeshObjects = {};

    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectNeighborAddedEvent [] theNeighborAdditions = {};
    
    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectNeighborRemovedEvent [] theNeighborRemovals = {};
    
    /**
     * The set of PropertyChanges that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectPropertyChangeEvent [] thePropertyChanges = {};
    
    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectRoleAddedEvent [] theRoleAdditions = {};
    
    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectRoleRemovedEvent [] theRoleRemovals = {};
    
    /**
     * The set of TypeAddedEvents that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectTypeAddedEvent [] theTypeAdditions = {};
    
    /**
     * The set of TypeRemovedEvents that the sender needs to convey to the receiver.
     */
    protected NetMeshObjectTypeRemovedEvent [] theTypeRemovals = {};
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, for which the
     * sender requests the lock.
     */
    protected MeshObjectIdentifier[] theRequestedLockObjects = MeshObjectIdentifier.EMPTY_ARRAY;
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, whose lock
     * the sender surrenders to the receiver.
     */
    protected MeshObjectIdentifier[] thePushLockObjects = MeshObjectIdentifier.EMPTY_ARRAY;
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, whose lock
     * the sender has forcefully reclaimed.
     */
    protected MeshObjectIdentifier[] theReclaimedLockObjects = MeshObjectIdentifier.EMPTY_ARRAY;
    
    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, that the sender
     * wishes to resynchronize as dependent replicas.
     */
    protected MeshObjectIdentifier[] theRequestedResynchronizeDependentReplicas = MeshObjectIdentifier.EMPTY_ARRAY;

    /**
     * The set of MeshObjects that are being sent by the sender to the receiver in
     * response to a synchronizedDependent request.
     */
    protected ExternalizedNetMeshObject[] theResynchronizedDependentReplicas = {};
}
