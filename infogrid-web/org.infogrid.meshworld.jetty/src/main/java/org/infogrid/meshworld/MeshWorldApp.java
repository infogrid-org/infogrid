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

package org.infogrid.meshworld;

import javax.servlet.ServletContext;
import org.infogrid.app.AppConfiguration;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.templates.StructuredResponse;

        /**
 * The MeshWorld app.
 */
public class MeshWorldApp
    extends
        InfoGridWebApp
{
    /**
     * Constructor.
     */
    public MeshWorldApp()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected StructuredResponse createStructuredResponse(
            SaneServletRequest request, 
            ServletContext     servletContext )
    {
        StructuredResponse ret = super.createStructuredResponse( request, servletContext );

        String contextPath = request.getOriginalSaneRequest().getContextPath();
        String appHeader = APP_HEADER.replace( "${CONTEXT}", contextPath );
        String appFooter = APP_FOOTER.replace( "${CONTEXT}", APP_FOOTER );
        
        ret.obtainSection( StructuredResponse.HTML_APP_HEADER_SECTION ).getWriter().print( appHeader );
        ret.obtainSection( StructuredResponse.HTML_FOOTER_SECTION ).getWriter().print( appFooter );
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerResources(
            AppConfiguration config )
    {
        registerAsset( "/s/images/built-on-infogrid.png", this );
        registerAsset( "/s/images/infogrid-medium.png",   this );
        registerAsset( "/s/images/meshworld.png",         this );
    }
    
    public static final String APP_HEADER
        = "<a class=\"infogrid\" href=\"http://infogrid.org/\"><img src=\"${CONTEXT}/s/images/infogrid-medium.png\" alt=\"[InfoGrid logo]\" /></a>\n"
        + "<a href=\"${CONTEXT}/\"><img id=\"app-logo\" src=\"${CONTEXT}/s/images/meshworld.png\" alt=\"[Logo]\" /></a>\n"
        + "<h1><a href=\"${CONTEXT}/\">The Mesh World</a></h1>\n";

    public static final String APP_FOOTER
        = "<p><a class=\"built-on-infogrid\" href=\"http://infogrid.org/\"><img src=\"${CONTEXT}/s/images/built-on-infogrid.png\" title=\"Built on InfoGrid&trade;\" alt=\"[Built on InfoGrid&trade;]\"/></a>\n"
        + "&copy; 2001-2016 Johannes Ernst. All rights reserved. InfoGrid is a trademark or registered trademark of Johannes Ernst.</p>\n"
        + "<p>Silk Icons from <a href=\"http://www.famfamfam.com/lab/icons/silk/\">famfamfam.com</a> using Creative Commons license.\n"
        + "<a href=\"http://infogrid.org/\">Learn more</a> about InfoGrid&trade;.</p>\n";
}
