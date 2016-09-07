/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.infogrid.web.viewlet;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.infogrid.util.ZeroElementCursorIterator;

/**
 * We use this as the ServletConfig object.
 */
public class SimpleServletConfig
    implements
        ServletConfig
{
    /**
     * Constructor.
     *
     * @param name name of this servlet instance
     * @param servletContext the ServletContext
     */
    public SimpleServletConfig( String name,
                                ServletContext servletContext )
    {
        theName = name;
        theServletContext = servletContext;
    }

    @Override
    public String getServletName()
    {
        return theName;
    }

    @Override
    public ServletContext getServletContext()
    {
        return theServletContext;
    }

    @Override
    public String getInitParameter( String name )
    {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return ZeroElementCursorIterator.create();
    }
    protected String theName;
    protected ServletContext theServletContext;
}
