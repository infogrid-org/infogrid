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

package org.infogrid.probe.shadow.a;

import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.AnetMeshObject;

import org.infogrid.meshbase.net.NetMeshBaseAccessSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecification;
import org.infogrid.meshbase.net.Proxy;
import org.infogrid.meshbase.net.a.AnetMeshBase;
import org.infogrid.meshbase.net.a.AnetMeshBaseLifecycleManager;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.meshbase.net.transaction.NetMeshObjectCreatedEvent;

import org.infogrid.model.primitives.EntityType;

import org.infogrid.probe.StagingMeshBaseLifecycleManager;
import org.infogrid.probe.shadow.DefaultShadowProxy;

import org.infogrid.util.FactoryException;
import org.infogrid.util.logging.Log;

/**
 * This MeshBaseLifecycleManager overrides the default times for object creation and updates.
 */
public class AStagingMeshBaseLifecycleManager
        extends
            AnetMeshBaseLifecycleManager
        implements
            StagingMeshBaseLifecycleManager
{
    private static final Log log = Log.getLogInstance( AStagingMeshBaseLifecycleManager.class ); // our own, private logger

    /**
     * Constructor. The application developer should not call this or a subclass constructor; use
     * MeshBase.getMeshObjectLifecycleManager() instead.
     * 
     * @param base the MeshBase on which this MeshBaseLifecycleManager works
     */
    protected AStagingMeshBaseLifecycleManager(
            AStagingMeshBase base )
    {
        super( base );
    }
    
    /**
     * Determine the current time for the purpose of assigning the right creation time to a
     * to-be-created MeshObject. This may be overridden in subclasses.
     *
     * @return the creation time to use, in System.currentTimeMillis() format
     */
    @Override
    protected long determineCreationTime()
    {
        AStagingMeshBase realBase = (AStagingMeshBase) theMeshBase;
        
        long ret = realBase.getCurrentUpdateStartedTime();
        return ret;
    }

    /**
      * <p>Create a ForwardReference without a type.</p>
      *
      * @param meshObjectLocation identifies the data source where the MeshObject can be found
      * @return the created MeshObject
      * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
      */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier meshObjectLocation )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation ),
                null,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());                
    }

    /**
      * <p>Create a ForwardReference with a type. This type may or may not be abstract: as this
      *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
      *
      * @param meshObjectLocation identifies the data source where the MeshObject can be found
      * @param type the EntityType with which the MeshObject will be blessed
      * @return the created MeshObject
      * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
      */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier meshObjectLocation,
            EntityType            type )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation ),
                new EntityType [] { type },
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());        
    }

    /**
      * <p>Create a ForwardReference with zero or more types. These types may or may not be abstract: as this
      *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
      *
      * @param meshObjectLocation identifies the data source where the MeshObject can be found
      * @param types the EntityTypes with which the MeshObject will be blessed
      * @return the created MeshObject
      * @throws IsAbstractException thrown if the ENtityType is abstract and cannot be instantiated
      * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
      * @throws NotPermittedException thrown if the blessing operation is not permitted
      */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier meshObjectLocation,
            EntityType []         types )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation ),
                types,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());        
    }

    /**
     * <p>Create a ForwardReference without a type.</p>
     * 
     * @param meshObjectLocation identifies the data source where the MeshObject can be found
     * @param identifier the Identifier of the MeshObject into which this ForwardReference resolves.
     * @throws TransactionException thrown if this method was invoked outside of proper Transaction boundaries
     * @throws ExteIdentifierNotUniqueExceptionown if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier   meshObjectLocation,
            NetMeshObjectIdentifier identifier )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation, identifier ),
                null,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }

    /**
     * <p>Create a ForwardReference with a type. This type may or may not be abstract: as this
     *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
     * 
     * @param meshObjectLocation identifies the data source where the MeshObject can be found
     * @param identifier the Identifier of the to-be-created MeshObject.
     * @param type the EntityType with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier   meshObjectLocation,
            NetMeshObjectIdentifier identifier,
            EntityType              type )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation, identifier ),
                new EntityType[] { type },
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }

    /**
     * <p>Create a ForwardReference with zero or more types. Each type may or may not be abstract: as this
     *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
     * 
     * @param meshObjectLocation identifies the data source where the MeshObject can be found
     * @param identifier the Identifier of the to-be-created MeshObject.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshBaseIdentifier   meshObjectLocation,
            NetMeshObjectIdentifier identifier,
            EntityType []           types )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                NetMeshObjectAccessSpecification.create( meshObjectLocation, identifier ),
                types,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }

    /**
     * <p>Create a ForwardReference without a type.</p>
     * 
     * @param pathToObject specifies where and how the MeshObject can be found
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshObjectAccessSpecification pathToObject )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                pathToObject,
                null,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }

    /**
     * <p>Create a ForwardReference with a type. This type may or may not be abstract: as this
     *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
     * 
     * @param pathToObject specifies where and how the MeshObject can be found
     * @param type the EntityType with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshObjectAccessSpecification pathToObject,
            EntityType                       type )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                pathToObject,
                new EntityType[] { type },
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }

    /**
     * <p>Create a ForwardReference with zero or more types. Each type may or may not be abstract: as this
     *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
     * 
     * @param pathToObject specifies where and how the MeshObject can be found
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */
    public AnetMeshObject createForwardReference(
            NetMeshObjectAccessSpecification pathToObject,
            EntityType []                    types )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        return createForwardReference(
                pathToObject,
                types,
                -1L,
                -1L,
                -1L,
                -1L,
                ((AnetMeshBase) theMeshBase).getDefaultWillGiveUpLock());
    }
    
    /**
     * <p>Create a ForwardReference with zero or more types. Each type may or may not be abstract: as this
     *    creates a ForwardReference, it may resolve in a MeshObject blessed with a subtype.</p>
     * 
     * 
     * @param meshObjectLocation identifies the data source where the MeshObject can be found
     * @param identifier the Identifier of the to-be-created MeshObject.
     * @param types the EntityTypes with which the MeshObject will be blessed
     * @return the created MeshObject
     * @throws TransactionException thrown if this method is invoked outside of proper Transaction boundaries
     * @throws ExternIdentifierNotUniqueExceptionn if a MeshObject exists already in this MeshBase with the specified Identifier
     */    
    public AnetMeshObject createForwardReference(
            NetMeshObjectAccessSpecification pathToObject,
            EntityType []                    types,
            long                             timeCreated,
            long                             timeUpdated,
            long                             timeRead,
            long                             timeAutoDeletes,
            boolean                          giveUpLock )
        throws
            TransactionException,
            MeshObjectIdentifierNotUniqueException
    {
        NetMeshBaseAccessSpecification [] accessPath = pathToObject.getAccessPath();
        NetMeshObjectIdentifier           identifier = pathToObject.getNetMeshObjectIdentifier();

        if( accessPath == null || accessPath.length == 0 ) {
            throw new IllegalArgumentException( "Cannot use empty access path in NetMeshBaseAccessSpecification to create ForwardReference" );
        }
        
        AnetMeshObject ret = null;
        try {
            DefaultShadowProxy placeholderProxy = (DefaultShadowProxy) ((AStagingMeshBase)theMeshBase).obtainProxyFor( accessPath[0].getNetMeshBaseIdentifier(), null );
            placeholderProxy.setIsPlaceholder( true );
            
            if( identifier == null ) {
                throw new IllegalArgumentException( "null Identifier" );
            }

            long now = determineCreationTime();
            if( timeCreated < 0 ) {
                timeCreated = now;
            }
            if( timeUpdated < 0 ) {
                timeUpdated = now;
            }
            if( timeRead < 0 ) {
                timeRead = now;
            }
            // not timeAutoDeletes

            AnetMeshBase realBase = (AnetMeshBase) theMeshBase;

            Transaction tx = realBase.checkTransaction();

            NetMeshObject existing = findInStore( identifier );
            if( existing != null ) {
                throw new MeshObjectIdentifierNotUniqueException( existing );
            }

            ret = instantiateMeshObjectImplementation(
                    identifier,
                    timeCreated,
                    timeUpdated,
                    timeRead,
                    timeAutoDeletes,
                    giveUpLock,
                    new Proxy[] { placeholderProxy },
                    0,
                    AnetMeshObject.HERE_CONSTANT );

            if( types != null && types.length > 0 ) {
                try {
                    ret.blessForwardReference( types );

                } catch( EntityBlessedAlreadyException ex ) {
                    log.error( ex );
                }
            }

            putIntoStore( ret );

            Proxy                 incomingProxy           = realBase.determineIncomingProxy();
            NetMeshBaseIdentifier incomingProxyIdentifier = incomingProxy != null ? incomingProxy.getPartnerMeshBaseIdentifier() : null;

            tx.addChange( new NetMeshObjectCreatedEvent(
                    realBase,
                    ret,
                    incomingProxyIdentifier ));

            assignOwner( ret );

        } catch( NotPermittedException ex ) {
            log.error( ex );
            
        } catch( FactoryException ex ) {
            log.error( ex );
            
        }
        return ret;
    }
}
