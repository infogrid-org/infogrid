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

package org.infogrid.meshbase.net.proxy;

import java.util.ArrayList;
import org.infogrid.comm.MessageEndpoint;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectEquivalentsAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectEquivalentsRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
import org.infogrid.meshbase.net.xpriso.XprisoMessage;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

/**
 * Instructions for processing, as determined by the ProxyPolicy.
 * 
 * FIXME: make sure we can't set the same value twice
 */
public class ProxyProcessingInstructions
{
    private static final Log log = Log.getLogInstance( ProxyProcessingInstructions.class ); // our own, private logger

    /**
     * Factory.
     * 
     * @return the DefaultProxyProcessingInstructions
     */
    public static ProxyProcessingInstructions create()
    {
        ProxyProcessingInstructions ret = new ProxyProcessingInstructions();
        
        return ret;
    }
    
    /**
     * Constructor.
     */
    protected ProxyProcessingInstructions()
    {
    }

    public void check()
    {
        // FIXME: Make internal consistency check
    }

    /**
     * Set whether the Proxy should cease communications.
     * 
     * @param newValue true if the Proxy should cease communications
     */
    public void setCeaseCommunications(
            boolean newValue )
    {
        theCeaseCommunications = newValue;
    }

    /**
     * Determine whether the Proxy should cease communications.
     * 
     * @return returns true if the Proxy should cease communications
     */
    public boolean getCeaseCommunications()
    {
        return theCeaseCommunications;
    }

    public void setStartCommunicating(
            boolean newValue )
    {
        theStartCommunicating = newValue;
    }
    public boolean getStartCommunicating()
    {
        return theStartCommunicating;
    }

//

    /**
     * Set the XprisoMessage to be sent via the WaitForReplicaResponseEndpoint.
     * 
     * @param outgoing the outgoing XprisoMessage
     */
    public void setSendViaWaitForReplicaResponseEndpoint(
            XprisoMessage outgoing )
    {
        if( theSendViaWaitForReplicaResponseEndpoint != null ) {
            throw new IllegalStateException( "Invoked a second time" );
        }
        theSendViaWaitForReplicaResponseEndpoint = outgoing;
    }
    
    /**
     * Obtain the XprisoMessage to be sent via the WaitForReplicaResponseEndpoint.
     * 
     * @return the outgoing XprisoMessage
     */
    public XprisoMessage getSendViaWaitForReplicaResponseEndpoint()
    {
        return theSendViaWaitForReplicaResponseEndpoint;
    }
    
    /**
     * Set the timeout for the WaitForReplicaResponseEndpoint in this operation.
     * 
     * @param newValue the timeout, in milliseconds
     */
    public void setWaitForReplicaResponseEndpointTimeout(
            long newValue )
    {
        theWaitForReplicaResponseEndpointTimeout = newValue;
    }
    
    /**
     * Obtain the timeout for the WaitForReplicaResponseEndpoint in this operation.
     * 
     * @return the timeout, in milliseconds
     */
    public long getWaitForReplicaResponseEndpointTimeout()
    {
        return theWaitForReplicaResponseEndpointTimeout;
    }
    
//

    /**
     * Set the XprisoMessage to be sent via the WaitForLockResponseEndpoint.
     * 
     * @param outgoing the outgoing XprisoMessage
     */
    public void setSendViaWaitForLockResponseEndpoint(
            XprisoMessage outgoing )
    {
        if( theSendViaWaitForLockResponseEndpoint != null ) {
            throw new IllegalStateException( "Invoked a second time" );
        }
        theSendViaWaitForLockResponseEndpoint = outgoing;
    }
    
    /**
     * Obtain the XprisoMessage to be sent via the WaitForLockResponseEndpoint.
     * 
     * @return the outgoing XprisoMessage
     */
    public XprisoMessage getSendViaWaitForLockResponseEndpoint()
    {
        return theSendViaWaitForLockResponseEndpoint;
    }
    
    /**
     * Set the timeout for the WaitForLockResponseEndpoint in this operation.
     * 
     * @param newValue the timeout, in milliseconds
     */
    public void setWaitForLockResponseEndpointTimeout(
            long newValue )
    {
        theWaitForLockResponseEndpointTimeout = newValue;
    }
    
    /**
     * Obtain the timeout for the WaitForLockResponseEndpoint in this operation.
     * 
     * @return the timeout, in milliseconds
     */
    public long getWaitForLockResponseEndpointTimeout()
    {
        return theWaitForLockResponseEndpointTimeout;
    }
    
//

