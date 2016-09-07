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

package org.infogrid.web.viewlet.meshbase;

import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.infogrid.app.AppConfiguration;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactoryChoice;
import org.infogrid.web.viewlet.DefaultJspViewletFactoryChoice;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.app.InfoGridWebApp;

/**
 * Viewlets related to MeshBase packaged as an InfoGridAccessory.
 */
public class MeshbaseViewletAccessory
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
    public static MeshbaseViewletAccessory moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        return new MeshbaseViewletAccessory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerResources(
            AppConfiguration config,
            InfoGridWebApp   app )
    {
        app.registerViewlet(
                (MeshObjectsToView toView) -> {
                    if( toView.getSubject().isHomeObject() ) {
                        return AllMeshObjectsViewlet.choice( (WebMeshObjectsToView) toView, ViewletFactoryChoice.AVERAGE_MATCH_QUALITY );
                    } else {
                        return null;
                    }
                },
                this
        );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/AllMeshObjectsViewlet.css", this );

        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/add.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/bin_closed.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_end.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_end_blue.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_fastforward.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_fastforward_blue.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_rewind.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_rewind_blue.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_start.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/control_start_blue.png", this );
        app.registerAsset( "/v/org/infogrid/web/viewlet/meshbase/pencil.png", this );
    }
}
