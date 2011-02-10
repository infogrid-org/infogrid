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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.xpriso;

import java.util.ArrayList;
import java.util.List;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
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
import org.infogrid.meshbase.transaction.Change;
import org.infogrid.util.logging.Log;

/**
 * Helper methods for Xpriso messages.
 */
public abstract class XprisoMessageHelper
{
    private static final Log log = Log.getLogInstance( XprisoMessageHelper.class ); // our own, private logger

    /**
     * Keep this abstract.
     */
    private XprisoMessageHelper() {}

    /**
     * Given a list of XprisoMessages that arrived, construct a semantically equivalent but shorter list of
     * XprisoMessages. If shortening is not possible, this will return the same list.
     *
     * @param candidates the candidates to be consolidated
     * @return the consolidated list
     */
    public static List<XprisoMessage> consolidate(
            List<XprisoMessage> candidates )
    {
        if( false ) {
            return candidates; // FIXME
        }
        if( candidates == null || candidates.size() <= 1 ) {
            return candidates;
        }
        List<XprisoMessage> ret = new ArrayList<XprisoMessage>( candidates.size() );

        XprisoMessage currentRet = null;
        for( XprisoMessage currentCandidate : candidates ) {

            if( currentRet == null ) {
                currentRet = currentCandidate;
                continue;
            }
            XprisoMessage merged = merge( currentRet, currentCandidate );
            if( merged != null ) {
                currentRet = merged;
                continue;
            }
            ret.add( currentRet );
            currentRet = currentCandidate;
        }
        if( currentRet != null ) {
            ret.add( currentRet );
        }
        return ret;
    }

