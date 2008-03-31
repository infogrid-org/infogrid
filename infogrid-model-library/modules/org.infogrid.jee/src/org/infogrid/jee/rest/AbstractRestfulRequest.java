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

package org.infogrid.jee.rest;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;

import org.infogrid.util.http.SaneRequest;

import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.infogrid.jee.sane.SaneServletRequest;

/**
 *
 */
public abstract class AbstractRestfulRequest
        implements
            RestfulRequest
{
    /**
     * Constructor.
     */
    protected AbstractRestfulRequest(
            SaneServletRequest lidRequest,
            String             contextPath )
    {
        theSaneRequest = lidRequest;
        theContextPath = contextPath;
        
        StringBuilder buf = new StringBuilder();
        buf.append( lidRequest.getRootUri() );
        buf.append( contextPath );
        theAbsoluteContextPath = buf.toString();
    }

    /**
     * Obtain the context path of the application.
     *
     * @return the context path
     */
    public String getContextPath()
    {
        return theContextPath;
    }

    /**
     * Obtain the absolute context path of the application.
     *
     * @return the context path
     */
    public String getAbsoluteContextPath()
    {
        return theAbsoluteContextPath;
    }

    /**
     * Obtain the underlying SaneRequest.
     *
     * @return the SaneRequest
     */
    public SaneRequest getSaneRequest()
    {
        return theSaneRequest;
    }

    /**
     * Determine the requested traversal, if any.
     * 
     * @return the traversal
     */
    public String getRequestedTraversal()
    {
        if( theRequestedTraversal == null ) {
            String format = theSaneRequest.getArgument( LID_TRAVERSAL_PARAMETER_NAME );

            if( format != null ) {
                theRequestedTraversal = format;
            } else {
                theRequestedTraversal = NO_ANSWER_STRING;
            }
        }
        if( (Object) theRequestedTraversal != (Object) NO_ANSWER_STRING ) { // typecast to avoid warning
            return theRequestedTraversal;
        } else {
            return null;
        }        
    }
    
    /**
     * Obtain the name of the requested Viewlet, if any.
     *
     * @return class name of the requested Viewlet
     */
    public String getRequestedViewletClassName()
    {
        if( theRequestedViewletClass == null ) {
            String format = theSaneRequest.getArgument( LID_FORMAT_PARAMETER_NAME );

            if( format != null && format.startsWith( VIEWLET_PREFIX )) {
                theRequestedViewletClass = format.substring( VIEWLET_PREFIX.length() );
            } else {
                theRequestedViewletClass = NO_ANSWER_STRING;
            }
        }
        if( (Object) theRequestedViewletClass != (Object) NO_ANSWER_STRING ) { // typecast to avoid warning
            return theRequestedViewletClass;
        } else {
            return null;
        }
    }
    
    /**
     * Obtain the name of the requested layout, if any.
     * 
     * @return class name of the requested layout, if any
     */
    public String getRequestedTemplate()
    {
        if( theRequestedTemplate == null ) {
            theRequestedTemplate = theSaneRequest.getArgument( LID_TEMPLATE_PARAMETER_NAME );

            if( theRequestedTemplate == null ) {
                theRequestedTemplate = theSaneRequest.getCookieValue( LID_TEMPLATE_COOKIE_NAME );
                
                if( theRequestedTemplate == null ) {
                    theRequestedTemplate = NO_ANSWER_STRING;
                }
            }
        }
        if( (Object) theRequestedTemplate != (Object) NO_ANSWER_STRING ) { // typecast to avoid warning
            return theRequestedTemplate;
        } else {
            return null;
        }
    }

    /**
     * Determine the identifier of the requested MeshBase.
     * 
     * @return the MeshBaseIdentifier
     */
    public MeshBaseIdentifier determineRequestedMeshBaseIdentifier()
            throws
                MeshObjectAccessException,
                URISyntaxException
    {
        if( theRequestedMeshBaseIdentifier == null ) {
            calculate();
        }
        return theRequestedMeshBaseIdentifier;
    }

    /**
     * Determine the identifier of the requested MeshObject.
     * 
     * @return the MeshObjectIdentifier
     */
    public MeshObjectIdentifier determineRequestedMeshObjectIdentifier()
            throws
                MeshObjectAccessException,
                URISyntaxException
    {
        if( theRequestedMeshObjectIdentifier == null ) {
            calculate();
        }
        return theRequestedMeshObjectIdentifier;
    }
    
    /**
     * Internal method to calculate the data.
     */
    protected abstract void calculate()
            throws
                MeshObjectAccessException,
                URISyntaxException;

    /**
     * Determine the requested MeshObject.
     * 
     * @return the MeshObject, or null if not found
     */
    public MeshObject determineRequestedMeshObject()
            throws
                MeshObjectAccessException,
                URISyntaxException
    {
        if( theRequestedMeshObject == null ) {
            calculate();
        }
        return theRequestedMeshObject;
    }

    /**
     * Determine the underlying HttpServletRequest.
     * 
     * @return the delegate
     */
    public HttpServletRequest getDelegate()
    {
        return theSaneRequest.getDelegate();
    }

    /**
     * The underlying SaneRequest.
     */
    protected SaneServletRequest theSaneRequest;

    /**
     * The context path of the web application.
     */
    protected String theContextPath;

    /**
     * The calculated absolute context path.
     */
    protected String theAbsoluteContextPath;

    /**
     * The requested traversal, if any. (buffered)
     */
    protected String theRequestedTraversal = null;

    /**
     * The requested Viewlet class, if any. (buffered)
     */
    protected String theRequestedViewletClass = null;

    /**
     * The requested formatting template, if any.
     */
    protected String theRequestedTemplate = null;

    /**
     * The requested MeshBaseIdentifier.
     */
    protected MeshBaseIdentifier theRequestedMeshBaseIdentifier;

    /**
     * The requested MeshObjectIdentifier.
     */
    protected MeshObjectIdentifier theRequestedMeshObjectIdentifier = null;

    /**
     * The MeshObject found by accessing the right MeshBase with the MeshObjectIdentifier.
     */
    protected MeshObject theRequestedMeshObject;

    /**
     * Buffered values are set to this String to express "we did the parsing, but did not
     * find an answer."
     */
    protected static final String NO_ANSWER_STRING = new String( "" ); // don't want to do String.intern() here

    /**
     * Name of the LID format parameter.
     */
    public static final String LID_FORMAT_PARAMETER_NAME = "lid-format";

    /**
     * The prefix in the lid-format string that indicates the name of a viewlet.
     */
    public static final String VIEWLET_PREFIX = "viewlet:";
    
    /**
     * Name of the LID traversal parameter.
     */
    public static final String LID_TRAVERSAL_PARAMETER_NAME = "lid-traversal";
    
    /**
     * The prefix in the lid-traversal string that indicates an Xpath.
     */
    public static final String XPATH_PREFIX = "xpath:";
    
    /**
     * Name of the LID template parameter.
     */
    public static final String LID_TEMPLATE_PARAMETER_NAME = "lid-template";
    
    /**
     * Name of the cookie representing the LID template.
     */
    public static final String LID_TEMPLATE_COOKIE_NAME = "org.netmesh.lid-template";

    /**
     * The prefix in the lid-template string that indicates the name of a class.
     */
    public static final String CLASS_PREFIX = "class:";
    
}
