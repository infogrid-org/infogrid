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

package org.infogrid.jee.taglib.viewlet;

import org.infogrid.jee.taglib.InfoGridJspUtils;
import org.infogrid.jee.viewlet.JeeViewlet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Container for a JeeViewlet definition in a JSP file.
 */
public class ViewletTag
        extends
            TagSupport
{
    /**
     * Constructor.
     */
    public ViewletTag()
    {
    }

    /**
     * Do the start tag operation.
     *
     * @return indicate how to continue processing
     * @throws JspException thrown if a processing error occurred
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag()
        throws
            JspException
    {
        JeeViewlet vl = (JeeViewlet) pageContext.getRequest().getAttribute( JeeViewlet.VIEWLET_ATTRIBUTE_NAME );

        StringBuilder content = new StringBuilder( 256 );
        content.append( "<div class=\"viewlet" );

        if( vl != null ) {
            // This should not happen (too often), just a defensive move for slightly abnormal Viewlets
            String vlName = vl.getClass().getName();

            content.append( " " ).append( vlName.replace( '.', '-') );
        }        
        content.append( "\">" );
        InfoGridJspUtils.println( pageContext, false, content.toString() );

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Do the end tag operation.
     *
     * @return indicate how to continue processing
     * @throws JspException thrown if a processing error occurred
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag()
        throws
            JspException
    {
        InfoGridJspUtils.println( pageContext, false, "</div>" );

        return EVAL_PAGE;
    }
}
