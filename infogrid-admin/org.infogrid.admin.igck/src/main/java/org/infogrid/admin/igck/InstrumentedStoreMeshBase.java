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

package org.infogrid.admin.igck;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.a.DefaultAMeshObjectIdentifierFactory;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSetFactory;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.meshbase.a.AMeshBaseLifecycleManager;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.store.StoreMeshBase;
import org.infogrid.meshbase.store.StoreMeshBaseEntryMapper;
import org.infogrid.meshbase.store.StoreMeshBaseSwappingHashMap;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.store.Store;
import org.infogrid.store.StoreEntryMapper;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.util.text.StringRepresentationParseException;

/**
 * An IterableStoreMeshBase that has been instrumented for the purposes of Igck.
 */
public class InstrumentedStoreMeshBase
    extends
        StoreMeshBase
{
    private static final Log log = Log.getLogInstance( InstrumentedStoreMeshBase.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @return the created InstrumentedStoreMeshBase
     */
    public static InstrumentedStoreMeshBase create(
            Store meshObjectStore )
    {
        try {
            ImmutableMMeshObjectSetFactory setFactory
                    = ImmutableMMeshObjectSetFactory.create( MeshObject.class, MeshObjectIdentifier.class );
            
            StoreMeshBaseEntryMapper objectMapper = new StoreMeshBaseEntryMapper();

            MyMap objectStorage = new MyMap( objectMapper, meshObjectStore );

            InstrumentedStoreMeshBase ret = new InstrumentedStoreMeshBase(
                    DefaultMeshBaseIdentifierFactory.create().fromExternalForm( "DefaultMeshBase" ),
                    DefaultAMeshObjectIdentifierFactory.create(),
                    setFactory,
                    ModelBaseSingleton.getSingleton(),
                    AMeshBaseLifecycleManager.create(),
                    null,
                    objectStorage,
                    SimpleContext.createRoot( "root context" ));

            setFactory.setMeshBase( ret );
            objectMapper.setMeshBase( ret );
            ret.initializeHomeObject();
            
            return ret;

        } catch( StringRepresentationParseException ex ) {
            log.error( ex );
            return null;
        }
    }
    
    /**
     * Constructor.
     *
     * @param identifier the MeshBaseIdentifier of this MeshBase
     * @param identifierFactory the factory for MeshObjectIdentifiers appropriate for this MeshBase
     * @param setFactory the factory for MeshObjectSets appropriate for this MeshBase
     * @param modelBase the ModelBase containing type information
     * @param life the MeshBaseLifecycleManager to use
     * @param accessMgr the AccessManager that controls access to this MeshBase
     * @param cache the CachingMap that holds the MeshObjects in this MeshBase
     * @param context the Context in which this MeshBase runs
     */
    protected InstrumentedStoreMeshBase(
            MeshBaseIdentifier                                            identifier,
            MeshObjectIdentifierFactory                                   identifierFactory,
            MeshObjectSetFactory                                          setFactory,
            ModelBase                                                     modelBase,
            AMeshBaseLifecycleManager                                     life,
            AccessManager                                                 accessMgr,
            StoreMeshBaseSwappingHashMap<MeshObjectIdentifier,MeshObject> cache,
            Context                                                       context )
    {
        super( identifier, identifierFactory, setFactory, modelBase, life, accessMgr, cache, context );
    }
    
    /**
     * Write a particular MeshObject back to disk.
     * 
     * @param obj the MeshObject to write to disk
     */
    public void flush(
            MeshObject obj )
    {
        MyMap map = (MyMap) theCache;
        
        map.flush( obj );
        
    }
    
    /**
     * Make some methods more accessible.
     */
    static class MyMap
        extends
            StoreMeshBaseSwappingHashMap<MeshObjectIdentifier,MeshObject>
    {
        /**
         * Constructor.
         * 
         * @param mapper the <code>StoreEntryMapper</code> to use
         * @param store the underlying <code>Store</code>
         */
        public MyMap(
                StoreEntryMapper<MeshObjectIdentifier,MeshObject> mapper,
                Store                                             store )
        {
            super( mapper, store );
        }
        
        /**
         * Write a particular MeshObject back to disk.
         * 
         * @param obj the MeshObject to write to disk
         */
        public void flush(
                MeshObject obj )
        {
            // the saveValueToStorage does not do anything in the superclass
            super.saveValueToStorageUponCommit( obj.getIdentifier(), obj );
        }
    }
}
