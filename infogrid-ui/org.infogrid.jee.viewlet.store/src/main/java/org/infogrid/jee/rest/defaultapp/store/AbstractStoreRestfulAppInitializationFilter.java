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

package org.infogrid.jee.rest.defaultapp.store;

import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.RestfulJeeFormatter;
import org.infogrid.jee.rest.defaultapp.AbstractRestfulAppInitializationFilter;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.jee.templates.defaultapp.AppInitializationException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifierFactory;
import org.infogrid.meshbase.m.MMeshBaseNameServer;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.meshbase.store.IterableStoreMeshBase;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.store.IterableStore;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.text.StringRepresentationDirectory;

/**
 * Common functionality of application initialization filters that are REST-ful and use a Store for MeshBase persistence.
 */
public abstract class AbstractStoreRestfulAppInitializationFilter
        extends
            AbstractRestfulAppInitializationFilter
{
    /**
     * Constructor.
     */
    protected AbstractStoreRestfulAppInitializationFilter()
    {
        // nothing
    }

    /**
     * <p>Perform initialization.</p>
     * 
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @throws Throwable something bad happened that cannot be fixed by re-invoking this method
     */
    protected void initialize(
            ServletRequest  request,
            ServletResponse response )
        throws
            Throwable
    {
        HttpServletRequest realRequest = (HttpServletRequest) request;
        SaneRequest        saneRequest = SaneServletRequest.create( realRequest );
        
        InfoGridWebApp app        = getInfoGridWebApp();
        Context        appContext = app.getApplicationContext();

        // ModelBase
        ModelBase modelBase = ModelBaseSingleton.getSingleton();
        appContext.addContextObject( modelBase );

        // MeshBaseIdentifierFactory
        MeshBaseIdentifierFactory meshBaseIdentifierFactory = DefaultMeshBaseIdentifierFactory.create();
        appContext.addContextObject( meshBaseIdentifierFactory );

        // Main MeshBase
        MeshBaseIdentifier mbId = determineMainMeshBaseIdentifier( saneRequest, meshBaseIdentifierFactory );
        MeshBase           mb   = null;

        try {
            mb = setupMeshBase( saneRequest, mbId, modelBase, app );

        } finally {
            if( mb != null ) {
                appContext.addContextObject( mb );
                // MeshBase adds itself to QuitManager

                // Name Server
                MMeshBaseNameServer<MeshBaseIdentifier,MeshBase> nameServer = MMeshBaseNameServer.create();
                nameServer.put( mbId, mb );
                appContext.addContextObject( nameServer );

                initializeContextObjects( saneRequest, appContext );

            } else {
                try {
                    initializeContextObjects( saneRequest, appContext );
                } catch( Throwable t ) {
                    // ignore
                }
            }
        }
    }

    /**
     * Overridable default implementation for how to setup a MeshBase.
     *
     * @param saneRequest the incoming request, so it is possible to determine hostname, context etc.
     * @param mbId the MeshBaseIdentifier for the MeshBase
     * @param modelBase the ModelBase for the MeshBase
     * @param app the InfoGridWebApp that is being set up
     * @return the set up MeshBase
     */
    protected MeshBase setupMeshBase(
            SaneRequest        saneRequest,
            MeshBaseIdentifier mbId,
            ModelBase          modelBase,
            InfoGridWebApp     app )
        throws
            NamingException,
            IOException,
            AppInitializationException
    {
        IterableStoreMeshBase meshBase = null;

        try {
            initializeDataSources();

        } finally {

            if( theMeshStore != null ) {
                AccessManager accessMgr = createAccessManager();

                meshBase = IterableStoreMeshBase.create( mbId, modelBase, accessMgr, theMeshStore, app.getApplicationContext() );
                populateMeshBase( saneRequest, meshBase );
            }
        }
        return meshBase;
    }

    /**
     * Initialize the data sources.
     *
     * @throws NamingException thrown if a data source could not be found or accessed
     * @throws IOException thrown if an I/O problem occurred
     * @throws AppInitializationException thrown if the application could not be initialized
     */
    protected abstract void initializeDataSources()
        throws
            NamingException,
            IOException,
            AppInitializationException;

    /**
     * Overridable method to create the AccessManager to use.
     *
     * @return the created AccessManager, or null
     */
    protected AccessManager createAccessManager()
    {
        return null;
    }

    /**
     * Initialize the context objects. This may be overridden by subclasses.
     *
     * @param incomingRequest the incoming request
     * @param rootContext the root Context
     * @throws Exception initialization may fail
     */
    @Override
    protected void initializeContextObjects(
            SaneRequest incomingRequest,
            Context     rootContext )
        throws
            Exception
    {
        super.initializeContextObjects( incomingRequest, rootContext );

        MeshBase                      defaultMeshBase = rootContext.findContextObject( MeshBase.class );
        StringRepresentationDirectory srepdir         = rootContext.findContextObjectOrThrow( StringRepresentationDirectory.class );

        RestfulJeeFormatter formatter = RestfulJeeFormatter.create( defaultMeshBase, srepdir );
        rootContext.addContextObject( formatter );
    }

    /**
     * The Store for MeshObjects. This must be set by a subclass.
     */
    protected IterableStore theMeshStore;
}
