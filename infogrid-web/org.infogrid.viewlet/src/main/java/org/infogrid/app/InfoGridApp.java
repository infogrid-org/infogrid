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

package org.infogrid.app;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBaseNameServer;
import org.infogrid.meshbase.m.MMeshBase;
import org.infogrid.meshbase.m.MMeshBaseNameServer;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.ObjectInContext;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.viewlet.ViewletMatcher;

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
    public InfoGridApp()
    {
    }
    
    /**
     * Invoked by the framework, run the various initialization methods with
     * the configuration options provided.
     * 
     * @param config the configuration options
     */
    public void initialize(
            AppConfiguration config )
    {
        initializeMeshBase( config );
    }
    
    /**
     * Overridable method to initialize the MeshBase and related.
     * 
     * @param config the configuration options
     */
    protected void initializeMeshBase(
            AppConfiguration config )
    {
        theMeshBase = MMeshBase.create();

        theMeshBaseNameServer = MMeshBaseNameServer.create();
        ((MMeshBaseNameServer)theMeshBaseNameServer).put( theMeshBase.getIdentifier(), theMeshBase );
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
     * Allows an InfoGridAccessory or InfoGridApp to register Viewlets with the
     * app. The app decides whether or not, or how to make those Viewlets
     * available. 
     * 
     * @param matcher the ViewletMatcher leading to the Viewlet being registered
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public abstract void registerViewlet(
            ViewletMatcher      matcher,
            InfoGridInstallable installable );

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
}
    