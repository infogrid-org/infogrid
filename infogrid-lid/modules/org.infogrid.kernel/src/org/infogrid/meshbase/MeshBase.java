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

package org.infogrid.meshbase;

import org.infogrid.context.ObjectInContext;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.security.AccessManager;

import org.infogrid.meshbase.transaction.MeshObjectLifecycleListener;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.meshbase.transaction.TransactionListener;

import org.infogrid.model.primitives.RoleType;
import org.infogrid.modelbase.ModelBase;

import org.infogrid.util.LiveDeadObject;
import org.infogrid.util.QuitListener;
import org.infogrid.util.text.StringRepresentation;

import java.beans.PropertyChangeListener;
import org.infogrid.mesh.set.MeshObjectSetFactory;

/**
  * <p>MeshBase represents the place where MeshObjects live. MeshBases collect MeshObjects
  * for management purposes. A MeshBase may retain its MeshObjects after reboot,
  * or it may be volatile, but this distinction is not visible by a client
  * of a MeshBase unless the client really wants to find out.</p>
  *
  * <p>Many "interesting" operations on the MeshBase are delegated to
  * managers of various sorts that may or may not be supported by any
  * given MeshBase implementation.</p>
  *
  * <p>A MeshBase's home object is a single MeshObject in the MeshBase
  * that is "known" to the outside world. This might often be some sort of
  * "directory object", or an object which is a logical entry point into the
  * information held by the MeshBase. The home object cannot be deleted, but
  * it may be updated (e.g. blessed, properties set, related etc.)</p>
  */