    /**
     * Attempt to merge a second XprisoMessage into a first. Return merged message if successful.
     *
     * @param first first XprisoMessage
     * @param second second XprisoMessage
     * @return the merged message if the merge was successful
     */
    public static XprisoMessage merge(
            XprisoMessage first,
            XprisoMessage second )
    {
        // we can merge if:
        // 1. sender and receiver are the same
        // 2. only one of the two messages carries request and/or response ids

        try {
            if( !first.getSenderIdentifier().equals( second.getSenderIdentifier() )) {
                return null;
            }
            if( !first.getReceiverIdentifier().equals( second.getReceiverIdentifier() )) {
                return null;
            }
            if( first.getRequestId() != 0 && second.getRequestId() != 0 ) {
                return null;
            }
            if( first.getResponseId() != 0 && second.getResponseId() != 0 ) {
                return null;
            }

            ParserFriendlyXprisoMessage ret = ParserFriendlyXprisoMessage.create(
                    first.getSenderIdentifier(),
                    first.getReceiverIdentifier() );

            //

            if( first.getRequestId() != 0 ) {
                ret.setRequestId( first.getRequestId() );
            } else if( second.getRequestId() != 0 ) {
                ret.setRequestId( second.getRequestId() );
            } // else no request id

            if( first.getResponseId() != 0 ) {
                ret.setResponseId( first.getResponseId() );
            } else if( second.getResponseId() != 0 ) {
                ret.setResponseId( second.getResponseId() );
            } // else no respohse id

            //

            ret.setCeaseCommunications( first.getCeaseCommunications() || second.getCeaseCommunications() );

            //

            ExternalizedNetMeshObject [] firstConveyed  = first.getConveyedMeshObjects();
            ExternalizedNetMeshObject [] secondConveyed = second.getConveyedMeshObjects();
            ret.addConveyedMeshObjects( firstConveyed );
            ret.addConveyedMeshObjects( secondConveyed ); // will complain if we have it already

            //

            NetMeshObjectAccessSpecification [] firstRequestedFirstTime  = first.getRequestedFirstTimeObjects();
            NetMeshObjectAccessSpecification [] secondRequestedFirstTime = second.getRequestedFirstTimeObjects();
            ret.addRequestedFirstTimeObjects( firstRequestedFirstTime );
            ret.addRequestedFirstTimeObjects( secondRequestedFirstTime );

            //

            NetMeshObjectIdentifier [] firstRequestedCanceled  = first.getRequestedCanceledObjects();
            NetMeshObjectIdentifier [] secondRequestedCanceled = second.getRequestedCanceledObjects();
            ret.addRequestedCanceledObjects( firstRequestedCanceled );
            ret.addRequestedCanceledObjects( secondRequestedCanceled );

            //

            NetMeshObjectIdentifier [] firstRequestedFreshen  = first.getRequestedFreshenReplicas();
            NetMeshObjectIdentifier [] secondRequestedFreshen = second.getRequestedFreshenReplicas();
            ret.addRequestedFreshenReplicas( firstRequestedFreshen );
            ret.addRequestedFreshenReplicas( secondRequestedFreshen );

            //

            NetMeshObjectIdentifier [] firstRequestedResync  = first.getRequestedResynchronizeReplicas();
            NetMeshObjectIdentifier [] secondRequestedResync = second.getRequestedResynchronizeReplicas();
            ret.addRequestedResynchronizeReplicas( firstRequestedResync );
            ret.addRequestedResynchronizeReplicas( secondRequestedResync );

            //

            NetMeshObjectIdentifier [] firstRequestedLocks  = first.getRequestedLockObjects();
            NetMeshObjectIdentifier [] secondRequestedLocks = second.getRequestedLockObjects();
            ret.addRequestedLockObjects( firstRequestedLocks );
            ret.addRequestedLockObjects( secondRequestedLocks );

            //

            NetMeshObjectIdentifier [] firstPushLocks  = first.getPushLockObjects();
            NetMeshObjectIdentifier [] secondPushLocks = second.getPushLockObjects();
            ret.addPushLockObjects( firstPushLocks );
            ret.addPushLockObjects( secondPushLocks );

            //

            NetMeshObjectIdentifier [] firstReclaimedLocks  = first.getReclaimedLockObjects();
            NetMeshObjectIdentifier [] secondReclaimedLocks = second.getReclaimedLockObjects();
            ret.addReclaimedLockObjects( firstReclaimedLocks );
            ret.addReclaimedLockObjects( secondReclaimedLocks );

            //

            NetMeshObjectIdentifier [] firstRequestedHome  = first.getRequestedHomeReplicas();
            NetMeshObjectIdentifier [] secondRequestedHome = second.getRequestedHomeReplicas();
            ret.addRequestedHomeReplicas( firstRequestedHome );
            ret.addRequestedHomeReplicas( secondRequestedHome );

            //

            NetMeshObjectIdentifier [] firstPushHome  = first.getPushHomeReplicas();
            NetMeshObjectIdentifier [] secondPushHome = second.getPushHomeReplicas();
            ret.addPushHomeReplicas( firstPushHome );
            ret.addPushHomeReplicas( secondPushHome );

            // only added types that aren't being removed again later.
            // The inverse is not true because it would reset properties

            NetMeshObjectTypeAddedEvent   [] firstTypesAdded    = first.getTypeAdditions();
            NetMeshObjectTypeAddedEvent   [] secondTypesAdded   = second.getTypeAdditions();
            NetMeshObjectTypeRemovedEvent [] firstTypesRemoved  = first.getTypeRemovals();
            NetMeshObjectTypeRemovedEvent [] secondTypesRemoved = second.getTypeRemovals();

            //

            ArrayList<NetMeshObjectTypeRemovedEvent> secondTypesRemovedDontAdd = new ArrayList<NetMeshObjectTypeRemovedEvent>();
            for( NetMeshObjectTypeAddedEvent currentAdded : firstTypesAdded ) {
                NetMeshObjectTypeRemovedEvent inverse = null;
                for( NetMeshObjectTypeRemovedEvent currentRemoved : secondTypesRemoved ) {
                    if( currentAdded.isInverse( currentRemoved )) {
                        inverse = currentRemoved;
                        break;
                    }
                }
                if( inverse == null ) {
                    ret.addTypeAddition( currentAdded );
                } else {
                    secondTypesRemovedDontAdd.add( inverse );
                }
            }
            ret.addTypeAdditions( secondTypesAdded );
            ret.addTypeRemovals( firstTypesRemoved );
            for( NetMeshObjectTypeRemovedEvent current : secondTypesRemoved ) {
                if( !secondTypesRemovedDontAdd.contains( current )) {
                    ret.addTypeRemoval( current );
                }
            }

            // only property changes that aren't undone later

            NetMeshObjectPropertyChangeEvent [] firstPropertyChanges  = first.getPropertyChanges();
            NetMeshObjectPropertyChangeEvent [] secondPropertyChanges = second.getPropertyChanges();

            for( NetMeshObjectPropertyChangeEvent currentFirst : firstPropertyChanges ) {
                boolean skip = false;
                for( NetMeshObjectPropertyChangeEvent currentSecond : secondPropertyChanges ) {
                    if( currentFirst.affectsSamePropertyAs( currentSecond )) {
                        skip = true;
                        break;
                    }
                }
                if( !skip ) {
                    ret.addPropertyChange( currentFirst );
                }
            }
            ret.addPropertyChanges( secondPropertyChanges );

            // only neighbor additions that aren't removed again later

            NetMeshObjectNeighborAddedEvent   [] firstNeighborAdditions  = first.getNeighborAdditions();
            NetMeshObjectNeighborAddedEvent   [] secondNeighborAdditions = second.getNeighborAdditions();
            NetMeshObjectNeighborRemovedEvent [] firstNeighborRemovals   = first.getNeighborRemovals();
            NetMeshObjectNeighborRemovedEvent [] secondNeighborRemovals  = second.getNeighborRemovals();

            ArrayList<NetMeshObjectNeighborRemovedEvent> secondRemovalsDontAdd = new ArrayList<NetMeshObjectNeighborRemovedEvent>();
            for( NetMeshObjectNeighborAddedEvent currentFirst : firstNeighborAdditions ) {
                NetMeshObjectNeighborRemovedEvent inverse = null;
                for( NetMeshObjectNeighborRemovedEvent currentSecond : secondNeighborRemovals ) {
                    if( currentFirst.isInverse( currentSecond )) {
                        inverse = currentSecond;
                        break;
                    }
                }
                if( inverse == null ) {
                    ret.addNeighborAddition( currentFirst );
                } else {
                    secondRemovalsDontAdd.add( inverse );
                }
            }
            ret.addNeighborRemovals( firstNeighborRemovals );
            ret.addNeighborAdditions( secondNeighborAdditions );
            for( NetMeshObjectNeighborRemovedEvent current : secondNeighborRemovals ) {
                if( !secondRemovalsDontAdd.contains( current )) {
                    ret.addNeighborRemoval( current );
                }
            }

            // equivalents: for right now, we just don't merge
            NetMeshObjectEquivalentsAddedEvent   [] firstEquivalentAdditions  = first.getEquivalentsAdditions();
            NetMeshObjectEquivalentsAddedEvent   [] secondEquivalentAdditions = second.getEquivalentsAdditions();
            NetMeshObjectEquivalentsRemovedEvent [] firstEquivalentRemovals   = first.getEquivalentsRemovals();
            NetMeshObjectEquivalentsRemovedEvent [] secondEquivalentRemovals  = second.getEquivalentsRemovals();

            if( firstEquivalentAdditions.length > 0 || secondEquivalentAdditions.length > 0 || firstEquivalentRemovals.length > 0 || secondEquivalentRemovals.length > 0 ) {
                return null;
            }

            // only role additions that aren't removed later
            // only role removals that aren't added later

            NetMeshObjectRoleAddedEvent   [] firstRoleAdditions  = first.getRoleAdditions();
            NetMeshObjectRoleAddedEvent   [] secondRoleAdditions = second.getRoleAdditions();
            NetMeshObjectRoleRemovedEvent [] firstRoleRemovals   = first.getRoleRemovals();
            NetMeshObjectRoleRemovedEvent [] secondRoleRemovals  = second.getRoleRemovals();

            ArrayList<NetMeshObjectRoleRemovedEvent> secondRoleRemovalsDontAdd = new ArrayList<NetMeshObjectRoleRemovedEvent>();
            for( NetMeshObjectRoleAddedEvent currentFirst : firstRoleAdditions ) {
                NetMeshObjectRoleRemovedEvent inverse = null;
                for( NetMeshObjectRoleRemovedEvent currentSecond : secondRoleRemovals ) {
                    if( currentFirst.isInverse( currentSecond )) {
                        inverse = currentSecond;
                        break;
                    }
                }
                if( inverse == null ) {
                    ret.addRoleAddition( currentFirst );
                } else {
                    secondRoleRemovalsDontAdd.add( inverse );
                }
            }
            ret.addRoleRemovals( firstRoleRemovals );
            ret.addRoleAdditions( secondRoleAdditions );
            for( NetMeshObjectRoleRemovedEvent current : secondRoleRemovals ) {
                if( !secondRoleRemovalsDontAdd.contains( current )) {
                    ret.addRoleRemoval( current );
                }
            }

            //

            NetMeshObjectDeletedEvent [] firstDeletions  = first.getDeletions();
            NetMeshObjectDeletedEvent [] secondDeletions = second.getDeletions();
            ret.addDeleteChanges( firstDeletions );
            ret.addDeleteChanges( secondDeletions );

            //

            return ret;

        } catch( Throwable t ) {
            log.error( t );
            return null;
        }
    }
}
