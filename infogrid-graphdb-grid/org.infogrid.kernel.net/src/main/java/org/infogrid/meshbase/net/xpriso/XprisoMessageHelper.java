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
import java.util.List;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.transaction.NetMeshObjectDeletedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectNeighborRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectPropertyChangeEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectRoleRemovedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeAddedEvent;
import org.infogrid.meshbase.net.transaction.NetMeshObjectTypeRemovedEvent;
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
//        if( false ) {
//            return candidates; // FIXME
//        }
        if( candidates == null || candidates.size() <= 1 ) {
            return candidates;
        }
        List<XprisoMessage> ret = new ArrayList<>( candidates.size() );

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
            try {
                ret.addConveyedMeshObjects( firstConveyed );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addConveyedMeshObjects( secondConveyed ); // will complain if we have it already
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectAccessSpecification [] firstRequestedFirstTime  = first.getRequestedFirstTimeObjects();
            NetMeshObjectAccessSpecification [] secondRequestedFirstTime = second.getRequestedFirstTimeObjects();
            try {
                ret.addRequestedFirstTimeObjects( firstRequestedFirstTime );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedFirstTimeObjects( secondRequestedFirstTime );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstRequestedCanceled  = first.getRequestedCanceledObjects();
            NetMeshObjectIdentifier [] secondRequestedCanceled = second.getRequestedCanceledObjects();
            try {
                ret.addRequestedCanceledObjects( firstRequestedCanceled );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedCanceledObjects( secondRequestedCanceled );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstRequestedFreshen  = first.getRequestedFreshenReplicas();
            NetMeshObjectIdentifier [] secondRequestedFreshen = second.getRequestedFreshenReplicas();
            try {
                ret.addRequestedFreshenReplicas( firstRequestedFreshen );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedFreshenReplicas( secondRequestedFreshen );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstRequestedResync  = first.getRequestedResynchronizeReplicas();
            NetMeshObjectIdentifier [] secondRequestedResync = second.getRequestedResynchronizeReplicas();
            try {
                ret.addRequestedResynchronizeReplicas( firstRequestedResync );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedResynchronizeReplicas( secondRequestedResync );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstRequestedLocks  = first.getRequestedLockObjects();
            NetMeshObjectIdentifier [] secondRequestedLocks = second.getRequestedLockObjects();
            try {
                ret.addRequestedLockObjects( firstRequestedLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedLockObjects( secondRequestedLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstPushLocks  = first.getPushLockObjects();
            NetMeshObjectIdentifier [] secondPushLocks = second.getPushLockObjects();
            try {
                ret.addPushLockObjects( firstPushLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addPushLockObjects( secondPushLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstReclaimedLocks  = first.getReclaimedLockObjects();
            NetMeshObjectIdentifier [] secondReclaimedLocks = second.getReclaimedLockObjects();
            try {
                ret.addReclaimedLockObjects( firstReclaimedLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addReclaimedLockObjects( secondReclaimedLocks );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstRequestedHome  = first.getRequestedHomeReplicas();
            NetMeshObjectIdentifier [] secondRequestedHome = second.getRequestedHomeReplicas();
            try {
                ret.addRequestedHomeReplicas( firstRequestedHome );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRequestedHomeReplicas( secondRequestedHome );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            NetMeshObjectIdentifier [] firstPushHome  = first.getPushHomeReplicas();
            NetMeshObjectIdentifier [] secondPushHome = second.getPushHomeReplicas();
            try {
                ret.addPushHomeReplicas( firstPushHome );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addPushHomeReplicas( secondPushHome );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            // only added types that aren't being removed again later.
            // The inverse is not true because it would reset properties

            NetMeshObjectTypeAddedEvent   [] firstTypesAdded    = first.getTypeAdditions();
            NetMeshObjectTypeAddedEvent   [] secondTypesAdded   = second.getTypeAdditions();
            NetMeshObjectTypeRemovedEvent [] firstTypesRemoved  = first.getTypeRemovals();
            NetMeshObjectTypeRemovedEvent [] secondTypesRemoved = second.getTypeRemovals();

            //

            ArrayList<NetMeshObjectTypeRemovedEvent> secondTypesRemovedDontAdd = new ArrayList<>();
            for( NetMeshObjectTypeAddedEvent currentAdded : firstTypesAdded ) {
                NetMeshObjectTypeRemovedEvent inverse = null;
                for( NetMeshObjectTypeRemovedEvent currentRemoved : secondTypesRemoved ) {
                    if( currentAdded.isInverse( currentRemoved )) {
                        inverse = currentRemoved;
                        break;
                    }
                }
                if( inverse == null ) {
                    try {
                        ret.addTypeAddition( currentAdded );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                } else {
                    secondTypesRemovedDontAdd.add( inverse );
                }
            }
            try {
                ret.addTypeAdditions( secondTypesAdded );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addTypeRemovals( firstTypesRemoved );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            for( NetMeshObjectTypeRemovedEvent current : secondTypesRemoved ) {
                if( !secondTypesRemovedDontAdd.contains( current )) {
                    try {
                        ret.addTypeRemoval( current );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
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
                    try {
                        ret.addPropertyChange( currentFirst );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                }
            }
            try {
                ret.addPropertyChanges( secondPropertyChanges );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            // only neighbor additions that aren't removed again later

            NetMeshObjectNeighborAddedEvent   [] firstNeighborAdditions  = first.getNeighborAdditions();
            NetMeshObjectNeighborAddedEvent   [] secondNeighborAdditions = second.getNeighborAdditions();
            NetMeshObjectNeighborRemovedEvent [] firstNeighborRemovals   = first.getNeighborRemovals();
            NetMeshObjectNeighborRemovedEvent [] secondNeighborRemovals  = second.getNeighborRemovals();

            ArrayList<NetMeshObjectNeighborRemovedEvent> secondRemovalsDontAdd = new ArrayList<>();
            for( NetMeshObjectNeighborAddedEvent currentFirst : firstNeighborAdditions ) {
                NetMeshObjectNeighborRemovedEvent inverse = null;
                for( NetMeshObjectNeighborRemovedEvent currentSecond : secondNeighborRemovals ) {
                    if( currentFirst.isInverse( currentSecond )) {
                        inverse = currentSecond;
                        break;
                    }
                }
                if( inverse == null ) {
                    try {
                        ret.addNeighborAddition( currentFirst );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                } else {
                    secondRemovalsDontAdd.add( inverse );
                }
            }
            try {
                ret.addNeighborRemovals( firstNeighborRemovals );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addNeighborAdditions( secondNeighborAdditions );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            for( NetMeshObjectNeighborRemovedEvent current : secondNeighborRemovals ) {
                if( !secondRemovalsDontAdd.contains( current )) {
                    try {
                        ret.addNeighborRemoval( current );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                }
            }

            // only role additions that aren't removed later
            // only role removals that aren't added later

            NetMeshObjectRoleAddedEvent   [] firstRoleAdditions  = first.getRoleAdditions();
            NetMeshObjectRoleAddedEvent   [] secondRoleAdditions = second.getRoleAdditions();
            NetMeshObjectRoleRemovedEvent [] firstRoleRemovals   = first.getRoleRemovals();
            NetMeshObjectRoleRemovedEvent [] secondRoleRemovals  = second.getRoleRemovals();

            ArrayList<NetMeshObjectRoleRemovedEvent> secondRoleRemovalsDontAdd = new ArrayList<>();
            for( NetMeshObjectRoleAddedEvent currentFirst : firstRoleAdditions ) {
                NetMeshObjectRoleRemovedEvent inverse = null;
                for( NetMeshObjectRoleRemovedEvent currentSecond : secondRoleRemovals ) {
                    if( currentFirst.isInverse( currentSecond )) {
                        inverse = currentSecond;
                        break;
                    }
                }
                if( inverse == null ) {
                    try {
                        ret.addRoleAddition( currentFirst );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                } else {
                    secondRoleRemovalsDontAdd.add( inverse );
                }
            }
            try {
                ret.addRoleRemovals( firstRoleRemovals );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addRoleAdditions( secondRoleAdditions );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            for( NetMeshObjectRoleRemovedEvent current : secondRoleRemovals ) {
                if( !secondRoleRemovalsDontAdd.contains( current )) {
                    try {
                        ret.addRoleRemoval( current );
                    } catch( IllegalStateException ex ) {
                        log.warn( ex );
                    }
                }
            }

            //

            NetMeshObjectDeletedEvent [] firstDeletions  = first.getDeletions();
            NetMeshObjectDeletedEvent [] secondDeletions = second.getDeletions();
            try {
                ret.addDeleteChanges( firstDeletions );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }
            try {
                ret.addDeleteChanges( secondDeletions );
            } catch( IllegalStateException ex ) {
                log.warn( ex );
            }

            //

            return ret;

        } catch( Throwable t ) {
            log.error( t );
            return null;
        }
    }
}
