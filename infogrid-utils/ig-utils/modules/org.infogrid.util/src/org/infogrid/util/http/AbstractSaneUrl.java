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

package org.infogrid.util.http;

import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;

/**
 * Factors out functionality common to many implementations of SaneUrl.
 */
public abstract class AbstractSaneUrl
        implements
            SaneUrl
{
    private static final Log log = Log.getLogInstance( AbstractSaneUrl.class ); // our own, private logger

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
    protected AbstractSaneUrl(
            String                 protocol,
            String                 server,
            int                    port,
            String                 serverPlusNonDefaultPort,
            String                 relativeBaseUri,
            String                 queryString,
            Map<String,String[]>   urlArguments,
            String                 contextPath )
    {
        theProtocol                 = protocol;
        theServer                   = server;
        thePort                     = port;
        theServerPlusNonDefaultPort = serverPlusNonDefaultPort;
        theRelativeBaseUri          = relativeBaseUri;
        theQueryString              = queryString;
        theUrlArguments             = urlArguments;
        theContextPath              = contextPath;
    }

    /**
     * Helper method to construct the host-port combination if non-standard ports.
     *
     * @param server just the server
     * @param protocol the protocol, e.g. http
     * @param port the port number
     * @return the server-port combination
     */
    protected static String constructServerPlusNonDefaultPort(
            String server,
            String protocol,
            int    port )
    {
        if( server == null ) {
            return null;
        }

        String httpHost = server;
        if( "http".equals( protocol )) {
            if( port != 80 ) {
                httpHost += ":" + port;
            }
        } else if( "https".equals( protocol )) {
            if( port != 443 ) {
                httpHost += ":" + port;
            }
        } else {
            httpHost += ":" + port;
        }
        return httpHost;
    }

    /**
     * Helper to parse URL-encoded name-value pairs, and put them in the right places.
     *
     * @param data the data to add
     * @param arguments the URL arguments in the process of being assembled
     */
    protected static void addUrlEncodedArguments(
            String               data,
            Map<String,String[]> arguments )
    {
        if( data == null || data.length() == 0 ) {
            return;
        }
        if( data.charAt( 0 ) == '?' ) {
            data = data.substring( 1 );
        }

        StringTokenizer pairTokenizer = new StringTokenizer( data, "&" );
        while( pairTokenizer.hasMoreTokens() ) {
            String    pair     = pairTokenizer.nextToken();
            String [] keyValue = pair.split( "=", 2 );

            String key   = HTTP.decodeUrlArgument( keyValue[0] );
            String value = HTTP.decodeUrlArgument( keyValue.length == 2 ? keyValue[1] : "" ); // reasonable default?

            String [] haveAlready = arguments.get( key );
            String [] newValue;
            if( haveAlready == null ) {
                newValue = new String[] { value };
            } else {
                newValue = ArrayHelper.append( haveAlready, value, String.class );
            }
            arguments.put( key, newValue );
        }
    }

    /**
     * Obtain the requested root URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123</code> (no trailing slash).
     *
     * @return the requested root URI
     */
    public String getRootUri()
    {
        if( theRootUri == null ) {
            StringBuilder buf = new StringBuilder( 64 );
            buf.append( getProtocol());
            buf.append( "://" );
            buf.append( getServerPlusNonDefaultPort());
            theRootUri = buf.toString();
        }
        return theRootUri;
    }

    /**
     * Determine the requested, relative base URI.
     * In a request for URL <code>http://example.com:123/foo/bar?abc=def</code>,
     * that would be <code>/foo/bar</code>.
     *
     * @return the requested base URI
     */
    public String getRelativeBaseUri()
    {
        return theRelativeBaseUri;
    }

    /**
     * Determine the requested, absolute base URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123/foo/bar</code>.
     *
     * @return the requested absolute base URI
     */
    public String getAbsoluteBaseUri()
    {
        if( theAbsoluteBaseUri == null ) {
            theAbsoluteBaseUri = getRootUri() + getRelativeBaseUri();
        }
        return theAbsoluteBaseUri;
    }

    /**
     * Determine the requested, contextual base URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>,
     * if the context is <code>/foo</code>,
     * that would be <code>/bar</code>.
     *
     * @return the requested absolute base URI
     */
    public String getContextualBaseUri()
    {
        String abs = getAbsoluteBaseUri();
        String ctx = getAbsoluteContextUri();

        if( !abs.startsWith( ctx ) ) {
            log.error( "Absolute URI does not start with context URI", abs, ctx );
            return abs;
        }
        return abs.substring( ctx.length() );
    }

    /**
     * Determine the requested, relative full URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>/foo/bar?abc=def</code>.
     *
     * @return the requested relative full URI
     */
    public String getRelativeFullUri()
    {
        if( theQueryString != null && theQueryString.length() != 0 ) {
            return theRelativeBaseUri + "?" + theQueryString;
        } else {
            return theRelativeBaseUri;
        }
    }

    /**
     * Determine the requested, absolute full URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123/foo/bar?abc=def</code>.
     *
     * @return the requested absolute full URI
     */
    public String getAbsoluteFullUri()
    {
        if( theAbsoluteFullUri == null ) {
            theAbsoluteFullUri = getRootUri() + getRelativeFullUri();
        }
        return theAbsoluteFullUri;
    }

    /**
     * Determine the requested, contextual full URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>,
     * if the context is <code>/foo</code>,
     * that would be <code>/bar?abc=def</code>.
     *
     * @return the requested contextual full URI
     */
    public String getContextualFullUri()
    {
        String abs = getAbsoluteFullUri();
        String ctx = getAbsoluteContextUri();

        if( !abs.startsWith( ctx ) ) {
            log.error( "Absolute URI does not start with context URI", abs, ctx );
            return abs;
        }
        return abs.substring( ctx.length() );
    }

    /**
     * Get the name of the server.
     *
     * @return the name of the server
     */
    public String getServer()
    {
        return theServer;
    }

    /**
     * Get the name of the server, plus non-default port
     *
     * @return the name of the server, plus non-default port
     */
    public String getServerPlusNonDefaultPort()
    {
        return theServerPlusNonDefaultPort;
    }

    /**
     * Get the port at which this request arrived.
     *
     * @return the port at which this request arrived
     */
    public int getPort()
    {
        return thePort;
    }

    /**
     * Get the protocol, i.e. <code>http</code> or <code>https</code>.
     *
     * @return <code>http</code> or <code>https</code>
     */
    public String getProtocol()
    {
        return theProtocol;
    }

    /**
     * Obtain all values of a multi-valued argument given in the URL.
     *
     * @param argName name of the argument
     * @return the values, or <code>null</code>
     */
    public String [] getMultivaluedUrlArgument(
            String argName )
    {
        String [] ret = theUrlArguments.get( argName );
        return ret;
    }

    /**
     * Obtain the value of a named argument given in the URL, or null.
     * If more than one argument is given by this name,
     * this will throw an IllegalStateException.
     *
     * @param name the name of the argument
     * @return the value of the argument with name name
     */
    public final String getUrlArgument(
            String name )
    {
        String [] almost = getMultivaluedUrlArgument( name );
        if( almost == null || almost.length == 0 ) {
            return null;
        } else if( almost.length == 1 ) {
            return almost[0];
        }
        // let it pass if all of them have the same value
        boolean letPass = true;
        String firstValue = almost[0];
        for( int i=1 ; i<almost.length ; ++i ) {
            if( firstValue == null ) {
                if( almost[i] != null ) {
                    letPass = false;
                    break;
                }
            } else if( !firstValue.equals( almost[i] )) {
                letPass = false;
                break;
            }
        }

        if( !letPass ) {
            throw new IllegalStateException( "Argument " + name + " has " + almost.length + " values" );
        } else {
            log.warn( "Multiple arguments but with same value: " + name + " -> " + firstValue );
        }
        return firstValue;
    }

    /**
     * Obtain the value of a named argument provided in the URL, or null.
     * If more than one argument is given by this name,
     * return the first one.
     *
     * @param name the name of the argument
     * @return the value of the argument with name name
     */
    public String getFirstUrlArgument(
            String name )
    {
        String [] almost = getMultivaluedUrlArgument( name );
        if( almost == null || almost.length == 0 ) {
            return null;
        } else {
            return almost[0];
        }
    }

    /**
     * Obtain all arguments of this request given in the URL.
     *
     * @return a Map of name to value mappings for all arguments
     */
    public Map<String,String[]> getUrlArguments()
    {
        return Collections.unmodifiableMap( theUrlArguments );
    }

    /**
     * Determine whether a named argument provided in the URL  has the given value.
     * This method is useful in case several arguments have been given with the same name.
     *
     * @param name the name of the argument
     * @param value the desired value of the argument
     * @return true if the request contains an argument with this name and value
     */
    public boolean matchUrlArgument(
            String name,
            String value )
    {
        String [] found = getMultivaluedUrlArgument( name );
        if( found == null ) {
            return false;
        }
        for( String current : found ) {
            if( value.equals( current )) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the relative context Uri of this application.
     *
     * @return the relative context URI
     */
    public String getContextPath()
    {
        return theContextPath;
    }

    /**
     * Obtain the absolute context Uri of this application.
     *
     * @return the absolute context URI
     */
    public String getAbsoluteContextUri()
    {
        if( theAbsoluteContextUri == null ) {
            StringBuilder buf = new StringBuilder();
            buf.append( getRootUri() );
            buf.append( theContextPath );
            theAbsoluteContextUri = buf.toString();
        }
        return theAbsoluteContextUri;
    }

    /**
     * Obtain the relative context Uri of this application with a trailing slash.
     *
     * @return the relative context URI with a trailing slash
     */
    public String getContextPathWithSlash()
    {
        return getContextPath() + "/";
    }

    /**
     * Obtain the absolute context Uri of this application with a trailing slash.
     *
     * @return the absolute context URI with a trailing slash.
     */
    public String getAbsoluteContextUriWithSlash()
    {
        return getAbsoluteContextUri() + "/";
    }

    /**
     * Obtain the query string, if any.
     *
     * @return the query string
     */
    public String getQueryString()
    {
        return theQueryString;
    }

    /**
     * Return this absolute full URL but with all URL arguments stripped whose names meet at least
     * one of the provided Patterns.
     * For example, http://example.com/?abc=def&abcd=ef&abcde=f&x=y would become http://example.com?abc=def&x=y
     * if invoked with Pattern "^abcd.*$".
     *
     * @param patterns the Patterns
     * @return the absolute full URL without the matched URL arguments
     */
    public String getAbsoluteFullUriWithoutMatchingArguments(
            Pattern [] patterns )
    {
        String        in  = getAbsoluteFullUri();

        return urlWithoutMatchingArguments( in, patterns );
    }

    /**
     * Helper method to remove all URL arguments whose names meet at least one of the provided Patterns.
     *
     * @param in the original String, interpreted as a URL
     * @param patterns the Patterns
     * @return the String without the removed URL arguments
     */
    public static String urlWithoutMatchingArguments(
            String     in,
            Pattern [] patterns )
    {
        StringBuilder ret = new StringBuilder( in.length() );

        int index = in.indexOf( '?' );
        if( index < 0 ) {
            return in;
        }
        ret.append( in.substring( 0, index ));
        char sep = '?';
        String [] pairs = in.substring( index+1 ).split( "&" );

        outer:
        for( int i=0 ; i<pairs.length ; ++i ) {
            int equals = pairs[i].indexOf( '=' );
            String name;
            if( equals >= 0 ) {
                name = pairs[i].substring( 0, equals );
            } else {
                name = pairs[i];
            }
            name = HTTP.decodeUrlArgument( name );

            for( int j=0 ; j<patterns.length ; ++j ) {
                Matcher m = patterns[j].matcher( name );
                if( m.matches() ) {
                    continue outer;
                }
            }
            // did not match
            ret.append( sep );
            ret.append( pairs[i] );
            sep = '&';
        }
        return ret.toString();
    }

    /**
     * The http or https protocol.
     */
    protected String theProtocol;

    /**
     * The server.
     */
    protected String theServer;

    /**
     * The port.
     */
    protected int thePort;

    /**
     * Same as the server, except if a non-default port number is used, in which case it is
     * server + ":" + thePort.
     */
    protected String theServerPlusNonDefaultPort;

    /**
     * The relative base URI of the Request.
     */
    protected String theRelativeBaseUri;

    /**
     * The root URI of the Request.
     */
    private String theRootUri;

    /**
     * The absolute base URI of the Request.
     */
    private String theAbsoluteBaseUri;

    /**
     * The absolute full URI of the Request.
     */
    private String theAbsoluteFullUri;

    /**
     * The query String, if any.
     */
    protected String theQueryString;

    /**
     * The arguments to the Request provided as part of the URL, mapping from argument name to argument value.
     */
    protected Map<String,String[]> theUrlArguments;

    /**
     * The full absolute context URI.
     */
    protected String theAbsoluteContextUri;

    /**
     * The relative Jee context path for this server.
     */
    protected String theContextPath;
}
