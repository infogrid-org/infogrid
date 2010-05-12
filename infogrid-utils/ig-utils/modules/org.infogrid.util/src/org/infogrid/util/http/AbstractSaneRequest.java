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

import java.util.Map;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;

/**
 * Factors out functionality common to many implementations of SaneRequest.
 */
public abstract class AbstractSaneRequest
        extends
            AbstractSaneUrl
        implements
            SaneRequest
{
    private static final Log log = Log.getLogInstance( AbstractSaneRequest.class ); // our own, private logger

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
     * @param requestAtProxy the SaneRequest received by the reverse proxy, if any
     */
    protected AbstractSaneRequest(
            String                 protocol,
            String                 server,
            int                    port,
            String                 serverPlusNonDefaultPort,
            String                 relativeBaseUri,
            String                 queryString,
            Map<String,String[]>   urlArguments,
            String                 contextPath,
            SaneRequest            requestAtProxy )
    {
        super( protocol, server, port, serverPlusNonDefaultPort, relativeBaseUri, queryString, urlArguments, contextPath );

        theRequestAtProxy = requestAtProxy;
    }

    /**
     * If this request was obtained by way of a reverse proxy, return the SaneRequest
     * that the reverse proxy received. Returns null if no reverse proxy was involved.
     *
     * @return the SaneRequest at the reverse proxy, or null if none
     */
    public SaneRequest getSaneRequestAtProxy()
    {
        return theRequestAtProxy;
    }

    /**
     * Obtain the original request as originally issued by the HTTP client. If a reverse
     * proxy was involved, return the SaneRequest that the reverse proxy received. If
     * no reverse proxy was involved, return this SaneRequest.
     *
     * @return the ultimate SaneRequest
     */
    public SaneRequest getOriginalSaneRequest()
    {
        if( theRequestAtProxy == null ) {
            return this;
        } else {
            return theRequestAtProxy.getOriginalSaneRequest();
        }
    }

    /**
     * Obtain a POST'd argument. If more than one argument is given by this name,
     * this will throw an IllegalStateException.
     *
     * @param argName name of the argument
     * @return value.
     */
    public final String getPostedArgument(
            String argName )
    {
        String [] almost = getMultivaluedPostedArgument( argName );
        if( almost == null || almost.length == 0 ) {
            return null;
        } else if( almost.length == 1 ) {
            return almost[0];
        } else {
            throw new IllegalStateException( "POST argument '" + argName + "' posted more than once: " + ArrayHelper.join( almost ));
        }
    }

    /**
     * Determine whether a named POST'd argument has the given value.
     * This method is useful in case several arguments have been given with the same name.
     *
     * @param name the name of the argument
     * @param value the desired value of the argument
     * @return true if the request contains an argument with this name and value
     */
    public boolean matchPostedArgument(
            String name,
            String value )
    {
        String [] found = getMultivaluedPostedArgument( name );
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
     * Obtain a named cookie, or null if not present.
     *
     * @param name the name of the cookie
     * @return the named cookie, or null
     */
    public IncomingSaneCookie getCookie(
            String name )
    {
        IncomingSaneCookie [] cookies = getCookies();
        if( cookies != null ) {
            for( int i=0 ; i<cookies.length ; ++i ) {
                if( cookies[i].getName().equals( name )) {
                    return cookies[i];
                }
            }
        }
        return null;
    }

    /**
     * Obtain the value of a named cookie, or null if not present.
     *
     * @param name the name of the cookie
     * @return the value of the named cookie, or null
     */
    public String getCookieValue(
            String name )
    {
        SaneCookie cook = getCookie( name );
        if( cook != null ) {
            return cook.getValue();
        } else {
            return null;
        }
    }

    /**
     * Obtain a MimePart that was HTTP POST'd. If more than one MimePart was posted with this name,
     * this will throw an IllegalStateException.
     *
     * @param argName name of the argument
     * @return the MimePart or null
     */
    public MimePart getMimePart(
            String argName )
    {
        MimePart [] parts = getMultivaluedMimeParts( argName );
        if( parts == null || parts.length == 0 ) {
            return null;
        } else if( parts.length == 1 ) {
            return parts[0];
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Helper method to convert a class name into a suitable attribute name.
     *
     * @param clazz the Class
     * @return the attribute name
     */
    public static String classToAttributeName(
            Class<?> clazz )
    {
        String ret = clazz.getName();
        ret = ret.replaceAll( "\\.", "_" );
        return ret;
    }

    /**
     * Helper method to convert a class name and a local fragment into a suitable attribute name.
     *
     * @param clazz the Class
     * @param fragment the fragment, or local id
     * @return the attribute name
     */
    public static String classToAttributeName(
            Class<?> clazz,
            String   fragment )
    {
        String ret = clazz.getName();
        ret = ret.replaceAll( "\\.", "_" );
        ret = ret + "__" + fragment;
        return ret;
    }

    /**
     * The request as it was received by the reverse proxy, if any.
     */
    protected SaneRequest theRequestAtProxy;

    /**
     * Name of the cookie that might contain Accept-Language information.
     */
    public static final String ACCEPT_LANGUAGE_COOKIE_NAME = "Accept-Language";

    /**
     * Name of the HTTP Header that specifies the acceptable MIME types.
     */
    protected static final String ACCEPT_HEADER = "Accept";
}
