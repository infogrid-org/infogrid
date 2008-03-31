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

import org.infogrid.jee.app.InfoGridWebApp;

import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshObjectAccessException;

import org.infogrid.util.NameServer;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.jee.sane.SaneServletRequest;

/**
 * Default implementation of RestfulRequest.
 */
public class DefaultRestfulRequest
        extends
            AbstractRestfulRequest
{
    private static final Log log = Log.getLogInstance( DefaultRestfulRequest.class ); // our own, private logger

    /**
     * Factory method.
     */
    public static DefaultRestfulRequest create(
            SaneServletRequest lidRequest,
            String             contextPath )
    {
        return new DefaultRestfulRequest( lidRequest, contextPath );
    }

    /**
     * Constructor.
     */
    protected DefaultRestfulRequest(
            SaneServletRequest lidRequest,
            String             contextPath )
    {
        super( lidRequest, contextPath );
    }

    /**
     * Internal method to calculate the data.
     */
    protected void calculate()
            throws
                MeshObjectAccessException,
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
            String meshObjectIdentifierString;
            
            String fallBackMeshBaseIdentifierString = theSaneRequest.getRootUri() + theContextPath + "/"; // does not have a slash at the end

            Matcher m = theUrlPattern.matcher( trailer );
            if( m.matches() ) {
                meshBaseIdentifierString   = m.group( 2 );
                meshObjectIdentifierString = m.group( 3 );

            } else {
                meshBaseIdentifierString   = null;
                meshObjectIdentifierString = trailer;
            }
            if( meshBaseIdentifierString == null ) {
                meshBaseIdentifierString = fallBackMeshBaseIdentifierString;
            }
            if( meshObjectIdentifierString == null ) {
                meshObjectIdentifierString = "";
            }
            theRequestedMeshBaseIdentifier = MeshBaseIdentifier.create( meshBaseIdentifierString );
            
            NameServer<MeshBaseIdentifier,MeshBase> meshBaseNameServer = InfoGridWebApp.getSingleton().getMeshBaseNameServer();
            
            MeshBase mb = meshBaseNameServer.get( theRequestedMeshBaseIdentifier );
            if( mb == null && !fallBackMeshBaseIdentifierString.equals( meshBaseIdentifierString )) {
                log.warn( "Could not find requested MeshBase: " + theRequestedMeshBaseIdentifier );

                MeshBaseIdentifier fallBackMeshBaseIdentifier = MeshBaseIdentifier.create( fallBackMeshBaseIdentifierString );

                mb = meshBaseNameServer.get( fallBackMeshBaseIdentifier );
            }

            if( mb == null ) {
                throw new URISyntaxException( meshBaseIdentifierString, "Cannot find default MeshBase at " + fallBackMeshBaseIdentifierString );
            }
            
            theRequestedMeshObjectIdentifier = mb.getMeshObjectIdentifierFactory().fromExternalForm( meshObjectIdentifierString );
            theRequestedMeshObject           = mb.accessLocally( theRequestedMeshObjectIdentifier );
            
        } else {
            throw new IllegalArgumentException( "Cannot process incoming relative URI " + relativeBaseUrl + " that is outside of context path " + theContextPath );
        }
    }
    
    /**
     * The pattern for URL parsing after the context path.
     */
    protected static final Pattern theUrlPattern = Pattern.compile( "^(\\[meshbase=([^\\]]*)\\])?(.*)$" );
}
