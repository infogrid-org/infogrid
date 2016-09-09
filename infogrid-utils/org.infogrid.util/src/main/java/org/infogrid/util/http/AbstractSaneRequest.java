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
// Copyright 1998-2015 by Johannes Ernst
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
     * {@inheritDoc}
     */
    @Override
    public SaneRequest getSaneRequestAtProxy()
    {
        return theRequestAtProxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SaneRequest getOriginalSaneRequest()
    {
        if( theRequestAtProxy == null ) {
            return this;
        } else {
            return theRequestAtProxy.getOriginalSaneRequest();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public IncomingSaneCookie getCookie(
            String name )
    {
        IncomingSaneCookie [] cookies = getSaneCookies();
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public boolean isSafePost()
    {
        return theIsSafe != null && theIsSafe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnsafePost()
    {
        return theIsSafe != null && !theIsSafe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mayBeSafeOrUnsafePost()
    {
        return theIsSafe == null;
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
     * Flag that indicates whether the request is a safe HTTP POST request
     * (true), an unsafe one (false) or undetermined (null). This is not
     * set in this class and thus always remains at null unless a subclass
     * sets it.
     */
    protected Boolean theIsSafe;

    /**
     * Name of the cookie that might contain Accept-Language information.
     */
    public static final String ACCEPT_LANGUAGE_COOKIE_NAME = "Accept-Language";

    /**
     * Name of the HTTP Header that specifies the acceptable MIME types.
     */
    protected static final String ACCEPT_HEADER = "Accept";
}
