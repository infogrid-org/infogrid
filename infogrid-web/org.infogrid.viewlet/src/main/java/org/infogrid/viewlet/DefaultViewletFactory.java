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

package org.infogrid.viewlet;

import java.util.HashMap;
import java.util.Map;
import org.infogrid.app.InfoGridInstallable;
import org.infogrid.util.ArrayHelper;

/**
 * This ViewletFactory asks each known ViewletMatcher for a score, and picks the
 * Viewlet most appropriate.
 */
public class DefaultViewletFactory
    extends
        AbstractViewletFactory
{
    /**
     * Factory method.
     * 
     * @param implementationMarkerInterfaceName name of an interface that indicates the right implementation
     *        technology for a given Viewlet, e.g. "org.viewlet.swing.SwingViewlet".
     * @return the created DefaultViewletFactory
     * @throws IllegalArgumentException if an empty implementationMarkerInterfaceName was provided
     */
    public static DefaultViewletFactory create(
            String implementationMarkerInterfaceName )
    {
        return new DefaultViewletFactory( implementationMarkerInterfaceName );
    }

    /**
     * Constructor.
     *
     * @param implementationMarkerInterfaceName name of an interface that indicates the right implementation
     *        technology for a given Viewlet, e.g. "org.viewlet.swing.SwingViewlet".
     * @throws IllegalArgumentException if an empty implementationMarkerInterfaceName was provided
     */
    protected DefaultViewletFactory(
            String implementationMarkerInterfaceName )
    {
        super( implementationMarkerInterfaceName );
    }

    /**
     * Find the ViewletFactoryChoices that apply to these MeshObjectsToView, but ignore the specified
     * viewlet type. If none are found, return an empty array.
     *
     * @param theObjectsToView the MeshObjectsToView
     * @return the found ViewletFactoryChoices, if any
     */
    @Override
    public ViewletFactoryChoice [] determineChoicesIgnoringType(
            MeshObjectsToView theObjectsToView )
    {
        ViewletFactoryChoice [] ret   = new ViewletFactoryChoice[ theViewletMatchers.size() ];
        int                     count = 0;
        
        for( ViewletMatcher matcher : theViewletMatchers.keySet() ) {
            ViewletFactoryChoice choice = matcher.match( theObjectsToView );
            if( choice != null ) {
                ret[count++] = choice;
            }
        }
        if( count < ret.length ) {
            ret = ArrayHelper.copyIntoNewArray( ret, 0, count, ViewletFactoryChoice.class );
        }
        return ret;
    }
    
    /**
     * Register a new Viewlet by way of its ViewletMatcher.
     * 
     * @param matcher the ViewletMatcher
     * @param installable the registering InfoGridInstallable
     */
    @Override
    public void registerViewlet(
            ViewletMatcher      matcher,
            InfoGridInstallable installable )
    {
        theViewletMatchers.put( matcher, installable );
    }
    
    /**
     * The known ViewletMatchers, mapped to their registering InfoGridInstallable.
     */
    protected Map<ViewletMatcher,InfoGridInstallable> theViewletMatchers = new HashMap<>();
}
