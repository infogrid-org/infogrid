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

package org.infogrid.jee.rest.net;

import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.AbstractRestfulRequest;
import org.infogrid.jee.sane.SaneServletRequest;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;

import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.Proxy;

import org.infogrid.util.NameServer;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.mesh.NotPermittedException;

/**
 * Default implementation of Net RestfulRequest.
 */
public class DefaultNetRestfulRequest
        extends
            AbstractRestfulRequest
        implements
            NetRestfulRequest
{
    private static final Log log = Log.getLogInstance( DefaultNetRestfulRequest.class ); // our own, private logger

    /**
     * Factory method.
     */
    public static DefaultNetRestfulRequest create(
            SaneServletRequest lidRequest,
            String             contextPath,
            String             defaultMeshBaseIdentifier )
    {
        return new DefaultNetRestfulRequest( lidRequest, contextPath,defaultMeshBaseIdentifier );
    }

    /**
     * Constructor.
     */
    protected DefaultNetRestfulRequest(
            SaneServletRequest lidRequest,
            String             contextPath,
            String             defaultMeshBaseIdentifier )
    {
        super( lidRequest, contextPath );
        
        theDefaultMeshBaseIdentifier = defaultMeshBaseIdentifier;
    }

    /**
     * Internal method to calculate the data.
     */
    protected void calculate()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                URISyntaxException
    {
        String relativeBaseUrl = theSaneRequest.getRelativeBaseUri();
        if( relativeBaseUrl.startsWith( theContextPath )) {
            String trailer = relativeBaseUrl.substring( theContextPath.length() );
            if( trailer.startsWith( "/" )) {
                trailer = trailer.substring( 1 );
            }
            trailer = HTTP.decodeUrl( trailer );

            String meshBaseIdentifierString;
            String proxyIdentifierString;
            String meshObjectIdentifierString;
            
            Matcher m = theUrlPattern.matcher( trailer );
            if( m.matches() ) {
                meshBaseIdentifierString   = m.group( 2 );
                proxyIdentifierString      = m.group( 4 );
                meshObjectIdentifierString = m.group( 5 );

            } else {
                meshBaseIdentifierString   = null;
                proxyIdentifierString      = null;
                meshObjectIdentifierString = trailer;
            }
            if( meshBaseIdentifierString == null ) {
                meshBaseIdentifierString = theDefaultMeshBaseIdentifier;
            }
            if( meshObjectIdentifierString == null ) {
                meshObjectIdentifierString = "";
            }
            theRequestedMeshBaseIdentifier = NetMeshBaseIdentifier.create( meshBaseIdentifierString );

            NameServer<MeshBaseIdentifier,MeshBase> meshBaseNameServer = InfoGridWebApp.getSingleton().getMeshBaseNameServer();

            MeshBase mb = meshBaseNameServer.get( theRequestedMeshBaseIdentifier );

            if( mb == null ) {
                throw new URISyntaxException( meshBaseIdentifierString, "Cannot find a MeshBase with this identifier" );
            }
            
            if( proxyIdentifierString != null ) {
                theRequestedProxyIdentifier = NetMeshBaseIdentifier.create( proxyIdentifierString );
                theRequestedProxy           = ((NetMeshBase)mb).getProxyFor( theRequestedProxyIdentifier );

            } else {
                theRequestedMeshObjectIdentifier = mb.getMeshObjectIdentifierFactory().fromExternalForm( meshObjectIdentifierString );
                theRequestedMeshObject           = mb.accessLocally( theRequestedMeshObjectIdentifier );
            }
            
        } else {
            throw new IllegalArgumentException( "Cannot process incoming relative URI " + relativeBaseUrl + " that is outside of context path " + theContextPath );
        }
    }

    /**
     * Determine the identifier of the requested Proxy, if any.
     * 
     * @return the NetMeshIdentifier
     */
    public NetMeshBaseIdentifier determineRequestedProxyIdentifier()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                URISyntaxException
    {
        if( theRequestedProxyIdentifier == null ) {
            calculate();
        }
        return theRequestedProxyIdentifier;
    }
    
    /**
     * Determine the requested Proxy, if any.
     * 
     * @return the Proxy
     */
    public Proxy determineRequestedProxy()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                URISyntaxException
    {
        if( theRequestedProxy == null ) {
            calculate();
        }
        return theRequestedProxy;
    }
    
    /**
     * The identifier of the default MeshBase.
     */
    protected String theDefaultMeshBaseIdentifier;

    /**
     * The identifier of the requested Proxy, if any.
     */
    protected NetMeshBaseIdentifier theRequestedProxyIdentifier;

    /**
     * The requested Proxy, if any.
     */
    protected Proxy theRequestedProxy;

    /**
     * The pattern for URL parsing after the context path.
     */
    protected static final Pattern theUrlPattern = Pattern.compile( "^(\\[meshbase=([^\\]]*)\\])?(\\[proxy=([^\\]]*)\\])?(.*)$" );
}
