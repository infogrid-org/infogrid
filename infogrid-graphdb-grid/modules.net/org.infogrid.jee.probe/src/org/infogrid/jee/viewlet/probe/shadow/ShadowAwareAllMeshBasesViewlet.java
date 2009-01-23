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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.viewlet.probe.shadow;

import java.net.URISyntaxException;
import javax.servlet.ServletException;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.templates.StructuredResponse;
import org.infogrid.jee.viewlet.meshbase.AllMeshBasesViewlet;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.NameServer;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;
import org.infogrid.viewlet.AbstractViewedMeshObjects;
import org.infogrid.viewlet.DefaultViewedMeshObjects;

/**
 * Adds the ability to start/stop ShadowMeshBases to AllMeshBasesViewlet.
 */
public class ShadowAwareAllMeshBasesViewlet
    extends
        AllMeshBasesViewlet
{
    private static final Log log = Log.getLogInstance( ShadowAwareAllMeshBasesViewlet.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param c the application context
     * @return the created PropertySheetViewlet
     */
    public static AllMeshBasesViewlet create(
            Context c )
    {
        DefaultViewedMeshObjects viewed = new DefaultViewedMeshObjects();
        AllMeshBasesViewlet      ret    = new ShadowAwareAllMeshBasesViewlet( viewed, c );

        viewed.setViewlet( ret );

        return ret;
    }

    /**
     * Constructor. This is protected: use factory method or subclass.
     *
     * @param viewed the AbstractViewedMeshObjects implementation to use
     * @param c the application context
     */
    protected ShadowAwareAllMeshBasesViewlet(
            AbstractViewedMeshObjects viewed,
            Context                   c )
    {
        super( viewed, c );
    }

    /**
     * <p>Invoked prior to the execution of the Servlet if the POST method has been requested
     *    and the FormTokenService determined that the incoming POST was safe.
     *    It is the hook by which the JeeViewlet can perform whatever operations needed prior to
     *    the POST execution of the servlet, e.g. the evaluation of POST commands.</p>
     * <p>Subclasses will often override this.</p>
     *
     * @param request the incoming request
     * @param response the response to be assembled
     * @throws ServletException thrown if an error occurred
     * @see #performBeforeGet
     * @see #performBeforeUnsafePost
     * @see #performAfter
     */
    @Override
    public void performBeforeSafePost(
            RestfulRequest     request,
            StructuredResponse response )
        throws
            ServletException
    {
        SaneRequest sane = request.getSaneRequest();

        String meshBaseName = sane.getArgument( FORM_MESHBASE_NAME );
        String runNowAction = sane.getArgument( FORM_RUNNOWACTION_NAME );
        String stopAction   = sane.getArgument( FORM_STOPACTION_NAME );

        boolean doRunNow = false;
        boolean doStop   = false;

        if( runNowAction != null && runNowAction.length() > 0 ) {
            doRunNow = true;

        } else if( stopAction != null && stopAction.length() > 0 ) {
            doStop = true;

        } else {
            return; // silently fail
        }

        InfoGridWebApp app = InfoGridWebApp.getSingleton();
        Context        c   = app.getApplicationContext();

        NetMeshBase           mainMeshBase       = c.findContextObjectOrThrow( NetMeshBase.class );
        NetMeshBaseIdentifier meshBaseIdentifier;

        try {
            meshBaseIdentifier = mainMeshBase.getMeshBaseIdentifierFactory().fromExternalForm( meshBaseName );

        } catch( URISyntaxException ex ) {
            log.warn( ex );
            return; // silently fail
        }

        @SuppressWarnings( "unchecked" )
        NameServer<MeshBaseIdentifier,MeshBase> ns  = c.findContextObjectOrThrow( NameServer.class );

        MeshBase found = ns.get( meshBaseIdentifier );
        if( found == null ) {
            log.warn( "MeshBase not found: " + meshBaseIdentifier.toExternalForm() );
            return; // silently fail
        }
        if( !( found instanceof ShadowMeshBase )) {
            log.warn( "MeshBase not a shadow: " + found );
        }

        ShadowMeshBase realFound = (ShadowMeshBase) found;

        try {
            if( doRunNow ) {
                realFound.doUpdateNow();
            } else if( doStop ) {
                realFound.cancelFutureUpdates();
            }
        } catch( Throwable t ) {
            log.warn( t );
            response.reportProblem( t );
        }
    }

    /**
     * Name of the HTML Form element that contains the name of the affected MeshBase.
     */
    public static final String FORM_MESHBASE_NAME = "MeshBase";

    /**
     * Name of the HTML input element that indicates to run the ShadowMeshBase now.
     */
    public static final String FORM_RUNNOWACTION_NAME = "RunNowAction";

    /**
     * Name of the HTML input element that indicates to stop the ShadowMeshBase now.
     */
    public static final String FORM_STOPACTION_NAME = "StopAction";
}
