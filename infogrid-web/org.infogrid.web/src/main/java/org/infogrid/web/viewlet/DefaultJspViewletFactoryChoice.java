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

package org.infogrid.web.viewlet;

import java.util.ArrayList;
import javax.servlet.Servlet;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;
import org.infogrid.viewlet.CannotViewException;
import org.infogrid.viewlet.Viewlet;

/**
 * <p>A ViewletFactoryChoice that instantiates the SimpleJeeViewlet as default, pretending to
 *    be a Viewlet class with a certain name, called the <code>pseudoClassName</code>. This
 *    is identical to creating a DefaultViewletFactoryChoice with a Viewlet class named
 *    pseudoClassName that does not add any functionality itself.</p>
 */
public class DefaultJspViewletFactoryChoice
        extends
            DefaultWebViewletFactoryChoice
{
    /**
     * Constructor.
     * 
     * @param toView the JeeMeshObjectsToView for which this is a choice
     * @param servletClass the name of the (non-exististing) Viewlet class
     * @param matchQuality the match quality
     */
    public DefaultJspViewletFactoryChoice(
            WebMeshObjectsToView     toView,
            Class<? extends Servlet> servletClass,
            double                   matchQuality )
    {
        this( toView, servletClass.getName(), servletClass, matchQuality );
    }

    /**
     * Constructor.
     * 
     * @param toView the JeeMeshObjectsToView for which this is a choice
     * @param viewletName computable name of the Viewlet
     * @param servletClass the name of the (non-exististing) Viewlet class
     * @param matchQuality the match quality
     */
    public DefaultJspViewletFactoryChoice(
            WebMeshObjectsToView     toView,
            String                   viewletName,
            Class<? extends Servlet> servletClass,
            double                   matchQuality )
    {
        super( toView, AbstractWebViewlet.class, servletClass.getName(), matchQuality );

        theName         = viewletName;
        theServletClass = servletClass;
    }

    /**
     * Obtain the computable name of the Viewlet.
     * 
     * @return the Viewlet's name
     */
    @Override
    public String getName()
    {
        return theName;
    }

    /**
      * Obtain the names of the interfaces provided by this ViewletFactoryChoice.
      *
      * @return the names of the interfaces provided by this ViewletFactoryChoice.
      */
    @Override
    public String [] getInterfaceNames()
    {
        ArrayList<String> almost = new ArrayList<>();

        almost.add( theServletClass.getName() );

        determineClassNames( theViewletClass, almost );

        String [] ret = ArrayHelper.copyIntoNewArray( almost, String.class );
        return ret;
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation. Always provided.
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    @Override
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String userVisibleName = ResourceHelper.getInstance( theName, theServletClass.getClassLoader() ).getResourceStringOrDefault( "UserVisibleName", theServletClass.getName() );

        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                StringRepresentation.DEFAULT_ENTRY,
                pars,
        /* 0 */ this,
        /* 1 */ theViewletClass.getName(),
        /* 2 */ userVisibleName,
        /* 3 */ theMatchQuality );

        return ret;
    }

    @Override
    public Viewlet instantiateViewlet()
            throws
            CannotViewException
    {
        return DefaultJspViewlet.create( theName, theServletClass, theToView.getMeshBase(), theToView.getContext() );
    }

    /**
     * The name of the viewlet.
     */
    protected String theName;

    /**
     * The servlet class.
     */
    protected Class<? extends Servlet> theServletClass;
}
