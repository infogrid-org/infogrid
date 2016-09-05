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
import org.infogrid.app.InfoGridAccessory;
import org.infogrid.app.InfoGridApp;

/**
 * An accessory to an InfoGridWebApp.
 */
public abstract class InfoGridWebAccessory
    extends
        InfoGridAccessory
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(
            AppConfiguration config,
            InfoGridApp      app )
    {
        registerResources( config, (InfoGridWebApp) app );
    }

    /**
     * Overridable method to register available resources with this app.
     * 
     * @param config the configuration options
     * @param app the InfoGridApp that this is an accessory for
     */
    protected void registerResources(
            AppConfiguration config,
            InfoGridWebApp   app )
    {
        
    }
}
