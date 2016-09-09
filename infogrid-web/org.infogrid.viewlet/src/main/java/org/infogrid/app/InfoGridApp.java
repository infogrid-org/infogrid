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

package org.infogrid.app;

import java.text.ParseException;
import org.infogrid.meshbase.DefaultMeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifierFactory;
import org.infogrid.meshbase.MeshBaseNameServer;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.m.MMeshBaseNameServer;
import org.infogrid.meshbase.security.AccessManager;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.ObjectInContext;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;

/**
 * The superclass of all InfoGridApps.
 */
public abstract class InfoGridApp
    extends
        InfoGridInstallable
    implements
        ObjectInContext
{
    private static final Log log = Log.getLogInstance( InfoGridApp.class ); // our own, private logger

    /**
     * This constructor can be used directly, or the class may be subclassed.
     */
    @SuppressWarnings( "LeakingThisInConstructor" )
    public InfoGridApp()
    {
        theRootContext.addContextObject( this );
    }
    
    /**
     * Invoked by the framework, initialize the app.
     * 
     * @param config the configuration options
     */
    public void initialize(
            AppConfiguration config )
    {
        initializeMeshBase( config );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext()
    {
        return theRootContext;
    }

    /**
     * Obtain the name server which enables lookup of named MeshBases if the app
     * has several.
     * 
     * @return the name server
     */
    public MeshBaseNameServer<MeshBaseIdentifier,MeshBase> getMeshBaseNameServer()
    {
        return theMeshBaseNameServer;
    }
    
    /**
     * Obtain the factory for MeshBaseIdentifiers.
     * 
     * @return the factory
     */
    public MeshBaseIdentifierFactory getMeshBaseIdentifierFactory()
    {
        return theMeshBaseIdentifierFactory;
    }
    
    /**
     * Obtain this app's main MeshBase.
     * 
     * @return the main MeshBase
     */
    public MeshBase getMainMeshBase()
    {
        return theMeshBase;
    }

    /**
     * Overridable method to initialize the MeshBase and related.
     * 
     * @param config the configuration options
     */
    protected void initializeMeshBase(
            AppConfiguration config )
    {
        theMeshBaseIdentifierFactory = DefaultMeshBaseIdentifierFactory.create();

        try {
            theMeshBase = MMeshBase.create(
                    theMeshBaseIdentifierFactory.fromExternalForm( "default" ),
                    ModelBaseSingleton.getSingleton(),
                    createAccessManager( config ),
                    theRootContext );
            theRootContext.addContextObject( theMeshBase );
        } catch( ParseException ex ) {
            log.error( ex );
        }

        theMeshBaseNameServer = MMeshBaseNameServer.create();
        ((MMeshBaseNameServer)theMeshBaseNameServer).put( theMeshBase.getIdentifier(), theMeshBase );
        theRootContext.addContextObject( theMeshBaseNameServer );
    }

    /**
     * Overridable method to create an AccessManager for the MeshBase, or return null if none.
     * 
     * @param config the configuration options
     * @return the AccessManager, or null
     */
    protected AccessManager createAccessManager(
            AppConfiguration config )
    {
        return null;
    }

    /**
     * The root context.
     */
    protected Context theRootContext = SimpleContext.createRoot( "root context" );

    /**
     * The main MeshBase for this app.
     */
    protected MeshBase theMeshBase;

    /**
     * If there are several MeshBases in this app, allows lookup of MeshBases
     * by their identifier.
     */
    protected MeshBaseNameServer<MeshBaseIdentifier,MeshBase> theMeshBaseNameServer;
    
    /**
     * The factory to use to convert Strings to MeshBaseIdentifiers.
     */
    protected MeshBaseIdentifierFactory theMeshBaseIdentifierFactory;
}