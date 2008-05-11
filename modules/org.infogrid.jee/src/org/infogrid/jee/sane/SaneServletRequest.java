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

package org.infogrid.jee.sane;

import org.infogrid.util.ArrayCursorIterator;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.CompositeIterator;
import org.infogrid.util.OneElementIterator;
import org.infogrid.util.StreamUtils;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.http.SaneCookie;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * A ServletRequest following the <code>SaneRequest</code> API.
 */
public class SaneServletRequest
        extends
            SaneRequest
{
    private static final Log log = Log.getLogInstance( SaneServletRequest.class ); // our own, private logger
    
    /**
      * Factory method.
      *
      * @param sRequest the HttpServletRequest from which to create a SaneRequest.
      * @return the created SaneServletRequest
      */
    public static SaneServletRequest create(
             HttpServletRequest sRequest )
    {
        return new SaneServletRequest( sRequest );
    }

    /**
     * Constructor.
     *
     * @param sRequest the HttpServletRequest from which the SaneServletRequest is created
     */
    protected SaneServletRequest(
            HttpServletRequest sRequest )
    {
        theDelegate = sRequest;

        String serverProtocol = sRequest.getScheme();
        String queryString    = sRequest.getQueryString();
        theMethod             = sRequest.getMethod();
        theServer             = sRequest.getServerName();
        thePort               = sRequest.getServerPort();

        theHttpHostOnly       = sRequest.getServerName();
        theHttpHost           = sRequest.getServerName();
        theProtocol = serverProtocol.equalsIgnoreCase( "https" ) ? "https" : "http";

        if( "http".equals( theProtocol )) {
            if( thePort != 80 ) {
                theHttpHost += ":" + thePort;
            }
        } else {
            if( thePort != 443 ) {
                theHttpHost += ":" + thePort;
            }
        }
        
        theRelativeBaseUri = sRequest.getRequestURI();
        theRelativeFullUri = theRelativeBaseUri;

        if( queryString != null && queryString.length() != 0 ) {
            theRelativeFullUri += "?" + queryString;
        }

        Cookie [] servletCookies = sRequest.getCookies();
        if( servletCookies != null ) {
            theCookies = new SaneCookie[ servletCookies.length ];
            for( int i=0 ; i<servletCookies.length ; ++i ) {
                theCookies[i] = new CookieAdapter( servletCookies[i] );
            }
        } else {
            theCookies = new SaneCookie[0];
        }
        // URL parameters override POSTed fields: more intuitive for the user
        if( "POST".equalsIgnoreCase( theMethod ) ) { // we do our own parsing
            try {
                int length = sRequest.getContentLength();
                byte [] postData = StreamUtils.slurp( sRequest.getInputStream(), length );

                thePostData = new String( postData, "utf-8" );
                addToArguments( thePostData, true );

            } catch( IOException ex ) {
                log.error( ex );
            }
        }

        addToArguments( queryString, false );
    }

    /**
     * Helper to parse URL and POST data, and put them in the right places.
     *
     * @param data the data to add
     * @param isPost true of this argument was provided in an HTTP Post.
     */
    protected void addToArguments(
            String  data,
            boolean isPost )
    {
        if( data == null || data.length() == 0 ) {
            return;
        }
        if( data.charAt( 0 ) == '?' ) {
            data = data.substring( 1 );
        }

        if( isPost && thePostArguments == null ) {
            thePostArguments = new HashMap<String,String[]>();
        }

        char sep = '?';
        StringTokenizer pairTokenizer = new StringTokenizer( data, "&" );
        while( pairTokenizer.hasMoreTokens() ) {
            String    pair     = pairTokenizer.nextToken();
            String [] keyValue = pair.split( "=", 2 );

            String key   = HTTP.decodeUrl( keyValue[0] );
            String value = HTTP.decodeUrl( keyValue.length == 2 ? keyValue[1] : "" ); // reasonable default?

            if( !"lid-submit".equalsIgnoreCase( key )) {
                // We need to remove the submit button's contribution
                
                String [] haveAlready = theArguments.get( key );
                String [] newValue;
                if( haveAlready == null ) {
                    newValue = new String[] { value };
                } else {
                    newValue = ArrayHelper.append( haveAlready, value, String.class );
                }
                theArguments.put( key, newValue );

                if( isPost ) {
                    haveAlready = thePostArguments.get( key );

                    if( haveAlready == null ) {
                        newValue = new String[] { value };
                    } else {
                        newValue = ArrayHelper.append( haveAlready, value, String.class );
                    }
                    thePostArguments.put( key, newValue );
                }
            }
        }
    }
    
    /**
     * Determine the HTTP method (such as GET).
     *
     * @return the HTTP method
     */
    public String getMethod()
    {
        return theDelegate.getMethod();
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
     * Determine the requested, relative full URI.
     * In a request to URL <code>http://example.com:123/foo/bar?abc=def</code>
     * that would be <code>/foo/bar?abc=def</code>.
     *
     * @return the requested relative full URI
     */
    public String getRelativeFullUri()
    {
        return theRelativeFullUri;
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
     * Obtain the host name.
     *
     * @return the host name
     */
    public String getHttpHost()
    {
        return theHttpHost;
    }

    /**
     * Get the value of the HTTP 1.1 host name field, but without the port.
     *
     * @return the HTTP 1.1 host name field, but without the port
     */
    public String getHttpHostOnly()
    {
        return theHttpHostOnly;
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
     * Obtain all values of a multi-valued argument.
     *
     * @param argName name of the argument
     * @return the values, or <code>null</code>
     */
    public String [] getMultivaluedArgument(
            String argName )
    {
        String [] ret = theArguments.get( argName );
        return ret;
    }

    /**
     * Obtain all arguments of this request.
     *
     * @return a Map of name to value mappings for all arguments
     */
    public Map<String,String[]> getArguments()
    {
        return theArguments;
    }

    /**
     * Obtain all values of a multi-valued POST'd argument.
     *
     * @param argName name of the argument
     * @return the values, or <code>null</code>
     */
    public String [] getMultivaluedPostArgument(
            String argName )
    {
        if( thePostArguments == null ) {
            return null;
        }
        String [] ret = thePostArguments.get( argName );
        return ret;
    }

    /**
     * Obtain all POST'd arguments of this request.
     *
     * @return a Map of name to value mappings for all POST'd arguments
     */
    public Map<String,String[]> getPostArguments()
    {
        return thePostArguments;
    }    
    
    /**
     * Obtain the JEE app's context path, but in absolute terms.
     * 
     * @return the absolute context path
     */
    public String getAbsoluteContextUri()
    {
        StringBuilder buf = new StringBuilder();
        buf.append( getRootUri() );
        buf.append( theDelegate.getContextPath() );

        return buf.toString();
    }

    /**
     * Obtain the cookies that were sent as part of this request.
     *
     * @return the cookies that were sent as part of this request.
     */
    public synchronized SaneCookie[] getCookies()
    {
        if( theCookies == null ) {
            Cookie [] delegateCookies = theDelegate.getCookies();

            theCookies = new SaneCookie[ delegateCookies.length ];
            for( int i=0 ; i<delegateCookies.length ; ++i ) {
                theCookies[i] = new CookieAdapter( delegateCookies[i] );
            }
        } 
        return theCookies;
    }

    /**
     * Obtain the content of the request, e.g. HTTP POST data.
     *
     * @return the content of the request, or <code>null</code>
     */
    public String getPostData()
    {
        return thePostData;
    }

    /**
     * Obtain an Iterator over the user's Locale preferences, in order of preference.
     * This Iterator takes into account a Locale cookie that might be set by the application,
     * followed by the value of the Accept-Language header in the HTTP request and
     * the default locale of the VM
     *
     * @return Iterator
     */
    @Override
    @SuppressWarnings(value={"unchecked"})
    public Iterator<Locale> acceptLanguageIterator()
    {
        SaneCookie  c        = getCookie( ACCEPT_LANGUAGE_COOKIE_NAME );
        Enumeration fromHttp = theDelegate.getLocales();
        if( c != null ) {
            String s = c.getValue();
            String [] parts = s.split( "-" );
            
            Locale cookieLocale;
            switch( parts.length ) {
                case 1:
                    cookieLocale = new Locale( parts[0] );
                    break;
                case 2:
                    cookieLocale = new Locale( parts[0], parts[2] );
                    break;
                default:
                    cookieLocale = new Locale( parts[0], parts[1], parts[2] );
                    break;
            }
            
            return new CompositeIterator<Locale>( new Enumeration[] {
                OneElementIterator.<Locale>create( cookieLocale ),
                fromHttp,
                OneElementIterator.<Locale>create( Locale.getDefault() ) } );

        } else {
            return new CompositeIterator<Locale>( new Enumeration[] {
                fromHttp,
                OneElementIterator.<Locale>create( Locale.getDefault() ) } );
        }
    }

    /**
     * Obtain an Iterator over the requested MIME types, if any. Return the higher-priority
     * MIME types first.
     *
     * @return Iterator over the requested MIME types, if any.
     */
    public Iterator<String> requestedMimeTypesIterator()
    {
        if( theRequestedMimeTypes == null ) {
            // first split by comma, then by semicolon
            String header = theDelegate.getHeader( ACCEPT_HEADER );
            if( header != null ) {
                theRequestedMimeTypes = header.split( "," );
                
                Arrays.sort( theRequestedMimeTypes, new Comparator<String>() {
                    public int compare(
                            String o1,
                            String o2 )
                    {
                        final String qString = ";q=";

                        float priority1;
                        float priority2;
                        
                        int semi1 = o1.indexOf( qString );
                        if( semi1 >= 0 ) {
                            priority1 = Float.parseFloat( o1.substring( semi1 + qString.length() ));
                        } else {
                            priority1 = 1.f;
                        }
                        int semi2 = o2.indexOf( qString );
                        if( semi2 >= 0 ) {
                            priority2 = Float.parseFloat( o2.substring( semi2 + qString.length() ));
                        } else {
                            priority2 = 1.f;
                        }

                        int ret;
                        if( semi1 > semi2 ) {
                            ret = 1;
                        } else if( semi1 == semi2 ) {
                            ret = 0;
                        } else {
                            ret = -1;
                        }
                        return ret;
                    }
                });
                
            } else {
                theRequestedMimeTypes = new String[0];
            }
        }
        return ArrayCursorIterator.<String>create( theRequestedMimeTypes );
    }

    /**
     * Obtain the delegate request.
     *
     * @return the delegate
     */
    public HttpServletRequest getDelegate()
    {
        return theDelegate;
    }

    /**
     * The underlying HttpServletRequest.
     */
    protected HttpServletRequest theDelegate;
    
    /**
     * The http method, such as GET.
     */
    protected String theMethod;

    /**
     * The http server.
     */
    protected String theServer;

    /**
     * The http host, potentially with port.
     */
    protected String theHttpHost;

    /**
     * The http host, without the port.
     */
    protected String theHttpHostOnly;

    /**
     * The port.
     */
    protected int thePort;

    /**
     * The relative base URI of the Request.
     */
    protected String theRelativeBaseUri;

    /**
     * The relative full URI of the Request.
     */
    protected String theRelativeFullUri;

    /**
     * The cookies on this request. Allocated when needed.
     */
    protected SaneCookie[] theCookies;
    
    /**
     * The http or https protocol.
     */
    protected String theProtocol;

    /**
     * The data that was posted, if any.
     */
    protected String thePostData;

    /**
     * The arguments to the Request, mapping from argument name to argument value.
     */
    protected Map<String,String[]> theArguments = new HashMap<String,String[]>();

    /**
     * The arguments to the Request that were POST'd, if any.
     */
    protected Map<String,String[]> thePostArguments = null;

    /**
     * The requested MIME types, in sequence of prioritization. Allocated as needed.
     */
    protected String [] theRequestedMimeTypes;

    /**
     * Bridges the SaneCookie interface into the servlet cookies.
     */
    static class CookieAdapter
        implements
            SaneCookie
    {
        /**
         * Constructor.
         *
         * @param delegate the Servlet Cookie we delegate to
         */
        public CookieAdapter(
                javax.servlet.http.Cookie delegate )
        {
            theDelegate = delegate;
        }

        /**
         * Get the Cookie name.
         *
         * @return the Cookie name
         */
        public String getName()
        {
            if( theName == null ) {
                theName = HTTP.decodeUrl( theDelegate.getName() );
            }
            return theName;
        }

        /**
         * Get the Cookie value.
         *
         * @return the Cookie value
         */
        public String getValue()
        {
            if( theValue == null ) {
                theValue = HTTP.decodeUrl( theDelegate.getValue() );
            }
            return theValue;
        }

        /**
         * Get the Cookie domain.
         *
         * @return the Cookie domain
         */
        public String getDomain()
        {
            return theDelegate.getDomain();
        }

        /**
         * Get the Cookie path.
         *
         * @return the Cookie path
         */
        public String getPath()
        {
            return theDelegate.getPath();
        }

        /**
         * Get the Cookie expiration date.
         *
         * @return the Cookie expiration date
         */
        public Date getExpires()
        {
            return new Date( System.currentTimeMillis() + 1000L*theDelegate.getMaxAge() );
        }

        /**
         * No op, here.
         */
        public void setRemoved()
        {
            // no op
        }

        /**
         * The Servlet Cookie we delegate to.
         */
        protected Cookie theDelegate;
        
        /**
         * The decoded name.
         */
        protected String theName;
        
        /**
         * The decoded value.
         */
        protected String theValue;
    }
}