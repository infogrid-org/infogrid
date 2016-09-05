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

package org.infogrid.web.templates;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.infogrid.web.ProblemReporter;

/**
 * Utility interface that enables us to treat StructuredResponse and StructuredResponseSection
 * uniformly for the purposes of processing their header preferences e.g. cookies.
 */
public interface HasHeaderPreferences
        extends
            ProblemReporter
{
    /**
     * Obtain the desired MIME type.
     *
     * @return the desired MIME type
     */
    public String getContentType();

    /**
     * Obtain the Cookies.
     *
     * @return the Cookies
     */
    public Collection<Cookie> getCookies();

    /**
     * Obtain the location header.
     *
     * @return the currently set location header
     */
    public String getLocation();

    /**
     * Obtain the HTTP response code.
     *
     * @return the HTTP response code
     */
    public int getStatus();

    /**
     * Obtain the locale.
     *
     * @return the locale
     */
    public Locale getLocale();

    /**
     * Obtain the character encoding.
     *
     * @return the character encoding
     */
    public String getCharacterEncoding();

    /**
     * Get the single value of an additional header.
     * 
     * @param name name of the header
     * @return value of the header
     */
    public String getHeader(
            String name );

    /**
     * Get the set of values of an additional header.
     * 
     * @param name name of the header
     * @return values of the header, or null
     */
    public Collection<String> getHeaders(
            String name );

    /**
     * Get the names of the additional headers.
     * 
     * @return the names of the additional headers
     */
    public Collection<String> getHeaderNames();

    /**
     * Obtain the additional headers.
     *
     * @return the headers, as Map
     */
    public Map<String,Collection<String>> getFullHeaders();

    /**
     * The name of the location header.
     */
    public static final String LOCATION_HEADER = "Location";   
}
