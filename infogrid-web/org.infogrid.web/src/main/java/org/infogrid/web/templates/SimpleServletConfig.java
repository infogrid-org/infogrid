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

package org.infogrid.web.templates;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.infogrid.util.ZeroElementCursorIterator;

/**
 * Simple implementation of the ServletConfig interface. It does not provide
 * any init parameters.
 */
public class SimpleServletConfig
    implements
        ServletConfig
{
    /**
     * Constructor.
     * 
     * @param name name of the servlet
     * @param servletContext the ServletContext
     */
    public SimpleServletConfig(
            String name,
            ServletContext servletContext )
    {
        theName           = name;
        theServletContext = servletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServletName()
    {
        return theName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext()
    {
        return theServletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitParameter(
            String name )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return ZeroElementCursorIterator.create();
    }

    /**
     * The Servlet name.
     */
    protected String theName;
    
    /**
     * The ServletContext.
     */
    protected ServletContext theServletContext;
}
