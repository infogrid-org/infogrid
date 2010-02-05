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

import org.infogrid.lid.AbstractSimpleLidSessionManager;
import org.infogrid.lid.LidPersonaManager;
import org.infogrid.lid.LidSession;
import org.infogrid.lid.LidSessionManager;
import org.infogrid.lid.LidSessionManagerArguments;
import org.infogrid.store.Store;
import org.infogrid.store.util.StoreBackedSwappingHashMap;
import org.infogrid.util.Factory;
import org.infogrid.util.IdentifierFactory;
import org.infogrid.util.SimpleStringIdentifierFactory;

/**
 * Implements LidSessionManager using the Store abstraction.
 */
public class StoreLidSessionManager
        extends
            AbstractSimpleLidSessionManager
        implements
            LidSessionManager
{
    /**
     * Factory method.
     *
     * @param store the Store to use
     * @param personaManager the LidPersonaManager to find any LidPersona referenced in a LidSession
     * @return the created StoreLidSessionManager
     */
    public static StoreLidSessionManager create(
            Store             store,
            LidPersonaManager personaManager )
    {
        return create( store, new SimpleStringIdentifierFactory(), personaManager, DEFAULT_SESSION_DURATION );
    }

    /**
     * Factory method.
     *
     * @param store the Store to use
     * @param idFact the IdentifierFactory for client and site identifiers
     * @param personaManager the LidPersonaManager to find any LidPersona referenced in a LidSession
     * @return the created StoreLidSessionManager
     */
    public static StoreLidSessionManager create(
            Store             store,
            IdentifierFactory idFact,
            LidPersonaManager personaManager )
    {
        return create( store, idFact, personaManager, DEFAULT_SESSION_DURATION );
    }

    /**
     * Factory method.
     * 
     * @param store the Store to use
     * @param idFact the IdentifierFactory for client and site identifiers
     * @param personaManager the LidPersonaManager to find any LidPersona referenced in a LidSession
     * @param sessionDuration the duration of the session, in milliseconds
     * @return the created StoreLidSessionManager
     */
    public static StoreLidSessionManager create(
            Store             store,
            IdentifierFactory idFact,
            LidPersonaManager personaManager,
            long              sessionDuration )
    {
        StoreLidSessionMapper mapper = new StoreLidSessionMapper( idFact, personaManager );
        
        StoreBackedSwappingHashMap<String,LidSession> storage = StoreBackedSwappingHashMap.createWeak(
                mapper,
                store );
        
        SimpleLidSessionDelegateFactory delegateFactory = new SimpleLidSessionDelegateFactory();
        
        StoreLidSessionManager ret = new StoreLidSessionManager( delegateFactory, storage, sessionDuration );
        delegateFactory.setLidSessionManager( ret );
        
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param delegateFactory the underlying factory for LidSessions
     * @param storage the storage to use
     * @param sessionDuration the duration of new or renewed sessions in milli-seconds
     */
    protected StoreLidSessionManager(
            Factory<String,LidSession,LidSessionManagerArguments> delegateFactory,
            StoreBackedSwappingHashMap<String,LidSession>         storage,
            long                                                  sessionDuration )
    {
        super( delegateFactory, storage, sessionDuration );
    }    
}

