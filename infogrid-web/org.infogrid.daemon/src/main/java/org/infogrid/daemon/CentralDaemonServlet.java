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

package org.infogrid.daemon;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.web.app.InfoGridWebApp;

/**
 * This is the only Servlet being invoked. It contains the logic to dispatch
 * into the various components of InfoGrid.
 */
public class CentralDaemonServlet
    extends
        GenericServlet // the HTTP-method-dispatching performed by HttpServlet does not help us
{
    /**
     * Enables our overridden ServletHolder to tell us about the InfoGridApp.
     * 
     * @param app the InfoGrid app.
     */
    public void setApp(
            InfoGridWebApp app )
    {
        theApp = app;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void service(
            ServletRequest  request,
            ServletResponse response )
        throws
            ServletException,
            IOException
    {
        theApp.service( (HttpServletRequest) request, (HttpServletResponse) response, getServletContext() );
    }

    /**
     * The InfoGridApp.
     */
    protected InfoGridWebApp theApp;
}
