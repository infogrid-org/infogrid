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

package org.infogrid.util.http;

import org.infogrid.util.StringHelper;
import org.infogrid.util.logging.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class containing HTTP client-side functions.
 */
public abstract class HTTP
{
    private static final Log log = Log.getLogInstance( HTTP.class ); // our own, private logger

    /**
     * Perform an HTTP GET and follow redirects.
     *
     * @param url the URL on which to perform the HTTP GET
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            URL url )
        throws
            IOException
    {
        return http_get( url, null, true, null );
    }

    /**
     * Perform an HTTP GET and follow redirects.
     *
     * @param url the URL on which to perform the HTTP GET
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            String url )
        throws
            IOException
    {
        return http_get( new URL( url ), null, true, null );
    }

    /**
     * Perform an HTTP GET and follow redirects. Specify which content types
     * are acceptable.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param acceptHeader value of the accept header, if any 
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            URL    url,
            String acceptHeader )
        throws
            IOException
    {
        return http_get( url, acceptHeader, true, null );
    }

    /**
     * Perform an HTTP GET and follow redirects. Specify which content types
     * are acceptable.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param acceptHeader value of the accept header, if any 
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            String url,
            String acceptHeader )
        throws
            IOException
    {
        return http_get( new URL( url ), acceptHeader, true, null );
    }

    /**
     * Perform an HTTP GET.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param followRedirects if true, automatically follow redirects.
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            URL     url,
            boolean followRedirects )
        throws
            IOException
    {
        return http_get( url, null, followRedirects, null );
    }

    /**
     * Perform an HTTP GET.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param followRedirects if true, automatically follow redirects.
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            String  url,
            boolean followRedirects )
        throws
            IOException
    {
        return http_get( new URL( url ), null, followRedirects, null );
    }

    /**
     * Perform an HTTP GET. Specify which content types
     * are acceptable, whether to follow redirects, and which Cookies to convey.
     * For simplicity, this can also open non-HTTP URLs although redirects,
     * acceptable content types, and cookies are then ignored.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param acceptHeader value of the accept header, if any 
     * @param followRedirects if true, automatically follow redirects.
     * @param cookies map of cookies to send
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            URL                url,
            String             acceptHeader,
            boolean            followRedirects,
            Map<String,String> cookies )
        throws
            IOException
    {
        URLConnection conn = url.openConnection();
        if( conn instanceof HttpURLConnection ) {
            HttpURLConnection realConn = (HttpURLConnection) conn;

            realConn.setInstanceFollowRedirects( followRedirects );
        }

        if( cookies != null && !cookies.isEmpty() ) {
            StringBuffer cookieString = new StringBuffer();
            String       sep = "";

            Iterator<String> iter = cookies.keySet().iterator();
            while( iter.hasNext() ) {
                String key   = iter.next();
                String value = cookies.get( key );
                cookieString.append( sep ).append( encodeToValidUrl( key ));
                cookieString.append( "=" ).append( encodeToValidUrl( value ));
                sep = "; ";
            }
            conn.setRequestProperty( "Cookie", cookieString.toString() );
        }
        if( acceptHeader != null && acceptHeader.length() > 0 ) {
            conn.setRequestProperty( "Accept", acceptHeader );
        }

        InputStream              input        = conn.getInputStream();
        int                      status       = (conn instanceof HttpURLConnection) ? ((HttpURLConnection)conn).getResponseCode() : 200;
        long                     lastModified = conn.getLastModified();
        Map<String,List<String>> headers      = conn.getHeaderFields();
        
        Response ret = new Response( url, String.valueOf( status ), input, lastModified, headers );
        
        input.close();

        return ret;
    }

    /**
     * Perform an HTTP GET. Specify which content types
     * are acceptable, whether to follow redirects, and which Cookies to convey.
     * For simplicity, this can also open non-HTTP URLs although redirects,
     * acceptable content types, and cookies are then ignored.
     *
     * @param url the URL on which to perform the HTTP GET
     * @param acceptHeader value of the accept header, if any 
     * @param followRedirects if true, automatically follow redirects.
     * @param cookies map of cookies to send
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_get(
            String             url,
            String             acceptHeader,
            boolean            followRedirects,
            Map<String,String> cookies )
        throws
            IOException
    {
        return http_get( new URL( url ), acceptHeader, followRedirects, cookies );
    }

    /**
     * Obtain an InputStream from a certain URL.
     *
     * @param url the URL on which to perform the HTTP GET
     * @return the InputStream from the URL
     * @throws IOException thrown if the InputStream could not be obtained
     */
    public static InputStream http_get_inputStream(
            URL url )
       throws
           IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setInstanceFollowRedirects( true );

