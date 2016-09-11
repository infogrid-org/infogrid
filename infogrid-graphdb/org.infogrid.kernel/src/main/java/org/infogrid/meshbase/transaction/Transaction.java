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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.transaction;

import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectGraphModificationException;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MultiplicityException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;
import org.infogrid.mesh.RoleTypeNotBlessedException;
import org.infogrid.mesh.security.PropertyReadOnlyException;
import org.infogrid.mesh.security.ThreadIdentityManager;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.MultiplicityValue;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.FlexibleListenerSet;
import org.infogrid.util.Pair;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.ToStringDumper;
import org.infogrid.util.logging.ToStringDumperFactory;

/**
  * <p>The concept of a Transaction in InfoGrid. This is an abstract class;
  *    specific implementations of MeshBase provide concrete subclasses.</p>
  * <p>Transactions are the bracket for a unit of Changes (called a ChangeSet) on
  *    a MeshBase.</p>
  */
public abstract class Transaction
        implements
            CanBeDumped
{
    private static final Log log = Log.getLogInstance(Transaction.class); // our own, private logger

    /**
      * Private constructor, as Transactions can only be created by the MeshBase that they guard.
      * They can also only be created within the Thread by which they will be used.
      *
      * @param transactable the MeshBase that the Transaction guards
      */
    protected Transaction(
            MeshBase transactable )
    {
        theTransactable = transactable;

        myThread  = Thread.currentThread();
        myInvoker = new TransactionConstructionMarker();

        status = Status.TRANSACTION_STARTED;

        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "constructor" );
        }
    }

    /**
     * Obtain the MeshBase to which this Transaction belongs.
     *
     * @return the MeshBase
     */
    public MeshBase getMeshBase()
    {
        return theTransactable;
    }

    /**
      * Commit a started Transaction.
      *
      * @throws MeshObjectGraphModificationException thrown if the changes made during this Transaction do not conform to the rules of the model
      */
    public final synchronized void commitTransaction()
        throws
            MeshObjectGraphModificationException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "commitTransaction" );
        }

        try {
            checkThreadIsAllowed();
        } catch( IllegalTransactionThreadException ex ) {
            throw new IllegalStateException( "trying to commit transaction from wrong thread" );
        }

        if( !( status == Status.TRANSACTION_STARTED || status == Status.TRANSACTION_VOTED )) {
            log.error( "illegal state for transaction: " + status );
        }

        ThreadIdentityManager.sudo();

        try {
            preCommitHook();

            checkTransactionValid();

            theChangeSet.freeze(); // only freeze here, otherwise can can't do the rollback

            status = Status.TRANSACTION_COMMITTED;

            postCommitSucceededHook();

        } catch( MultiplicityException t ) {
            try {
                doRollback();
            } catch( Throwable t2 ) {
                log.error( "MultiplicityException leading to failed rollback", t );
                log.error( "Failed rollback", t2 );
            } finally {
                postCommitFailedHook( t ); // don't leave any transactions open
            }

            throw t;

        } finally {
            ThreadIdentityManager.sudone();
        }
    }

    /**
     * Roll back all changes performed within this Transaction so far.
     *
     * @param thrown the Throwable that caused us to rollback the Transaction
     */
    public final synchronized void rollbackTransaction(
            Throwable thrown )
    {
        if( log.isInfoEnabled() ) {
            log.info( ToStringDumperFactory.create( ToStringDumper.DEFAULT_MAXLEVEL, Integer.MAX_VALUE ), this, "rollbackTransaction", thrown );
        }

        try {
            checkThreadIsAllowed();
        } catch( IllegalTransactionThreadException ex ) {
            throw new IllegalStateException( "trying to rollback transaction from wrong thread" );
        }

        if( !( status == Status.TRANSACTION_STARTED || status == Status.TRANSACTION_VOTED )) {
            log.error( "illegal state for transaction: " + status );
        }

        ThreadIdentityManager.sudo();

        try {
            preRollbackHook();

            doRollback();

            status = Status.TRANSACTION_ROLLEDBACK;

            postRollbackSucceededHook( thrown );

        } finally {
            ThreadIdentityManager.sudone();
        }
    }

    /**
     * Internal method to perform the actual rollback.
     */
    protected void doRollback()
    {
        // go backwards in the change set
        CursorIterator<Change> iter = ChangeSet.createCopy( theChangeSet ).iterator();
        iter.moveToAfterLast();
        while( iter.hasPrevious() ) {
            Change current = iter.previous();
            try {
                Change inverted = current.inverse();

                if( inverted != null ) {
                    inverted.applyTo( theTransactable );
                } else {
                    log.error( "Could not invert change", current );
                }

            } catch( CannotApplyChangeException ex ) {
                Throwable cause = ex.getCause();

                if(    !( cause instanceof PropertyReadOnlyException )
                    && !( cause instanceof IllegalPropertyValueException )
                    && !( cause instanceof NotRelatedException )
                    && !( cause instanceof RoleTypeNotBlessedException )
                    && !( cause instanceof RelatedAlreadyException )
                    && !( cause instanceof RoleTypeBlessedAlreadyException ))
                {
                    log.error( ex );
                    // that's the best we can do
                }

            } catch( MeshObjectGraphModificationException ex ) {
                log.error( ex );
                // that's the best we can do
            } catch( TransactionException ex ) {
                log.error( ex );
                // that's the best we can do
            } catch( Throwable ex ) {
                log.error( ex );
                // that's the best we can do
            }
        }
    }

    /**
     * Determine whether the calling Thread is compatible with this Transaction.
     *
     * @throws IllegalTransactionThreadException if the calling Thread is not
     *         compatible with this Transaction
     */
    public void checkThreadIsAllowed()
        throws
            IllegalTransactionThreadException
    {
        // FIXME? This should perhaps be somewhat looser than this: Threads should be able to
        // "empower" other threads to do work within a transaction on their behalf

        if( !owns( Thread.currentThread() )) {
            throw new IllegalTransactionThreadException( theTransactable );
        }
    }

    /**
     * Determine whether the passed-in Thread owns this Transaction.
     *
     * @param t the Thread to test
     * @return true if t owns this Transaction
     */
    public boolean owns(
            Thread t )
    {
        return myThread == t;
    }

    /**
      * Obtain the current status of this Transaction.
      *
      * @return the current status of this Transaction
      */
    public Status getStatus()
    {
        return status;
    }

    /**
      * Obtain the set of Changes that have been made as part of this
      * Transaction so far. Depending on the state of the Transaction,
      * the ChangeSet might be complete, frozen or not.
      *
      * @return the set of Changes up to this point
      */
    public ChangeSet getChangeSet()
    {
        return theChangeSet;
    }

    /**
     * Check that changes made during the Transaction lead to a valid graph.
     *
     * @throws MultiplicityException a RoleType's multiplicity was violated
     */
    protected void checkTransactionValid()
        throws
            MultiplicityException
    {
        // go through the changes, and make sure all MeshObjects touched still meet all the constraints
        for( Change change : theChangeSet ) {
            MeshObject affected = change.getAffectedMeshObject();
            if( affected.getIsDead() ) {
                continue;
            }

            for( EntityType entityType : affected.getTypes()) {
                for( RoleType roleType : entityType.getAllRoleTypes()) {
                    MeshObjectIdentifier [] others = affected.traverseToIdentifiers( roleType );
                    roleType.checkMultiplicity( affected, others );
                }
            }
        }
    }

    /**
     * This hook is invoked just prior to committing the Transaction. This allows subclasses to hook
     * in before the commit actually happens.
     */
    protected void preCommitHook()
    {
        // no op on this level
    }

    /**
     * This hook is invoked after the Transaction has been committed. This allows subclasses to hook
     * in once we know that the commit actually happened.
     */
    protected void postCommitSucceededHook()
    {

    }

    /**
     * This hook is invoked after an attempt to commit the Transaction failed. This allows subclasses to hook
     * in once we know that the attempted commit failed.
     *
     * @param thrown the Throwable that caused us to rollback the Transaction
     */
    protected void postCommitFailedHook(
            Throwable thrown )
    {

    }

    /**
     * This hook is invoked just prior to rolling back the Transaction. This allows subclasses to hook in
     * before the rollback actually happens.
     */
    protected void preRollbackHook()
    {

    }

    /**
     * This hook is invoked after the Transaction has been rolled back. This allows subclasses to hook
     * in once we know that the rollback actually happened.
     *
     * @param thrown the Throwable that caused us to rollback the Transaction
     */
    protected void postRollbackSucceededHook(
            Throwable thrown )
    {

    }

    /**
      * Add a Change to the ChangeSet. This shall not be invoked by the
      * application programmer; InfoGrid internals do.
      *
      * We don't synchronize, as it is unlikely that this method is invoked from the wrong Thread.
      *
      * @param newChange the Change to be added to this Transaction
      */
    public void addChange(
            Change newChange )
    {
        theChangeSet.addChange( newChange );
    }

    /**
     * Add a new listener object to this set using a WeakReference.
     *
     * @param newListener the listener to be added to this set
     */
    public void addWeakTransactionListener(
            TransactionListener newListener )
    {
        theListeners.addWeak( newListener );
    }

    /**
     * Add a new listener object to this set using a SoftReference.
     *
     * @param newListener the listener to be added to this set
     */
    public void addSoftTransactionListener(
            TransactionListener newListener )
    {
        theListeners.addSoft( newListener );
    }

    /**
     * Add a new listener object to this set directly, i.e. without using References.
     *
     * @param newListener the listener to be added to this set
     */
    public void addTransactionListener(
            TransactionListener newListener )
    {
        theListeners.addDirect( newListener );
    }

    /**
     * Internal helper to createCopy a listener set.
     *
     * @return the created listener set
     */
    protected FlexibleListenerSet<TransactionListener,Transaction,Status> createListenerSet()
    {
        FlexibleListenerSet<TransactionListener,Transaction,Status> ret
                = new FlexibleListenerSet<TransactionListener,Transaction,Status>() {
                        /**
                         * Fire the event to one contained object.
                         *
                         * @param listener the receiver of this event
                         * @param event the sent event
                         * @param parameter dispatch parameter
                         */
                        @Override
                        protected void fireEventToListener(
                                TransactionListener listener,
                                Transaction         event,
                                Status              parameter )
                        {
                            if( status == Status.TRANSACTION_STARTED ) {
                                listener.transactionStarted( Transaction.this );
                            // } else if( status == TRANSACTION_VOTED ) {
                            //     theListener.transactionVoted( this );
                            } else if( status == Status.TRANSACTION_COMMITTED ) {
                                listener.transactionCommitted( Transaction.this );
                            // } else if( status == TRANSACTION_ROLLEDBACK ) {
                            //     theListener.transactionRolledBack( this );
                            } else {
                                log.error( "unknown value for status in Transaction" );
                            }
                        }
                };
        return ret;
    }

    /**
     * Remove a TransactionListener from this Transaction.
     *
     * @param oldListener the new listener to be removed
     * @see #addTransactionListener
     */
    public synchronized void removeTransactionListener(
            TransactionListener oldListener )
    {
        if( theListeners != null ) {
            theListeners.remove( oldListener );
        }
    }

    /**
      * This internal helper notifies all listeners that the state
      * of this Transaction has changed.
      */
    protected void notifyStateChanged()
    {
        FlexibleListenerSet<TransactionListener,Transaction,Status> listeners = theListeners;
        if( listeners != null ) {
            listeners.fireEvent( this, status );
        }
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "transactable",
                    "changes",
                    "myThread",
                    "myInvoker"
                },
                new Object[] {
                    theTransactable,
                    theChangeSet,
                    myThread,
                    myInvoker
                });
    }

    /**
     * Obtain the TransactionConstructionMarker that marks how and where this Transaction was created.
     * For debugging only.
     *
     * @return the TransactionConstructionMarker that marks when this Transaction was created
     */
    public TransactionConstructionMarker getStartMarker()
    {
        return myInvoker;
    }

    /**
      * The Thread on which this Transaction was started and on which it
      * needs to be executed.
      */
    protected Thread myThread;

    /**
     * An Exception created in the constructor of this Transaction, so we can
     * determine "whose" Transaction this is.
     */
    protected TransactionConstructionMarker myInvoker;

    /**
      * The current status of this Transaction.
      */
    protected Status status;

    /**
      * The set of Changes accumulated during this Transaction so far.
      */
    protected ChangeSet theChangeSet = new ChangeSet();

    /**
      * The transactable that we belong to.
      */
    protected MeshBase theTransactable;

    /**
      * The set of TransactionListeners. Allocated as needed.
      */
    protected FlexibleListenerSet<TransactionListener,Transaction,Status> theListeners = null;

    /**
     * The number of attempts of recalculations we do before we declare this a
     * circular loop and give up.
     */
    protected static final int MAX_RECALCULATE_ATTEMPTS = 100;

    /**
     * Defines the values for a transaction status. FIXME: Not all of these values
     * have been implemented so far.
     */
    public static enum Status
    {
        /**
          * Indicates that this Transaction is currently being started.
          */
        TRANSACTION_BEING_STARTED,

        /**
          * Indicates that this Transaction has been started but not voted on.
          */
        TRANSACTION_STARTED,

        /**
          * Indicates that this Transaction has voted but not committed or rollback'ed yet.
          */
        TRANSACTION_VOTED,

        /**
          * Indicates that this Transaction has been committed.
          */
        TRANSACTION_COMMITTED,

        /**
          * Indicates that this Transaction has been rolled back.
          */
        TRANSACTION_ROLLEDBACK
    }
}
