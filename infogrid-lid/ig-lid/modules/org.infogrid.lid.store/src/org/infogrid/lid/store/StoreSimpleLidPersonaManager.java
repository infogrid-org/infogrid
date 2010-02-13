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

package org.infogrid.lid.store;

import java.util.ArrayList;
import java.util.Map;
import org.infogrid.lid.AbstractLidPersonaManager;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.LidPersonaExistsAlreadyException;
import org.infogrid.lid.LidPersona;
import org.infogrid.lid.SimpleLidPersona;
import org.infogrid.store.Store;
import org.infogrid.store.prefixing.PrefixingStore;
import org.infogrid.store.util.StoreBackedSwappingHashMap;
import org.infogrid.util.AbstractFactory;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CannotFindHasIdentifierException;
import org.infogrid.util.Factory;
import org.infogrid.util.FactoryException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.ObjectExistsAlreadyFactoryException;
import org.infogrid.util.PatientSmartFactory;
import org.infogrid.util.SmartFactory;

/**
 * Store implementation of LidLocalPersonaManager.
 */
public class StoreSimpleLidPersonaManager
        extends
            AbstractLidPersonaManager
{
    /**
     * Factory method.
     *
     * @param store the Store to store to
     * @param availableCredentialTypes the credential types known to the application
     * @return the created StoreSimpleLidPersonaManager
     */
    public static StoreSimpleLidPersonaManager create(
            LidCredentialType [] availableCredentialTypes,
            Store                store )
    {
        PrefixingStore localPersonaStore  = PrefixingStore.create( "local", store );
        PrefixingStore remotePersonaStore = PrefixingStore.create( "remote", store );
        return create( availableCredentialTypes, localPersonaStore, remotePersonaStore );
    }

    /**
     * Factory method.
     *
     * @param localPersonaStore the Store to store LidPersonas to
     * @param remotePersonaStore the Store to map remote personas to LidPersonas in
     * @param availableCredentialTypes the credential types known to the application
     * @return the created StoreSimpleLidPersonaManager
     */
    public static StoreSimpleLidPersonaManager create(
            LidCredentialType [] availableCredentialTypes,
            Store                localPersonaStore,
            Store                remotePersonaStore )
    {
        SimpleLidPersonaMapper personaMapper = new SimpleLidPersonaMapper( availableCredentialTypes );
        
        Factory<Identifier,SimpleLidPersona,PersonaData> personaFactory
                = new AbstractFactory<Identifier,SimpleLidPersona,PersonaData>() {
                    public SimpleLidPersona obtainFor(
                            Identifier  identifier,
                            PersonaData arg )
                    {
                        SimpleLidPersona ret = SimpleLidPersona.create(
                                identifier,
                                ArrayHelper.copyIntoNewArray( arg.getRemoteIdentifiers(), Identifier.class ),
                                arg.getAttributes(),
                                arg.getCredentialTypes(),
                                arg.getCredentialValues());
                        return ret;
                    }
                };

        StoreBackedSwappingHashMap<Identifier,SimpleLidPersona> personaStorage = StoreBackedSwappingHashMap.createWeak( personaMapper, localPersonaStore );
        
        SmartFactory<Identifier,SimpleLidPersona,PersonaData> smartPersonaFactory
                = new PatientSmartFactory<Identifier,SimpleLidPersona,PersonaData>( personaFactory, personaStorage );

        IdentifierMapper identifierMapper = new IdentifierMapper();

        Map<Identifier,Identifier> remoteLocalMap = StoreBackedSwappingHashMap.createWeak( identifierMapper, remotePersonaStore );

        StoreSimpleLidPersonaManager ret = new StoreSimpleLidPersonaManager(
                smartPersonaFactory,
                remoteLocalMap );
        return ret;
    }

    /**
     * Constructor, use factory method.
     * 
     * @param delegateFactory the underlying SmartFactory
     * @param remoteLocalMap maps remote identifiers to local identifiers
     */
    protected StoreSimpleLidPersonaManager(
            SmartFactory<Identifier,SimpleLidPersona,PersonaData> delegateFactory,
            Map<Identifier,Identifier>                            remoteLocalMap )
    {
        theDelegateFactory = delegateFactory;
        theRemoteLocalMap  = remoteLocalMap;
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
        ArrayList<Identifier> remoteIdentifiers = new ArrayList<Identifier>( remotePersonas != null ? remotePersonas.length : 0 );
        if( remotePersonas != null ) {
            for( HasIdentifier remote : remotePersonas ) {
                remoteIdentifiers.add( remote.getIdentifier() );
            }
        }
        PersonaData attCred = new PersonaData( remoteIdentifiers, attributes, credentials );
        
        try {
            LidPersona ret = theDelegateFactory.obtainNewFor( localIdentifier, attCred );
            localIdentifier = ret.getIdentifier();

            if( remotePersonas != null ) {
                for( HasIdentifier remote : remotePersonas ) {
                    theRemoteLocalMap.put( remote.getIdentifier(), localIdentifier );
                }
            }

            return ret;

        } catch( ObjectExistsAlreadyFactoryException ex ) {
            throw new LidPersonaExistsAlreadyException( (LidPersona) ex.getExisting(), ex );

        } catch( FactoryException ex ) {
            throw new RuntimeException( ex );
        }
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
        LidPersona ret = theDelegateFactory.get( identifier );
        if( ret == null ) {
            throw new CannotFindHasIdentifierException( identifier );
        }
        return ret;
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
        Identifier local = theRemoteLocalMap.get( remote.getIdentifier() );
        if( local == null ) {
            return null;
        }
        LidPersona ret = theDelegateFactory.get( local );
        return ret;
    }

    /**
     * Delete a LidPersona.
     */
    @Override
    public void delete(
            LidPersona toDelete )
    {
        LidPersona found = theDelegateFactory.remove( toDelete.getIdentifier() );

        Identifier [] remoteIdentifiers = found.getRemoteIdentifiers();
        if( remoteIdentifiers != null ) {
            for( Identifier remote : remoteIdentifiers ) {
                theRemoteLocalMap.remove( remote );
            }
        }
    }

    /**
     * The underlying SmartFactory. This is hidden so we can do access control and
     * expose the API we want.
     */
    protected SmartFactory<Identifier,SimpleLidPersona,PersonaData> theDelegateFactory;

    /**
     * The map from remote identifier to local identifier.
     */
    protected Map<Identifier,Identifier> theRemoteLocalMap;
}
