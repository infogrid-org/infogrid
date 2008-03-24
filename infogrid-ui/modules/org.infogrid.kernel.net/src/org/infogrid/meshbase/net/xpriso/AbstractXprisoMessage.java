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

import org.infogrid.meshbase.net.NetMeshBaseIdentifier;

/**
 * Factors out functionality common to XprisoMessage implementations.
 */
public abstract class AbstractXprisoMessage
        implements
            XprisoMessage
{
    /**
     * Constructor.
     */
    protected AbstractXprisoMessage()
    {
        // no op
    }

    /**
     * Obtain the request ID.
     *
     * @return the request ID
     */
    public long getRequestId()
    {
        return theRequestId;
    }

    /**
     * Obtain the response ID.
     *
     * @return the response ID
     */
    public long getResponseId()
    {
        return theResponseId;
    }

    /**
     * Obtain the NetMeshBaseIdentifier of the sender.
     * 
     * @return the sender's NNetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getSenderIdentifier()
    {
        return theSenderIdentifier;
    }
    
    /**
     * Obtain the NetMeshBaseIdentifier of the receiver.
     * 
     * @return the receiver's NNetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getReceiverIdentifier()
    {
        return theReceiverIdentifier;
    }

    /**
     * Determine whether or not to cease communications after this message.
     *
     * @return if true, cease communications
     */
    public boolean getCeaseCommunications()
    {
        return theCeaseCommunications;
    }
    
    /**
     * Default implementation of equals().
     *
     * @param other the Object to compare against
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof XprisoMessage )) {
            return false;
        }
        XprisoMessage realOther = (XprisoMessage) other;

        if( theRequestId != realOther.getRequestId() ) {
            return false;
        }
        if( theResponseId != realOther.getResponseId() ) {
            return false;
        }
        if( !objectsEquals( theSenderIdentifier, realOther.getSenderIdentifier() )) {
            return false;
        }
        if( !objectsEquals( theReceiverIdentifier, realOther.getReceiverIdentifier() )) {
            return false;
        }
        if( !arraysEquals( getRequestedFirstTimeObjects(), realOther.getRequestedFirstTimeObjects() )) {
            return false;
        }
        if( !arraysEquals( getRequestedCanceledObjects(), realOther.getRequestedCanceledObjects() )) {
            return false;
        }
        if( !arraysEquals( getCreations(), realOther.getCreations() )) {
            return false;
        }
        if( !arraysEquals( getDeleteChanges(), realOther.getDeleteChanges() )) {
            return false;
        }
        if( !arraysEquals( getConveyedMeshObjects(), realOther.getConveyedMeshObjects() )) {
            return false;
        }
        if( !arraysEquals( getNeighborAdditions(), realOther.getNeighborAdditions() )) {
            return false;
        }
        if( !arraysEquals( getNeighborRemovals(), realOther.getNeighborRemovals() )) {
            return false;
        }
        if( !arraysEquals( getPropertyChanges(), realOther.getPropertyChanges() )) {
            return false;
        }
        if( !arraysEquals( getRoleAdditions(), realOther.getRoleAdditions() )) {
            return false;
        }
        if( !arraysEquals( getRoleRemovals(), realOther.getRoleRemovals() )) {
            return false;
        }
        if( !arraysEquals( getTypeAdditions(), realOther.getTypeAdditions() )) {
            return false;
        }
        if( !arraysEquals( getTypeRemovals(), realOther.getTypeRemovals() )) {
            return false;
        }
        if( !arraysEquals( getRequestedLockObjects(), realOther.getRequestedLockObjects() )) {
            return false;
        }
        if( !arraysEquals( getPushLockObjects(), realOther.getPushLockObjects() )) {
            return false;
        }
        if( !arraysEquals( getReclaimedLockObjects(), realOther.getReclaimedLockObjects() )) {
            return false;
        }
        if( !arraysEquals( getRequestedResynchronizeDependentReplicas(), realOther.getRequestedResynchronizeDependentReplicas() )) {
            return false;
        }
        if( !arraysEquals( getResynchronizeDependentReplicas(), realOther.getResynchronizeDependentReplicas() )) {
            return false;
        }
        if( theCeaseCommunications != realOther.getCeaseCommunications()) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to compare two objects.
     *
     * @param one the first Object
     * @param two the second Object
     * @return true if the Objects are equal
     */
    protected boolean objectsEquals(
            Object one,
            Object two )
    {
        if( one != null ) {
            if( !one.equals( two )) {
                return false;
            }
        } else if( two != null ) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to compare the content of two arrays.
     *
     * @param one first array
     * @param two second array
     */
    protected boolean arraysEquals(
            Object [] one,
            Object [] two )
    {
        if( one != null ) {
            if( two == null ) {
                return false;
            } else if( one.length != two.length ) {
                return false;
            } else {
                for( int i=0 ; i<one.length ; ++i ) {
                    boolean found = false;
                    for( int j=0 ; j<two.length ; ++j ) {
                        if( objectsEquals( one[i], two[j] )) {
                            found = true;
                            break;
                        }
                    }
                    if( !found ) {
                        return false;
                    }
                }
            }
        } else if( two != null ) {
            return false;
        }
        return true;
    }

    /**
     * The request invocation ID.
     */
    protected long theRequestId;

    /**
     * The response invocation ID.
     */
    protected long theResponseId;

    /**
     * The return address. This is the same as the sender's address.
     */
    protected NetMeshBaseIdentifier theSenderIdentifier;
    
    /**
     * The destination address.
     */
    protected NetMeshBaseIdentifier theReceiverIdentifier;
    
    /**
     * If true, this indicates that communications should cease after this message.
     */
    protected boolean theCeaseCommunications = false;
}