        InputStream  input = conn.getInputStream();
        return input;
    }

    /**
     * Obtain an InputStream from a certain URL.
     *
     * @param url the URL on which to perform the HTTP GET
     * @return the InputStream from the URL
     * @throws IOException thrown if the InputStream could not be obtained
     */
    public static InputStream http_get_inputStream(
            String url )
       throws
           IOException
    {
        return http_get_inputStream( new URL( url ));
    }

    /**
     * Perform an HTTP POST. Specify the POST parameters, and whether to follow redirects.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param pars the name-value pairs such as from an HTML form
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_post(
            URL                url,
            Map<String,String> pars,
            boolean            followRedirects )
       throws
           IOException
    {
        String           sep       = "";
        StringBuffer     parBuffer = new StringBuffer();
        Iterator<String> iter      = pars.keySet().iterator();

        while( iter.hasNext() ) {
            String key   = iter.next();
            String value = pars.get( key );

            parBuffer.append( sep );
            parBuffer.append( encodeToValidUrl( key ));
            parBuffer.append( "=" );
            parBuffer.append( encodeToValidUrl( value ));
            sep = "&";
        }
        return http_post( url, "application/x-www-form-urlencoded", parBuffer.toString().getBytes(), DEFAULT_VERSION, followRedirects );
    }

    /**
     * Perform an HTTP POST. Specify the POST parameters, and whether to follow redirects.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param pars the name-value pairs such as from an HTML form
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_post(
            String             url,
            Map<String,String> pars,
            boolean            followRedirects )
       throws
           IOException
    {
        return http_post( new URL( url ), pars, followRedirects );
    }

    /**
     * Perform an HTTP POST. Specify the POST payload, and whether to follow redirects.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param contentType the MIME type of the content to be posted to the URL
     * @param payload the content to be posted to the URL
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_post(
            URL     url,
            String  contentType,
            byte [] payload,
            boolean followRedirects )
       throws
           IOException
    {
        return http_post( url, contentType, payload, DEFAULT_VERSION, followRedirects );
    }

    /**
     * Perform an HTTP POST. Specify the POST payload, and whether to follow redirects.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param contentType the MIME type of the content to be posted to the URL
     * @param payload the content to be posted to the URL
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    public static Response http_post(
            String  url,
            String  contentType,
            byte [] payload,
            boolean followRedirects )
       throws
           IOException
    {
        return http_post( new URL( url ), contentType, payload, followRedirects );
    }

    /**
     * Perform an HTTP POST. Specify the POST payload, and whether to follow redirects.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param contentType the MIME type of the content to be posted to the URL
     * @param payload the content to be posted to the URL
     * @param version the version identifier of the client posting
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    protected static Response http_post(
            URL     url,
            String  contentType,
            byte [] payload,
            String  version,
            boolean followRedirects )
       throws
           IOException
    {
        if( version == null || version.length() == 0 ) {
            version = "current";
        }
        
        String urlString = url.toExternalForm();

        // This implementation is similar to the implementation of LWP::Simple::_trivial_http_get
        Pattern p = Pattern.compile( "^(https?)://([^/:\\@]+)(?::(\\d+))?(/\\S*)?$" );
        Matcher m = p.matcher( urlString );
        if( !m.matches()) {
            throw new IllegalArgumentException( "Not a valid URL to HTTP POST to: " + url.toExternalForm() );
        }

        String proto = m.group( 1 );
        String host  = m.group( 2 );
        String port  = m.group( 3 );

        String standardPort;
        if( "http".equals( proto )) {
            standardPort = "80";
        } else {
            standardPort = "443";
        }
        if( port == null || port.length() == 0 ) {
            port = standardPort;
        }
        // String path = m.group( 3 );

        String netloc = host;
        if( !standardPort.equals( port )) {
            netloc += ":" + port;
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setInstanceFollowRedirects( followRedirects );
        conn.setRequestMethod( "POST" );
        conn.setDoInput( true );
        conn.setDoOutput( true );

        conn.setRequestProperty( "Host",           netloc );
        conn.setRequestProperty( "User-Agent",     HTTP.class.getName() );
        conn.setRequestProperty( "Connection",     "close" );
        conn.setRequestProperty( "Content-Length", String.valueOf( payload.length ));
        conn.setRequestProperty( "Content-Type",   contentType );

        OutputStream outStream = new BufferedOutputStream( conn.getOutputStream());

        outStream.write( payload );
        outStream.flush();

        InputStream              input        = conn.getInputStream();
        int                      status       = conn.getResponseCode();
        long                     lastModified = conn.getLastModified();
        Map<String,List<String>> headers      = conn.getHeaderFields();
        
        Response ret = new Response( url, String.valueOf( status ), input, lastModified, headers );
        
        outStream.close();
        input.close();
        
        return ret;
    }

    /**
     * Perform an HTTP POST.
     *
     * @param url the URL on which to perform the HTTP POST
     * @param contentType the MIME type of the content to be posted to the URL
     * @param payload the content to be posted to the URL
     * @param version the version identifier of the client posting
     * @param followRedirects if true, we follow redirects and post the content there instead
     * @return the Response obtained from that URL
     * @throws IOException thrown if the content could not be obtained
     */
    protected static Response http_post(
            String  url,
            String  contentType,
            byte [] payload,
            String  version,
            boolean followRedirects )
       throws
           IOException
    {
        return http_post( new URL( url ), contentType, payload, version, followRedirects );
    }

    /**
     * Helper method to escape a String in a URL. This allows us to avoid writing
     * all this exception code all over the place.
     *
     * @param s the String
     * @return the escaped String
     * @see AbstractMethodError#decodeUrl
     */
    public static String encodeToValidUrl(
            String s )
    {
        try {
            StringBuilder ret = new StringBuilder( s.length() * 5 / 4 ); // fudge factor

            for( int i=0 ; i<s.length() ; ++i ) {
                char c = s.charAt( i );

                if( Character.isLetterOrDigit( c )) {
                    ret.append( c );
                } else if(    c == '/'
                           || c == '.'
                           || c == '-'
                           || c == '_' ) {
                    ret.append( c );
                            // Due to Tomcat 6 and http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2007-0450
                            // we have to send slashes in clear text
                            // ret = ret.replaceAll( "%2[Ff]", "/" );
                } else {
                    // FIXME there must be something more efficient than this
                    byte [] utf8 = new String( new char[] { c } ).getBytes( "UTF-8" );
                    for( int j=0 ; j<utf8.length ; ++j ) {
                        ret.append( "%" );
                        int positive = utf8[j] > 0 ? utf8[0] : ( 256 + utf8[j] );

                        String hex = Integer.toHexString( positive ).toUpperCase();
                        switch( hex.length() ) {
                            case 0:
                                ret.append( "00" );
                                break;
                            case 1:
                                ret.append( "0" ).append( hex );
                                break;
                            case 2:
                                ret.append( hex );
                                break;
                            case 3:
                                log.error( "How did we get here? " + s );
                                break;
                        }
                    }
                }
            }
            return ret.toString();
        } catch( UnsupportedEncodingException ex ) {
            log.error( ex );
            return s; // at least something
        }
    }

    /**
     * Helper method to unescape a String in a URL. This allows us to avoid writing
     * all this exception code all over the place.
     *
     * @param s the escaped String
     * @return the original String
     * @see #encodeToValidUrl
     */
    public static String decodeUrl(
            String s )
    {
        try {
            String ret = URLDecoder.decode( s, "utf-8" );
            return ret;
            
        } catch( UnsupportedEncodingException ex ) {
            log.error( ex );
            return null;
        }
    }
    
    /**
     * Helper method to escape a String suitably before it can be appended to the query parameters
     * in a URL.
     *
     * @param s the String
     * @return the escaped String
     * @see #decodeUrlArgument
     */
    public static String encodeToValidUrlArgument(
            String s )
    {
        try {
            String ret = URLEncoder.encode( s, "utf-8" );
            
            return ret;

        } catch( UnsupportedEncodingException ex ) {
            log.error( ex );
            return null;
        }
    }
    
    /**
     * Helper method to descape a String suitable before it is extracted as one of the query parameters
     * in a URL.
     *
     * @param s the String
     * @return the descaped String
     * @see #encodeToValidUrlArgument
     */
    public static String decodeUrlArgument(
            String s )
    {
        try {
            String ret = URLDecoder.decode( s, "utf-8" );
            return ret;
            
        } catch( UnsupportedEncodingException ex ) {
            log.error( ex );
            return null;
        }
    }

    /**
     * Append an argument to a URL.
     *
     * @param url the URL to which we append the argument
     * @param argument the argument, such as <tt>foo</tt> or <tt>foo=bar</tt>, without ambersand or question mark separators
     * @return the result
     */
    public static URL appendArgumentToUrl(
            URL    url,
            String argument )
    {
        try {
            String urlString = url.toExternalForm();
            String ret = appendArgumentToUrl( urlString, argument );
            return new URL( ret );
        } catch( MalformedURLException ex ) {
            log.error( ex );
            return url; // some kind of fallback
        }
    }

    /**
     * Append an argument to a URL.
     *
     * @param url the URL to which we append the argument
     * @param argument the argument, such as <tt>foo</tt> or <tt>foo=bar</tt>, without ambersand or question mark separators
     * @return the result
     */
    public static String appendArgumentToUrl(
            String url,
            String argument )
    {
        if( url.indexOf( '?' ) >= 0 ) {
            return url + "&" + argument;
        } else {
            return url + "?" + argument;
        }
    }

    /**
     * Obtain a named argument from a URL.
     *
     * @param u the URL
     * @param arg the name of the argument
     * @return the value of the named argument
     */
    public static String getUrlArgument(
            String u,
            String arg )
    {
        int q = u.indexOf( '?' );
        if( q < 0 ) {
            return null;
        }
        String args = u.substring( q + 1 );
        String [] pairs = args.split( "&" );
        for( int i=0 ; i<pairs.length ; ++i ) {
            String current = pairs[i];
            int    equals  = current.indexOf( '=' );
            if( equals < 0 ) {
                continue; // won't have a value
            }
            String name = decodeUrlArgument( current.substring( 0, equals ));
            if( arg.equals( name )) {
                String value = decodeUrlArgument( current.substring( equals+1 ));
                return value;
            }
        }
        return null;
    }

    /**
     * Our default HTTP client version.
     */
    protected static final String DEFAULT_VERSION = "current";

    /**
     * The Pattern to extract the charset from the content type.
     */
    protected static final Pattern theContentTypePattern = Pattern.compile( "([^;]*)(;.*charset=(.*))?", Pattern.CASE_INSENSITIVE );

    /**
     * Encapsulates the response from an HTTP request.
     */
    public static class Response
    {
        /**
         * Constructor.
         *
         * @param url the URL of which this is the Response
         * @param responseCode the HTTP response code
         * @param stream the InputStream from which we read the content of the Response
         * @param lastModified the time when the stream was last modified
         * @param headerFields the HTTP header fields
         * @throws IOException thrown if an I/O problem occurred
         */
        Response(
                URL                      url,
                String                   responseCode,
                InputStream              stream,
                long                     lastModified,
                Map<String,List<String>> headerFields )
            throws
                IOException
        {
            theUrl          = url;
            theResponseCode = responseCode;
            theLastModified = lastModified;

            // turns out that HTTP headers are supposed to be case insensitive, but the Java implementation
            // does not do that ... so we do it ourselves.

            theHeaderFields = new HashMap<String,String>( headerFields.size() );
            Iterator<String> iter = headerFields.keySet().iterator();
            while( iter.hasNext() ) {
                String key   = iter.next();
                Object value = headerFields.get( key );
                if( value instanceof Collection ) {
                    Collection realValue = (Collection) value;
                    if( realValue.isEmpty() ) {
                        value = null;
                    } else {
                        value = ((Collection)value).iterator().next();
                    }
                }
                if( key != null && value != null ) {
                    theHeaderFields.put( key.toLowerCase(), (String) value );
                }
            }

            theContent = org.infogrid.util.StreamUtils.slurp( stream );
        }

        /**
         * Obtain the URL to which this is the Response.
         *
         * @return the URL
         */
        public URL getURL()
        {
            return theUrl;
        }

        /**
         * Obtain the HTTP response code.
         *
         * @return the HTTP response code (could potentially be null)
         */
        public String getResponseCode()
        {
            return theResponseCode;
        }

        /**
         * Obtain the time this Response was last modified.
         *
         * @return the time this Response was last modified
         */
        public long getLastModified()
        {
            return theLastModified;
        }

        /**
         * Does this response indicate success.
         *
         * @return true for all HTTP 200 status codes
         */
        public boolean isSuccess()
        {
            boolean ret = theResponseCode.startsWith( "2" );
            return ret;
        }

        /**
         * Helper method to determine the content type and character set.
         */
        protected void determineContentTypeAndCharset()
        {
            String rawContentType = theHeaderFields.get( "content-type" );
            if( rawContentType != null ) {
                rawContentType = rawContentType.trim();
                
                Matcher contentTypeMatcher = theContentTypePattern.matcher( rawContentType );
                if( contentTypeMatcher.find() ) {
                    theContentType = contentTypeMatcher.group( 1 );
                    if( contentTypeMatcher.groupCount() >= 3 ) {
                        theCharset     = contentTypeMatcher.group( 3 );
                    }
                }
            }
        }

        /**
         * Obtain the content (MIME) type.
         *
         * @return the content (MIME) type (could potentially be null)
         */
        public String getContentType()
        {
            if( theContentType == null ) {
                determineContentTypeAndCharset();
            }
            return theContentType;
        }

        /**
         * Obtain the char set, if applicable.
         *
         * @return the char set
         */
        public String getCharset()
        {
            if( theCharset == null ) {
                determineContentTypeAndCharset();
            }
            return theCharset;
        }

        /**
         * Obtain the content in the Response.
         *
         * @return the content in the response (could potentially be null)
         */
        public byte [] getContent()
        {
            return theContent;
        }

        /**
         * Obtain the content in the Response as String
         *
         * @return the content in the response (could potentially be null)
         */
        public String getContentAsString()
        {
            String charset = getCharset();
            if( charset != null ) {
                try {
                    return new String( theContent, charset );

                } catch( UnsupportedEncodingException ex ) {
                    log.warn( ex );
                }
            }
            return new String( theContent );
        }

        /**
         * Obtain the redirection URL, if any.
         *
         * @return the redirection URL, if any
         */
        public String getLocation()
        {
            return getHttpHeaderField( "location" );
        }

        /**
         * Obtain all HTTP headers in this Response.
         *
         * @return a Map of all HTTP headers in this Response
         */
        public Map<String,String> getHttpHeaderFields()
        {
            return theHeaderFields;
        }

        /**
         * Obtain the value of a particular HTTP header, or null if not present.
         *
         * @param headerName name of the header field to retrieve
         * @return value of a particular HTTP header, or null
         */
        public String getHttpHeaderField(
                String headerName )
        {
            String ret = theHeaderFields.get( headerName.toLowerCase() );
            return ret;
        }

        /**
         * Obtain in String form, for debugging.
         *
         * @return String representation
         */
        @Override
        public String toString()
        {
            return StringHelper.objectLogString(
                    this,
                    new String[] {
                        "theResponseCode",
                        "theLastModified",
                        "theHeaderFields",
                        "theContent",
                        "theContent(length)",
                        "theContent(string)"
                    },
                    new Object[] {
                        theResponseCode,
                        theLastModified,
                        theHeaderFields,
                        theContent,
                        ( theContent != null ) ? theContent.length : "n/a",
                        new String( theContent )
                    } );
        }

        /**
         * The URL to which this is the Response.
         */
        protected URL theUrl;

        /**
         * The HTTP status in the Response.
         */
        protected String theResponseCode;

        /**
         * The time this Response was last modified.
         */
        protected long theLastModified;

        /**
         * The content in the Response.
         */
        protected byte [] theContent;

        /**
         * The HTTP header fields.
         */
        protected Map<String,String> theHeaderFields;
        
        /**
         * The content type.
         */
        protected String theContentType;
        
        /**
         * The character set.
         */
        protected String theCharset;
    }
}