public interface MeshBase
        extends
            ObjectInContext,
            QuitListener,
            LiveDeadObject
{    
    /**
     * Obtain the MeshBaseIdentifier that identifies this MeshBase.
     * 
     * @return the MeshBaseIdentifier
     */
    public abstract MeshBaseIdentifier getIdentifier();

    /**
     * Obtain a MeshObject whose unique identifier is known.
     * 
     * @param identifier the Identifier property of the MeshObject
     * @return the locally found MeshObject, or null if not found locally
     * @throws MeshObjectAccessException thrown if something went wrong accessing the MeshObject
     */
    public abstract MeshObject accessLocally(
            MeshObjectIdentifier identifier )
        throws
            MeshObjectAccessException;

    /**
     * Obtain N locally available MeshObjects whose unique identifiers are known.
     * 
     * @param identifiers the Identifier properties of the MeshObjects
     * @return array of the same length as identifiers, with the locally found MeshObjects filled
     *         in at the same positions. If one or more of the MeshObjects were not found, the location
     *         in the array will be null.
     * @throws MeshObjectAccessException thrown if something went wrong accessing one or more MeshObjects
     */
    public abstract MeshObject [] accessLocally(
            MeshObjectIdentifier[] identifiers )
        throws
            MeshObjectAccessException;

   /**
     * <p>Obtain a manager for MeshObject lifecycles.</p>
     * 
     * @return a MeshBaseLifecycleManager that works on this MeshBase
     */
    public abstract MeshBaseLifecycleManager getMeshBaseLifecycleManager();

    /**
     * Obtain the ModelBase that contains the type descriptions
     * for the content of this MeshBase.
     *
     * @return the MeshBase that contains the type descriptions
     */
    public abstract ModelBase getModelBase();

    /**
     * Create a new Transaction as soon as possible. This means the calling Thread may be suspended
     * for some amount of time before it can start, or it may time out.
     *
     * @return the created and started Transaction
     * @throws TransactionException.TransactionAsapTimeout a Transaction timeout has occurred
     */
    public abstract Transaction createTransactionAsap()
        throws
            TransactionException.TransactionAsapTimeout;

    /**
     * Create a new Transaction now, or throw an Exception if this is not possible at this very
     * moment. The calling Thread will not be suspended.
     *
     * @return the created and started Transaction
     * @throws TransactionException.TransactionActiveAlready a Transaction was active already
     */
    public abstract Transaction createTransactionNow()
        throws
            TransactionException.TransactionActiveAlready;

    /**
      * Create a new Transaction as soon as possible, but only if we are not currently on a Thread
      * that has already opened a Transaction. If we are on such a Thread, return null,
      * indicating that the operation can go ahead and there is no need to commit a
      * potential sub-Transaction. Otherwise behave like createTransactionAsap().
      *
      * @return the created and started Transaction, or null if one is already open on this Thread
      * @throws TransactionException.TransactionAsapTimeout a Transaction timeout has occurred
      */
    public abstract Transaction createTransactionAsapIfNeeded()
        throws
            TransactionException.TransactionAsapTimeout;

    /**
      * Create a Transaction now, but only if we are not currently on a Thread
      * that has already opened a Transaction. If we are on such a Thread, return null,
      * indicating that the operation can go ahead and there is no need to commit a
      * potential sub-Transaction. Otherwise behave like createTransactionNow().
      *
      * @return the created and started Transaction, or null if one is already open on this Thread
      * @throws TransactionException.TransactionActiveAlready a Transaction was active already on a different Thread
      */
    public abstract Transaction createTransactionNowIfNeeded()
        throws
            TransactionException.TransactionActiveAlready;

    /**
      * Obtain the currently active Transaction (if any).
      *
      * @return the currently active Transaction, or null if there is none
      */
    public abstract Transaction getCurrentTransaction();

    /**
     * Internal helper to check that we are on a, and on the right Transaction.
     *
     * @return the current Transaction
     * @throws TransactionException throw if this has been invoked outside of proper Transaction boundaries
     */
    public abstract Transaction checkTransaction()
        throws
            TransactionException;

    /**
     * <p>Find a MeshObject in this MeshBase by its Identifier. Unlike
     * the {@link #accessLocally accessLocally} methods, this method purely considers MeshObjects in the
     * MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.</p>
     * <p>If not found, returns <code>null</code>.</p>
     * 
     * @param identifier the Identifier of the MeshObject that shall be found
     * @return the found MeshObject, or null if not found
     * @see #findMeshObjectByIdentifierOrThrow
     */
    public abstract MeshObject findMeshObjectByIdentifier(
            MeshObjectIdentifier identifier );

    /**
     * <p>Find a set of MeshObjects in this MeshBase by their Identifiers. Unlike
     *    the {@link #accessLocally accessLocally} methods, this method purely considers MeshObjects in the
     *    MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.</p>
     * <p>If one or more of the MeshObjects could not be found, returns <code>null</code> at
     *    the respective index in the returned array.</p>
     * 
     * @param identifiers the Identifiers of the MeshObjects that shall be found
     * @return the found MeshObjects, which may contain null values for MeshObjects that were not found
     */
    public abstract MeshObject [] findMeshObjectsByIdentifier(
            MeshObjectIdentifier[] identifiers );

    /**
     * <p>Find a MeshObject in this MeshBase by its Identifier. Unlike
     * the {@link #accessLocally accessLocally} methods, this method purely considers MeshObjects in the
     * MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.</p>
     * <p>If not found, throws {@link MeshObjectsNotFoundException MeshObjectsNotFoundException}.</p>
     * 
     * @param identifier the Identifier of the MeshObject that shall be found
     * @return the found MeshObject, or null if not found
     * @throws MeshObjectsNotFoundException if the MeshObject was not found
     */
    public abstract MeshObject findMeshObjectByIdentifierOrThrow(
            MeshObjectIdentifier identifier )
        throws
            MeshObjectsNotFoundException;

    /**
     * <p>Find a set of MeshObjects in this MeshBase by their Identifiers. Unlike
     *    the {@link #accessLocally accessLocally} method, this method purely considers MeshObjects in the
     *    MeshBase, and does not attempt to obtain them if they are not in the MeshBase yet.</p>
     * <p>If one or more of the MeshObjects could not be found, throws
     *    {@link MeshObjectsNotFoundException MeshObjectsNotFoundException}.</p>
     * 
     * @param identifiers the Identifiers of the MeshObjects that shall be found
     * @return the found MeshObjects, which may contain null values for MeshObjects that were not found
     */
    public abstract MeshObject [] findMeshObjectsByIdentifierOrThrow(
            MeshObjectIdentifier[] identifiers )
        throws
            MeshObjectsNotFoundException;

    /**
      * Obtain the MeshBase's home object. The home object is
      * the only well-known object in a MeshBase, but it is guaranteed to exist and
      * cannot be deleted.
      *
      * @return the MeshObject that is this MeshBase's home object
      */
    public abstract MeshObject getHomeObject();

    /**
      * Determine whether this is a persistent MeshBase.
      * A MeshBase is persistent if the information stored in it last longer
      * than the lifetime of the virtual machine running this MeshBase.
      *
      * @return true if this is a persistent MeshBase.
      */
    public abstract boolean isPersistent();

    /**
     * Obtain the AccessManager associated with this MeshBase, if any.
     *
     * @return the AccessManager
     */
    public abstract AccessManager getAccessManager();

    /**
     * Set a Sweeper for this MeshBase.
     *
     * @param newSweeper the new Sweeper
     */
    public abstract void setSweeper(
            Sweeper newSweeper );
    
    /**
     * Obtain the currently set Sweeper for this MeshBase, if any.
     *
     * @return the Sweeper, if any
     */
    public abstract Sweeper getSweeper();
    
    /**
     * Obtain a factory for MeshObjectIdentifiers that is appropriate for this MeshBase.
     *
     * @return the factory
     */
    public abstract MeshObjectIdentifierFactory getMeshObjectIdentifierFactory();

    /**
     * Obtain a factory for MeshObjectSets.
     * 
     * @return the factory
     */
    public abstract MeshObjectSetFactory getMeshObjectSetFactory();

    /**
     * Clear the in-memory cache, if this MeshBase has one. This method only makes any sense
     * if the MeshBase is persistent. Any MeshBase may implement this as a no op.
     * This must only be invoked if no clients hold references to MeshObjects in the cache.
     */
    public abstract void clearMemoryCache();

    /**
     * Determine the set of MeshObjects that are neighbors of both of the passed-in MeshObjects.
     * This is a convenience method that can have substantial performance benefits, depending on
     * the underlying implementation of MeshObject.
     *
     * @param one the first MeshObject
     * @param two the second MeshObject
     * @return the set of MeshObjects that are neighbors of both MeshObject one and MeshObject two
     */
    public abstract MeshObjectSet findCommonNeighbors(
            MeshObject one,
            MeshObject two );

    /**
     * Determine the set of MeshObjects that are neighbors of all of the passed-in MeshObjects.
     * This is a convenience method that can have substantial performance benefits, depending on
     * the underlying implementation of MeshObject.
     *
     * @param all the MeshObjects whose common neighbors we seek.
     * @return the set of MeshObjects that are neighbors of all MeshObjects
     */
    public abstract MeshObjectSet findCommonNeighbors(
            MeshObject [] all );
    
    /**
     * Determine the set of MeshObjects that are neighbors of both of the passed-in MeshObjects
     * while playing particular RoleTypes.
     * This is a convenience method that can have substantial performance benefits, depending on
     * the underlying implementation of MeshObject.
     *
     * @param one the first MeshObject
     * @param oneType the RoleType to be played by the first MeshObject with the to-be-found MeshObjects
     * @param two the second MeshObject
     * @param twoType the RoleType to be played by the second MeshObject with the to-be-found MeshObjects
     * @return the set of MeshObjects that are neighbors of both MeshObject one and MeshObject two
     */
    public abstract MeshObjectSet findCommonNeighbors(
            MeshObject one,
            RoleType   oneType,
            MeshObject two,
            RoleType   twoType );

    /**
     * Determine the set of MeshObjects that are neighbors of all of the passed-in MeshObjects
     * while playing particular RoleTypes.
     * This is a convenience method that can have substantial performance benefits, depending on
     * the underlying implementation of MeshObject.
     *
     * @param all the MeshObjects whose common neighbors we seek.
     * @param allTypes the RoleTypes to be played by the MeshObject at the same position in the array
     *        with the to-be-found MeshObjects
     * @return the set of MeshObjects that are neighbors of all MeshObjects
     */
    public abstract MeshObjectSet findCommonNeighbors(
            MeshObject [] all,
            RoleType []   allTypes );

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addDirectPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addWeakPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Add a PropertyChangeListener.
      *
      * @param newListener the to-be-added PropertyChangeListener
      * @see #removePropertyChangeListener
      */
    public abstract void addSoftPropertyChangeListener(
            PropertyChangeListener newListener );

    /**
      * Remove a PropertyChangeListener.
      *
      * @param oldListener the to-be-removed PropertyChangeListener
      * @see #addPropertyChangeListener
      */
    public abstract void removePropertyChangeListener(
            PropertyChangeListener oldListener );

    /**
      * Add a TransactionListener.
      *
      * @param newListener the to-be-added TransactionListener
      * @see #removeTransactionListener
      */
    public abstract void addDirectTransactionListener(
            TransactionListener newListener );

    /**
      * Add a TransactionListener.
      *
      * @param newListener the to-be-added TransactionListener
      * @see #removeTransactionListener
      */
    public abstract void addWeakTransactionListener(
            TransactionListener newListener );

    /**
      * Add a TransactionListener.
      *
      * @param newListener the to-be-added TransactionListener
      * @see #removeTransactionListener
      */
    public abstract void addSoftTransactionListener(
            TransactionListener newListener );

    /**
      * Remove a TransactionListener.
      *
      * @param oldListener the to-be-removed TransactionListener
      * @see #addTransactionListener
      */
    public abstract void removeTransactionListener(
            TransactionListener oldListener );

    /**
     * Subscribe to events indicating the addition/removal/etc
     * of MeshObjects to/from this MeshBase.
     * 
     * @param newListener the to-be-added MMeshObjectLifecycleListener@see #removeMeshObjectLifecycleEventListener
     */
    public abstract void addDirectMeshObjectLifecycleEventListener(
            MeshObjectLifecycleListener newListener );

    /**
     * Subscribe to events indicating the addition/removal/etc
     * of MeshObjects to/from this MeshBase.
     * 
     * @param newListener the to-be-added MMeshObjectLifecycleListener@see #removeMeshObjectLifecycleEventListener
     */
    public abstract void addWeakMeshObjectLifecycleEventListener(
            MeshObjectLifecycleListener newListener );

    /**
     * Subscribe to events indicating the addition/removal/etc
     * of MeshObjects to/from this MeshBase.
     * 
     * @param newListener the to-be-added MMeshObjectLifecycleListener@see #removeMeshObjectLifecycleEventListener
     */
    public abstract void addSoftMeshObjectLifecycleEventListener(
            MeshObjectLifecycleListener newListener );

    /**
     * Unsubscribe from events indicating the addition/removal/etc
     * of MeshObjects to/from this MeshBase.
     * 
     * @param oldListener the to-be-removed MMeshObjectLifecycleListener@see #addMeshObjectLifecycleEventListener
     */
    public abstract void removeMeshObjectLifecycleEventListener(
            MeshObjectLifecycleListener oldListener );

    /**
     * Obtain a String representation of this MeshBase that can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param isDefaultMeshBase true if the MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentation(
            StringRepresentation rep,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the start part of a String representation of this MeshBase that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkStart(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );

    /**
     * Obtain the end part of a String representation of this MeshBase that acts
     * as a link/hyperlink and can be shown to the user.
     * 
     * @param rep the StringRepresentation
     * @param contextPath the context path
     * @param isDefaultMeshBase true if the MeshBase is the default MeshBase
     * @return String representation
     */
    public abstract String toStringRepresentationLinkEnd(
            StringRepresentation rep,
            String               contextPath,
            boolean              isDefaultMeshBase );
}
