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

package org.infogrid.jee.viewlet.wikiobject;

import org.infogrid.context.Context;
import org.infogrid.jee.viewlet.SimpleJeeViewlet;
import org.infogrid.mesh.NotPermittedException;

import org.infogrid.model.primitives.BlobValue;
import org.infogrid.model.Wiki.WikiSubjectArea;

import org.infogrid.util.logging.Log;

/**
 * Viewlet that can display a WikiObject.
 */
public class WikiObjectDisplayViewlet
        extends
            SimpleJeeViewlet
{
    private static final Log log = Log.getLogInstance( WikiObjectDisplayViewlet.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created Viewlet
     */
    public static WikiObjectDisplayViewlet create(
            Context c )
    {
        return new WikiObjectDisplayViewlet( c );
    }

    /**
     * Constructor. This is protected: use factory method or subclass.
     *
     * @param c the application context
     */
    protected WikiObjectDisplayViewlet(
            Context c )
    {
        super( c );
    }
    
    /**
     * Obtain the current content of the WikiObject.
     *
     * @return the current content
     */
    public String getContent()
        throws
            NotPermittedException
    {
//        try {
            BlobValue oldValue = (BlobValue) getSubject().getPropertyValue( WikiSubjectArea.WIKIOBJECT_CONTENT );
            String    ret;
            if( oldValue != null ) {
                ret = oldValue.getAsString();
            } else {
                ret = "";
            }
            return ret;

//        } catch( NotPermittedException ex ) {
//            throw new ServletException( ex );
//        }        
    }
}