    /**
     * Set the XprisoMessage to be sent via the WaitForHomeResponseEndpoint.
     * 
     * @param outgoing the outgoing XprisoMessage
     */
    public void setSendViaWaitForHomeResponseEndpoint(
            XprisoMessage outgoing )
    {
        if( theSendViaWaitForHomeResponseEndpoint != null ) {
            throw new IllegalStateException( "Invoked a second time" );
        }
        theSendViaWaitForHomeResponseEndpoint = outgoing;
    }
    
    /**
     * Obtain the XprisoMessage to be sent via the WaitForHomeResponseEndpoint.
     * 
     * @return the outgoing XprisoMessage
     */
    public XprisoMessage getSendViaWaitForHomeResponseEndpoint()
    {
        return theSendViaWaitForHomeResponseEndpoint;
    }
    
    /**
     * Set the timeout for the WaitForHomeResponseEndpoint in this operation.
     * 
     * @param newValue the timeout, in milliseconds
     */
    public void setWaitForHomeResponseEndpointTimeout(
            long newValue )
    {
        theWaitForHomeResponseEndpointTimeout = newValue;
    }
    
    /**
     * Obtain the timeout for the WaitForHomeResponseEndpoint in this operation.
     * 
     * @return the timeout, in milliseconds
     */
    public long getWaitForHomeResponseEndpointTimeout()
    {
        return theWaitForHomeResponseEndpointTimeout;
    }
    
//
    
    public void setSendViaEndpoint(
            XprisoMessage outgoing )
    {
        if( theSendViaEndpoint != null ) {
            throw new IllegalStateException( "Invoked a second time" );
        }
        theSendViaEndpoint = outgoing;
    }
    public XprisoMessage getSendViaEndpoint()
    {
        return theSendViaEndpoint;
    }

//
    
    
    /**
     * Set the paths of NetMeshObjects requested for the first time.
     * 
     * @param newValue the paths
     */
    public void setRequestedFirstTimePaths(
            NetMeshObjectAccessSpecification [] newValue )
    {
        theRequestedFirstTimePaths = newValue;
    }
    
    /**
     * Obtain the paths of NetMeshObjects requested for the first time.
     * 
     * @return the paths
     */
    public NetMeshObjectAccessSpecification [] getRequestedFirstTimePaths()
    {
        return theRequestedFirstTimePaths;
    }

//

    public void setIncomingXprisoMessage(
            XprisoMessage newValue )
    {
        theIncomingXprisoMessage = newValue;
    }
    public XprisoMessage getIncomingXprisoMessage()
    {
        return theIncomingXprisoMessage;
    }
    
    public void setIncomingXprisoMessageEndpoint(
            MessageEndpoint<XprisoMessage> newValue )
    {
        theIncomingXprisoMessageEndpoint = newValue;
    }
    public MessageEndpoint<XprisoMessage> getIncomingXprisoMessageEndpoint()
    {
        return theIncomingXprisoMessageEndpoint;
    }
    
    public void addRegisterReplicationIfNotAlready(
            NetMeshObject toAdd )
    {
        theRegisterReplicationsIfNotAlready.add( toAdd );
    }
    public NetMeshObject [] getRegisterReplicationsIfNotAlready()
    {
        return ArrayHelper.copyIntoNewArray( theRegisterReplicationsIfNotAlready, NetMeshObject.class  );
    }

    public void addRippleCreate(
            RippleInstructions toAdd )
    {
        theRippleCreates.add( toAdd );
    }
    public RippleInstructions [] getRippleCreates()
    {
        return ArrayHelper.copyIntoNewArray( theRippleCreates, RippleInstructions.class );
    }
    
    public void addRippleResynchronize(
            RippleInstructions toAdd )
    {
        theRippleResynchronizes.add( toAdd );
    }
    public RippleInstructions [] getRippleResynchronizes()
    {
        return ArrayHelper.copyIntoNewArray( theRippleResynchronizes, RippleInstructions.class );
    }
    
