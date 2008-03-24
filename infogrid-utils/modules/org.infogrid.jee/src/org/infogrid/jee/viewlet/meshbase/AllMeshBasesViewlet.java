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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.viewlet.meshbase;

import org.infogrid.context.Context;

import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.viewlet.AbstractJeeViewlet;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;

import org.infogrid.util.NameServer;

import java.util.Iterator;

/**
 * A Viewlet that shows all locally known MeshBases.
 */ 
public class AllMeshBasesViewlet
        extends
            AbstractJeeViewlet
        implements
            Iterable<MeshBase>
{
    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created AllMeshObjectsViewlet
     */
    public static AllMeshBasesViewlet create(
            Context c )
    {
        return new AllMeshBasesViewlet( c );
    }

    /**
     * Constructor.
     *
     * @param c the application context
     */
    protected AllMeshBasesViewlet(
            Context c )
    {
        super( c );
    }

    /**
     * Obtain the MeshBases to display.
     *
     * @return Iterator over the MeshBases
     */
    public Iterator<MeshBase> iterator()
    {
        InfoGridWebApp                          app = InfoGridWebApp.getSingleton();
        NameServer<MeshBaseIdentifier,MeshBase> ns  = app.getMeshBaseNameServer();
        
        Iterator<MeshBase> ret = ns.values().iterator();
        
        return ret;
    }
    
    /**
     * Obtain the MeshBases to display.
     *
     * @return Iterator over the MeshBases
     */
    public Iterator<MeshBase> getIterator()
    {
        return iterator();
    }
}
