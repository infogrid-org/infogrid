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

package org.infogrid.meshbase.net.a;

import org.infogrid.context.Context;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;

import org.infogrid.meshbase.a.AMeshBase;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseAccessSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessException;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.ProxyManager;
import org.infogrid.meshbase.net.security.NetAccessManager;
import org.infogrid.meshbase.transaction.Transaction;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CachingMap;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.FactoryException;
import org.infogrid.util.NameServer;
import org.infogrid.util.RemoteQueryTimeoutException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.ReturnSynchronizer;
import org.infogrid.util.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import org.infogrid.mesh.set.MeshObjectSetFactory;

/**
 * The subtype of MeshBase suitable for the AnetMeshObject implementation.
 */
public abstract class AnetMeshBase
        extends
            AMeshBase
        implements
            NetMeshBase
{
    private static final Log log = Log.getLogInstance( AnetMeshBase.class ); // our own, private logger

    /**
     * Constructor for subclasses only. This does not initialize content.
     *
     * @param identifier the MeshBaseIdentifier of this MeshBase
     * @param identifierFactory the factory for MeshObjectIdentifiers appropriate for this MeshBase
     * @param modelBase the ModelBase containing type information
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the CachingMap that holds the MeshObjects in this MeshBase
     * @param proxyManager the ProxyManager for this NetMeshBase
     * @param context the Context in which this MeshBase runs.
     */
    protected AnetMeshBase(
            NetMeshBaseIdentifier                       identifier,
            NetMeshObjectIdentifierFactory              identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            NetAccessManager                            accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            ProxyManager                                proxyManager,
            Context                                     context )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, context );
        
        theProxyManager = proxyManager;
    }

    /**
     * <p>Obtain a manager for object lifecycles.</p>
     * 
     * @return a MeshBaseLifecycleManager that works on this MeshBase with the specified parameters
     */
    @Override
    public synchronized AnetMeshBaseLifecycleManager getMeshBaseLifecycleManager()
    {
        if( theMeshBaseLifecycleManager == null ) {
            theMeshBaseLifecycleManager = new AnetMeshBaseLifecycleManager( this );
        }
        return (AnetMeshBaseLifecycleManager) theMeshBaseLifecycleManager;
    }

    /**
     * Obtain a factory for MeshObjectIdentifiers that is appropriate for this MeshBase.
     *
     * @return the factory
     */
    @Override
    public NetMeshObjectIdentifierFactory getMeshObjectIdentifierFactory()
    {
        return (NetMeshObjectIdentifierFactory) super.getMeshObjectIdentifierFactory();
    }

    /**
     * Find a MeshObject in this MeshBase by its Identifier. Unlike
     * the accessLocally method, this method does not attempt to contact other
     * MeshBases.
     * 
     * @param identifier the Identifier of the MeshObject that shall be found
     * @return the found MeshObject, or null if not found
     */
    @Override
    public NetMeshObject findMeshObjectByIdentifier(
            MeshObjectIdentifier identifier )
    {
        MeshObject ret = super.findMeshObjectByIdentifier( identifier );

        return (NetMeshObject) ret;
    }

    /**
     * Find a set of MeshObjects in this MeshBase by their Identifiers. Unlike
     * the accessLocally method, this method purely considers MeshObjects in the
     * MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.
     * 
     * @param identifiers the Identifiers of the MeshObjects that shall be found
     * @return the found MeshObjects, which may contain null values for MeshObjects that were not found
     */
    @Override
    public NetMeshObject [] findMeshObjectsByIdentifier(
            MeshObjectIdentifier[] identifiers )
    {
        NetMeshObject [] ret = new NetMeshObject[ identifiers.length ];
        
        for( int i=0 ; i<ret.length ; ++i ) {
            ret[i] = findMeshObjectByIdentifier( identifiers[i] );
        }
        return ret;
    }
    
    /**
      * Obtain a MeshObject whose unique identifier is known.
      *
      * @param nameOfLocalObject the Identifier property of the MeshObject
      * @return the locally found MeshObject, or null if not found locally
      * @throws NetMeshObjectAccessException thrown if something went wrong accessing the NetMeshObject
      */
    @Override
    public NetMeshObject accessLocally(
            MeshObjectIdentifier nameOfLocalObject )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObject [] found = accessLocally( new NetMeshObjectIdentifier[] { (NetMeshObjectIdentifier) nameOfLocalObject } );
        
        return found[0];
    }

    /**
      * Obtain N locally available MeshObjects whose unique identifiers are known.
      *
      * @param nameOfLocalObjects the Identifier properties of the MeshObjects
      * @return array of the same length as nameOfLocalObjects, with the locally found MeshObjects filled
      *         in at the same positions. If one or more of the MeshObjects were not found, the location
      *         in the array will be null.
      * @throws NetMeshObjectAccessException thrown if something went wrong accessing the NetMeshObject
      */
    @Override
    public NetMeshObject [] accessLocally(
            MeshObjectIdentifier [] nameOfLocalObjects )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification [] specs = new NetMeshObjectAccessSpecification[ nameOfLocalObjects.length ];
        for( int i=0 ; i<nameOfLocalObjects.length ; ++i ) {
            specs[i] = NetMeshObjectAccessSpecification.createToLocalObject( (NetMeshObjectIdentifier) nameOfLocalObjects[i] );
        }
        NetMeshObject [] ret = accessLocally( specs );
        return ret;
    }

    /**
     * <p>Obtain a MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param networkLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier networkLocation )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification path = NetMeshObjectAccessSpecification.create( networkLocation );
        return accessLocally( path );
    }

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject )
        throws
            NetMeshObjectAccessException
    {
        return accessLocally( remoteLocation, nameOfRemoteObject, theAccessLocallyTimeout );
    }

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier. Specify a non-default timeout.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            long                    timeoutInMillis )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification path = NetMeshObjectAccessSpecification.create( remoteLocation, nameOfRemoteObject );
        return accessLocally( path, timeoutInMillis );
    }

    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            CoherenceSpecification  coherence )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification path = NetMeshObjectAccessSpecification.create( remoteLocation, nameOfRemoteObject, coherence );
        return accessLocally( path, theAccessLocallyTimeout );
    }

    /**
     * Obtain a MeshObject from a remote NetMeshBaseIdentifier.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier  remoteLocation,
            CoherenceSpecification coherence )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification path = NetMeshObjectAccessSpecification.create( remoteLocation, coherence );
        return accessLocally( path, theAccessLocallyTimeout );
    }
    
    /**
     * <p>Obtain a non-standard MeshObject from a remote NetMeshBaseIdentifier. Specify a non-default timeout.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param remoteLocation the NetMeshBaseIdentifier for the location from where we obtain the remote MeshObject
     * @param nameOfRemoteObject the Identifier of the non-standard remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @param coherence the CoherenceSpecification desired by the caller
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshBaseIdentifier   remoteLocation,
            NetMeshObjectIdentifier nameOfRemoteObject,
            CoherenceSpecification  coherence,
            long                    timeoutInMillis )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObjectAccessSpecification path = NetMeshObjectAccessSpecification.create( remoteLocation, nameOfRemoteObject, coherence );
        return accessLocally( path, timeoutInMillis );
    }

    /**
     * <p>Obtain a MeshObject from a remote NetMeshObjectAccessSpecification.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * @param pathToObject the NetMeshObjectAccessSpecification indicating the location and path to use to access the remote MeshObject
     * @return the locally replicated MeshObject, or null if not found
     * @exception ModelObjectAccessException the object's access control policy does not allow this
     *        MeshBase to obtain a replica of the remote object
     * @exception ModelObjectRepositoryAccessException the remote ModelObjectRepository does not allow this
     *        MeshBase to access it
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshObjectAccessSpecification pathToObject )
        throws
            NetMeshObjectAccessException
    {
        return accessLocally( pathToObject, theAccessLocallyTimeout );
    }

    /**
     * <p>Obtain a MeshObject from a remote NetMeshObjectAccessSpecification.
     *    This call does not obtain update rights for the obtained replica.</p>
     * 
     * 
     * @param pathToObject the NetMeshObjectAccessSpecification indicating the location and path to use to access the remote MeshObject
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObject, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject accessLocally(
            NetMeshObjectAccessSpecification pathToObject,
            long                             timeoutInMillis )
        throws
            NetMeshObjectAccessException
    {
        NetMeshObject [] ret = accessLocally( new NetMeshObjectAccessSpecification[] { pathToObject }, timeoutInMillis );
        return ret[0];
    }

    /**
     * <p>Obtain a number of MeshObjects from one ore more remote NetworkPaths.
     * This call does not obtain update rights for the obtained replicas.</p>
     * 
     * @param pathsToObjects the NetworkPaths indicating the location and path to use to access the remote MeshObjects
     * @return the locally replicated MeshObjects in the same sequence, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject [] accessLocally(
            NetMeshObjectAccessSpecification [] pathsToObjects )
        throws
            NetMeshObjectAccessException
    {
        return accessLocally( pathsToObjects, theAccessLocallyTimeout );
    }

    /**
     * <p>Obtain a number of MeshObjects from one ore more remote NetworkPaths. Specify a non-default timeout.
     * This call does not obtain update rights for the obtained replicas.</p>
     * 
     * @param pathsToObjects the NetworkPaths indicating the location and path to use to access the remote MeshObjects
     * @param timeoutInMillis the timeout parameter for this call, in milli-seconds
     * @return the locally replicated MeshObjects in the same sequence, or null if not found
     * @exception NetMeshObjectAccessException the remote MeshObject could not be obtained
     * @exception RemoteQueryTimeoutException an attempted remote access timed out
     */
    public NetMeshObject [] accessLocally(
            NetMeshObjectAccessSpecification [] pathsToObjects,
            long                                timeoutInMillis )
        throws
            NetMeshObjectAccessException
    {
        if( log.isDebugEnabled() ) {
            log.debug(
                    this
                    + ".accessLocally( "
                    + ArrayHelper.arrayToString( pathsToObjects )
                    + ", "
                    + timeoutInMillis
                    + " )" );
        }

        // strip out cyclical and non-sensical items from path
        NetMeshObjectAccessSpecification [] correctRemotePaths;
        if( pathsToObjects != null ) {
            correctRemotePaths = new NetMeshObjectAccessSpecification[ pathsToObjects.length ];
            for( int i=0 ; i<pathsToObjects.length ; ++i ) {
                correctRemotePaths[i] = correctPath( pathsToObjects[i] );
            }
        } else {
            correctRemotePaths = null;
        }
        
        NetMeshObject [] ret       = new NetMeshObject[ pathsToObjects.length ];
        boolean       [] foundRet  = new boolean[ ret.length ]; // we keep a separate array to keep track of which we found already
                                                                // (or know to be null for sure, which is the same) and which not
        boolean       [] sentQuery = new boolean[ ret.length ]; // we keep a separate array to keep track of which we sent a query already

        // first check whether we have any of them already
        int stillToGet = ret.length;
        for( int i=0 ; i<ret.length ; ++i ) {
            NetMeshObject localObject = null;
            boolean       foundObject = false; // this is separate from localObject -- a local object may simply not exist,
                                               // and we don't want to go out on the wire to look for it

            if( correctRemotePaths[i].getNetMeshObjectIdentifier() == null ) {

                if( correctRemotePaths[i].getAccessPath().length == 0 ) {

                    ret[i]       = getHomeObject();
                    foundRet[i]  = true;
                    sentQuery[i] = true;
                    --stillToGet;
                    
                    continue;
                }
            } else {
                // else: we need to keep looking remotely, we don't know locally

                localObject = findMeshObjectByIdentifier( correctRemotePaths[i].getNetMeshObjectIdentifier() );
                if( localObject != null ) {
                    ret[i]       = localObject;
                    foundRet[i]  = true;
                    sentQuery[i] = true;
                    --stillToGet;

                } else if( correctRemotePaths[i].getAccessPath() == null || correctRemotePaths[i].getAccessPath().length == 0 ) {
                    ret[i]       = null;
                    foundRet[i]  = true; // we found it -- well, we know for sure it isn't there
                    sentQuery[i] = true;
                    --stillToGet;
                }
            }
        }

        // return if it looks like we are done
        if( stillToGet == 0 ) {
            return ret;
        }

        // make sure caller has permission
        if( theAccessManager != null ) {
            NetAccessManager realAccessManager = (NetAccessManager) theAccessManager;
            realAccessManager.checkPermittedAccessLocally( this, ret, correctRemotePaths ); // may throw exception
        }

        // we collect all exceptions here, and the corresponding NetworkPaths
        ArrayList<Exception>                          thrownExceptions  = new ArrayList<Exception>();
        ArrayList<NetMeshObjectAccessSpecification[]> failedObjectPaths = new ArrayList<NetMeshObjectAccessSpecification[]>();

        // find the sync object. The proxies will declare the appropriate open queries
        // and everything will work out fine because we are working on the the same
        // instance of the synchronizer
        Object monitor = theReturnSynchronizer.getSyncObject();

        // now break down the still remaining objects into chunks, one chunk per
        // different proxy, and get them until we have everything.
        boolean ok;
        synchronized( monitor ) {

            int pivotIndex = 0;
            while( stillToGet > 0 ) {
                // find the first one we have not gotten yet
                for( ; foundRet[pivotIndex] || sentQuery[pivotIndex] ; ++pivotIndex )
                {}

                int runningIndex = pivotIndex;

                // now find all that have the same first NetMeshBaseIdentifier element
                NetMeshBaseAccessSpecification pivot     = correctRemotePaths[ runningIndex ].getAccessPath()[0];
                NetMeshBaseIdentifier          pivotName = pivot.getNetMeshBaseIdentifier();
                CoherenceSpecification         pivotCalc = pivot.getCoherenceSpecification();

                // create a new set of object names that we still need to get
                NetMeshObjectAccessSpecification [] nextObjectPaths = new NetMeshObjectAccessSpecification[ stillToGet ]; // potentially over-allocated

                nextObjectPaths[0] = NetMeshObjectAccessSpecification.create(
                        ArrayHelper.subarray( correctRemotePaths[ runningIndex ].getAccessPath(), 1, NetMeshBaseAccessSpecification.class ),
                        correctRemotePaths[ runningIndex ].getNetMeshObjectIdentifier() );

                sentQuery[ runningIndex ] = true;
                int nextObjectCount = 1;

                for( ++runningIndex ; runningIndex < correctRemotePaths.length ; ++runningIndex ) {
                    if( foundRet[runningIndex] || sentQuery[runningIndex] ) {
                        continue; // skip
                    }

                    if( ! pivotName.equals( correctRemotePaths[runningIndex].getAccessPath()[0] )) {
                        continue; // has different pivot
                    }

                    nextObjectPaths[ nextObjectCount ] = NetMeshObjectAccessSpecification.create(
                            ArrayHelper.subarray( correctRemotePaths[ runningIndex ].getAccessPath(), 1, NetMeshBaseAccessSpecification.class ),
                            correctRemotePaths[ runningIndex ].getNetMeshObjectIdentifier() );

                    sentQuery[ runningIndex ] = true;

                    ++nextObjectCount;
                }

                // we may have over-allocated, so reduce if necessary
                if( nextObjectCount < nextObjectPaths.length ) {
                    nextObjectPaths = ArrayHelper.subarray( nextObjectPaths, 0, nextObjectCount, NetMeshObjectAccessSpecification.class );
                }

                Proxy theProxy; // out here is better for debugging
                try {
                    theProxy = obtainProxyFor( pivotName, pivotCalc ); // this triggers the Shadow creation in the right subclasses
                    if( theProxy != null ) {
                        theProxy.obtainReplica( nextObjectPaths );
                    }

                } catch( FactoryException ex ) {
                    // log.error( ex );
                    thrownExceptions.add( ex );
                    failedObjectPaths.add( NetMeshObjectAccessSpecification.withPrefix( pivot, nextObjectPaths ) );

                } catch( NetMeshObjectAccessException ex ) {
                    // log.error( ex );
                    thrownExceptions.add( ex );
                    failedObjectPaths.add( NetMeshObjectAccessSpecification.withPrefix( pivot, nextObjectPaths ) );

                }
                stillToGet -= nextObjectPaths.length;
            }

            try {
                ok = theReturnSynchronizer.join( timeoutInMillis );

                if( !ok && thrownExceptions.size() == 0 ) {
                    log.warn( this + ".accessLocally() timed out trying to reach " + ArrayHelper.arrayToString( pathsToObjects ) + ", timeout: " + timeoutInMillis );
                }

                if( timeoutInMillis < 0L ) {
                    ok = true;
                }

            } catch( InterruptedException ex ) {
                log.error( ex );
                ok = false;
            }
        }

        if( ! thrownExceptions.isEmpty() ) {
            ok = false;
        }

        // now insert the results
        boolean allFound  = true;
        boolean someFound = false;
        for( int i=0 ; i<correctRemotePaths.length ; ++i ) {
            if( foundRet[i] ) {
                someFound = true;
                continue;
            }

            NetMeshObject newlyFound = findMeshObjectByIdentifier( correctRemotePaths[i].getNetMeshObjectIdentifier() );
            if( newlyFound == null ) {
                allFound = false;

            } else {
                someFound = true;
                ret[i]    = newlyFound;
            }
        }

        if( ok ) { // all queries returned, definitive answer
            return ret;

        } else if( allFound ) { // we timed out, but we have the answer anyway
            return ret;

        } else if( thrownExceptions.isEmpty() ) { // we timed out, but have a partial result, future results still incoming
            throw new NetMeshObjectAccessException(
                    this,
                    ret,
                    pathsToObjects,
                    new RemoteQueryTimeoutException.QueryIsOngoing( this, someFound, ret ));

        } else {
            Exception      firstException         = thrownExceptions.get( 0 );
            NetMeshObjectAccessSpecification [] firstFailedObjectPaths = failedObjectPaths.get( 0 );

            throw new NetMeshObjectAccessException( this, ret, firstFailedObjectPaths, firstException ); // FIXME
        }
    }
    
    /**
     * This internal helper strips loops and other insecure and non-sensical things
     * out of a NetMeshObjectAccessSpecification. (FIXME: this needs to cover more cases)
     * 
     * @param raw the input NetMeshObjectAccessSpecification that shall be corrected
     * @return the corrected NetMeshObjectAccessSpecification
     */
    protected NetMeshObjectAccessSpecification correctPath(
            NetMeshObjectAccessSpecification raw )
    {
        if( raw == null ) {
            return null;
        }

        NetMeshBaseAccessSpecification [] path = raw.getAccessPath();

        if( path == null ) {
            return raw;
        }

        for( int i=path.length-1 ; i>=0 ; --i ) {
            NetMeshBaseIdentifier candidateName = path[i].getNetMeshBaseIdentifier();
            if( theMeshBaseIdentifier.equals( candidateName )) {

                return NetMeshObjectAccessSpecification.create(
                       ArrayHelper.subarray( path, i+1, NetMeshBaseAccessSpecification.class ),
                       raw.getNetMeshObjectIdentifier() );
            }
        }
        return raw;
    }
    
    /**
      * Obtain the MeshBase's home object. The home object is
      * the only well-known object in a MeshBase, but it is guaranteed to exist.
      *
      * @return the MeshObject that is this MeshBase's home object
      */
    @Override
    public NetMeshObject getHomeObject()
    {
        MeshObject ret = super.getHomeObject();
        return (NetMeshObject) ret;
    }

    /**
     * Obtain the NetMeshBaseIdentifier at which this NetMeshBase is located.
     * 
     * @return the NetMeshBaseIdentifier
     */
    @Override
    public NetMeshBaseIdentifier getIdentifier()
    {
        return (NetMeshBaseIdentifier) super.getIdentifier();
    }

    /**
     * Obtain the AccessManager that controls access to the MeshObjects in this MeshBase.
     *
     * @return the AccessManager
     */
    @Override
    public NetAccessManager getAccessManager()
    {
        return (NetAccessManager) super.getAccessManager();
    }

    /**
     * Set the number of milliseconds this MeshBase is willing to suspend
     * any Thread that invokes an accessLocally operation.
     * After that time, the operation times out and throws a PartialResultException.
     *
     * @param delay the new value for the number of milliseconds this MeshBase
     * is willing to suspend any Thread that invokes an accessLocally operation
     * @see #getAccessLocallyTimesOutAfter
     */
    public void setAccessLocallyTimesOutAfter(
            long delay )
    {
        long oldValue = theAccessLocallyTimeout;

        theAccessLocallyTimeout = delay;

        firePropertyChange(
                this,
                ACCESS_LOCALLY_TIMES_OUT_AFTER_PROPERTY,
                new Long( oldValue ),
                new Long( theAccessLocallyTimeout ));
    }

    /**
     * Obtain the number of milliseconds this MeshBase is willing to suspend
     * any Thread that invokes an accessLocally operation.
     * After that time, the operation times out and throws a PartialResultException.
     *
     * @return the number of milliseconds this MeshBase is willing to suspend
     * any Thread that invokes an accessLocally operation.
     * @see #setAccessLocallyTimesOutAfter
     */
    public final long getAccessLocallyTimesOutAfter()
    {
        return theAccessLocallyTimeout;
    }
    
    /**
     * Obtain the desired timeout, in milliseconds, for tryToObtainLock requests.
     *
     * @return the timeout, in milliseconds
     */
    public final long getTryToObtainLockTimesOutAfter()
    {
        return theTryToObtainLockTimeout;
    }

    /**
     * Set the default value for new NetMeshObject's giveUpLock property if not otherwise specified.
     *
     * @param newValue the new value
     * @see #getDefaultWillGiveUpLock
     */
    public void setDefaultWillGiveUpLock(
            boolean newValue )
    {
        theDefaultWillGiveUpLock = newValue;
    }
    
    /**
     * Obtain the default value for new NetMeshObject's giveUpLock property if not otherwise specified.
     *
     * @param return the default value
     * @see #setDefaultWillGiveUpLock
     */
    public boolean getDefaultWillGiveUpLock()
    {
        return theDefaultWillGiveUpLock;
    }
    
    /**
     * If true, the NetMeshBase will never give up locks, regardless what the individual MeshObjects
     * would like.
     *
     * @return if true, never gives up locks
     */
    public boolean refuseToGiveUpLock()
    {
        return false;
    }

    /**
     * If is set to true, this NetMeshBase prefers that new Replicas create a branch from its own Replicas
     * in the replication graph. If this is set to false, this NetMeshBase prefers that new Replicas create a
     * branch from the Replicas in the third NetMeshBase from which this NetMeshBase has obtained its own
     * Replicas (if it has)
     *
     * @param newValue the new value
     */
    public void setPointsReplicasToItself(
            boolean newValue )
    {
        thePointsReplicasToItself = newValue;
    }

    /**
     * If this returns true, this NetMeshBase prefers that new Replicas create a branch from its own Replicas
     * in the replication graph. If this returns false, this NetMeshBase prefers that new Replicas create a
     * branch from the Replicas in the third NetMeshBase from which this NetMeshBase has obtained its own
     * Replicas (if it has)
     *
     * @return true if Replicas are supposed to become Replicas of locally held Replicas 
     */
    public boolean getPointsReplicasToItself()
    {
        return thePointsReplicasToItself;
    }

    /**
     * <p>Obtain the set of currently active Proxies.</p>
     * 
     * @return the NetMeshBaseIdentifier
     */
    public CursorIterator<Proxy> proxies()
    {
        return theProxyManager.proxies();
    }

    /**
     * Obtain or create a Proxy to the specified NetMeshBaseIdentifier.
     * 
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @param coherence the CoherenceSpecification to use
     * @return the Proxy
     */
    public Proxy obtainProxyFor(
            NetMeshBaseIdentifier  networkIdentifier,
            CoherenceSpecification coherence )
        throws
            FactoryException
    {
        return theProxyManager.obtainFor( networkIdentifier, coherence );
    }
    
    /**
     * Obtain an existing Proxy to the specified NetMeshBaseIdentifier. Return null if no such
     * Proxy exists.
     *
     * @param networkIdentifier the NetMeshBaseIdentifier
     * @return the Proxy
     */
    public Proxy getProxyFor(
            NetMeshBaseIdentifier  networkIdentifier )
    {
        return theProxyManager.get( networkIdentifier );
    }

    /**
     * Obtain this NetMeshBase as a NameServer for its Proxies, keyed by the NetMeshBaseIdentifiers
     * of the partner NetMeshBases.
     * 
     *  @return the NameServer mapping NetMeshBaseIdentifiers to Proxies.
     */
    public NameServer<NetMeshBaseIdentifier,Proxy> getAsProxyNameServer()
    {
        return theProxyManager;
    }

    /**
     * Determine the Proxy, if any, that originated the current Thread.
     *
     * @return the Proxy
     */
    public Proxy determineIncomingProxy()
    {
        synchronized( theThreadProxyTable ) {
            Proxy ret = theThreadProxyTable.get( Thread.currentThread() );

            return ret;
        }
    }

    /**
     * Set the incoming Proxy for this Thread. To be called only by Proxies.
     *
     * @param incomingProxy the incoming Proxy for this Thread
     */
    public void registerIncomingProxy(
            Proxy incomingProxy )
    {
        synchronized( theThreadProxyTable ) {
            Proxy found = theThreadProxyTable.put( Thread.currentThread(), incomingProxy );
            if( found != null ) {
                log.error( "This thread has a proxy already: " + found );
            }
        }
    }

    /**
     * Unregister the incoming Proxy for this Thread. To be called only be Proxies.
     */
    public void unregisterIncomingProxy()
    {
        synchronized( theThreadProxyTable ) {
            Proxy found = theThreadProxyTable.remove( Thread.currentThread() );

            if( found == null ) {
                log.error( "This thread had no proxy" );
            }
        }
    }

    /**
     * Update the lastUpdated property. This is delegated to here so ShadowMeshBases
     * and do this differently than regular NetMeshBases.
     *
     * @param timeUpdated the time to set to, or -1L to indicate the current time
     * @return the time to set to
     */
    public long calculateLastUpdated(
            long timeUpdated )
    {
        long ret;
        if( timeUpdated != -1L ) {
            ret = timeUpdated;
        } else {
            ret = System.currentTimeMillis();
        }
        return ret;
    }

    /**
     * Update the lastRead property. This does not trigger an event generation -- not necessary.
     * This may be overridden.
     *
     * @param timeRead the time to set to, or -1L to indicate the current time
     */
    public long calculateLastRead(
            long timeRead )
    {
        long ret;
        if( timeRead != -1L ) {
            ret = timeRead;
        } else {
            ret = System.currentTimeMillis();
        }
        return ret;
    }
    
    /**
     * Clean up.
     * 
     * @param isPermanent if true, this MeshBase will go away permanmently; if false, it may come alive again some time later
     */
    @Override
    protected void internalDie(
            boolean isPermanent )
    {
        theProxyManager.die( isPermanent );

        theProxyManager = null;
    }

    /**
     * Obtain the right ResourceHelper for StringRepresentation.
     * 
     * @return the ResourceHelper
     */
    @Override
    protected ResourceHelper getResourceHelperForStringRepresentation()
    {
        return ResourceHelper.getInstance( AnetMeshBase.class );
    }

    /**
     * This method may be overridden by subclasses to perform suitable actions when a
     * Transaction was committed.
     *
     * @param tx Transaction the Transaction that was committed
     */
    @Override
    protected void transactionCommittedHook(
            Transaction tx )
    {
        super.transactionCommittedHook( tx );
        
        for( Proxy current : proxies() ) {
            current.transactionCommitted( tx );
        }
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( NetMeshBase.class );

    /**
     * The duration, in milliseconds, that we are willing to suspend a Thread to wait for accessLocally()
     * results to come in.
     */
    private long theAccessLocallyTimeout = theResourceHelper.getResourceLongOrDefault(
            "AccessLocallyTimeout",
            10000L ); // 10 sec

    /**
     * The duration, in milliseconds, that we are willing to suspend a Thread to wait for tryToObtainLock()
     * results to come in.
     */
    private long theTryToObtainLockTimeout = theResourceHelper.getResourceLongOrDefault(
            "TryToObtainLockTimeout",
            2000L ); // 2 sec

    /**
     * The default value for the willGiveUpLock property of newly created NetMeshObjects.
     */
    private boolean theDefaultWillGiveUpLock = theResourceHelper.getResourceBooleanOrDefault( "DefaultWillGiveUpLock", true );

    /**
     * Will this NetMeshBase point new Replicas to itself in the Replication Graph, or not.
     */
    private boolean thePointsReplicasToItself = false;

    /**
     * This object helps us with synchronizing results we are getting asynchronously.
     */
    protected ReturnSynchronizer theReturnSynchronizer = new ReturnSynchronizer( this );
    
    /**
     * We delegate to this ProxyManager to manage our Proxies.
     */
    protected ProxyManager theProxyManager;
    
    /**
     * Table to map Threads to Proxies.
     */
    protected HashMap<Thread,Proxy> theThreadProxyTable = new HashMap<Thread,Proxy>();
}
