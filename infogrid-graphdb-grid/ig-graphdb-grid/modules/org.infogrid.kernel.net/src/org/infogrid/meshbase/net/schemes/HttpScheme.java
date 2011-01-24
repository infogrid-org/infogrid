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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
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
        this(   "http",
                Pattern.compile( "((?i:http://[a-z0-9](?:[a-z0-9\\-.]*[a-z0-9])?))(?::\\d+)?/\\S*" ));
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
            String actual = matchesStrictly( context, candidate );
            if( actual != null ) {
                actual = stripDirectoryPaths( removeUnnecessaryPort( actual ));
                return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
            }
            if( candidate.indexOf( "://" ) >= 0 ) {
                String tryThis = appendSlashIfNeeded( candidate );
                if( tryThis.equals( candidate )) {
                    return null; // didn't work, it's a different scheme
                }
                actual = matchesStrictly( context, tryThis );
                if( actual != null ) {
                    actual = stripDirectoryPaths( removeUnnecessaryPort( actual ));

                    return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
                }
            } else {
                String tryThis = theName + "://" + candidate;
                actual = matchesStrictly( context, tryThis );
                if( actual != null ) {
                    actual = stripDirectoryPaths( removeUnnecessaryPort( actual ));

                    return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
                }
                String tryThis2 = appendSlashIfNeeded( tryThis );
                if( tryThis2.equals( tryThis )) {
                    return null; // didn't work, has a slash already, must be something else
                }

                actual = matchesStrictly( context, tryThis2 );
                if( actual != null ) {
                    actual = stripDirectoryPaths( removeUnnecessaryPort( actual ));

                    return new NetMeshBaseIdentifier( fact, actual, new URI( actual ), candidate, true );
                }
            }

        } catch( URISyntaxException ex ) {
            if( log.isDebugEnabled() ) {
                log.debug( ex );
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
     * Helper method to append a slash if needed.
     *
     * @param candidate the candidate String
     * @return the candidate, with or without appended slash
     */
    protected String appendSlashIfNeeded(
            String candidate )
    {
        // we know that candidate starts with http:// or https://
        String ret;
        int doubleSlashes = candidate.indexOf( "//" );
        if( candidate.indexOf( '/', doubleSlashes+2 ) == -1 ) {
            // doesn't have a single slash, append one
            ret  = candidate + "/";
        } else {
            ret = candidate;
        }
        return ret;
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
}
