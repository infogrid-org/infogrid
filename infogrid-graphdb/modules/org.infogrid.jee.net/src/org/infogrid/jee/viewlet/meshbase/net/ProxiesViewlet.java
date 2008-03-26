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

package org.infogrid.jee.viewlet.meshbase.net;

import org.infogrid.jee.viewlet.AbstractCursorIterableViewlet;

import org.infogrid.context.Context;

import org.infogrid.meshbase.net.Proxy;

/**
 * A Viewlet that shows a NetMeshBase's Proxies.
 */ 
public class ProxiesViewlet
        extends
            AbstractCursorIterableViewlet<Proxy>
{
    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created AllMeshObjectsViewlet
     */
    public static ProxiesViewlet create(
            Context c )
    {
        return new ProxiesViewlet( c );
    }

    /**
     * Constructor.
     *
     * @param c the application context
     */
    protected ProxiesViewlet(
            Context c )
    {
        super( c );
    }
}