    public void addToResynchronizeInstructions(
            NetMeshObjectIdentifier identifierToAdd,
            NetMeshBaseIdentifier   proxyIdentifierToAdd )
    {
        ResynchronizeInstructions found = null;
        for( ResynchronizeInstructions current : theResynchronizeInstructions ) {
            if( current.getProxyIdentifier().equals( proxyIdentifierToAdd )) {
                found = current;
                break;
            }
        }
        if( found != null ) {
            found.addNetMeshObjectIdentifier( identifierToAdd );
        } else {
            ResynchronizeInstructions toAdd = new ResynchronizeInstructions();
            toAdd.addNetMeshObjectIdentifier( identifierToAdd );
            toAdd.setProxyIdentifier( proxyIdentifierToAdd );
            theResynchronizeInstructions.add( toAdd );
        }
    }
    
    public ResynchronizeInstructions [] getResynchronizeInstructions()
    {
        return ArrayHelper.copyIntoNewArray( theResynchronizeInstructions, ResynchronizeInstructions.class );
    }
    
    public void addToCancelInstructions(
            NetMeshObject objectToAdd,
            Proxy         proxyToAdd )
    {
        CancelInstructions found = null;
        for( CancelInstructions current : theCancelInstructions ) {
            if( current.getProxy() == proxyToAdd ) {
                found = current;
                break;
            }
        }
        if( found != null ) {
            found.addNetMeshObject( objectToAdd );
        } else {
            CancelInstructions toAdd = new CancelInstructions();
            toAdd.addNetMeshObject( objectToAdd );
            toAdd.setProxy( proxyToAdd );
            theCancelInstructions.add( toAdd );
        }
    }
    
    public CancelInstructions [] getCancelInstructions()
    {
        return ArrayHelper.copyIntoNewArray( theCancelInstructions, CancelInstructions.class );
    }
    

        
    
    public void addSurrenderLock(
            NetMeshObject toAdd )
    {
        theSurrenderLocks.add( toAdd );
    }
    public NetMeshObject [] getSurrenderLocks()
    {
        return ArrayHelper.copyIntoNewArray(  theSurrenderLocks, NetMeshObject.class );
    }

    public void addSurrenderHome(
            NetMeshObject toAdd )
    {
        theSurrenderHomes.add( toAdd );
    }
    public NetMeshObject [] getSurrenderHomes()
    {
        return ArrayHelper.copyIntoNewArray(  theSurrenderHomes, NetMeshObject.class );
    }

   
    public void addCancel(
            NetMeshObject toAdd )
    {
        theCancels.add( toAdd );
    }
    public NetMeshObject [] getCancels()
    {
        return ArrayHelper.copyIntoNewArray( theCancels, NetMeshObject.class );
    }
    
    public void setPropertyChanges(
            NetMeshObjectPropertyChangeEvent [] newValue )
    {
        if( newValue != null ) {
            thePropertyChanges = newValue;
        } else {
            thePropertyChanges = new NetMeshObjectPropertyChangeEvent[0];
        }
    }
    
    public NetMeshObjectPropertyChangeEvent [] getPropertyChanges()
    {
        return thePropertyChanges;
    }
    
    public void setTypeAdditions(
            NetMeshObjectTypeAddedEvent [] newValue )
    {
        theTypeAdditions = newValue;
    }
    public NetMeshObjectTypeAddedEvent [] getTypeAdditions()
    {
        return theTypeAdditions;
    }

    public void setTypeRemovals(
            NetMeshObjectTypeRemovedEvent [] newValue )
    {
        theTypeRemovals = newValue;
    }
    public NetMeshObjectTypeRemovedEvent [] getTypeRemovals()
    {
        return theTypeRemovals;
    }
    
    public void addEquivalentsAddition(
            NetMeshObjectEquivalentsAddedEvent toAdd )
    {
        theEquivalentsAdditions.add( toAdd );
    }
    public NetMeshObjectEquivalentsAddedEvent [] getEquivalentsAdditions()
    {
        return ArrayHelper.copyIntoNewArray( theEquivalentsAdditions, NetMeshObjectEquivalentsAddedEvent.class );
    }

    public void addEquivalentsRemoval(
            NetMeshObjectEquivalentsRemovedEvent toAdd )
    {
        theEquivalentsRemovals.add( toAdd );
    }
    public NetMeshObjectEquivalentsRemovedEvent [] getEquivalentsRemovals()
    {
        return ArrayHelper.copyIntoNewArray( theEquivalentsRemovals, NetMeshObjectEquivalentsRemovedEvent.class );
    }
    
    public void addNeighborAddition(
            NetMeshObjectNeighborAddedEvent toAdd )
    {
        theNeighborAdditions.add( toAdd );
    }
    public NetMeshObjectNeighborAddedEvent [] getNeighborAdditions()
    {
        return ArrayHelper.copyIntoNewArray( theNeighborAdditions, NetMeshObjectNeighborAddedEvent.class );
    }

