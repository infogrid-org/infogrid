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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.account.translate;

import java.util.Map;
import org.infogrid.lid.account.AbstractLidAccountManager;
import org.infogrid.lid.account.LidAccountExistsAlreadyException;
import org.infogrid.lid.account.LidAccountManager;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;

/**
 * Implements the LidAccountManager interface by delegating to another LidAccountManager
 * after translating identifiers. The translation methods must be defined in
 * subclasses.
 */
public abstract class TranslatingLidAccountManager
        extends
            AbstractLidAccountManager
{
    /**
     * Constructor for subclasses only.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param delegate the delegate LidIdentityManager
     */
    protected TranslatingLidAccountManager(
            Identifier        siteIdentifier,
            LidAccountManager delegate )
    {
        super( siteIdentifier );

        theDelegate = delegate;
    }

    /**
     * Constructor for subclasses only.
     * 
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param identifierFactory the IdentifierFactory to use
     * @param delegate the delegate LidIdentityManager
     */
    protected TranslatingLidAccountManager(
            Identifier        siteIdentifier,
            IdentifierFactory identifierFactory,
            LidAccountManager delegate )
    {
        super( siteIdentifier, identifierFactory );
        
        theDelegate = delegate;
    }

    /**
     * Obtain a HasIdentifier, given its Identifier. This will either return a LidAccount
     * or not. If it returns a LidAccount, the identifier referred to that locally provisioned
     * LidAccount. If it returns something other than a LidAccount, it refers to a remote
     * persona. To determine the LidAccount that may be associated with the remote persona,
     * call determineLidAccountFromRemoteIdentifier.
     *
     * @param identifier the Identifier for which the HasIdentifier will be retrieved
     * @return the found HasIdentifier
     * @throws CannotFindHasIdentifierException thrown if the HasIdentifier cannot be found
     * @throws InvalidIdentifierException thrown if the provided Identifier was invalid for this HasIdentifierFinder
     */
    public HasIdentifier find(
            Identifier identifier )
        throws
            CannotFindHasIdentifierException,
            InvalidIdentifierException
    {
        if( identifier == null ) {
            throw new NullPointerException( "identifier must not be null" );
        }
        Identifier delegateIdentifier = translateIdentifierForward( identifier );

        HasIdentifier delegateHasIdentifier = theDelegate.find( delegateIdentifier );
        if( delegateHasIdentifier instanceof LidAccount ) {
            LidAccount ret = translateAccountBackward( (LidAccount) delegateHasIdentifier );
            return ret;
        } else {
            return delegateHasIdentifier;

        }
    }
    
    /**
     * Given a remote persona and a site, determine the LidAccount that has been provisioned for
     * the remote persona at the site. May return null if none has been provisioned.
     *
     * @param remote the remote persona
     * @param siteIdentifier identifier of the site at which the account has been provisioned
     * @return the found LidAccount, or null
     */
    public LidAccount determineLidAccountFromRemotePersona(
            HasIdentifier remote,
            Identifier    siteIdentifier )
    {
        LidAccount delegateAccount = theDelegate.determineLidAccountFromRemotePersona( remote, siteIdentifier );
        if( delegateAccount == null ) {
            return null;
        }
        LidAccount ret = translateAccountBackward( delegateAccount );
        return ret;
    }

    /**
     * Provision a LidAccount.
     *
     * @param localIdentifier the Identifier for the to-be-created LidAccount. This may be null, in which case
     *        the LidAccountManager assigns a localIdentifier
     * @param remotePersonas the remote personas to be associated with the locally provisioned LidAccount
     * @param attributes the attributes for the to-be-created LidAccount
     * @param groupIdentifiers identifiers of the groups the Account belongs to
     * @return the LidAccount that was created
     * @throws LidAccountExistsAlreadyException thrown if a LidAccount with this Identifier exists already
     */
    @Override
    public LidAccount provisionAccount(
            Identifier                    localIdentifier,
            HasIdentifier []              remotePersonas,
            Map<String,String>            attributes,
            Identifier []                 groupIdentifiers )
        throws
            LidAccountExistsAlreadyException
    {
        Identifier delegateIdentifier = localIdentifier != null ? translateIdentifierForward( localIdentifier ) : null;
        
        try {
            LidAccount delegateAccount = theDelegate.provisionAccount(
                    delegateIdentifier,
                    remotePersonas,
                    attributes,
                    groupIdentifiers );
            
            LidAccount ret = translateAccountBackward( delegateAccount );
            return ret;

        } catch( LidAccountExistsAlreadyException ex ) {
            LidAccount ret = translateAccountBackward( ex.getAccount() );
            throw new LidAccountExistsAlreadyException( ret, ex );
        }        
    }

    /**
     * Delete a LidAccount. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidAccount that will be deleted
     * @throws UnsupportedOperationException thrown if this LidAccountManager does not permit the deletion of LidAccounts
     */
    @Override
    public void delete(
            LidAccount toDelete )
    {
        LidAccount delegate = translateAccountForward( (TranslatingLidAccount) toDelete );

        theDelegate.delete( delegate );
    }
    
    /**
     * Translate the Identifier as used by this class into the Identifier as used by the delegate.
     * 
     * @param identifier input parameter
     * @return translated identifier
     */
    protected abstract Identifier translateIdentifierForward(
            Identifier identifier );

    /**
     * Translate the Identifier as used by the delegate into the Identifier as used by this class.
     * 
     * @param identifier input parameter
     * @return translated identifier
     */
    protected abstract Identifier translateIdentifierBackward(
            Identifier identifier );
    
    /**
     * Translate a LidAccount as used by this class into the LidAccount as used by the delegate.
     * 
     * @param account input parameter
     * @return translated LidAccount
     */
    protected LidAccount translateAccountForward(
            TranslatingLidAccount account )
    {
        if( account == null ) {
            return null;
        }

        LidAccount ret = account.getDelegate();
        return ret;
    }

    /**
     * Translate a LidAccount as used by the delegate into the LidAccount as used by this class.
     * 
     * @param persona input parameter
     * @return translated LidAccount
     */
    protected TranslatingLidAccount translateAccountBackward(
            LidAccount persona )
    {
        if( persona == null ) {
            return null;
        }
        Identifier delegateIdentifier = translateIdentifierBackward( persona.getIdentifier() );

        TranslatingLidAccount ret = new TranslatingLidAccount( delegateIdentifier, persona );
        return ret;
    }    

    /**
     * The delegate LidAccountManager.
     */
    protected LidAccountManager theDelegate;
}
