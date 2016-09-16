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

import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.http.SaneUrl;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;

/**
 * Default implementation of WebViewletStateTransition.
 */
public enum DefaultWebViewletStateTransitionEnum
        implements
            WebViewletStateTransition
{
    DO_EDIT( "do-edit" ) {
        /**
         * Obtain the desired state after this transition has been taken.
         *
         * @return the desired state after this transition has been taken
         */
        @Override
        public WebViewletState getNextState()
        {
            return DefaultWebViewletStateEnum.EDIT;
        }
    },
    DO_PREVIEW( "do-preview" ) {
        /**
         * Obtain the desired state after this transition has been taken.
         *
         * @return the desired state after this transition has been taken
         */
        @Override
        public WebViewletState getNextState()
        {
            return DefaultWebViewletStateEnum.PREVIEW;
        }
    },
    DO_COMMIT( "do-commit" ) {
        /**
         * Obtain the desired state after this transition has been taken.
         *
         * @return the desired state after this transition has been taken
         */
        @Override
        public WebViewletState getNextState()
        {
            return DefaultWebViewletStateEnum.VIEW;
        }
    },
    DO_CANCEL( "do-cancel" ) {
        /**
         * Obtain the desired state after this transition has been taken.
         *
         * @return the desired state after this transition has been taken
         */
        @Override
        public WebViewletState getNextState()
        {
            return DefaultWebViewletStateEnum.VIEW;
        }
    };
    
    /**
     * Constructur.
     *
     * @param transitionName name of the transition which the Viewlet is about to make
     */
    private DefaultWebViewletStateTransitionEnum(
            String transitionName )
    {
        theTransitionName = transitionName;
    }

    /**
     * Obtain the name of this transition.
     *
     * @return the name of this transition
     */
    @Override
    public String getName()
    {
        return theTransitionName;
    }

    /**
     * Obtain the correct member of this enum, given this incoming request.
     *
     * @param request the incoming request
     * @return the DefaultJeeViewletStateEnum
     */
    public static DefaultWebViewletStateTransitionEnum fromRequest(
            SaneUrl request )
    {
        if( request instanceof SaneRequest ) {
            String value = ((SaneRequest)request).getPostedArgument( VIEWLET_STATE_TRANSITION_PAR_NAME );
            // this must be a post argument, while the state is determined from a regular argument
            if( value != null ) {
                for( DefaultWebViewletStateTransitionEnum candidate : DefaultWebViewletStateTransitionEnum.values() ) {
                    if( candidate.theTransitionName.equals( value )) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     */
    @Override
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
    {
        return theResourceHelper.getResourceString( toString() + "_VALUE" );
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
    {
        return "";
    }

    /**
     * Name of the transition.
     */
    protected String theTransitionName;

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( DefaultWebViewletStateTransitionEnum.class );
}