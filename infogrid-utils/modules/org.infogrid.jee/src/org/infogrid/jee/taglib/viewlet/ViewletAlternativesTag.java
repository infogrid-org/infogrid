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

package org.infogrid.jee.taglib.viewlet;

import org.infogrid.jee.taglib.InfoGridJspUtils;
import org.infogrid.jee.taglib.AbstractInfoGridTag;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.viewlet.JeeViewlet;

import org.infogrid.context.Context;
import org.infogrid.mesh.MeshObject;

import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.Viewlet;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.viewlet.ViewletFactoryChoice;

import org.infogrid.util.logging.Log;
import org.infogrid.util.ResourceHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import java.util.HashMap;

/**
 * Allows the user to select an alternate JeeViewlet to display the current subject.
 */
public class ViewletAlternativesTag
    extends
        AbstractInfoGridTag
{
    private static final Log log = Log.getLogInstance( ViewletAlternativesTag.class ); // our own, private logger

    /**
     * Constructor.
     */
    public ViewletAlternativesTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        super.initializeToDefaults();
    }

    /**
     * Process the start tag.
     *
     * @return evaluate or skip body
     * @throws JspException if a JSP exception has occurred
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        Viewlet currentViewlet = (Viewlet) lookupOrThrow( JeeViewlet.VIEWLET_ATTRIBUTE_NAME );
        
        MeshObject subject = currentViewlet.getSubject();
        Context    c       = currentViewlet.getContext();
        
        ViewletFactory factory = c.findContextObject( ViewletFactory.class );
        
        MeshObjectsToView toView = MeshObjectsToView.create( subject );

        try {
            ViewletFactoryChoice [] candidates = factory.determineFactoryChoicesOrderedByMatchQuality( toView );

            if( candidates.length > 0 ) {
                String nameInCss = getClass().getName().replace( '.', '-' );
                println( "<div class=\"" + nameInCss + "\" id=\"" + nameInCss + "\">" );
                print( "<h3><a href=\"javascript:toggle_viewlet_alternatives()\">" );
                print( theResourceHelper.getResourceString( "Title" ));
                println( "</a></h3>" );
                println( "<ul>" );

                String                 url = ((HttpServletRequest)pageContext.getRequest()).getRequestURI();
                HashMap<String,String> map = new HashMap<String,String>();
                
                for( int i=0 ; i<candidates.length ; ++i ) {
                    map.put( "lid-format", "viewlet:" + candidates[i].getImplementationName() );
                    print( "<li" );
                    if( candidates[i].getImplementationName().equals( currentViewlet.getClass().getName() )) {
                        print( " class=\"selected\"" );
                    }
                    print( ">" );
                    print( "<a href=\"" );
                    print( InfoGridJspUtils.constructHrefWithDifferentArguments( url, map ));
                    print( "\">" );
                    print( candidates[i].getUserVisibleName() );
                    print( "</a>" );
                    println( "</li>" );
                }
                println( "</ul>" );
                println( "</div>" );
            }

        } catch( CannotViewException ex ) {
            log.warn( ex );
        }
        return SKIP_BODY;
    }
    
    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( ViewletAlternativesTag.class );
}
