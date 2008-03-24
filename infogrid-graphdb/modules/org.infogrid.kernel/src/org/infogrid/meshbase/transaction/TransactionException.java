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

package org.infogrid.meshbase.transaction;

import org.infogrid.meshbase.MeshBase;

/**
  * This is a supertype for all transaction-related exceptions. Inner classes
  * provide more concrete subclasses.
  */
public abstract class TransactionException
        extends
            Exception
{
    /**
     * Construct one.
     *
     * @param trans the MeshBase that raised this TransactionException
     * @param tx the Transaction that raised this TransactionException
     */
    protected TransactionException(
            MeshBase    trans,
            Transaction tx )
    {
        theTransactable = trans;
        theTransaction  = tx;
    }

    /**
     * Obtain the MeshBase that raised this TransactionException.
     *
     * @return the MeshBase that raised this TransactionException
     */
    public final MeshBase getTransactable()
    {
        return theTransactable;
    }

    /**
     * Obtain the Transaction that raised this TransactionException.
     *
     * @return the Transaction that raised this TransactionException
     */
    public final Transaction getTransaction()
    {
        return theTransaction;
    }

    /**
     * Convert this into a string, for debugging.
     *
     * @return this instance as a string
     */
    @Override
    public String toString()
    {
        return super.toString() + " -- MB: " + theTransactable + ", TX: " + theTransaction;
    }

    /**
     * The MeshBase the raised this TransactionException.
     */
    protected transient MeshBase theTransactable;

    /**
     * The Transaction that raised this TransactionException.
     */
    protected transient Transaction theTransaction;

    /**
      * This TransactionException is thrown if a (potentially) modifying operation
      * is invoked by a thread that does not belong to the currently active Transaction.
      */
    public static class IllegalTransactionThread
            extends
                TransactionException
    {
        /**
         * Constructor.
         *
         * @param trans the MeshBase that was affected
         */
        public IllegalTransactionThread(
                MeshBase trans )
        {
            super( trans, trans.getCurrentTransaction() );
        }
    }

    /**
      * This TransactionException is thrown if a (potentially) modifying operation
      * is invoked outside of Transaction boundaries.
      */
    public static class NotWithinTransactionBoundaries
            extends
                TransactionException
    {
        /**
         * Constructor.
         *
         * @param trans the MeshBase that was affected
         */
        public NotWithinTransactionBoundaries(
                MeshBase trans )
        {
            super( trans, null );
        }
    }

    /**
      * This TransactionException is thrown to indicate that a Transaction is
      * already active and thus the asked-for new Transaction cannot be created.
      */
    public static class TransactionActiveAlready
            extends
                TransactionException
    {
        /**
         * Constructor.
         *
         * @param trans the MeshBase that was affected
         * @param blockingTransaction the Transaction that blocked the new Transaction
         */
        public TransactionActiveAlready(
                MeshBase    trans,
                Transaction blockingTransaction )
        {
            super( trans, blockingTransaction );
        }
    }

    /**
      * This TransactionException is thrown to indicate that the current Thread reached a time out for
      * trying to create a Transaction "asap". While there may be genuine congestion in an application,
      * the most likely cause of this Exception is that the developer forgot to complete a previously
      * opened Transaction, in which case the next Transaction cannot be started.
      * <p>In our experience, the best pattern to use for Transactions is this:
      * <pre>
      * Transaction tx = null;
      * try {
      *    tx = meshBase.createTransactionXXX(); // use one of the factory methods, mor being the ModelObjectRepository for which the Transaction shall be created
      *    // code that is subject to the Transaction
      * } catch( ... ) {
      *    // catch code for whatever Exceptions need to be handled her
      * } finally {
      *    if( tx != null ) {
      *        tx.commit();
      *    }
      * }
      * </pre>
      * This code works with all Transaction factory methods, including the ones that do not actuall create
      * a Transaction because the current Thread has an open Transaction already.
      */
    public static class TransactionAsapTimeout
            extends
                TransactionException
    {
        /**
         * Constructor.
         *
         * @param trans the MeshBase that was affected
         * @param blockingTransaction the Transaction that blocked the new Transaction
         */
        public TransactionAsapTimeout(
                MeshBase    trans,
                Transaction blockingTransaction )
        {
            super( trans, blockingTransaction );
        }
    }
}
