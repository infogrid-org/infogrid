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

package org.infogrid.web.app;

import org.infogrid.app.AppConfiguration;

/**
 * Carries information about the configuration of a web application.
 */
public abstract class WebAppConfiguration
    extends
        AppConfiguration
{
    /**
     * Determine the virtual hostname at which the app should run.
     * Example: example.com
     * 
     * @return the virtual hostname
     */
    public String getAppVirtualHost()
    {
        return theAppVirtualHost;
    }

    /**
     * Determine the relative context path at which the app should run.
     * Example: /blog
     * 
     * @return the relative context path with no trailing slash, not even at root
     */
    public String getAppContextPath()
    {
        return theAppContextPath;
    }
    /**
     * The application's virtual host.
     */
    protected String theAppVirtualHost;
    
    /**
     * The application's relative context path.
     */
    protected String theAppContextPath;
}
