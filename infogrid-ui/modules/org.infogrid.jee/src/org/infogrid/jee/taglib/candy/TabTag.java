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

import org.infogrid.jee.taglib.AbstractInfoGridBodyTag;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.taglib.InfoGridJspUtils;

import javax.servlet.jsp.JspException;

/**
 * Tag that collects {@link TabHeaderTag header} and {@link TabContentTag content}
 * of a single tab in a TabbedTag.
 */
public class TabTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public TabTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theIsSelected = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the selected property.
     *
     * @return value of the selected property
     * @see #setIsSelected
     */
    public String getIsSelected()
    {
        return theIsSelected;
    }

    /**
     * Set value of the selected property.
     *
     * @param newValue new value of the selected property
     * @see #getIsSelected
     */
    public void setIsSelected(
            String newValue )
    {
        theIsSelected = newValue;
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
        print( "<li" );
        if( InfoGridJspUtils.isTrue( theIsSelected )) {
            print( " selected=\"selected\"" );
        }
        println( "\">" );
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
            JspException
    {
        println( "</li>" );
        return EVAL_PAGE;
    }
    
    /**
     * Captures whether this tab has been selected.
     */
    protected String theIsSelected;
}
