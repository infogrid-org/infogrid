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

package org.infogrid.jee.taglib.candy;

import org.infogrid.jee.servlet.InitializationFilter;
import org.infogrid.jee.taglib.AbstractInfoGridBodyTag;
import org.infogrid.jee.taglib.IgnoreException;

import javax.servlet.jsp.JspException;

import java.io.IOException;

/**
 * Generates an HTML overlay in a consistent manner.
 */
public class OverlayTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public OverlayTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theHtmlClass = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the htmlClass property.
     *
     * @return value of the htmlClass property
     * @see #setHtmlClass
     */
    public String getHtmlClass()
    {
        return theHtmlClass;
    }

    /**
     * Set value of the htmlClass property.
     *
     * @param newValue new value of the htmlClass property
     * @see #getHtmlClass
     */
    public void setHtmlClass(
            String newValue )
    {
        theHtmlClass = newValue;
    }

    /**
     * Do the start tag operation.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        String context = (String) pageContext.getRequest().getAttribute( InitializationFilter.CONTEXT_PARAMETER );
        if( context == null ) {
            context = "";
        }

        print( "<div class=\"" );
        print( getClass().getName().replace( '.', '-' ) );
        if( theHtmlClass != null ) {
            print( " " );
            print( theHtmlClass );
        }
        print( "\"" );

        if( id != null ) {
            print( " id=\"" + id + "\"" );
        }
        println( ">" );

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Do the end tag operation.
     *
     * @return evaluate or skip page
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    @Override
    protected int realDoEndTag()
        throws
            JspException,
            IOException
    {
        println( "</div>" );

        return EVAL_PAGE;
    }
    
    /**
     * HTML class of the top-level element to be generated.
     */
    protected String theHtmlClass;
}