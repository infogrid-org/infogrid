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
// Copyright 1998-2009 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.rest.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.rest.AbstractRestfulRequest;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseNameServer;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.text.StringRepresentationParseException;

/**
 * Default implementation of Net RestfulRequest.
 */
public class DefaultNetRestfulRequest
        extends
            AbstractRestfulRequest
        implements
            NetRestfulRequest
{
    /**
     * Factory method.
     * 
     * @param lidRequest the incoming request
     * @param contextPath the application's JEE context path
     * @param defaultMeshBaseIdentifier the identifier, in String form, of the default MeshBase
     * @return the created DefaultNetRestfulRequest
     */
    public static DefaultNetRestfulRequest create(
            SaneRequest lidRequest,
            String      contextPath,
            String      defaultMeshBaseIdentifier )
    {
        NetMeshBaseIdentifierFactory meshBaseIdentifierFactory
                = InfoGridWebApp.getSingleton().getApplicationContext().findContextObject( NetMeshBaseIdentifierFactory.class );
        
        if( meshBaseIdentifierFactory == null ) {
            meshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create();
        }
        return new DefaultNetRestfulRequest( lidRequest, contextPath, defaultMeshBaseIdentifier, meshBaseIdentifierFactory );
    }

    /**
     * Constructor.
     * 
     * @param lidRequest the incoming request
     * @param contextPath the application's JEE context path
     * @param defaultMeshBaseIdentifier the identifier, in String form, of the default MeshBase
     * @param meshBaseIdentifierFactory the factory for MeshBaseIdentifiers if any are specified in the request
     */
    protected DefaultNetRestfulRequest(
            SaneRequest                  lidRequest,
            String                       contextPath,
            String                       defaultMeshBaseIdentifier,
            NetMeshBaseIdentifierFactory meshBaseIdentifierFactory )
    {
        super( lidRequest, contextPath );
        
        theDefaultMeshBaseIdentifier = defaultMeshBaseIdentifier;
        theMeshBaseIdentifierFactory = meshBaseIdentifierFactory;
    }

    /**
     * Internal method to calculate the data.
     * 
     * @throws MeshObjectAccessException thrown if a MeshObject could not be accessed
     * @throws NotPermittedException thrown if the caller was not permitted to perform this operation
     * @throws StringRepresentationParseException thrown if the MeshBaseIdentifier passed into the constructor could not be parsed
     */
    protected void calculate()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                StringRepresentationParseException
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
            theRequestedMeshBaseIdentifier = theMeshBaseIdentifierFactory.guessFromExternalForm( meshBaseIdentifierString );

            @SuppressWarnings( "unchecked" )
            NetMeshBaseNameServer<NetMeshBaseIdentifier,NetMeshBase> meshBaseNameServer
                    = InfoGridWebApp.getSingleton().getApplicationContext().findContextObjectOrThrow( 
                            NetMeshBaseNameServer.class );

            NetMeshBase mb = meshBaseNameServer.get( (NetMeshBaseIdentifier) theRequestedMeshBaseIdentifier );

            if( mb == null ) {
                throw new StringRepresentationParseException( meshBaseIdentifierString, null, null );
            }
            
            theRequestedMeshObjectIdentifier = mb.getMeshObjectIdentifierFactory().fromExternalForm( meshObjectIdentifierString );
            theRequestedMeshObject           = mb.accessLocally( theRequestedMeshObjectIdentifier );

            if( proxyIdentifierString != null ) {
                theRequestedProxyIdentifier = theMeshBaseIdentifierFactory.guessFromExternalForm( proxyIdentifierString );
                theRequestedProxy           = mb.getProxyFor( theRequestedProxyIdentifier );
            }
            
        } else {
            throw new IllegalArgumentException( "Cannot process incoming relative URI " + relativeBaseUrl + " that is outside of context path " + theContextPath );
        }
    }

    /**
     * Determine the identifier of the requested Proxy, if any.
     * 
     * @return the NetMeshBaseIdentifier
     * @throws MeshObjectAccessException thrown if a MeshObject could not be accessed
     * @throws NotPermittedException thrown if the caller was not permitted to perform this operation
     * @throws StringRepresentationParseException thrown if the MeshBaseIdentifier passed into the constructor could not be parsed
     */
    public NetMeshBaseIdentifier determineRequestedProxyIdentifier()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                StringRepresentationParseException
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
     * @throws MeshObjectAccessException thrown if a MeshObject could not be accessed
     * @throws NotPermittedException thrown if the caller was not permitted to perform this operation
     * @throws StringRepresentationParseException thrown if the MeshBaseIdentifier passed into the constructor could not be parsed
     */
    public Proxy determineRequestedProxy()
            throws
                MeshObjectAccessException,
                NotPermittedException,
                StringRepresentationParseException
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
     * The factory for MeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory;

    /**
     * The requested Proxy, if any.
     */
    protected Proxy theRequestedProxy;

    /**
     * The pattern for URL parsing after the context path.
     */
    protected static final Pattern theUrlPattern = Pattern.compile( "^(\\[meshbase=([^\\]]*)\\])?(\\[proxy=([^\\]]*)\\])?(.*)$" );
}
