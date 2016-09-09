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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.web.viewlet.propertysheet;

import jsps.org.infogrid.web.viewlet.propertysheet.PropertySheetViewlet_jsp;
import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.infogrid.app.AppConfiguration;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.viewlet.ViewletFactoryChoice;
import org.infogrid.web.viewlet.DefaultJspViewletFactoryChoice;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.app.WebAppResourceManager;

/**
 * A PropertySheet packaged as an InfoGridAccessory.
 */
public class PropertySheetViewletAccessory
    extends
        InfoGridWebAccessory
{
    /**
     * Diet4j module activation.
     * 
     * @param thisModule the Module being activated
     * @return the accessory that was activated
     * @throws ModuleActivationException thrown if module activation failed
     */
    public static PropertySheetViewletAccessory moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        return new PropertySheetViewletAccessory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerResources(
            AppConfiguration config,
            InfoGridWebApp   app )
    {
        WebAppResourceManager rm     = app.getResourceManager();
        ViewletFactory        vlFact = app.getViewletFactory();
        
        vlFact.registerViewlet(
                (MeshObjectsToView toView) -> new DefaultJspViewletFactoryChoice(
                        (WebMeshObjectsToView) toView,
                        "org.infogrid.web.viewlet.propertysheet.PropertySheetViewlet",
                        PropertySheetViewlet_jsp.class,
                        ViewletFactoryChoice.BAD_MATCH_QUALITY ),
                this
        );

        final String [] ASSETS = {
            "PropertySheetViewlet.css",
            "bin_closed.png",
            "medal_bronze_add.png",
            "medal_bronze_delete.png",
            "medal_silver_add.png",
            "medal_silver_delete.png",
            "link_add.png",
            "link_delete.png",
        };     
        for( String name : ASSETS ) {
            rm.registerAsset( "/v/org/infogrid/web/viewlet/propertysheet/" + name, this  );
        }
    }
}
