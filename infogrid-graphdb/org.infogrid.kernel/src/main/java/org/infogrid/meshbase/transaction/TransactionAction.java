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

/**
 * An action that is performed within Transaction boundaries.
 *
 * @param <T> the return type of the action
 */
@FunctionalInterface
public interface TransactionAction<T>
{
    /**
     * Obtain the number of times an attempted commit is retried.
     * This number is limited to a maximum value specified by the MeshBase.
     *
     * @return the number of retries
     */
    default int getCommitRetries()
    {
        return 1;
    }

    /**
     * Execute the action. This will be invoked within valid Transaction
     * boundaries.
     *
     * @param tx the Transaction being executed
     * @return a return object, if any
     * @throws Throwable this declaration makes it easy to implement this method
     */
    public abstract T execute(
            Transaction tx )
        throws
            Throwable;

    /**
     * Overridable callback executed just prior to attempting to perform a commit.
     *
     * @param tx the Transaction
     */
    default void preCommitTransaction(
            Transaction tx )
    {
        // no op
    }

    /**
     * Overridable callback executed just after having been successful performing a commit.
     *
     * @param tx the Transaction
     */
    default void postCommitTransaction(
            Transaction tx )
    {
        // no op
    }

    /**
     * Overridable callback executed just prior to attempting to perform a rollback.
     *
     * @param tx the Transaction
     * @param causeForRollback the Throwable that was the cause for this rollback
     */
    default void preRollbackTransaction(
            Transaction tx,
            Throwable   causeForRollback )
    {
        // no op
    }

    /**
     * Overridable callback executed just after having been successful performing a rollback.
     *
     * @param tx the Transaction
     * @param causeForRollback the Throwable that was the cause for this rollback
     */
    default void postRollbackTransaction(
            Transaction tx,
            Throwable   causeForRollback )
    {
        // no op
    }

    /**
     * Make it easy for TransactionActions to perform other TransactionActions on the same Transaction.
     * Note: this will not execute the pre and post commit/rollback callbacks on the subtransaction.
<<<<<<< HEAD
     *
     * @param <T2>
     * @param tx
     * @param subAct the TransactionAction to perform
     * @return
     * @throws java.lang.Throwable
=======
     * 
     * @param <T2> the return type of the subaction
     * @param subAct the TransactionAction to perform
     * @return return value of the subaction
     * @throws Throwable all sorts of problems might occur
>>>>>>> master
     */
    default <T2> T2 executeSubTransactionAction(
            Transaction           tx,
            TransactionAction<T2> subAct )
        throws
            Throwable
    {
        T2 ret = subAct.execute( tx );

        return ret;
    }
}
