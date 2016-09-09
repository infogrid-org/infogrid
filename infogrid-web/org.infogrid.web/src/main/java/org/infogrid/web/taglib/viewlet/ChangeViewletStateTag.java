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

package org.infogrid.web.taglib.viewlet;

import java.util.Deque;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.StringHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.taglib.AbstractInfoGridTag;
import org.infogrid.web.taglib.IgnoreException;
import org.infogrid.web.viewlet.DefaultWebViewletStateEnum;
import org.infogrid.web.viewlet.WebMeshObjectsToView;
import org.infogrid.web.viewlet.WebViewedMeshObjects;
import org.infogrid.web.viewlet.WebViewlet;
import org.infogrid.web.viewlet.WebViewletState;

/**
 * Allows the user to select an alternate JeeViewletState.
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class ChangeViewletStateTag
    extends
        AbstractInfoGridTag
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public ChangeViewletStateTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theViewletStates = null;
        theDisplay       = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the viewletStates property.
     *
     * @return value of the viewletStates property
     * @see #setViewletStates
     */
    public final String getViewletStates()
    {
        return theViewletStates;
    }

    /**
     * Set value of the viewletStates property.
     *
     * @param newValue new value of the viewletStates property
     * @see #getViewletStates
     */
    public final void setViewletStates(
            String newValue )
    {
        theViewletStates = newValue;
    }

    /**
     * Obtain value of the display property.
     *
     * @return value of the display property
     * @see #setDisplay
     */
    public final String getDisplay()
    {
        return theDisplay;
    }

    /**
     * Set value of the display property.
     *
     * @param newValue new value of the display property
     * @see #getDisplay
     */
    public final void setDisplay(
            String newValue )
    {
        theDisplay = newValue;
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        // this needs to be simple lookup so the periods in the class name don't trigger nestedLookup
        SaneRequest request        = SaneServletRequest.create( (HttpServletRequest) pageContext.getRequest() );
        WebViewlet  currentViewlet = (WebViewlet) lookupOrThrow( WebViewlet.VIEWLET_ATTRIBUTE_NAME );

        WebViewletState    currentState    = currentViewlet.getViewletState();
        WebViewletState [] possibleStates  = currentViewlet.getPossibleViewletStates();
        String []          specifiedStates = theViewletStates.trim().split( "\\s*,\\s*" );

        if( specifiedStates.length >= 1 ) {
            StringBuilder buf             = new StringBuilder();
            boolean       hasOtherElement = false;

            WebMeshObjectsToView currentlyToView = currentViewlet.getViewedMeshObjects().getMeshObjectsToView();
            @SuppressWarnings("unchecked")
            Deque<WebViewedMeshObjects> parentViewedStack = (Deque<WebViewedMeshObjects>) request.getAttribute( IncludeViewletTag.PARENT_STACK_ATTRIBUTE_NAME );

            // String href = request.getAbsoluteFullUri();
            // href = theFormatter.filter( href );

            String nameInCss = getClass().getName().replace( '.', '-' );
            buf.append( "<div class=\"" ).append( nameInCss ).append( "\" id=\"" ).append( nameInCss ).append( "\">\n" );
            buf.append( " <ul>\n" );
            for( String current : specifiedStates ) {
                WebViewletState found = null;
                for( WebViewletState possible : possibleStates ) {
                    if( current.equals( possible.getName() )) {
                        found = possible;
                        break;
                    }
                }
                if( found == null ) {
                    continue;
                }
                try {
                    if( found.equals( currentState )) {
                        if( !DISPLAY_COMPACT.equals( theDisplay )) {
                            buf.append( "  <li>" );
                            buf.append( "<b>" );
                            buf.append( found.toStringRepresentation( null, StringRepresentationParameters.EMPTY ) ); // arguments don't matter
                            buf.append( "</b>" );
                            buf.append( "  </li>\n" );
                        }
                    } else {
                        buf.append( "  <li>" );
                        buf.append( "<a href=\"" );
                        // buf.append( HTTP.replaceOrAppendArgumentToUrl( href, JeeViewletState.VIEWLET_STATE_PAR_NAME, current ));

                        WebMeshObjectsToView newToView  = currentlyToView.createCopy();
                        newToView.setViewletState( found );

                        String href = newToView.getAsUrl( parentViewedStack );
                        buf.append( StringHelper.stringToHtml( href ));

                        buf.append( "\">" );
                        buf.append( found.toStringRepresentation( null, StringRepresentationParameters.EMPTY ) ); // arguments don't matter
                        buf.append( "</a>" );
                        buf.append( "  </li>\n" );

                        hasOtherElement = true;
                    }

                } catch( StringifierException ex ) {
                    throw new JspException( ex );
                }
            }
            buf.append( " </ul>\n" );
            buf.append( "</div>\n" );

            if( hasOtherElement ) {
                print( buf.toString() );
            }
        }

        return SKIP_BODY;
    }

    /**
     * The selectable ViewletStates, separated by commas.
     */
    protected String theViewletStates;

    /**
     * The type of display.
     */
    protected String theDisplay;

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( ChangeViewletStateTag.class );

    /**
     * The default default selectable ViewletStates, separated by commas.
     */
    protected static final String DEFAULT_DEFAULT_VIEWLET_STATES;
    static {
        final StringBuilder buf = new StringBuilder();
        String sep = "";
        for( DefaultWebViewletStateEnum current : DefaultWebViewletStateEnum.values()) {
            buf.append( sep );
            buf.append( current.getName() );
            sep = ",";
        }
        DEFAULT_DEFAULT_VIEWLET_STATES = buf.toString();
    }

    /**
     * The default selectable ViewletStates, separated by commas.
     */
    protected static final String DEFAULT_VIEWLET_STATES = theResourceHelper.getResourceStringOrDefault(
            "DefaultViewletStates",
            DEFAULT_DEFAULT_VIEWLET_STATES );

    /**
     * A value for the display property.
     */
    public static final String DISPLAY_COMPACT = "compact";
}
