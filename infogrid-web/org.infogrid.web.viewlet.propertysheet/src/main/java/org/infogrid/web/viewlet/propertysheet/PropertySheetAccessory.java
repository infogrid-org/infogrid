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

package org.infogrid.web.viewlet.propertysheet;

import jsps.org.infogrid.web.viewlet.propertysheet.PropertySheetViewlet_jsp;
import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.infogrid.app.AppConfiguration;
import org.infogrid.app.InfoGridApp;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactoryChoice;
import org.infogrid.web.viewlet.DefaultJspViewletFactoryChoice;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.app.InfoGridWebApp;

/**
 * A PropertySheet packaged as an InfoGridAccessory.
 */
public class PropertySheetAccessory
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
    public static PropertySheetAccessory moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        return new PropertySheetAccessory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerResources(
            AppConfiguration config,
            InfoGridApp      app )
    {
        app.registerViewlet(
                (MeshObjectsToView toView) -> new DefaultJspViewletFactoryChoice(
                        (WebMeshObjectsToView) toView, PropertySheetViewlet_jsp.class, ViewletFactoryChoice.BAD_MATCH_QUALITY ),
                this
        );
        ((InfoGridWebApp)app).registerAsset(
                "/v/org/infogrid/web/viewlet/propertysheet/PropertySheetViewlet.css",
                getClass().getClassLoader(),
                this );
    }
}
