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
import jsps.org.infogrid.web.laf.common.templates.BareHtmlTemplate_jsp;
import jsps.org.infogrid.web.laf.common.templates.DefaultHtmlTemplate_jsp;
import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.infogrid.app.AppConfiguration;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.app.WebAppResourceManager;

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

        WebAppResourceManager rm = app.getResourceManager();

        // template
        
        rm.registerViewletTemplate(
                "default",
                HTML,
                DefaultHtmlTemplate_jsp.class,
                this );
        rm.registerViewletTemplate(
                "bare",
                HTML,
                BareHtmlTemplate_jsp.class,
                this );
        
        rm.registerAsset( "/s/org/infogrid/web/laf/common/assets/master.css", this );
        rm.registerAsset( "/s/org/infogrid/web/laf/common/assets/layout.css", this );
        rm.registerAsset( "/s/org/infogrid/web/laf/common/assets/color.css",  this );

        rm.registerAsset( "/s/org/infogrid/web/laf/common/images/cancel.png", this );
        rm.registerAsset( "/s/org/infogrid/web/laf/common/images/tick.png",   this );

        // implementation of org.infogrid.web functionality
        
        rm.registerAsset( "/s/org/infogrid/web/taglib/candy/OverlayTag.css", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/candy/OverlayTag.js", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/candy/ToggleCssClass.js", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/mesh/PropertyTag.css", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/mesh/PropertyTag.js", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/mesh/RefreshTag.css", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/viewlet/ChangeViewletStateTag.css", this );
        rm.registerAsset( "/s/org/infogrid/web/taglib/viewlet/ViewletAlternativesTag.css", this );

    // JSPOs
        
        rm.registerJspo(
                "accessLocally",
                HTML,
                AccessLocally_jspo.class,
                this );
        rm.registerJspo(
                "blessRole",
                HTML,
                BlessRole_jspo.class,
                this );
        rm.registerJspo(
                "bless",
                HTML,
                Bless_jspo.class,
                this );
        rm.registerJspo(
                "create",
                HTML,
                Create_jspo.class,
                this );
        rm.registerJspo(
                "delete",
                HTML,
                Delete_jspo.class,
                this );
        rm.registerJspo(
                "relate",
                HTML,
                Relate_jspo.class,
                this );
        rm.registerJspo(
                "sweepAll",
                HTML,
                SweepAll_jspo.class,
                this );
        rm.registerJspo(
                "sweep",
                HTML,
                Sweep_jspo.class,
                this );
        rm.registerJspo(
                "unblessRole",
                HTML,
                UnblessRole_jspo.class,
                this );
        rm.registerJspo(
                "unbless",
                HTML,
                Unbless_jspo.class,
                this );
        rm.registerJspo(
                "unrelate",
                HTML,
                Unrelate_jspo.class,
                this );

    // styles for other viewlets
    
        rm.registerAsset( "/s/org/infogrid/web/viewlet/propertysheet/PropertySheetViewlet.css", this );
    }
}
