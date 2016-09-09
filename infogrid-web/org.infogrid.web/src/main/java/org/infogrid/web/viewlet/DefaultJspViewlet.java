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

package org.infogrid.web.viewlet;

import javax.servlet.Servlet;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.util.context.Context;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.ViewletFactoryChoice;

/**
 * A Viewlet Class that can impersonate any other JSP-based Viewlet, as long as the Viewlet
 * does not override or add any methods.
 */
public class DefaultJspViewlet
        extends
            AbstractWebViewlet
{
    /**
     * Factory method.
     *
     * @param viewletName the computable name of the Viewlet
     * @param servletClass the Servlet implementing this Viewlet
     * @param mb the MeshBase from which the viewed MeshObjects are taken
     * @param c the application context
     * @return the created Viewlet
     */
    public static DefaultJspViewlet create(
            String                   viewletName,
            Class<? extends Servlet> servletClass,
            MeshBase                 mb,
            Context                  c )
    {
        DefaultWebViewedMeshObjects viewed = new DefaultWebViewedMeshObjects( mb );
        DefaultJspViewlet           ret    = new DefaultJspViewlet( viewletName, servletClass, viewed, c );

        viewed.setViewlet( ret );

        return ret;
    }

    /**
     * Factory method for a ViewletFactoryChoice that instantiates this Viewlet.
     *
     * @param toView the JeeMeshObjectsToView for which this is a choice
     * @param servletClass the servlet implementing this Viewlet
     * @param matchQuality the match quality
     * @return the ViewletFactoryChoice
     */
    public static ViewletFactoryChoice choice(
            WebMeshObjectsToView     toView,
            Class<? extends Servlet> servletClass,
            double                   matchQuality )
    {
        return new DefaultJspViewletFactoryChoice( toView, servletClass, matchQuality ) {
                @Override
                public DefaultJspViewlet instantiateViewlet()
                    throws
                        CannotViewException
                {
                    return create( theName, servletClass, getMeshObjectsToView().getMeshBase(), getMeshObjectsToView().getContext() );
                }
        };
    }

    /**
     * Constructor. This is protected: use factory method or subclass.
     *
     * @param viewletName the computable name of the Viewlet
     * @param servletClass the Servlet implementing this Viewlet
     * @param viewed the JeeViewedMeshObjects to use
     * @param c the application context
     */
    protected DefaultJspViewlet(
            String                   viewletName,
            Class<? extends Servlet> servletClass,
            WebViewedMeshObjects     viewed,
            Context                  c )
    {
        super( viewletName, servletClass, viewed, c );
    }
}
