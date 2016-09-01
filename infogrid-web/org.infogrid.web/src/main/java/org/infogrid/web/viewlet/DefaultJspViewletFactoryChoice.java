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

/**
 * <p>A ViewletFactoryChoice that instantiates the SimpleJeeViewlet as default, pretending to
 *    be a Viewlet class with a certain name, called the <code>pseudoClassName</code>. This
 *    is identical to creating a DefaultViewletFactoryChoice with a Viewlet class named
 *    pseudoClassName that does not add any functionality itself.</p>
 * <p>The main purpose of this class is to avoid having to write empty Viewlet classes.</p>
 */
public abstract class DefaultJspViewletFactoryChoice
        extends
            DefaultWebViewletFactoryChoice
{
    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param toView the JeeMeshObjectsToView for which this is a choice
     * @param servletClass the name of the (non-exististing) Viewlet class
     * @param matchQuality the match quality
     */
    protected DefaultJspViewletFactoryChoice(
            WebMeshObjectsToView     toView,
            Class<? extends Servlet> servletClass,
            double                   matchQuality )
    {
        super( toView, AbstractWebViewlet.class, servletClass.getName(), matchQuality );

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
        return theServletClass.getName();
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
        String userVisibleName = ResourceHelper.getInstance( theServletClass ).getResourceStringOrDefault("UserVisibleName", theServletClass.getName() );

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

    /**
     * The servlet class
     */
    protected Class<? extends Servlet> theServletClass;
}
