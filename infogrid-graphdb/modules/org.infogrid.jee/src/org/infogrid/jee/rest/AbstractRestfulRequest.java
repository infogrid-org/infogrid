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
            SaneRequest lidRequest,
            String      contextPath )
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
     * Obtain the name of the requested Viewlet, if any.
     *
     * @return class name of the requested Viewlet
     */
    public String getRequestedViewletClass()
    {
        if( theRequestedViewletClass == null ) {
            String format = theSaneRequest.getArgument( "lid-format" );
            final String tag = "viewlet:";
            if( format != null && format.startsWith( tag )) {
                theRequestedViewletClass = format.substring( tag.length() );
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
     * The underlying SaneRequest.
     */
    protected SaneRequest theSaneRequest;

    /**
     * The context path of the web application.
     */
    protected String theContextPath;

    /**
     * The calculated absolute context path.
     */
    protected String theAbsoluteContextPath;
    
    /**
     * The requested Viewlet class, if any. (buffered)
     */
    protected String theRequestedViewletClass = null;

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
}
