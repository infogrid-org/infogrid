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

import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.meshbase.a.AMeshBase;
import org.infogrid.meshbase.a.DefaultAMeshObjectIdentifierFactory;
import org.infogrid.meshbase.security.AccessManager;

import org.infogrid.modelbase.ModelBase;

import org.infogrid.store.Store;
import org.infogrid.store.util.StoreBackedMap;

import org.infogrid.util.CachingMap;
import org.infogrid.util.logging.Log;

/**
  * A MeshBase that delegates persistence to an external Store. All of its intelligence
  * resides either in the factory method, or in the AMeshBase implementation.
  */
public class StoreMeshBase
        extends
            AMeshBase
{
    private static final Log log = Log.getLogInstance( StoreMeshBase.class ); // our own, private logger

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param accessMgr the AccessManager that controls access to this MeshBase
      * @param store the Store in which to store the MeshObjects
      * @param c the Context in which this MeshBase will run
      */
    public static StoreMeshBase create(
            MeshBaseIdentifier identifier,
            ModelBase          modelBase,
            AccessManager      accessMgr,
            Store              meshObjectStore,
            Context            c )
    {
        ImmutableMMeshObjectSetFactory setFactory = ImmutableMMeshObjectSetFactory.create( MeshObject.class, MeshObjectIdentifier.class );

        StoreMeshBase ret = StoreMeshBase.create( identifier, setFactory, modelBase, accessMgr, meshObjectStore, c );

        return ret;
    }

    /**
      * Factory method.
      *
      * @param modelBase the ModelBase with the type definitions we use
      * @param accessMgr the AccessManager that controls access to this MeshBase
      * @param store the Store in which to store the MeshObjects
      * @param c the Context in which this MeshBase will run
      */
    public static StoreMeshBase create(
            MeshBaseIdentifier   identifier,
            MeshObjectSetFactory setFactory,
            ModelBase            modelBase,
            AccessManager        accessMgr,
            Store                meshObjectStore,
            Context              c )
    {
        StoreMeshBaseEntryMapper objectMapper = new StoreMeshBaseEntryMapper();
        
        StoreBackedMap<MeshObjectIdentifier,MeshObject> objectStorage = StoreBackedMap.createWeak( objectMapper, meshObjectStore );

        MeshObjectIdentifierFactory identifierFactory = DefaultAMeshObjectIdentifierFactory.create();

        StoreMeshBase ret = new StoreMeshBase( identifier, identifierFactory, setFactory, modelBase, accessMgr, objectStorage, c );

        setFactory.setMeshBase( ret );
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
    protected StoreMeshBase(
            MeshBaseIdentifier                          identifier,
            MeshObjectIdentifierFactory                 identifierFactory,
            MeshObjectSetFactory                        setFactory,
            ModelBase                                   modelBase,
            AccessManager                               accessMgr,
            CachingMap<MeshObjectIdentifier,MeshObject> cache,
            Context                                     c )
    {
        super( identifier, identifierFactory, setFactory, modelBase, accessMgr, cache, c );
    }

    /**
     * Helper method for typecasting to the right subtype of CachingMap.
     */
    protected StoreBackedMap<MeshObjectIdentifier, MeshObject> getCachingMap()
    {
        return (StoreBackedMap<MeshObjectIdentifier,MeshObject>) theCache;
    }
}
