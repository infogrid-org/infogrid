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
import java.util.regex.Pattern;

/**
 * A saner API for URLs that is also compatible with SaneRequest.
 */
public interface SaneUrl
{
    /**
     * Obtain the requested root URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123</code> (no trailing slash).
     *
     * @return the requested root URI
     */
    public abstract String getRootUri();

    /**
     * Determine the requested, relative base URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>/foo/bar</code>.
     *
     * @return the requested base URI
     */
    public abstract String getRelativeBaseUri();

    /**
     * Determine the requested, absolute base URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123/foo/bar</code>.
     *
     * @return the requested absolute base URI
     */
    public abstract String getAbsoluteBaseUri();

    /**
     * Determine the requested, relative full URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>/foo/bar?abc=def</code>.
     *
     * @return the requested relative full URI
     */
    public abstract String getRelativeFullUri();

    /**
     * Determine the requested, absolute full URI.
     * In a Request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>http://example.com:123/foo/bar?abc=def</code>.
     *
     * @return the requested absolute full URI
     */
    public abstract String getAbsoluteFullUri();

    /**
     * Get the name of the server.
     *
     * @return the name of the server
     */
    public abstract String getServer();

    /**
     * Get the name of the server with concatenated port if the port is a non-default port for this protocol
     *
     * @return the name of the server plus port
     */
    public abstract String getServerPlusNonDefaultPort();

    /**
     * Get the port at which this Request arrived.
     *
     * @return the port at which this Request arrived
     */
    public abstract int getPort();

    /**
     * Get the protocol, i.e. http or https.
     *
     * @return http or https
     */
    public abstract String getProtocol();

    /**
     * Obtain all values of a multi-valued argument given in the URL.
     *
     * @param argName name of the argument
     * @return value.
     */
    public abstract String [] getMultivaluedUrlArgument(
            String argName );

    /**
     * Obtain the value of a named argument provided in the URL, or null.
     * If more than one argument is given by this name,
     * this will throw an IllegalStateException.
     *
     * @param name the name of the argument
     * @return the value of the argument with name name
     */
    public abstract String getUrlArgument(
            String name );

    /**
     * Obtain the value of a named argument provided in the URL, or null.
     * If more than one argument is given by this name,
     * return the first one.
     *
     * @param name the name of the argument
     * @return the value of the argument with name name
     */
    public abstract String getFirstUrlArgument(
            String name );

    /**
     * Obtain all arguments of this Request provided in the URL.
     *
     * @return a Map of name to value mappings for all arguments
     */
    public abstract Map<String,String[]> getUrlArguments();

    /**
     * Determine whether a named argument provided in the URL  has the given value.
     * This method is useful in case several arguments have been given with the same name.
     *
     * @param name the name of the argument
     * @param value the desired value of the argument
     * @return true if the request contains an argument with this name and value
     */
    public abstract boolean matchUrlArgument(
            String name,
            String value );

    /**
     * Obtain the relative context Uri of this application.
     *
     * @return the relative context URI
     */
    public abstract String getContextPath();

    /**
     * Obtain the relative context Uri of this application with a trailing slash.
     *
     * @return the relative context URI with a trailing slash
     */
    public abstract String getContextPathWithSlash();

    /**
     * Obtain the absolute context Uri of this application.
     *
     * @return the absolute context URI
     */
    public abstract String getAbsoluteContextUri();

    /**
     * Obtain the absolute context Uri of this application with a trailing slash.
     *
     * @return the absolute context URI with a trailing slash.
     */
    public abstract String getAbsoluteContextUriWithSlash();

    /**
     * Obtain the query string, if any.
     *
     * @return the query string
     */
    public abstract String getQueryString();

    /**
     * Return this absolute full URL but with all URL arguments stripped whose names meet at least
     * one of the provided Patterns.
     * For example, http://example.com/?abc=def&abcd=ef&abcde=f&x=y would become http://example.com?abc=def&x=y
     * if invoked with Pattern "^abcd.*$".
     *
     * @param patterns the Patterns
     * @return the absolute full URL without the matched URL arguments
     */
    public abstract String getAbsoluteFullUriWithoutMatchingArguments(
            Pattern [] patterns );
}
