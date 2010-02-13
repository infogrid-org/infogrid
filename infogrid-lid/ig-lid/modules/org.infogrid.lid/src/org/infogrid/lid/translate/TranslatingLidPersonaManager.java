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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.translate;

import java.util.Map;
import org.infogrid.lid.AbstractLidPersonaManager;
import org.infogrid.lid.LidPersonaExistsAlreadyException;
import org.infogrid.lid.LidPersonaManager;
import org.infogrid.lid.LidPersona;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.InvalidIdentifierException;

/**
 * Implements the LidPersonaManager interface by delegating to another LidPersonaManager
 * after translating identifiers. The translation methods must be defined in
 * subclasses.
 */
public abstract class TranslatingLidPersonaManager
        extends
            AbstractLidPersonaManager
{
    /**
     * Constructor for subclasses only.
     *
     * @param delegate the delegate LidIdentityManager
     */
    protected TranslatingLidPersonaManager(
            LidPersonaManager delegate )
    {
        theDelegate = delegate;
    }

    /**
     * Constructor for subclasses only.
     * 
     * @param identifierFactory the IdentifierFactory to use
     * @param delegate the delegate LidIdentityManager
     */
    protected TranslatingLidPersonaManager(
            IdentifierFactory identifierFactory,
            LidPersonaManager delegate )
    {
        super( identifierFactory );
        
        theDelegate = delegate;
    }

    /**
     * Obtain a HasIdentifier, given its Identifier. This will either return a LidPersona
     * or not. If it returns a LidPersona, the identifier referred to that locally provisioned
     * LidPersona. If it returns something other than a LidPersona, it refers to a remote
     * persona. To determine the LidPersona that may be associated with the remote persona,
     * call determineLidPersonaFromRemoteIdentifier.
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
        if( delegateHasIdentifier instanceof LidPersona ) {
            LidPersona ret = translatePersonaBackward( (LidPersona) delegateHasIdentifier );
            return ret;
        } else {
            return delegateHasIdentifier;

        }
    }
    
    /**
     * Given a remote persona, determine the locally provisioned corresponding
     * LidPersona. May return null if none has been provisioned.
     *
     * @param remote the remote persona
     * @return the found LidPersona, or null
     */
    public LidPersona determineLidPersonaFromRemotePersona(
            HasIdentifier remote )
    {
        LidPersona delegatePersona = theDelegate.determineLidPersonaFromRemotePersona( remote );
        if( delegatePersona == null ) {
            return null;
        }
        LidPersona ret = translatePersonaBackward( delegatePersona );
        return ret;
    }

    /**
     * Provision a LidPersona.
     *
     * @param localIdentifier the Identifier for the to-be-created LidPersona. This may be null, in which case
     *        the LidPersonaManager assigns a localIdentifier
     * @param remotePersonas the remote personas to be associated with the locally provisioned LidPersona
     * @param attributes the attributes for the to-be-created LidPersona
     * @param credentials the credentials for the to-be-created LidPersona
     * @return the LidPersona that was created
     * @throws LidPersonaExistsAlreadyException thrown if a LidPersona with this Identifier exists already
     */
    @Override
    public LidPersona provisionPersona(
            Identifier                    localIdentifier,
            HasIdentifier []              remotePersonas,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials )
        throws
            LidPersonaExistsAlreadyException
    {
        Identifier delegateIdentifier = localIdentifier != null ? translateIdentifierForward( localIdentifier ) : null;
        
        try {
            LidPersona delegatePersona = theDelegate.provisionPersona(
                    delegateIdentifier,
                    remotePersonas,
                    attributes,
                    credentials );
            
            LidPersona ret = translatePersonaBackward( delegatePersona );
            return ret;

        } catch( LidPersonaExistsAlreadyException ex ) {

            LidPersona ret = translatePersonaBackward( ex.getPersona() );
            throw new LidPersonaExistsAlreadyException( ret, ex );
        }        
    }

    /**
     * Delete a LidPersona. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param toDelete the LidPersona that will be deleted
     * @throws UnsupportedOperationException thrown if this LidPersonaManager does not permit the deletion of LidPersonas
     */
    @Override
    public void delete(
            LidPersona toDelete )
    {
        LidPersona delegate = translatePersonaForward( (TranslatingLidPersona) toDelete );

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
     * Translate a LidPersona as used by this class into the LidPersona as used by the delegate.
     * 
     * @param persona input parameter
     * @return translated LidPersona
     */
    protected LidPersona translatePersonaForward(
            TranslatingLidPersona persona )
    {
        if( persona == null ) {
            return null;
        }

        LidPersona ret = persona.getDelegate();
        return ret;
    }

    /**
     * Translate a LidPersona as used by the delegate into the LidPersona as used by this class.
     * 
     * @param persona input parameter
     * @return translated LidPersona
     */
    protected TranslatingLidPersona translatePersonaBackward(
            LidPersona persona )
    {
        if( persona == null ) {
            return null;
        }
        Identifier delegateIdentifier = translateIdentifierBackward( persona.getIdentifier() );

        TranslatingLidPersona ret = new TranslatingLidPersona( delegateIdentifier, persona );
        return ret;
    }    

    /**
     * The delegate LidPersonaManager.
     */
    protected LidPersonaManager theDelegate;
}
