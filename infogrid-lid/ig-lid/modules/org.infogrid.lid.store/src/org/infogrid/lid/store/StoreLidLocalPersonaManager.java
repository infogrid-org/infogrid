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

import java.util.Map;
import org.infogrid.lid.AbstractLidPersonaManager;
import org.infogrid.lid.LidPersona;
import org.infogrid.lid.LidPersonaUnknownException;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.LidPersonaExistsAlreadyException;
import org.infogrid.lid.SimpleLidPersona;
import org.infogrid.store.Store;
import org.infogrid.store.util.StoreBackedSwappingHashMap;
import org.infogrid.util.AbstractFactory;
import org.infogrid.util.Factory;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Identifier;
import org.infogrid.util.ObjectExistsAlreadyFactoryException;
import org.infogrid.util.PatientSmartFactory;
import org.infogrid.util.SmartFactory;

/**
 * Store implementation of LidLocalPersonaManager.
 */
public class StoreLidLocalPersonaManager
        extends
            AbstractLidPersonaManager
{
    /**
     * Factory method.
     *
     * @param store the Store to store to
     * @param credentialTypeClassLoader the ClassLoader to use to instantiate LidCredentialTypes
     * @return the created StoreLidLocalPersonaManager
     */
    public static StoreLidLocalPersonaManager create(
            Store       store,
            ClassLoader credentialTypeClassLoader )
    {
        LidLocalPersonaMapper mapper = new LidLocalPersonaMapper( credentialTypeClassLoader );
        
        Factory<Identifier,LidPersona,AttributesCredentials> delegateFactory
                = new AbstractFactory<Identifier,LidPersona,AttributesCredentials>() {
                    public SimpleLidPersona obtainFor(
                            Identifier            identifier,
                            AttributesCredentials attCred )
                    {
                        SimpleLidPersona ret = SimpleLidPersona.create(
                                identifier,
                                null,
                                attCred.getAttributes(),
                                attCred.getCredentialTypes(),
                                attCred.getCredentialValues());
                        return ret;
                    }
                };

        StoreBackedSwappingHashMap<Identifier,LidPersona> storage = StoreBackedSwappingHashMap.createWeak( mapper, store );
        
        SmartFactory<Identifier,LidPersona,AttributesCredentials> smartFactory
                = new PatientSmartFactory<Identifier,LidPersona,AttributesCredentials>( delegateFactory, storage );
        
        StoreLidLocalPersonaManager ret = new StoreLidLocalPersonaManager( smartFactory );
        return ret;
    }

    /**
     * Constructor, use factory method.
     * 
     * @param delegateFactory the underlying SmartFactory
     */
    protected StoreLidLocalPersonaManager(
            SmartFactory<Identifier,LidPersona,AttributesCredentials> delegateFactory )
    {
        theDelegateFactory = delegateFactory;
    }

    /**
     * Create a LidLocalPersona.
     *
     * @param identifier the identifier for the to-be-created LidLocalPersona
     * @param attributes the attributes for the to-be-created LidLocalPersona
     * @param credentials the credentials for the to-be-created LidLocalPersona
     * @return the LocalPersona that was created
     * @throws LidPersonaExistsAlreadyException thrown if a LidLocalPersona with this identifier exists already
     * @throws UnsupportedOperationException thrown if this LidLocalPersonaManager does not permit the creation of new LidLocalPersonas
     */
    @Override
    public LidPersona provisionPersona(
            Identifier                    identifier,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials )
        throws
            LidPersonaExistsAlreadyException,
            UnsupportedOperationException
    {
        AttributesCredentials attCred = new AttributesCredentials( attributes, credentials );
        
        try {
            LidPersona ret = theDelegateFactory.obtainNewFor( identifier, attCred );
            return ret;

        } catch( ObjectExistsAlreadyFactoryException ex ) {
            throw new LidPersonaExistsAlreadyException( (LidPersona) ex.getExisting(), ex );

        } catch( FactoryException ex ) {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Obtain a LidPersona, given its identifier.
     *
     * @param identifier the identifier for which the LidPersona will be retrieved
     * @return the found LidPersona
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this identifier
     */
    public LidPersona find(
            Identifier identifier )
        throws
            LidPersonaUnknownException
    {
        LidPersona ret = theDelegateFactory.get( identifier );
        if( ret == null ) {
            throw new LidPersonaUnknownException( identifier );
        }
        return ret;
    }

    /**
     * Delete a LidLocalPersona, given its identifier.
     * 
     * @param identifier the identifier of the LidLocalPersona that will be deleted
     * @throws UnsupportedOperationException thrown if this LidLocalPersonaManager does not permit the deletion of LidLocalPersonas
     * @throws LidPersonaUnknownException thrown if no LidLocalPersona exists with this identifier
     */
    @Override
    public void delete(
            Identifier identifier )
        throws
            UnsupportedOperationException,
            LidPersonaUnknownException
    {
        LidPersona found = theDelegateFactory.remove( identifier );
        if( found == null ) {
            throw new LidPersonaUnknownException( identifier );
        }
    }

    /**
     * The underlying SmartFactory. This is hidden so we can do access control and
     * expose the API we want.
     */
    protected SmartFactory<Identifier,LidPersona,AttributesCredentials> theDelegateFactory;
}
