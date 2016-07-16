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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.xpriso;

import java.util.ArrayList;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;

/**
 * An XprisoMessage that is suitable for parsers that pick up one item at a time, instead of
 * all of them at the same time.
 */
public class ParserFriendlyXprisoMessage
        extends
            AbstractXprisoMessage
        implements
            CanBeDumped
{
    private static final long serialVersionUID = 1L; // helps with serialization
    private static final Log  log              = Log.getLogInstance( ParserFriendlyXprisoMessage.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param sender identifies the sender of this message
     * @param receiver identifies the receiver of this message
     * @return the created ParserFriendlyXprisoMessage
     */
    public static ParserFriendlyXprisoMessage create(
            NetMeshBaseIdentifier sender,
            NetMeshBaseIdentifier receiver )
    {
        if( sender == null ) {
            throw new NullPointerException();
        }
        if( receiver == null ) {
            throw new NullPointerException();
        }
        ParserFriendlyXprisoMessage ret = new ParserFriendlyXprisoMessage( sender, receiver );

        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( ret, "constructor" );
        }
        return ret;
    }

    /**
     * Constructor.
     *
     * @param sender identifies the sender of this message
     * @param receiver identifies the receiver of this message
     */
    protected ParserFriendlyXprisoMessage(
            NetMeshBaseIdentifier sender,
            NetMeshBaseIdentifier receiver )
    {
        super( sender, receiver );
    }

    /**
     * Set the request ID.
     *
     * @param id the request ID
     */
    @Override
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
    @Override
    public void setResponseId(
            long id )
    {
        theResponseId = id;
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
     * Add an externalized representation of a NetMeshObject that is conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @param toAdd the ExternalizedNetMeshObject
     */
    public void addConveyedMeshObject(
            ExternalizedNetMeshObject toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theConveyedMeshObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theConveyedMeshObjects.add( toAdd );
    }

    /**
     * Add several externalized representations of NetMeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @param toAdd the ExternalizedNetMeshObjects
     */
    public void addConveyedMeshObjects(
            ExternalizedNetMeshObject [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theConveyedMeshObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theConveyedMeshObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the externalized representation of the NetMeshObjects that are conveyed
     * by the sender to the receiver, e.g. in response to a first-time lease request.
     *
     * @return the ExternalizedNetMeshObjects
     */
    @Override
    public ExternalizedNetMeshObject [] getConveyedMeshObjects()
    {
        ExternalizedNetMeshObject [] ret = ArrayHelper.copyIntoNewArray( theConveyedMeshObjects, ExternalizedNetMeshObject.class );
        return ret;
    }

    /**
     * Add a NetMeshObjectAccessSpecification to a NetMeshObject for which the sender requests
     * a lease for the first time.
     *
     * @param toAdd the NetMeshObjectAccessSpecification to the NetMeshObject
     */
    public void addRequestedFirstTimeObject(
            NetMeshObjectAccessSpecification toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedFirstTimeObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedFirstTimeObjects.add( toAdd );
    }

    /**
     * Add several NetMeshObjectAccessSpecifications to NetMeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @param toAdd the NetMeshObjectAccessSpecifications to the NetMeshObjects
     */
    public void addRequestedFirstTimeObjects(
            NetMeshObjectAccessSpecification [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedFirstTimeObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedFirstTimeObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the NetMeshObjectAccessSpecifications to the NetMeshObjects for which the sender requests
     * a lease for the first time.
     *
     * @return the NetMeshObjectAccessSpecifications for the NetMeshObjects
     */
    @Override
    public NetMeshObjectAccessSpecification[] getRequestedFirstTimeObjects()
    {
        NetMeshObjectAccessSpecification [] ret = ArrayHelper.copyIntoNewArray( theRequestedFirstTimeObjects, NetMeshObjectAccessSpecification.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObjects
     */
    public void addRequestedCanceledObject(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedCanceledObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedCanceledObjects.add( toAdd );
    }

    /**
     * Add several identifiers for NetMeshObjects for which the sender requests
     * that a currently valid lease be chanceled.
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjectss
     */
    public void addRequestedCanceledObjects(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedCanceledObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedCanceledObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender requests
     * that a currently valid lease be canceled.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getRequestedCanceledObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedCanceledObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender requests
     * a freshening.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addRequestedFreshenReplica(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedFreshenReplicas.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedFreshenReplicas.add( toAdd );
    }

    /**
     * Add several identifiers for the NetMeshObjects for which the sender requests
     * a freshening.
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addRequestedFreshenReplicas(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedFreshenReplicas.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedFreshenReplicas.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender requests
     * a freshening.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getRequestedFreshenReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedFreshenReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender has a replica
     * that it wishes to resynchronize.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addRequestedResynchronizeReplica(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedResynchronizeReplicas.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedResynchronizeReplicas.add( toAdd );
    }

    /**
     * Add several identifiers for NetMeshObjects for which the sender has replicas
     * that it wishes to resynchronize.
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addRequestedResynchronizeReplicas(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedResynchronizeReplicas.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedResynchronizeReplicas.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender has a replica
     * that it wishes to resynchronize.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getRequestedResynchronizeReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedResynchronizeReplicas, NetMeshObjectIdentifier.class  );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addRequestedLockObject(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedLockObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedLockObjects.add( toAdd );
    }

    /**
     * Add several identifiers for NetMeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addRequestedLockObjects(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedLockObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedLockObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender requests
     * the lock from the receiver (i.e. update rights).
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getRequestedLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addPushLockObject(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( thePushLockObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        thePushLockObjects.add( toAdd );
    }

    /**
     * Add several identifiers for NetMeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addPushLockObjects(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( thePushLockObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            thePushLockObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender surrenders
     * the lock to the receiver (i.e. update rights).
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getPushLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( thePushLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender has forcefully
     * reclaimed the lock.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addReclaimedLockObject(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theReclaimedLockObjects.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theReclaimedLockObjects.add( toAdd );
    }

    /**
     * Add several identifiers for the NetMeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addReclaimedLockObjects(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theReclaimedLockObjects.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theReclaimedLockObjects.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender has forcefully
     * reclaimed the lock.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getReclaimedLockObjects()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theReclaimedLockObjects, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender requests
     * home replica status.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addRequestedHomeReplica(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRequestedHomeReplicas.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRequestedHomeReplicas.add( toAdd );
    }

    /**
     * Add several identifiers for the NetMeshObjects for which the sender requests
     * home replica status.
     *
     * @param toAdd the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    public void addRequestedHomeReplicas(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRequestedHomeReplicas.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRequestedHomeReplicas.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender requests
     * home replica status.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getRequestedHomeReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( theRequestedHomeReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add an identifier for a NetMeshObject for which the sender surrenders
     * the home replica status to the receiver.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addPushHomeReplica(
            NetMeshObjectIdentifier toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( thePushHomeReplicas.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        thePushHomeReplicas.add( toAdd );
    }

    /**
     * Add several identifiers for the NetMeshObjects for which the sender surrenders
     * the home replica status to the receiver.
     *
     * @param toAdd the NetMeshObjectIdentifier for the NetMeshObject
     */
    public void addPushHomeReplicas(
            NetMeshObjectIdentifier [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( thePushHomeReplicas.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            thePushHomeReplicas.add( toAdd[i] );
        }
    }

    /**
     * Obtain the identifiers for the NetMeshObjects for which the sender surrenders
     * the home replica status to the receiver.
     *
     * @return the NetMeshObjectIdentifiers for the NetMeshObjects
     */
    @Override
    public NetMeshObjectIdentifier [] getPushHomeReplicas()
    {
        NetMeshObjectIdentifier [] ret = ArrayHelper.copyIntoNewArray( thePushHomeReplicas, NetMeshObjectIdentifier.class );
        return ret;
    }

    /**
     * Add a type added event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the type addition event
     */
    public void addTypeAddition(
            NetMeshObjectTypeAddedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theTypeAdditions.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theTypeAdditions.add( toAdd );
    }

    /**
     * Add several type added events that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the type addition events
     */
    public void addTypeAdditions(
            NetMeshObjectTypeAddedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theTypeAdditions.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theTypeAdditions.add( toAdd[i] );
        }
    }

    /**
     * Obtain the type addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the type addition events
     */
    @Override
    public NetMeshObjectTypeAddedEvent [] getTypeAdditions()
    {
        NetMeshObjectTypeAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theTypeAdditions, NetMeshObjectTypeAddedEvent.class );
        return ret;
    }

    /**
     * Add a type removed event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the type removed event
     */
    public void addTypeRemoval(
            NetMeshObjectTypeRemovedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theTypeRemovals.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theTypeRemovals.add( toAdd );
    }

    /**
     * Add several type removed events that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the type removed event
     */
    public void addTypeRemovals(
            NetMeshObjectTypeRemovedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theTypeRemovals.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theTypeRemovals.add( toAdd[i] );
        }
    }

    /**
     * Obtain the type removal events that the sender needs to convey to the
     * receiver.
     *
     * @return the type removal events
     */
    @Override
    public NetMeshObjectTypeRemovedEvent [] getTypeRemovals()
    {
        NetMeshObjectTypeRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theTypeRemovals, NetMeshObjectTypeRemovedEvent.class );
        return ret;
    }

    /**
     * Add a property change event that the sender needs to convey to the
     * received.
     *
     * @param toAdd the property change event
     */
    public void addPropertyChange(
            NetMeshObjectPropertyChangeEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( thePropertyChanges.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        thePropertyChanges.add( toAdd );
    }

    /**
     * Add several property change events that the sender needs to convey to the
     * received.
     *
     * @param toAdd the property change events
     */
    public void addPropertyChanges(
            NetMeshObjectPropertyChangeEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( thePropertyChanges.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            thePropertyChanges.add( toAdd[i] );
        }
    }

    /**
     * Obtain the property change events that the sender needs to convey to the
     * receiver.
     *
     * @return the property change events
     */
    @Override
    public NetMeshObjectPropertyChangeEvent [] getPropertyChanges()
    {
        NetMeshObjectPropertyChangeEvent [] ret = ArrayHelper.copyIntoNewArray( thePropertyChanges, NetMeshObjectPropertyChangeEvent.class );
        return ret;
    }

    /**
     * Add a neighbor addition event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the neighbor addition event
     */
    public void addNeighborAddition(
            NetMeshObjectNeighborAddedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theNeighborAdditions.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theNeighborAdditions.add( toAdd );
    }

    /**
     * Add several neighbor addition events that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the neighbor addition events
     */
    public void addNeighborAdditions(
            NetMeshObjectNeighborAddedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theNeighborAdditions.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theNeighborAdditions.add( toAdd[i] );
        }
    }

    /**
     * Obtain the neighbor addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor addition events
     */
    @Override
    public NetMeshObjectNeighborAddedEvent [] getNeighborAdditions()
    {
        NetMeshObjectNeighborAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theNeighborAdditions, NetMeshObjectNeighborAddedEvent.class );
        return ret;
    }

    /**
     * Add a neighbor removal event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the neighbor removal events
     */
    public void addNeighborRemoval(
            NetMeshObjectNeighborRemovedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theNeighborRemovals.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theNeighborRemovals.add( toAdd );
    }

    /**
     * Add several neighbor removal events that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the neighbor removal events
     */
    public void addNeighborRemovals(
            NetMeshObjectNeighborRemovedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theNeighborRemovals.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theNeighborRemovals.add( toAdd[i] );
        }
    }

    /**
     * Obtain the neighbor removal events that the sender needs to convey to the
     * receiver.
     *
     * @return the neighbor removal events
     */
    @Override
    public NetMeshObjectNeighborRemovedEvent [] getNeighborRemovals()
    {
        NetMeshObjectNeighborRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theNeighborRemovals, NetMeshObjectNeighborRemovedEvent.class );
        return ret;
    }

    /**
     * Add a role addition event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the role addition event
     */
    public void addRoleAddition(
            NetMeshObjectRoleAddedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRoleAdditions.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRoleAdditions.add( toAdd );
    }

    /**
     * Add several role addition events that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the role addition events
     */
    public void addRoleAdditions(
            NetMeshObjectRoleAddedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRoleAdditions.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRoleAdditions.add( toAdd[i] );
        }
    }

    /**
     * Obtain the role addition events that the sender needs to convey to the
     * receiver.
     *
     * @return the role addition events
     */
    @Override
    public NetMeshObjectRoleAddedEvent [] getRoleAdditions()
    {
        NetMeshObjectRoleAddedEvent [] ret = ArrayHelper.copyIntoNewArray( theRoleAdditions, NetMeshObjectRoleAddedEvent.class );
        return ret;

    }

    /**
     * Add a role removal event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the role removal event
     */
    public void addRoleRemoval(
            NetMeshObjectRoleRemovedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theRoleRemovals.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theRoleRemovals.add( toAdd );
    }

    /**
     * Add several role removal event that the sender needs to convey to the
     * receiver.
     *
     * @param toAdd the role removal events
     */
    public void addRoleRemovals(
            NetMeshObjectRoleRemovedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theRoleRemovals.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theRoleRemovals.add( toAdd[i] );
        }
    }

    /**
     * Obtain the role removal events that the sender needs to convey to the
     * receiver.
     *
     * @return the role removal events
     */
    @Override
    public NetMeshObjectRoleRemovedEvent [] getRoleRemovals()
    {
        NetMeshObjectRoleRemovedEvent [] ret = ArrayHelper.copyIntoNewArray( theRoleRemovals, NetMeshObjectRoleRemovedEvent.class );
        return ret;
    }

    /**
     * Add a deletion event for the NetMeshObject that the sender has deleted
     * semantically and that the sender needs to convey to the receiver.
     *
     * @param toAdd the deletion event
     */
    public void addDeleteChange(
            NetMeshObjectDeletedEvent toAdd )
    {
        if( toAdd == null ) {
            throw new NullPointerException();
        }
        if( theDeleteChanges.contains( toAdd )) {
            throw new IllegalStateException( "Have element already: " + toAdd );
        }
        theDeleteChanges.add( toAdd );
    }

    /**
     * Add several deletion events for NetMeshObjects that the sender has deleted
     * semantically and that the sender needs to convey to the receiver.
     *
     * @param toAdd the deletion events
     */
    public void addDeleteChanges(
            NetMeshObjectDeletedEvent [] toAdd )
    {
        for( int i=0 ; i<toAdd.length ; ++i ) {
            if( toAdd[i] == null ) {
                throw new NullPointerException();
            }
            if( theDeleteChanges.contains( toAdd[i] )) {
                throw new IllegalStateException( "Have element already: " + toAdd[i] );
            }
            theDeleteChanges.add( toAdd[i] );
        }
    }

    /**
     * Obtain the deletion events for the NetMeshObjects that the sender has deleted
     * semantically and that the sender needs to convey to the receiver.
     *
     * @return the deletion events
     */
    @Override
    public NetMeshObjectDeletedEvent [] getDeletions()
    {
        NetMeshObjectDeletedEvent [] ret = ArrayHelper.copyIntoNewArray( theDeleteChanges, NetMeshObjectDeletedEvent.class );
        return ret;
    }

    /**
     * Determine whether this message contains any valid payload or is empty.
     *
     * @return true if it is empty
     */
    @Override
    public boolean isEmpty()
    {
        // ignore theSenderIdentifier;
        // ignore theReceiverIdentifier;
        // ignore theRequestId;
        // do NOT ignore responseID: may acknowledge receipt of incoming message

        // alphabetically, so we can make sure we have all of them by comparing with the IDE

        if( theCeaseCommunications ) {
            return false;
        }
        if( theConveyedMeshObjects.size() > 0 ) {
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
        if( theRequestedFreshenReplicas.size() > 0 ) {
            return false;
        }
        if( theRequestedHomeReplicas.size() > 0 ) {
            return false;
        }
        if( theRequestedLockObjects.size() > 0 ) {
            return false;
        }
        if( theRequestedResynchronizeReplicas.size() > 0 ) {
            return false;
        }
        // do NOT ignore responseID: may acknowledge receipt of incoming message
        if( theResponseId != 0 ) {
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
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "theSenderIdentifier",
                    "theReceiverIdentifier",
                    "theRequestId",
                    "theResponseId",
                    "theCeaseCommunications",
                    "theConveyedMeshObjects",
                    "theDeleteChanges",
                    "theNeighborAdditions",
                    "theNeighborRemovals",
                    "thePropertyChanges",
                    "thePushHomeReplicas",
                    "thePushLockObjects",
                    "theReclaimedLockObjects",
                    "theRequestedCanceledObjects",
                    "theRequestedFirstTimeObjects",
                    "theRequestedFreshenReplicas",
                    "theRequestedHomeReplicas",
                    "theRequestedLockObjects",
                    "theRequestedResynchronizeReplicas",
                    "theRoleAdditions",
                    "theRoleRemovals",
                    "theTypeAdditions",
                    "theTypeRemovals",
                },
                new Object[] {
                    theSenderIdentifier,
                    theReceiverIdentifier,
                    theRequestId,
                    theResponseId,
                    theCeaseCommunications,
                    theConveyedMeshObjects,
                    theDeleteChanges,
                    theNeighborAdditions,
                    theNeighborRemovals,
                    thePropertyChanges,
                    thePushHomeReplicas,
                    thePushLockObjects,
                    theReclaimedLockObjects,
                    theRequestedCanceledObjects,
                    theRequestedFirstTimeObjects,
                    theRequestedFreshenReplicas,
                    theRequestedHomeReplicas,
                    theRequestedLockObjects,
                    theRequestedResynchronizeReplicas,
                    theRoleAdditions,
                    theRoleRemovals,
                    theTypeAdditions,
                    theTypeRemovals,
                });
    }

    /**
     * The set of MeshObjects that is being conveyed by the sender to the receiver,
     * e.g. in response to a first-time lease requested.
     */
    protected ArrayList<ExternalizedNetMeshObject> theConveyedMeshObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their NetMeshObjectAccessSpecifications, for which the
     * sender would like to obtain a first-time lease.
     */
    protected ArrayList<NetMeshObjectAccessSpecification> theRequestedFirstTimeObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, for which the
     * sender currently has a lease, but whose lease the sender would like to
     * cancel.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedCanceledObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, for which the
     * sender requests a freshening.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedFreshenReplicas = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, that the sender
     * wishes to resynchronize as dependent replicas.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedResynchronizeReplicas = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, for which the
     * sender requests the lock.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedLockObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, whose lock
     * the sender surrenders to the receiver.
     */
    protected ArrayList<NetMeshObjectIdentifier> thePushLockObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, whose lock
     * the sender has forcefully reclaimed.
     */
    protected ArrayList<NetMeshObjectIdentifier> theReclaimedLockObjects = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifiers, whose which the
     * sender requests the homeReplica status.
     */
    protected ArrayList<NetMeshObjectIdentifier> theRequestedHomeReplicas = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by the MeshObjectIdentifiers, whose homeReplica status
     * the sender surrenders to the receiver.
     */
    protected ArrayList<NetMeshObjectIdentifier> thePushHomeReplicas = new ArrayList<>();

    /**
     * The set of TypeAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectTypeAddedEvent> theTypeAdditions = new ArrayList<>();

    /**
     * The set of TypeRemovedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectTypeRemovedEvent> theTypeRemovals = new ArrayList<>();

    /**
     * The set of PropertyChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectPropertyChangeEvent> thePropertyChanges = new ArrayList<>();

    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectNeighborAddedEvent> theNeighborAdditions = new ArrayList<>();

    /**
     * The set of NeighborAddedEvents that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectNeighborRemovedEvent> theNeighborRemovals = new ArrayList<>();

    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectRoleAddedEvent> theRoleAdditions = new ArrayList<>();

    /**
     * The set of RoleChanges that the sender needs to convey to the receiver.
     */
    protected ArrayList<NetMeshObjectRoleRemovedEvent> theRoleRemovals = new ArrayList<>();

    /**
     * The set of MeshObjects, identified by their MeshObjectIdentifier, that have been
     * deleted semantically by the sender, and of whose deletion the receiver
     * needs to be notified.
     */
    protected ArrayList<NetMeshObjectDeletedEvent> theDeleteChanges = new ArrayList<>();
}
