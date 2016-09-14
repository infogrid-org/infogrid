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

package org.infogrid.meshbase.transaction;

import org.infogrid.util.AbstractLocalizedException;

/**
 * Thrown if a Transaction commit failed and was automatically rolled back.
 * This typically happens if a semantic constraint (such as a multiplicity on
 * a RelationshipType) was found to be violated at commit time.
 */
public class CommitFailedException
    extends
        AbstractLocalizedException
{
    /**
     * Constructor.
     * 
     * @param tx the Transaction that failed
     * @param cause the underlying cause
     */
    public CommitFailedException(
            Transaction tx,
            Throwable   cause )
    {
        super( cause );
        
        theTransaction = tx;
    }
    
    /**
     * Obtain the Transaction that failed.
     * 
     * @return the Transaction
     */
    public Transaction getTransaction()
    {
        return theTransaction;
    }
    
    /**
     * Obtain resource parameters for the internationalization.
     *
     * @return the resource parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] {
            theTransaction,
            theTransaction.getMeshBase()
        };
    }
    
    /**
     * The Transaction that failed.
     */
    protected Transaction theTransaction;
}