    public void addNeighborRemoval(
            NetMeshObjectNeighborRemovedEvent toAdd )
    {
        theNeighborRemovals.add( toAdd );
    }
    public NetMeshObjectNeighborRemovedEvent [] getNeighborRemovals()
    {
        return ArrayHelper.copyIntoNewArray( theNeighborRemovals, NetMeshObjectNeighborRemovedEvent.class );
    }

    public void addRoleAddition(
            NetMeshObjectRoleAddedEvent toAdd )
    {
        theRoleAdditions.add( toAdd );
    }
    public NetMeshObjectRoleAddedEvent [] getRoleAdditions()
    {
        return ArrayHelper.copyIntoNewArray( theRoleAdditions, NetMeshObjectRoleAddedEvent.class );
    }
    
    public void addRoleRemoval(
            NetMeshObjectRoleRemovedEvent toAdd )
    {
        theRoleRemovals.add( toAdd );
    }
    public NetMeshObjectRoleRemovedEvent [] getRoleRemovals()
    {
        return ArrayHelper.copyIntoNewArray( theRoleRemovals, NetMeshObjectRoleRemovedEvent.class );
    }
    
    public void setExpectectedObtainReplicasWait(
            long newValue )
    {
        theExpectectedObtainReplicasWait = newValue;
    }
    public long getExpectedObtainReplicasWait()
    {
        return theExpectectedObtainReplicasWait;
    }

    public void setDeletions(
            NetMeshObjectDeletedEvent [] deletions )
    {
        theDeletions = deletions;
    }
    public NetMeshObjectDeletedEvent [] getDeletions()
    {
        return theDeletions;
    }
    
    /**
     * Determine whether these instructions contain no content.
     * 
     * @return true if they are empty
     */
    public boolean isEmpty()
    {
        if( theCeaseCommunications ) {
            return false;
        }
        if( theRequestedFirstTimePaths != null && theRequestedFirstTimePaths.length > 0 ) {
            return false;
        }
        if( theSendViaWaitForReplicaResponseEndpoint != null ) {
            return false;
        }
        if( theSendViaWaitForLockResponseEndpoint != null ) {
            return false;
        }
        if( theSendViaWaitForHomeResponseEndpoint != null ) {
            return false;
        }
        if( theSendViaEndpoint != null ) {
            return false;
        }
        if( theRegisterReplicationsIfNotAlready != null && !theRegisterReplicationsIfNotAlready.isEmpty() ) {
            return false;
        }
    
        if( theRippleCreates != null && !theRippleCreates.isEmpty() ) {
            return false;
        }
        if( theRippleResynchronizes != null && !theRippleResynchronizes.isEmpty() ) {
            return false;
        }
        if( theResynchronizeInstructions != null && !theResynchronizeInstructions.isEmpty() ) {
            return false;
        }
        if( theSurrenderLocks != null && !theSurrenderLocks.isEmpty() ) {
            return false;
        }
        if( theSurrenderHomes != null && !theSurrenderHomes.isEmpty() ) {
            return false;
        }
        if( theCancels != null && !theCancels.isEmpty() ) {
            return false;
        }
        
        if( thePropertyChanges != null && thePropertyChanges.length > 0 ) {
            return false;
        }
        return true;
    }

