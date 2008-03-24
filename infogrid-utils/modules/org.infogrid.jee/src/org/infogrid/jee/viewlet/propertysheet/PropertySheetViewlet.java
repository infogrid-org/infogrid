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

package org.infogrid.jee.viewlet.propertysheet;

import org.infogrid.jee.viewlet.AbstractJeeViewlet;

import org.infogrid.context.Context;

/**
 * A Viewlet that shows a PropertySheet for its subject.
 */
public class PropertySheetViewlet
        extends
            AbstractJeeViewlet
{
    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created PropertySheetViewlet
     */
    public static PropertySheetViewlet create(
            Context c )
    {
        return new PropertySheetViewlet( c );
    }

    /**
     * Constructor, for subclasses only.
     *
     * @param c the application context
     */
    protected PropertySheetViewlet(
            Context c )
    {
        super( c );
    }
}
