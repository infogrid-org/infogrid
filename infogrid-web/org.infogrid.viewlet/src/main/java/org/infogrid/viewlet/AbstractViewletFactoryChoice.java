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

package org.infogrid.viewlet;

import java.util.HashSet;
import java.util.Set;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * Factors out common functionality for ViewletFactoryChoice implementations.
 */
public abstract class AbstractViewletFactoryChoice
        implements
            ViewletFactoryChoice
{
    /**
     * Private constructor for subclasses only.
     *
     * @param toView the MeshObjectsToView for which this is a choice
     */
    protected AbstractViewletFactoryChoice(
            MeshObjectsToView toView )
    {
        theToView = toView;
    }

    /**
     * Determine the MeshObjectsToView for which this is a choice.
     *
     * @return the MeshObjectsToView
     */
    @Override
    public MeshObjectsToView getMeshObjectsToView()
    {
        return theToView;
    }

    /**
     * Obtain the computable name of this choice. By default, this maps to the
     * implementation name.
     *
     * @return the Viewlet's name
     */
    @Override
    public String getName()
    {
        return getImplementationName();
    }

    /**
     * Obtain the names of the interfaces provided by this ViewletFactoryChoice.
     *
     * @param viewletClass the viewlet class
     * @return the names of the interfaces provided by this ViewletFactoryChoice.
     */
    public static String [] getInterfaceNames(
            Class viewletClass )
    {
        Set<String> almost = new HashSet<>();

        determineClassNames( viewletClass, almost );

        String [] ret = ArrayHelper.copyIntoNewArray( almost, String.class );
        return ret;
    }

    /**
     * Helper method that recursively looks up the names of all interface
     * and class names supported by a Class.
     *
     * @param clazz the Class
     * @param found the set of names found
     */
    public static void determineClassNames(
            Class       clazz,
            Set<String> found )
    {
        found.add( clazz.getName() );

        Class toAdd = clazz.getSuperclass();
        if( toAdd != null ) {
            determineClassNames( toAdd, found );
        }
        for( Class intfc : clazz.getInterfaces()) {
            if( !found.contains( intfc.getName() )) {
                determineClassNames( intfc, found );
            }
        }
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    @Override
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return "";
    }

    /**
     * The MeshObjectsToView for which this is a choice.
     */
    protected MeshObjectsToView theToView;
}