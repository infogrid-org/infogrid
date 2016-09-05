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

package org.infogrid.web.laf.common;

import jsps.org.infogrid.web.laf.common.actions.AccessLocally_jspo;
import jsps.org.infogrid.web.laf.common.actions.BlessRole_jspo;
import jsps.org.infogrid.web.laf.common.actions.Bless_jspo;
import jsps.org.infogrid.web.laf.common.actions.Create_jspo;
import jsps.org.infogrid.web.laf.common.actions.Delete_jspo;
import jsps.org.infogrid.web.laf.common.actions.Relate_jspo;
import jsps.org.infogrid.web.laf.common.actions.SweepAll_jspo;
import jsps.org.infogrid.web.laf.common.actions.Sweep_jspo;
import jsps.org.infogrid.web.laf.common.actions.UnblessRole_jspo;
import jsps.org.infogrid.web.laf.common.actions.Unbless_jspo;
import jsps.org.infogrid.web.laf.common.actions.Unrelate_jspo;
import jsps.org.infogrid.web.laf.common.templates.DefaultHtmlTemplate_jsp;
import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.infogrid.app.AppConfiguration;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.app.InfoGridWebApp;

/**
 * Common look-and-feel packaged as an InfoGridAccessory.
 */
public class CommonLafAccessory
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
    public static CommonLafAccessory moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        return new CommonLafAccessory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerResources(
            AppConfiguration config,
            InfoGridWebApp   app )
    {
        final String HTML = "text/html";

    // template
        
        app.registerViewletTemplate(
                "default",
                HTML,
                DefaultHtmlTemplate_jsp.class,
                this );
        
        app.registerAsset( "/s/org/infogrid/web/laf/common/assets/master.css", this );
        app.registerAsset( "/s/org/infogrid/web/laf/common/assets/layout.css", this );
        app.registerAsset( "/s/org/infogrid/web/laf/common/assets/color.css",  this );

    // implementation of org.infogrid.web functionality
        
        app.registerAsset( "/s/org/infogrid/web/taglib/candy/OverlayTag.css", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/candy/OverlayTag.js", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/candy/ToggleCssClass.js", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/mesh/PropertyTag.css", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/mesh/PropertyTag.js", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/mesh/RefreshTag.css", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/viewlet/ChangeViewletStateTag.css", this );
        app.registerAsset( "/s/org/infogrid/web/taglib/viewlet/ViewletAlternativesTag.css", this );

    // JSPOs
        
        app.registerJspo(
                "accessLocally",
                HTML,
                AccessLocally_jspo.class,
                this );
        app.registerJspo(
                "blessRole",
                HTML,
                BlessRole_jspo.class,
                this );
        app.registerJspo(
                "bless",
                HTML,
                Bless_jspo.class,
                this );
        app.registerJspo(
                "create",
                HTML,
                Create_jspo.class,
                this );
        app.registerJspo(
                "delete",
                HTML,
                Delete_jspo.class,
                this );
        app.registerJspo(
                "relate",
                HTML,
                Relate_jspo.class,
                this );
        app.registerJspo(
                "sweepAll",
                HTML,
                SweepAll_jspo.class,
                this );
        app.registerJspo(
                "sweep",
                HTML,
                Sweep_jspo.class,
                this );
        app.registerJspo(
                "unblessRole",
                HTML,
                UnblessRole_jspo.class,
                this );
        app.registerJspo(
                "unbless",
                HTML,
                Unbless_jspo.class,
                this );
        app.registerJspo(
                "unrelate",
                HTML,
                Unrelate_jspo.class,
                this );

    // styles for other viewlets
    
        app.registerAsset( "/s/org/infogrid/web/viewlet/propertysheet/PropertySheetViewlet.css", this );
    
    }
}