    /**
     * Convert to String representation, for debugging.
     * 
     * @return String respresentation
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theStartCommunicating",
                    "theCeaseCommunications",
                    // "theRequestedFirstTimePaths",
                    "theSendViaWaitForReplicaResponseEndpoint",
                    "theSendViaWaitForLockResponseEndpoint",
                    "theSendViaWaitForHomeResponseEndpoint",
                    "theSendViaEndpoint",
                    "theRegisterReplications",
                    "theRippleCreates",
                    "theRippleResynchronizes",
                    "theResynchronizeInstructions",
                    "theCancelInstructions",
                    "theSurrenderHomes",
                    "theSurrenderLocks",
                    "theCancels",
                    "thePropertyChanges",
                    "theTypeAdditions",
                    "theTypeRemovals",
                    "theEquivalentsAdditions",
                    "theEquivalentsRemovals",
                    "theNeighborAdditions",
                    "theNeighborRemovals",
                    "theRoleAdditions",
                    "theRoleRemovals"
                },
                new Object[] {
                    theStartCommunicating,
                    theCeaseCommunications,
                    // theRequestedFirstTimePaths,
                    theSendViaWaitForReplicaResponseEndpoint,
                    theSendViaWaitForLockResponseEndpoint,
                    theSendViaWaitForHomeResponseEndpoint,
                    theSendViaEndpoint,
                    theRegisterReplicationsIfNotAlready,
                    theRippleCreates,
                    theRippleResynchronizes,
                    theResynchronizeInstructions,
                    theCancelInstructions,
                    theSurrenderHomes,
                    theSurrenderLocks,
                    theCancels,
                    thePropertyChanges,
                    theTypeAdditions,
                    theTypeRemovals,
                    theEquivalentsAdditions,
                    theEquivalentsRemovals,
                    theNeighborAdditions,
                    theNeighborRemovals,
                    theRoleAdditions,
                    theRoleRemovals
                
                },
                StringHelper.LOG_FLAGS.SHOW_NON_NULL | StringHelper.LOG_FLAGS.SHOW_NON_ZERO );
    }
    
    protected boolean theStartCommunicating = false; // default

    /**
     * Should the Proxy cease communications.
     */
    protected boolean theCeaseCommunications = false; // default
    
    
    protected NetMeshObjectAccessSpecification [] theRequestedFirstTimePaths;

    protected XprisoMessage theIncomingXprisoMessage;
    
    protected MessageEndpoint<XprisoMessage> theIncomingXprisoMessageEndpoint;

    protected XprisoMessage theSendViaWaitForReplicaResponseEndpoint = null;
    protected XprisoMessage theSendViaWaitForLockResponseEndpoint = null;
    protected XprisoMessage theSendViaWaitForHomeResponseEndpoint = null;
    protected XprisoMessage theSendViaEndpoint = null;
    
    protected long theWaitForReplicaResponseEndpointTimeout;
    protected long theWaitForLockResponseEndpointTimeout;
    protected long theWaitForHomeResponseEndpointTimeout;


    
    protected ArrayList<NetMeshObject> theRegisterReplicationsIfNotAlready = new ArrayList<NetMeshObject>();
    
    protected ArrayList<RippleInstructions> theRippleCreates = new ArrayList<RippleInstructions>();
    protected ArrayList<RippleInstructions> theRippleResynchronizes = new ArrayList<RippleInstructions>();
    protected ArrayList<ResynchronizeInstructions> theResynchronizeInstructions = new ArrayList<ResynchronizeInstructions>();
    protected ArrayList<CancelInstructions> theCancelInstructions = new ArrayList<CancelInstructions>();
    
    protected ArrayList<NetMeshObject> theSurrenderLocks = new ArrayList<NetMeshObject>();
    protected ArrayList<NetMeshObject> theSurrenderHomes = new ArrayList<NetMeshObject>();
    
    protected ArrayList<NetMeshObject> theCancels = new ArrayList<NetMeshObject>();
    
    
    protected NetMeshObjectPropertyChangeEvent [] thePropertyChanges = {};

    protected NetMeshObjectTypeAddedEvent [] theTypeAdditions = {};
    protected NetMeshObjectTypeRemovedEvent [] theTypeRemovals = {};

    protected ArrayList<NetMeshObjectEquivalentsAddedEvent> theEquivalentsAdditions = new ArrayList<NetMeshObjectEquivalentsAddedEvent>();
    protected ArrayList<NetMeshObjectEquivalentsRemovedEvent> theEquivalentsRemovals = new ArrayList<NetMeshObjectEquivalentsRemovedEvent>();
    protected ArrayList<NetMeshObjectNeighborAddedEvent> theNeighborAdditions = new ArrayList<NetMeshObjectNeighborAddedEvent>();
    protected ArrayList<NetMeshObjectNeighborRemovedEvent> theNeighborRemovals = new ArrayList<NetMeshObjectNeighborRemovedEvent>();
    protected ArrayList<NetMeshObjectRoleAddedEvent> theRoleAdditions = new ArrayList<NetMeshObjectRoleAddedEvent>();
    protected ArrayList<NetMeshObjectRoleRemovedEvent> theRoleRemovals = new ArrayList<NetMeshObjectRoleRemovedEvent>();
    protected NetMeshObjectDeletedEvent [] theDeletions = {};
    
    protected long theExpectectedObtainReplicasWait = 2000L; // default. FIXME?
}