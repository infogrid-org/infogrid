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

package org.infogrid.meshbase.store;

import org.infogrid.context.Context;

import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.IterableMeshBaseDifferencer;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.meshbase.Sweeper;
import org.infogrid.meshbase.a.DefaultAMeshObjectIdentifierFactory;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.sweeper.SweepStep;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.store.IterableStore;
import org.infogrid.store.util.IterableStoreBackedMap;

import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;

import java.util.concurrent.ScheduledExecutorService;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;

/**
 * This is a StoreMeshBase that we can iterate over. For this to work, the underlying
 * Store must be an IterableStore.
 */
public class IterableStoreMeshBase
        extends
            StoreMeshBase
        implements
            IterableMeshBase
{
    private static final Log log = Log.getLogInstance( IterableStoreMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param accessMgr the AccessManager that controls access to this MeshBase
      * @param theHomeObjectTypes the EntityTypes with which to bless the home object, if not initialized yet
      * @param store the IterableStore in which to store the MeshObjects
      * @param c the Context in which this MeshBase will run
      * @throws IsAbstractException thrown if the given EntityType for the home object is abstract and cannot be instantiated
      */
    public static IterableStoreMeshBase create(
            MeshBaseIdentifier identifier,
            ModelBase          modelBase,
            AccessManager      accessMgr,
            IterableStore      meshObjectStore,
            Context            c )
        throws
            IsAbstractException
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create();

        IterableStoreMeshBase ret = IterableStoreMeshBase.create( identifier, setFactory, modelBase, accessMgr, meshObjectStore, c );

        return ret;
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param accessMgr the AccessManager that controls access to this MeshBase
      * @param theHomeObjectTypes the EntityTypes with which to bless the home object, if not initialized yet
      * @param store the IterableStore in which to store the MeshObjects
      * @param c the Context in which this MeshBase will run
      * @throws IsAbstractException thrown if the given EntityType for the home object is abstract and cannot be instantiated
      */
    public static IterableStoreMeshBase create(
            MeshBaseIdentifier   identifier,
            MeshObjectSetFactory setFactory,
            ModelBase            modelBase,
            AccessManager        accessMgr,
            IterableStore        meshObjectStore,
            Context              c )
        throws
            IsAbstractException
    {
        StoreMeshBaseEntryMapper objectMapper = new StoreMeshBaseEntryMapper();
        
        IterableStoreBackedMap<MeshObjectIdentifier,MeshObject> objectStorage = IterableStoreBackedMap.createWeak( objectMapper, meshObjectStore );

        MeshObjectIdentifierFactory identifierFactory = DefaultAMeshObjectIdentifierFactory.create();

        IterableStoreMeshBase ret = new IterableStoreMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, c );

        objectMapper.setMeshBase( ret );
        ret.initializeHomeObject();
        
        if( log.isDebugEnabled() ) {
            log.debug( "created " + ret );
        }
        return ret;
    }

    /**
      * Constructor.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param accessMgr the AccessManager that controls access to this MeshBase
      * @param cache the in-memory cache to use
      * @param mapper the Mapper to and from the Store
      * @param c the Context in which this MeshBase will run
      */
    protected IterableStoreMeshBase(
            MeshBaseIdentifier                                      identifier,
            MeshObjectIdentifierFactory                             identifierFactory,
            MeshObjectSetFactory                                    setFactory,
            ModelBase                                               modelBase,
            AccessManager                                           accessMgr,
            IterableStoreBackedMap<MeshObjectIdentifier,MeshObject> cache,
            Context                                                 c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, c );
    }
    
    /**
     * Obtain an Iterator over all MeshObjects in the Store.
     *
     * @return the Iterator
     */
    public CursorIterator<MeshObject> iterator()
    {
        return getCachingMap().valuesIterator( null, MeshObject.class );
    }
    
    /**
     * Obtain an Iterator over all MeshObjects in the Store.
     *
     * @return the Iterator
     */
    final public CursorIterator<MeshObject> getIterator()
    {
        return iterator();
    }

    /**
     * Determine the number of MeshObjects in this MeshBase.
     *
     * @return the number of MeshObjets in this MeshBase
     */
    public int size()
    {
        return ((IterableStore) getCachingMap().getStore()).size();
    }

    /**
     * Factory method for a IterableMeshBaseDifferencer, with this IterableMeshBase
     * being the comparison base.
     *
     * @return the IterableMeshBaseDifferencer
     */
    public IterableMeshBaseDifferencer getDifferencer()
    {
        return new IterableMeshBaseDifferencer( this );
    }

    /**
     * Continually sweep this IterableMeshBase in the background, according to
     * the configured Sweeper.
     *
     * @param scheduleVia the ScheduledExecutorService to use for scheduling
     * @throws NullPointerException thrown if no Sweeper has been set
     */
    public void startBackgroundSweeping(
            ScheduledExecutorService scheduleVia )
        throws
            NullPointerException
    {
        Sweeper sweep = theSweeper;
        if( sweep == null ) {
            throw new NullPointerException();
        }
        theSweeperScheduler = scheduleVia;

        scheduleSweepStep();
    }
    
    /**
     * Stop the background sweeping.
     */
    public void stopBackgroundSweeping()
    {
        SweepStep nextStep = theNextSweepStep;
        if( nextStep == null ) {
            return;
        }
        synchronized( nextStep ) {
            nextStep.cancel();
            theNextSweepStep = null;
        }
    }
    
    /**
     * Perform a sweep on every single MeshObject in this InterableMeshBase.
     * This may take a long time; using background sweeping is almost always
     * a better alternative.
     */
    public synchronized void sweepAllNow()
    {
        Sweeper sweep = theSweeper;
        if( sweep == null ) {
            throw new NullPointerException();
        }
        for( MeshObject candidate : this ) {
            sweep.potentiallyDelete( candidate );
        }
    }

    /**
     * Invoked by the SweepStep, schedule the next SweepStep.
     */
    public void scheduleSweepStep()
    {
        if( theNextSweepStep != null ) {
            theNextSweepStep = theNextSweepStep.nextStep();
        } else {
            theNextSweepStep = SweepStep.create( this );
        }
        theNextSweepStep.scheduleVia( theSweeperScheduler );
    }

    /**
     * The Scheduler for the Sweeper, if any.
     */
    protected ScheduledExecutorService theSweeperScheduler;
    
    /**
     * The next background Sweep task, if any.
     */
    protected SweepStep theNextSweepStep;
}
