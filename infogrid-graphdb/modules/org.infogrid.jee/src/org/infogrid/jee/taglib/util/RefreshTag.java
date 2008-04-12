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

package org.infogrid.jee.taglib.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import java.io.IOException;

import org.infogrid.jee.taglib.AbstractInfoGridTag;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.taglib.InfoGridJspUtils;

import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.viewlet.templates.JspStructuredResponseTemplate;
import org.infogrid.jee.viewlet.templates.StructuredResponse;
import org.infogrid.jee.viewlet.templates.TextHtmlStructuredResponseSection;
        
/**
 * <p>Generates a consistent refresh button.
 *    See description in the
 *   {@link org.infogrid.jee.taglib.util package documentation}.</p>
 */
public class RefreshTag
        extends
             AbstractInfoGridTag
{
    /**
     * Constructor.
     */
    public RefreshTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        super.initializeToDefaults();
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
        StructuredResponse theResponse = (StructuredResponse) lookupOrThrow(
                JspStructuredResponseTemplate.STRUCTURED_RESPONSE_ATTRIBUTE_NAME );

        // this needs to be simple lookup so the periods in the class name don't trigger nestedLookup
        RestfulRequest restful = (RestfulRequest) InfoGridJspUtils.simpleLookup(
                pageContext,
                RestfulRequest.class.getName(),
                getScope() );

        HttpServletRequest realRequest = ((HttpServletRequest)pageContext.getRequest());
        
        String href  = realRequest.getRequestURI();
        String query = realRequest.getQueryString();
        
        if( query != null && query.length() > 0 ) {
            href = href + '?' + query;
        }
        href = InfoGridJspUtils.filter( href );
        
        StringBuilder buf = new StringBuilder();
        buf.append( "<div class=\"" ).append( getClass().getName().replace( '.', '-' )).append( "\">" );
        buf.append( "<a href=\"" ).append( href ).append( "\">" );

        print( buf.toString() );

        StringBuilder css = new StringBuilder();
        css.append( "<link rel=\"stylesheet\" href=\"" );
        css.append( restful.getContextPath() );
        css.append( "/v/" );
        css.append( getClass().getName().replace( '.' , '/' ));
        css.append( ".css" );
        css.append( "\" />\n" );

        theResponse.appendToSectionContent( TextHtmlStructuredResponseSection.HTML_HEAD_SECTION, css.toString() );

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
        print( "</a></div>" );

        return EVAL_PAGE;
    }
}
