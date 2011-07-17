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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.meshbase.net.schemes;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

/**
 * Represents HTTP for the DefaultNetMeshBaseIdentifierFactory.
 */
public class HttpScheme
        extends
            AbstractRegexScheme
        implements
            Scheme
{
    private static final Log log = Log.getLogInstance( HttpScheme.class ); // our own, private logger

    /**
     * Constructor.
     */
    public HttpScheme()
    {
        this("http", HTTP_PATTERN );
    }

    /**
     * Constructor for subclasses only.
     *
     * @param protocolName the name of the protocol, i.e. http or https
     * @param regex the pattern to check strictly
     */
    protected HttpScheme(
            String  protocolName,
            Pattern regex )
    {
        super( protocolName, regex );
    }

    /**
     * Attempt to convert this candidate identifier String into an identifier with this
     * scheme, taking creative license if needed. If successful, return the identifier,
     * null otherwise.
     *
     * @param context the identifier root that forms the context
     * @param candidate the candidate identifier
     * @param fact the NetMeshBaseIdentifierFactory on whose behalf we create this NetMeshBaseIdentifier
     * @return the successfully created identifier, or null otherwise
     */
    public NetMeshBaseIdentifier guessAndCreate(
            String                       context,
            String                       candidate,
            NetMeshBaseIdentifierFactory fact )
    {
        try {
            int colonDoubleSlash = candidate.indexOf( "://" );
            if( colonDoubleSlash >= 0 && candidate.startsWith( theName ) && colonDoubleSlash == theName.length() ) {
                NetMeshBaseIdentifier ret = tryThis( context, candidate, fact );
                return ret; // may or may not be null

            } else {
                String tryThis = theName + "://" + candidate;

                NetMeshBaseIdentifier ret = tryThis( context, tryThis, fact );
                return ret; // may or may not be null
            }

        } catch( URISyntaxException ex ) {
            if( log.isDebugEnabled() ) {
                log.debug( ex );
            }
        }
        return null;
    }

    /**
     * Determine whether this Scheme is restful.
     *
     * @return true if the Scheme is restful
     */
    public boolean isRestful()
    {
        return true;
    }

    /**
     * Helper method to try one attempt.
     *
     * @param context the identifier root that forms the context
     * @param candidate the candidate identifier
     * @param fact the NetMeshBaseIdentifierFactory on whose behalf we create this NetMeshBaseIdentifier
     * @return the successfully created identifier, or null otherwise
     * @throws URISyntaxException thrown if the candidate could not be parsed
     */
    protected NetMeshBaseIdentifier tryThis(
            String                       context,
            String                       candidate,
            NetMeshBaseIdentifierFactory fact )
        throws
            URISyntaxException
    {
        NetMeshBaseIdentifier ret = strictlyMatchAndCreate( context, candidate, fact );
        if( ret != null ) {
            return ret;
        }

        String tryThis = appendSlashEscapeIfNeeded( candidate );
        if( !tryThis.equals( candidate )) {
            ret = strictlyMatchAndCreate( context, tryThis, fact );
            if( ret != null ) {
                return ret;
            }
        }
        return null;
    }

    /**
     * Helper method to remove an unnecessary port specification.
     *
     * @param candidate the candidate String
     * @return the candidate, with any unnecessary port specification removed
     */
    protected String removeUnnecessaryPort(
            String candidate )
    {
        String ret;

        Matcher m = thePort80Pattern.matcher( candidate );
        if( m.matches() ) {
            ret = m.group( 1 ) + m.group( 2 );
        } else {
            ret = candidate;
        }
        return ret;
    }

    /**
     * Helper method to append a slash if needed, or escape characters after the slash.
     *
     * @param candidate the candidate String
     * @return the candidate, with or without appended slash, and with or without escaped characters
     */
    protected String appendSlashEscapeIfNeeded(
            String candidate )
    {
        // we know that candidate starts with http:// or https://
        String ret;
        int doubleSlashes = candidate.indexOf( "//" );
        int singleSlash   = candidate.indexOf( '/', doubleSlashes+2 );

        if( singleSlash == -1 ) {
            // doesn't have a single slash, append one
            ret  = candidate + "/";
        } else if( singleSlash == candidate.length()-1 ) {
            // it's the end
            ret = candidate;
        } else {
            ret = candidate.substring( 0, singleSlash+1 ) + HTTP.encodeToValidUrl( candidate.substring( singleSlash+1 ));
        }
        return ret;
    }


    /**
     * Convert a valid candidate to its canonical form. For example, remove redundant "foo/../"
     *
     * @param candidate the valid candidate
     * @return the canonical form
     */
    @Override
    protected String canonisize(
            String candidate )
    {
        return stripDirectoryPaths( removeUnnecessaryPort( candidate ));
    }

    /**
     * Helper method to remove .. and . from paths.
     *
     * @param in the input String
     * @return the output String
     */
    protected static String stripDirectoryPaths(
            String in )
    {
        return in; // FIXME
    }

    /**
     * The pattern that allows us to remove a unnecessary port 80 from a URL spec.
     */
    public static final Pattern thePort80Pattern = Pattern.compile(
            "^(http://[^/:]+):80(/.*)$" );

    /**
     * The Pattern that we use for this scheme.
     */
    public static final Pattern HTTP_PATTERN = Pattern.compile( "((?i:http://[a-z0-9](?:[a-z0-9\\-.]*[a-z0-9])?))(?::\\d+)?/[a-zA-Z0-9*\\$\\-_@.&+!*\"'(),%?=;/]*" );
}
