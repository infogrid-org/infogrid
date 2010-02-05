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
import org.infogrid.lid.LidPersona;
import org.infogrid.lid.LidPersonaExistsAlreadyException;
import org.infogrid.lid.LidPersonaManager;
import org.infogrid.lid.LidPersonaUnknownException;
import org.infogrid.lid.credential.LidCredentialType;
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
     * Provision a LidPersona.
     *
     * @param localIdentifier the Identifier for the to-be-created LidPersona
     * @param attributes the attributes for the to-be-created LidPersona
     * @param credentials the credentials for the to-be-created LidPersona
     * @return the LidPersona that was created
     * @throws LidPersonaExistsAlreadyException thrown if a LidPersona with this Identifier exists already
     */
    @Override
    public LidPersona provisionPersona(
            Identifier                    localIdentifier,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials )
        throws
            LidPersonaExistsAlreadyException
    {
        if( localIdentifier == null ) {
            throw new NullPointerException( "localIdentifier must not be null" );
        }
        Identifier delegateIdentifier = translateIdentifierForward( localIdentifier );
        
        try {
            LidPersona delegatePersona = theDelegate.provisionPersona(
                    delegateIdentifier,
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
     * Obtain a LidPersona, given its Identifier.
     *
     * @param identifier the Identifier for which the LidPersona will be retrieved
     * @return the found LidPersona
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this Identifier
     * @throws InvalidIdentifierException thrown if an invalid Identifier was provided
     */
    public LidPersona find(
            Identifier identifier )
        throws
            LidPersonaUnknownException,
            InvalidIdentifierException
    {
        if( identifier == null ) {
            throw new NullPointerException( "identifier must not be null" );
        }
        Identifier delegateIdentifier = translateIdentifierForward( identifier );

        try {
            LidPersona delegatePersona = theDelegate.find( delegateIdentifier );
            LidPersona ret             = translatePersonaBackward( delegatePersona );
            return ret;

        } catch( LidPersonaUnknownException ex ) {
            throw new LidPersonaUnknownException( identifier, ex );
        }        
    }

    /**
     * Delete a LidPersona, given its identifier. This overridable method always throws
     * UnsupportedOperationException.
     *
     * @param identifier the identifier of the LidPersona that will be deleted
     * @throws UnsupportedOperationException thrown if this LidPersonaManager does not permit the deletion of LidPersonas
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this identifier
     */
    @Override
    public void delete(
            Identifier identifier )
        throws
            UnsupportedOperationException,
            LidPersonaUnknownException
    {
        if( identifier == null ) {
            throw new NullPointerException( "identifier must not be null" );
        }
        Identifier delegateIdentifier = translateIdentifierForward( identifier );

        try {
            theDelegate.delete( delegateIdentifier );

        // we don't catch UnsupportedOperationException
        } catch( LidPersonaUnknownException ex ) {
            throw new LidPersonaUnknownException( identifier, ex );
        }
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
     * @throws LidPersonaUnknownException thrown if the LidPersona is unknown
     * @throws InvalidIdentifierException thrown if the identifier is invalid
     */
    protected LidPersona translatePersonaForward(
            LidPersona persona )
        throws
            LidPersonaUnknownException,
            InvalidIdentifierException
    {
        if( persona == null ) {
            return null;
        }
        Identifier delegateIdentifier = translateIdentifierForward( persona.getIdentifier() );
        
        LidPersona ret = theDelegate.find( delegateIdentifier );

        return ret;
    }

    /**
     * Translate a LidPersona as used by the delegate into the LidPersona as used by this class.
     * 
     * @param persona input parameter
     * @return translated LidLocalPersona
     */
    protected LidPersona translatePersonaBackward(
            LidPersona persona )
    {
        if( persona == null ) {
            return null;
        }
        Identifier delegateIdentifier = translateIdentifierBackward( persona.getIdentifier() );
        
        LidPersona ret = new TranslatingLidPersona( delegateIdentifier, persona );

        return ret;
    }    

    /**
     * The delegate LidPersonaManager.
     */
    protected LidPersonaManager theDelegate;
}
