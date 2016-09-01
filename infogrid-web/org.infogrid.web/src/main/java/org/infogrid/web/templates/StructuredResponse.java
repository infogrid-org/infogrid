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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.web.ProblemReporter;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequestUtils;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;

/**
 * Encapsulates the content of an HTTP response in structured form. It is also
 * a HttpServletResponse that buffers its content.
 * 
 * Both a byte[] and a String buffer may be created, in order to avoid converting
 * to and from String and byte [] all the time.
 */
public class StructuredResponse
        implements
            HasHeaderPreferences,
            ProblemReporter,
            HttpServletResponse,
            CanBeDumped
{
    private static final Log log = Log.getLogInstance( StructuredResponse.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @return the created StructuredResponse
     */
    public static StructuredResponse create(
            ServletContext      servletContext )
    {
        StructuredResponse ret = new StructuredResponse( servletContext, DEFAULT_MAX_PROBLEMS, DEFAULT_MAX_INFO_MESSAGES );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @param maxProblems the maxmimum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     * @return the created StructuredResponse
     */
    public static StructuredResponse create(
            ServletContext      servletContext,
            int                 maxProblems,
            int                 maxInfoMessages )
    {
        StructuredResponse ret = new StructuredResponse( servletContext, maxProblems, maxInfoMessages );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @param maxProblems the maximum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     */
    protected StructuredResponse(
            ServletContext      servletContext,
            int                 maxProblems,
            int                 maxInfoMessages )
    {
        theServletContext  = servletContext;
        theMaxProblems     = maxProblems;
        theMaxInfoMessages = maxInfoMessages;
    }

    /**
     * Obtain the default text section.
     *
     * @return the default text section
     */
    public TextStructuredResponseSection getDefaultTextSection()
    {
        return obtainTextSection( TEXT_DEFAULT_SECTION );
    }

    /**
     * Obtain the default binary section.
     *
     * @return the default binary section
     */
    public BinaryStructuredResponseSection getDefaultBinarySection()
    {
        return obtainBinarySection( BINARY_DEFAULT_SECTION );
    }

    /**
     * Obtain a text section; if the section does not exist, create it.
     *
     * @param template the section type
     * @return the section
     */
    public TextStructuredResponseSection obtainTextSection(
            String template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        TextStructuredResponseSection ret = theTextSections.get( template );
        if( ret == null ) {
            ret = TextStructuredResponseSection.create();
            theTextSections.put( template, ret );
        }
        return ret;
    }

    /**
     * Obtain a binary section; if the section does not exist, create it.
     *
     * @param template the section type
     * @return the section
     */
    public BinaryStructuredResponseSection obtainBinarySection(
            String template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        BinaryStructuredResponseSection ret = theBinarySections.get( template );
        if( ret == null ) {
            ret = BinaryStructuredResponseSection.create();
            theBinarySections.put( template, ret );
        }
        return ret;
    }

    /**
     * Get a text section; if the section does not exist, return null.
     *
     * @param template the section type
     * @return the section, or null
     */
    public TextStructuredResponseSection getTextSection(
            String template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        TextStructuredResponseSection ret = theTextSections.get( template );
        return ret;
    }

    /**
     * Obtain a binary section; if the section does not exist, return null.
     *
     * @param template the section type
     * @return the section, or null
     */
    public BinaryStructuredResponseSection getBinarySection(
            String template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        BinaryStructuredResponseSection ret = theBinarySections.get( template );
        return ret;
    }

    /**
     * Obtain an Iterator over the text section templates currently used.
     *
     * @return the Iterator
     */
    public Iterator<String> textSectionTemplateIterator()
    {
        return theTextSections.keySet().iterator();
    }

    /**
     * Obtain an Iterator over the binary section templates currently used.
     *
     * @return the Iterator
     */
    public Iterator<String> binarySectionTemplateIterator()
    {
        return theBinarySections.keySet().iterator();
    }

    /**
     * Helper method to find the cause of an Exception.
     * Unfortunately JspExceptions do getRootCause() instead of getCause().
     *
     * @param t the incoming Throwable whose cause we need to determine
     * @return the found cause, or null
     */
    protected Throwable findCause(
            Throwable t )
    {
        Throwable ret;
        if( t instanceof ServletException ) {
            ret = ((ServletException)t).getRootCause();
        } else {
            ret = t.getCause();
        }
        return ret;
    }

    /**
     * Quote HTML angle brackets so we can insert them into a &lt;pre&gt; element.
     *
     * @param in the unquoted String
     * @return the quoted String
     */
    protected String quoteAngleBrackets(
            String in )
    {
        String ret = in.replaceAll( "<", "&lt;" );
        ret = ret.replaceAll( ">", "&gt;" );
        return ret;
    }

    /**
     * Report a problem that should be shown to the user.
     *
     * @param t the Throwable indicating the problem
     */
    @Override
    public void reportProblem(
            Throwable t )
    {
        if( log.isDebugEnabled() ) {
            log.debug( "Reporting problem: ", t );
        }
        if( theCurrentProblems.size() <= theMaxProblems ) {
            // make sure we aren't growing this indefinitely
            theCurrentProblems.add( t );

        } else {
            log.error( "Too many problems. Ignored ", t ); // late initialization
        }
    }

    /**
     * Convenience method to report several problems that should be shown to the user.
     *
     * @param ts [] the Throwables indicating the problems
     */
    @Override
    public void reportProblems(
            Throwable [] ts )
    {
        for( int i=0 ; i<ts.length ; ++i ) {
            reportProblem( ts[i] );
        }
    }

    /**
     * Determine whether problems have been reported.
     *
     * @return true if at least one problem has been reported
     */
    @Override
    public boolean haveProblemsBeenReported()
    {
        if( !theCurrentProblems.isEmpty() ) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether problems have been reported here and in all contained sections.
     *
     * @return true if at least one problem has been reported
     */
    public boolean haveProblemsBeenReportedAggregate()
    {
        if( !theCurrentProblems.isEmpty() ) {
            return true;
        }

        for( TextStructuredResponseSection current : theTextSections.values() ) {
            if( current.haveProblemsBeenReported() ) {
                return true;
            }
        }
        for( BinaryStructuredResponseSection current : theBinarySections.values() ) {
            if( current.haveProblemsBeenReported() ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtain the problems reported so far.
     *
     * @return problems reported so far, in sequence
     */
    @Override
    public Iterator<Throwable> problems()
    {
        return theCurrentProblems.iterator();
    }

    /**
     * Obtain the problems reported so far here and in all contained sections.
     *
     * @return problems reported so far
     */
    public Iterator<Throwable> problemsAggregate()
    {
        ArrayList<Throwable> ret =  new ArrayList<>();
        ret.addAll( theCurrentProblems );

        for( TextStructuredResponseSection current : theTextSections.values() ) {
            Iterator<Throwable> problemIter = current.problems();
            while( problemIter.hasNext() ) {
                ret.add( problemIter.next() );
            }
        }
        for( BinaryStructuredResponseSection current : theBinarySections.values() ) {
            Iterator<Throwable> problemIter = current.problems();
            while( problemIter.hasNext() ) {
                ret.add( problemIter.next() );
            }
        }

        return ret.iterator();
    }

    /**
     * Report an informational message that should be shown to the user.
     *
     * @param t the THrowable indicating the message
     */
    @Override
    public void reportInfoMessage(
            Throwable t )
    {
        if( log.isDebugEnabled() ) {
            log.debug( "Reporting info message: ", t );
        }
        if( theCurrentInfoMessages.size() <= theMaxInfoMessages ) {
            // make sure we aren't growing this indefinitely
            theCurrentInfoMessages.add( t );

        } else {
            log.error( "Too many info messages. Ignored ", t ); // late initialization
        }
    }

    /**
     * Convenience method to report several informational messages that should be shown to the user.
     *
     * @param ts [] the Throwables indicating the informational messages
     */
    @Override
    public void reportInfoMessages(
            Throwable [] ts )
    {
        for( int i=0 ; i<ts.length ; ++i ) {
            reportInfoMessage( ts[i] );
        }
    }

    /**
     * Determine whether informational messages have been reported.
     *
     * @return true if at least one informational message has been reported
     */
    @Override
    public boolean haveInfoMessagesBeenReported()
    {
        if( !theCurrentInfoMessages.isEmpty() ) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether informational messages have been reported here and in all contained sections.
     *
     * @return true if at least one informational message has been reported
     */
    public boolean haveInfoMessagesBeenReportedAggregate()
    {
        if( !theCurrentInfoMessages.isEmpty() ) {
            return true;
        }

        for( TextStructuredResponseSection current : theTextSections.values() ) {
            if( current.haveInfoMessagesBeenReported() ) {
                return true;
            }
        }
        for( BinaryStructuredResponseSection current : theBinarySections.values() ) {
            if( current.haveInfoMessagesBeenReported() ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtain the informational messages reported so far.
     *
     * @return informational messages reported so far, in sequence
     */
    @Override
    public Iterator<Throwable> infoMessages()
    {
        return theCurrentInfoMessages.iterator();
    }

    /**
     * Obtain the informational messages reported so far here and in all contained sections.
     *
     * @return informational messages reported so far
     */
    public Iterator<Throwable> infoMessagesAggregate()
    {
        ArrayList<Throwable> ret =  new ArrayList<>();
        ret.addAll( theCurrentInfoMessages );

        for( TextStructuredResponseSection current : theTextSections.values() ) {
            Iterator<Throwable> infoMessageIter = current.infoMessages();
            while( infoMessageIter.hasNext() ) {
                ret.add( infoMessageIter.next() );
            }
        }
        for( BinaryStructuredResponseSection current : theBinarySections.values() ) {
            Iterator<Throwable> infoMessageIter = current.infoMessages();
            while( infoMessageIter.hasNext() ) {
                ret.add( infoMessageIter.next() );
            }
        }

        return ret.iterator();
    }

    /**
     * Obtain the desired MIME type.
     *
     * @return the desired MIME type
     */
    @Override
    public String getMimeType()
    {
        return theMimeType;
    }

    /**
     * Set the desired MIME type.
     *
     * @param newValue the new value
     */
    public void setMimeType(
            String newValue )
    {
        theMimeType = newValue;
    }

    /**
     * Obtain the Cookies.
     *
     * @return the Cookies
     */
    @Override
    public Collection<Cookie> getCookies()
    {
        return theCookies;
    }

    /**
     * Add a Cookie.
     *
     * @param toAdd the Cookie to add
     */
    @Override
    public void addCookie(
            Cookie toAdd )
    {
        theCookies.add( toAdd );
    }

    /**
     * Obtain the location header.
     *
     * @return the currently set location header
     */
    @Override
    public String getLocation()
    {
        String [] found = theOutgoingHeaders.get( LOCATION_HEADER );
        if( found == null ) {
            return null;
        } else if( found.length == 1 ) {
            return found[0];
        } else {
            log.error( "More than one location header:", found );
            return null;
        }
    }

    /**
     * Set a redirect location.
     *
     * @param newValue the new value
     */
    public void setLocation(
            String newValue )
    {
        addHeader( LOCATION_HEADER, newValue );
    }

    /**
     * Obtain the HTTP response code.
     *
     * @return the HTTP response code
     */
    @Override
    public int getHttpResponseCode()
    {
        return theHttpResponseCode;
    }

    /**
     * Set the desired HTTP response code.
     *
     * @param newValue the new value
     */
    public void setHttpResponseCode(
            int newValue )
    {
        theHttpResponseCode = newValue;
    }

    /**
     * Obtain the locale.
     *
     * @return the locale
     */
    @Override
    public Locale getLocale()
    {
        return theLocale;
    }

    /**
     * Set the locale.
     *
     * @param newValue the new value
     */
    @Override
    public void setLocale(
            Locale newValue )
    {
        theLocale = newValue;
    }

    /**
     * Obtain the character encoding.
     *
     * @return the character encoding
     */
    @Override
    public String getCharacterEncoding()
    {
        return theCharacterEncoding;
    }

    /**
     * Set the character encoding.
     *
     * @param newValue the new value
     */
    @Override
    public void setCharacterEncoding(
            String newValue )
    {
        theCharacterEncoding = newValue;
    }

    /**
     * Obtain the ServletContext within this response is being assembled.
     *
     * @return the ServletContext
     */
    public ServletContext getServletContext()
    {
        return theServletContext;
    }

    /**
     * Set the name of the template that is being requested. Null represents "default".
     *
     * @param newValue the name of the template that is being requested
     */
    public void setRequestedTemplateName(
            String newValue )
    {
        theRequestedTemplateName = newValue;
    }

    /**
     * Obtain the name of the template that is being requested. Null represents "default".
     *
     * @return the name of the template that is being requested
     */
    public String getRequestedTemplateName()
    {
        return theRequestedTemplateName;
    }

    /**
     * Add an additional header.
     *
     * @param name name of the header to add
     * @param value value of the header to add
     */
    @Override
    public void addHeader(
            String name,
            String value )
    {
        addHeader( name, new String[] { value } );
    }

    /**
     * Add an additional header.
     *
     * @param name name of the header to add
     * @param value value of the header to add
     */
    public void addHeader(
            String    name,
            String [] value )
    {
        String [] already = theOutgoingHeaders.put( name, value );
        if( already != null && already.length > 0 ) {
            theOutgoingHeaders.put( name, ArrayHelper.append( already, value, String.class ));
        }
    }

    /**
     * Obtain the additional headers.
     *
     * @return the headers, as Map
     */
    @Override
    public Map<String,String[]> getHeaders()
    {
        return theOutgoingHeaders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsHeader(
            String name )
    {
        return theOutgoingHeaders.containsKey( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType()
    {
        return theContentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream()
        throws
            IOException
    {
        if( theServletOutputStream == null ) {
            if( theOutputStream == null ) {
                theOutputStream = new ByteArrayOutputStream( 2048 );
            }
            theServletOutputStream = new MyServletOutputStream( theOutputStream );
        }
        return theServletOutputStream;        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter()
        throws
            IOException
    {
        if( thePrintWriter == null ) {
            if( theWriter == null ) {
                theWriter = new StringWriter( 2048 );
            }
            thePrintWriter = new PrintWriter( theWriter );
        }
        return thePrintWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(
            int len )
    {
        theContentLength = len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLengthLong(
            long len )
    {
        theContentLength = len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(
            String type )
    {
        theContentType = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferSize(
            int size )
    {
        // ignored (FIXME?)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize()
    {
        return Integer.MAX_VALUE; // FIXME?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushBuffer()
        throws
            IOException
    {
        if( thePrintWriter != null ) {
            thePrintWriter.flush();
        }
        if( theOutputStream != null ) {
            theOutputStream.flush();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetBuffer()
    {
        resetCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted()
    {
        return false; // this is always false as this is buffer
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        resetCache();
        theOutgoingHeaders.clear();
        theStatusCode = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeURL(
            String url )
    {
        return url; // we don't do session so no rewrite is needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeRedirectURL(
            String url )
    {
        return url; // we don't do session so no rewrite is needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "deprecation" )
    public String encodeUrl(
            String url )
    {
        return url; // we don't do session so no rewrite is needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "deprecation" )
    public String encodeRedirectUrl(
            String url )
    {
        return url; // we don't do session so no rewrite is needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(
            int    sc,
            String msg )
        throws
            IOException
    {
        theStatusCode    = sc;
        theStatusMessage = msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(
            int sc )
        throws
            IOException
    {
        sendError( sc, null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(
            String location )
        throws
            IOException
    {
        resetBuffer();

        theStatusCode = 302; // Found code
        if( theOutgoingHeaders.put( LOCATION_HEADER, new String[] { location } ) != null ) {
            log.warn( "Replacing location header" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateHeader(
            String name,
            long   date )
    {
        setHeader( name, theFormat.format( date ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDateHeader(
            String name,
            long   date )
    {
        addHeader( name, theFormat.format( date ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeader(
            String name,
            String value )
    {
        Object already = theOutgoingHeaders.put( name, new String[] { value } ); // return value for debugging only
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntHeader(
            String name,
            int    value )
    {
        setHeader( name, String.valueOf( value ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIntHeader(
            String name,
            int    value )
    {
        addHeader( name, String.valueOf( value ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(
            int sc )
    {
        setStatus( sc, null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "deprecation" )
    public void setStatus(
            int    sc,
            String sm )
    {
        theStatusCode    = sc;
        theStatusMessage = sm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus()
    {
        return theStatusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(
            String name )
    {
        String [] values = theOutgoingHeaders.get( name );
        if( values == null || values.length == 0 ) {
            return null;
        }
        return values[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaders(
            String name )
    {
        String [] values = theOutgoingHeaders.get( name );
        ArrayList<String> ret = new ArrayList<>( values.length );
        
        for( int i=0 ; i<values.length ; ++i ) {
            ret.add( values[i] );
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaderNames()
    {
        return theOutgoingHeaders.keySet();
    }

    /**
     * Determine whether this StructuredResponse is empty.
     *
     * @return true if the structure is empty
     */
    public boolean isStructuredEmpty()
    {
        if( theHttpResponseCode > 0 && theHttpResponseCode != 200 ) {
            return false;
        }
        if( haveProblemsBeenReported()) {
            return false;
        }
        if( haveInfoMessagesBeenReported()) {
            return false;
        }
        for( TextStructuredResponseSection value : theTextSections.values() ) {
            if( !value.isEmpty() ) {
                return false;
            }
        }
        for( BinaryStructuredResponseSection value : theBinarySections.values() ) {
            if( !value.isEmpty() ) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Determine whether the buffers are empty.
     * 
     * @return true if the buffers are empty.
     */
    public boolean isBuffersEmpty()
    {
        if( theOutputStream != null && theOutputStream.size() > 0 ) {
            return false;
        }
        if( theWriter != null && theWriter.getBuffer().length() > 0 ) {
            return false;
        }
        return true;
    }

    /**
     * Obtain the entire buffered output that was written via the PrintWriter.
     *
     * @return the buffered output, or null
     * @throws IOException an I/O problem occurred
     */
    public String getBufferedPrintWriterOutput()
        throws
            IOException
    {
        if( thePrintWriter != null ) {
            thePrintWriter.flush();
        }
        if( theWriter != null ) {
            String ret = theWriter.getBuffer().toString();
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Obtain the entire buffered output that was written via the ServletOutputStream.
     *
     * @return the buffered output, or null
     * @throws IOException an I/O problem occurred
     */
    public byte [] getBufferedServletOutputStreamOutput()
        throws
            IOException
    {
        if( theServletOutputStream != null ) {
            theServletOutputStream.flush();
        }
        if( theOutputStream != null ) {
            byte [] ret = theOutputStream.toByteArray();
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Reset the locally held cache.
     */
    protected void resetCache()
    {
        if( thePrintWriter != null ) {
            thePrintWriter = null;
        }
        if( theWriter != null ) {
            theWriter = null;
        }
        if( theServletOutputStream != null ) {
            theServletOutputStream = null;
        }
        if( theOutputStream != null ) {
            theOutputStream = null;
        }
    }

    /**
     * Determine whether or not this context type is text.
     *
     * @return true if this content type is text
     */
    public boolean isText()
    {
        String type = theContentType.toLowerCase();
        if( type.startsWith( "text/" )) {
            return true;
        }
        if( type.startsWith( "application/xhtml" )) {
            return true;
        }
        if( type.startsWith( "application/xml" )) {
            return true;
        }
        return false;
    }

    /**
     * Copy the buffer into this HttpServletResponse.
     * 
     * @param destination the HttpServletResponse to copy to
     * @throws IOException thown if an input/output error occurred
     */
    @SuppressWarnings( "unchecked" )
    public void copyTo(
            HttpServletResponse destination )
        throws
            IOException
    {
        if( theStatusCode > 0 ) {
            destination.setStatus( theStatusCode ); // FIXME? Status code
        }
        if( theContentType != null ) {
            destination.setContentType( theContentType );
        }
        if( theLocale != null ) {
            destination.setLocale( theLocale );
        }
        // if( theCharacterEncoding != null ) {
        //     destination.setCharacterEncoding( theCharacterEncoding );
        // The version of JEE I have does not seem to have this method
        // }
        if( theContentLength > 0 ) {
            destination.setContentLengthLong( theContentLength );
        }
        for( String key : theOutgoingHeaders.keySet() ) {
            for( String value : theOutgoingHeaders.get( key )) {
                destination.addHeader( key, value );
            }
        }
        for( Cookie c : theCookies ) {
            destination.addCookie( c );
        }
        
        String  stringContent = getBufferedPrintWriterOutput();
        byte [] byteContent   = getBufferedServletOutputStreamOutput();
        
        if( stringContent != null ) {
            destination.getOutputStream().print( stringContent );
        } else if( byteContent != null ) {
            destination.getOutputStream().write( byteContent );
        } else {
            // do nothing
        }
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String [] {
                    "theRequestedTemplateName",
                    "theCurrentProblems",
                    "theCurrentInfoMessages",
                    "theMimeType",
                    "theCookies",
                    "theHttpResponseCode",
                    "theLocale",
                    "theCharacterEncoding",
                    "theOutgoingHeaders"
                },
                new Object [] {
                    theRequestedTemplateName,
                    theCurrentProblems,
                    theCurrentInfoMessages,
                    theMimeType,
                    theCookies,
                    theHttpResponseCode,
                    theLocale,
                    theCharacterEncoding,
                    theOutgoingHeaders
                });
    }

    /**
     * The sections of the response that are represented as text.
     */
    protected HashMap<String,TextStructuredResponseSection> theTextSections = new HashMap<>();

    /**
     * The sections of the response that are represented as binary.
     */
    protected HashMap<String,BinaryStructuredResponseSection> theBinarySections = new HashMap<>();

    /**
     * The ServletContext within which this response is assembled.
     */
    protected ServletContext theServletContext;

    /**
     * Name of the template that is being requested, if any.
     */
    protected String theRequestedTemplateName = null;

    /**
     * The current problems, in sequence of occurrence.
     */
    protected ArrayList<Throwable> theCurrentProblems = new ArrayList<>();

    /**
     * The current informational messages, in sequence of occurrence.
     */
    protected ArrayList<Throwable> theCurrentInfoMessages = new ArrayList<>();

    /**
     * The maximum number of problems to store in this type of section.
     */
    protected int theMaxProblems;

    /**
     * The maximum number of informational messages to store in this type of section.
     */
    protected int theMaxInfoMessages;

    /**
     * The desired MIME type. Currently not used.
     */
    protected String theMimeType;

    /**
     * The desired cookies. Currently not used.
     */
    protected Collection<Cookie> theCookies = new ArrayList<>();

    /**
     * The desired HTTP response code.
     */
    protected int theHttpResponseCode = -1;

    /**
     * The desired locale.
     */
    protected Locale theLocale;

    /**
     * The desired character encoding.
     */
    protected String theCharacterEncoding;

    /**
     * Content type of this response.
     */
    protected String theContentType;
    
    /**
     * The HTTP content length, if not -1.
     */
    protected long theContentLength;

    /**
     * The outgoing headers.
     */
    protected HashMap<String,String[]> theOutgoingHeaders = new HashMap<>();

    /**
     * The HTTP status code.
     */
    protected int theStatusCode;
    
    /**
     * The HTTP status message.
     */
    protected String theStatusMessage;

    /**
     * Byte-buffer to write into.
     */
    protected ByteArrayOutputStream theOutputStream;
    
    /**
     * ServletOutputStream on top of theOutputStream.
     */
    protected ServletOutputStream theServletOutputStream;
    
    /**
     * String-buffer to write into.
     */
    protected StringWriter theWriter;
    
    /**
     * PrintWriter on top of the theWriter.
     */
    protected PrintWriter thePrintWriter;

    /**
     * Name of the request attribute that contains the StructuredResponse. Make sure
     * this constant does not contain any characters that might make some processor
     * interpret it as being an expression.
     */
    public static final String STRUCTURED_RESPONSE_ATTRIBUTE_NAME
            = SaneRequestUtils.classToAttributeName( StructuredResponse.class );

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( StructuredResponse.class );

    /**
     * The default maximum number of problems to store.
     */
    public static final int DEFAULT_MAX_PROBLEMS = theResourceHelper.getResourceIntegerOrDefault( "DefaultMaxProblems", 20 );

    /**
     * The default maximum number of informational messages to store.
     */
    public static final int DEFAULT_MAX_INFO_MESSAGES = theResourceHelper.getResourceIntegerOrDefault( "DefaultMaxInfoMessages", 20 );

    /**
     * The date format for date headers.
     */
    protected static final SimpleDateFormat theFormat;
    static {
        theFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US );
        theFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
    }

    /**
     * The single default section for text content. Output will be written into this section
     * unless otherwise specified.
     */
    public static final String TEXT_DEFAULT_SECTION = "text-default";

    /**
     * The single default section for binary content. Binary output will be written into this section
     * unless otherwise specified.
     */
    public static final String BINARY_DEFAULT_SECTION = "binary-default";

    /**
     * The section representing the head of an HTML document.
     */
    public static final String HTML_HEAD_SECTION = "html-head";

    /**
     * The section representing the title of an HTML document. While this could be considered
     * a part of the head of the HTML document, in practice it has turned out to be useful if
     * it is kept separate.
     */
    public static final String HTML_TITLE_SECTION = "html-title";

    /**
     * The section representing the messages section of an HTML document.
     */
    public static final String HTML_MESSAGES_SECTION = "html-messages";

    /**
     * The section representing the main menu in an HTML document. Many HTML documents don't have
     * such a section, but it is common enough that we make it explicit here.
     */
    public static final String HTML_MAIN_MENU_SECTION = "html-main-menu";


    /**
     * Simple implementation of ServletOutputStream.
     */
    static class MyServletOutputStream
            extends
                ServletOutputStream
    {
        /**
         * Constructor.
         *
         * @param delegate the OutputStream to write to.
         */
        public MyServletOutputStream(
                OutputStream delegate )
        {
            theDelegate = delegate;
        }
        
        /**
         * Write method.
         *
         * @param i the integer to write
         * @throws IOException
         */
        @Override
        public void write(
                int i )
            throws
                IOException
        {
            theDelegate.write( i );
        }

        /**
         * Can data be written without blocking.
         * 
         * @return true or false
         */
        @Override
        public boolean isReady()
        {
            return true;
        }

        /**
         * Set a listener
         * 
         * @param writeListener 
         */
        @Override
        public void setWriteListener(
                WriteListener writeListener )
        {
            throw new UnsupportedOperationException(); // FIXME?
        }

        /**
         * The underlying stream.
         */
        protected OutputStream theDelegate;
    }
}
