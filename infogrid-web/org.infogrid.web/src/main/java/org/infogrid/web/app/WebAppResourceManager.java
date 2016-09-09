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

package org.infogrid.web.app;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import org.infogrid.app.InfoGridInstallable;
import org.infogrid.web.httpshell.HttpShellHandler;
import org.infogrid.web.sane.SaneServletRequest;
import org.infogrid.web.templates.StructuredResponse;

/**
 * Manages resources on behalf of the InfoGridWebApp.
 */
public interface WebAppResourceManager
{
    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * @param path the relative path of the asset
     * @param url the URL of the asset
     * @param mime the content type of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            URL                 url,
            String              mime,
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * @param path the relative path of the asset
     * @param url the URL of the asset
     * @param mime the content type of the asset
     * @param locale the Locale of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            URL                 url,
            String              mime,
            Locale              locale,
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served.
     * 
     * @param path the relative path of the asset
     * @param loader the ClassLoader to resolve the path against
     * @param mime the content type of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            ClassLoader         loader,
            String              mime,
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served.
     * 
     * @param path the relative path of the asset
     * @param loader the ClassLoader to resolve the path against
     * @param mime the content type of the asset
     * @param locale the Locale of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            ClassLoader         loader,
            String              mime,
            Locale              locale, 
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served. The ClassLoader is the InfoGridInstallable's.
     * 
     * @param path the relative path of the asset
     * @param locale the Locale of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            Locale              locale,
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register assets. The
     * WebAppResourceManager decides whether or not, or how to make those assets
     * available. 
     * 
     * This method assumes that the path in which the asset is available
     * relative to the ClassLoader is the same as the relative URL at which
     * it is supposed to be served. The ClassLoader is the InfoGridInstallable's.
     * 
     * @param path the relative path of the asset
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerAsset(
            String              path,
            InfoGridInstallable installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a ViewletTemplate.
     * The WebAppResourceManager decides whether or not, or how to make the
     * ViewletTemplate available. 
     * 
     * @param name the name of the template
     * @param mime the MIME type which this template will emit
     * @param locale the Locale of the asset
     * @param servletClass the Servlet class implementing the Viewlet template
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerViewletTemplate(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a ViewletTemplate.
     * The WebAppResourceManager decides whether or not, or how to make the
     * ViewletTemplate available. 
     * 
     * @param name the name of the template
     * @param mime the MIME type which this template will emit
     * @param servletClass the Servlet class implementing the Viewlet template
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerViewletTemplate(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP function servlet.
     * The app decides whether or not, or how to make those JSP function servlets
     * available. 
     * 
     * @param name the name of the JSP function
     * @param mime the MIME type where this JSP function applies
     * @param locale the Locale of the asset
     * @param servletClass the Servlet class implementing the JSP function
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspf(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP function servlet.
     * The app decides whether or not, or how to make those JSP function servlets
     * available. 
     * 
     * @param name the name of the JSP function
     * @param mime the MIME type where this JSP function applies
     * @param servletClass the Servlet class implementing the JSP function
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspf(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP overlay servlet.
     * The WebAppResourceManager decides whether or not, or how to make those JSP overlay servlets
     * available. 
     * 
     * @param name the name of the JSP overlay
     * @param mime the MIME type where this JSP overlay applies
     * @param locale the Locale of the asset
     * @param servletClass the Servlet class implementing the JSP overlay
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspo(
            String                   name,
            String                   mime,
            Locale                   locale,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Allows an InfoGridAccessory or InfoGridApp to register a JSP overlay servlet.
     * The WebAppResourceManager decides whether or not, or how to make those JSP overlay servlets
     * available. 
     * 
     * @param name the name of the JSP overlay
     * @param mime the MIME type where this JSP overlay applies
     * @param servletClass the Servlet class implementing the JSP overlay
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerJspo(
            String                   name,
            String                   mime,
            Class<? extends Servlet> servletClass,
            InfoGridInstallable      installable );

    /**
     * Register a HttpShellHandler.
     * 
     * @param name name of the HttpShellHandler
     * @param handler the handler
     * @param installable the registering InfoGridAccessory or InfoGridApp
     */
    public void registerHttpShellHandler(
            String              name,
            HttpShellHandler    handler,
            InfoGridInstallable installable );

    /**
     * Process the request for an asset.
     * 
     * @param request the incoming SaneRequest
     * @param response the structured response
     * @param servletContext the ServletContext
     * @throws ServletException Servlet processing problem
     * @throws IOException I/O problem
     */
    public void processAsset(
            SaneServletRequest request,
            StructuredResponse response,
            ServletContext     servletContext )
        throws
            ServletException,
            IOException;

    /**
     * Process a request for JSPF.
     * 
     * @param name name of the JSPF
     * @param pageContext the pageContext
     * @throws ServletException
     * @throws IOException 
     */
    public void processJspf(
            String      name,
            PageContext pageContext )
        throws
            ServletException,
            IOException;

    /**
     * Process a request for JSPO.
     * 
     * @param name name of the JSPO
     * @param pageContext the pageContext
     * @throws ServletException
     * @throws IOException 
     */
    public void processJspo(
            String      name,
            PageContext pageContext )
        throws
            ServletException,
            IOException;

    /**
     * Find a HttpShellHandler by name.
     * 
     * @param name name of the HttpShellHandler
     * @return the HttpShellHandler or null
     */
    public HttpShellHandler findHttpShellHandler(
            String name );

    /**
     * Performs template processing.
     * 
     * @param request the incoming SaneRequest
     * @param response the structured response
     * @param servletContext the ServletContext
     * @throws ServletException Servlet processing problem
     * @throws IOException I/O problem
     */
    public void processTemplate(
            SaneServletRequest  request,
            StructuredResponse  response,
            ServletContext      servletContext )
        throws
            ServletException,
            IOException;

    /**
     * Determine the MIME type from the name of a file. This implements a certain,
     * simple, heuristic. It is easiest for apps and accessories to follow this
     * heuristic, but if not, this method can be overridden.
     * 
     * @param name file name
     * @return MIME type, or null
     */
    public String determineMimeFromFile(
            String name );
}