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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util.http;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple URL class meeting the SaneUrl interface.
 */
public class SimpleSaneUrl
        extends
            AbstractSaneUrl
{
    /**
     * Factory method.
     *
     * @param s the URL in String form
     * @param contextPath the URL of the application's web context
     * @return the created SimpleSaneUrl
     * @throws MalformedURLException thrown if the URL is malformed
     */
    public static SimpleSaneUrl create(
            String s,
            String contextPath )
        throws
            MalformedURLException
    {
        Matcher m = ABSOLUTE_URL_PATTERN.matcher( s );
        if( m.matches() ) {
            String protocol    = m.group( 1 );
            String server      = m.group( 2 );
            String port        = m.group( 4 );
            String file        = m.group( 5 );
            String queryString = m.group( 7 );

            int realPort;
            if( port != null && port.length() > 0 ) {
                realPort = Integer.parseInt( port );
            } else {
                if( "http".equals( protocol )) {
                    realPort = 80;
                } else if( "https".equals( protocol )) {
                    realPort = 443;
                } else {
                    realPort = -1; // don't know
                }
            }
            String serverPlusNonDefaultPort = constructServerPlusNonDefaultPort( server, protocol, realPort );

            HashMap<String,String[]> urlArguments = new HashMap<String,String[]>();
            addUrlEncodedArguments( queryString, urlArguments );

            SimpleSaneUrl ret = new SimpleSaneUrl(
                    protocol,
                    server,
                    realPort,
                    serverPlusNonDefaultPort,
                    file,
                    queryString,
                    urlArguments,
                    contextPath );
            
            return ret;
        }

        // try relative instead -- for that, we first parse the context path
        m = ABSOLUTE_URL_PATTERN.matcher( contextPath );
        if( !m.matches() ) {
            throw new MalformedURLException( "Context path malformed: " + contextPath );
        }
        String protocol        = m.group( 1 );
        String server          = m.group( 2 );
        String port            = m.group( 4 );
        String relativeContext = m.group( 5 );

        int realPort;
        if( port != null && port.length() > 0 ) {
            realPort = Integer.parseInt( port );
        } else {
            if( "http".equals( protocol )) {
                realPort = 80;
            } else if( "https".equals( protocol )) {
                realPort = 443;
            } else {
                realPort = -1; // don't know
            }
        }
        String serverPlusNonDefaultPort = constructServerPlusNonDefaultPort( server, protocol, realPort );

        m = RELATIVE_URL_PATTERN.matcher( s );
        if( m.matches() ) {
            String file        = m.group( 1 );
            String queryString = m.group( 3 );

            HashMap<String,String[]> urlArguments = new HashMap<String,String[]>();
            addUrlEncodedArguments( queryString, urlArguments );

            SimpleSaneUrl ret = new SimpleSaneUrl(
                    protocol,
                    server,
                    realPort,
                    serverPlusNonDefaultPort,
                    file,
                    queryString,
                    urlArguments,
                    relativeContext ); // relative context path

            return ret;
        }

        // didn't work out
        throw new MalformedURLException( s );
    }

    /**
     * Private constructor, for subclasses only.
     *
     * @param protocol http or https
     * @param server the server
     * @param port the server port
     * @param serverPlusNonDefaultPort the server, plus, if the port is non-default, a colon and the port number
     * @param relativeBaseUri the relative base URI
     * @param queryString the string past the ? in the URL
     * @param urlArguments the arguments given in the URL, if any
     * @param contextPath the JEE context path
     */
    protected SimpleSaneUrl(
            String                 protocol,
            String                 server,
            int                    port,
            String                 serverPlusNonDefaultPort,
            String                 relativeBaseUri,
            String                 queryString,
            Map<String,String[]>   urlArguments,
            String                 contextPath )
    {
        super( protocol, server, port, serverPlusNonDefaultPort, relativeBaseUri, queryString, urlArguments, contextPath );
    }

    /**
     * The Pattern we use to parse relative URLs. This is quite lenient, which may be a bug or a feature, not sure.
     */
    public static final Pattern RELATIVE_URL_PATTERN = Pattern.compile( "^(/[^\\?#]*)?(\\?([^#]*))?(#(.*))?$" );

    /**
     * The Pattern we use to parse absolute URLs. This is quite lenient, which may be a bug or a feature, not sure.
     */
    public static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile( "^([^:/]+)://([^:/]+)(:(\\d+))?(/[^\\?#]*)?(\\?([^#]*))?(#(.*))?$" );
}
