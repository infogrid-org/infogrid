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
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.logging.Log;

/**
 * A section in a StructuredResponse.
 */
public class StructuredResponseSection
        implements
            HasHeaderPreferences
{
    private static final Log log = Log.getLogInstance( StructuredResponseSection.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param maxProblems the maximum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     * @return the created StructuredResponseSection
     */
    public static StructuredResponseSection create(
            int maxProblems,
            int maxInfoMessages )
    {
        return new StructuredResponseSection( maxProblems, maxInfoMessages );
    }

    /**
     * Constructor.
     * 
     * @param maxProblems the maximum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     */
    protected StructuredResponseSection(
            int maxProblems,
            int maxInfoMessages )
    {
        theMaxProblems     = maxProblems;
        theMaxInfoMessages = maxInfoMessages;
    }

    /**
     * Determine whether this section is empty.
     *
     * @return true if this section is empty
     */
    public boolean isEmpty()
    {
        if( theStatus > 0 && theStatus != 200 ) {
            return false;
        }
        if( theTextWriter != null && theTextWriter.size() > 0 ) {
            return false;
        }
        if( theBinaryStream != null && theBinaryStream.size() > 0 ) {
            return false;
        }
        if( theOutgoingCookies != null && !theOutgoingCookies.isEmpty() ) {
            return false;
        }
        if( theCurrentProblems != null && !theCurrentProblems.isEmpty() ) {
            return false;
        }
        return true;
    }

    /**
     * Stream the content of this StructuredResponseSection to an HttpServletResponse.
     *
     * @param delegate the HttpServletResponse to write to
     * @return true if something was output, false otherwise
     * @throws IOException thrown if an I/O error occurred
     */
    public boolean doContentOutput(
            HttpServletResponse delegate )
        throws
            IOException
    {
        if( theTextWriter != null ) {
            delegate.getWriter().print( theTextWriter.toString() );
            return true;

        } else if( theBinaryStream != null ) {
            delegate.getOutputStream().write( theBinaryStream.toByteArray() );
            return true;

        } else {
            return false;
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
        return theCurrentProblems != null && !theCurrentProblems.isEmpty();
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
        if( theCurrentProblems == null ) {
            theCurrentProblems = new ArrayList<>();
        }
        if( theCurrentProblems.size() < theMaxProblems ) {
            theCurrentProblems.add( t );
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
        if( theCurrentProblems == null ) {
            theCurrentProblems = new ArrayList<>();
        }
        int max = Math.min( theMaxProblems - theCurrentProblems.size(), ts.length );
        for( int i=0 ; i<max ; ++i ) {
            theCurrentProblems.add( ts[i] );
        }
    }

    /**
     * Obtain the problems reported so far.
     *
     * @return problems reported so far, in sequence. For efficiency, may return null
     */
    @Override
    public Iterator<Throwable> problems()
    {
        if( theCurrentProblems != null ) {
            return theCurrentProblems.iterator();
        } else {
            return null;
        }
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
        if( theCurrentInfoMessages == null ) {
            theCurrentInfoMessages = new ArrayList<>();
        }
        if( theCurrentInfoMessages.size() < theMaxInfoMessages ) {
            theCurrentInfoMessages.add( t );
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
        if( theCurrentInfoMessages == null ) {
            theCurrentInfoMessages = new ArrayList<>();
        }
        int max = Math.min( theMaxInfoMessages - theCurrentInfoMessages.size(), ts.length );
        for( int i=0 ; i<max ; ++i ) {
            theCurrentInfoMessages.add( ts[i] );
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
        return theCurrentInfoMessages != null && !theCurrentInfoMessages.isEmpty();
    }

    /**
     * Obtain the informational messages reported so far.
     *
     * @return informational messages reported so far, in sequence
     */
    @Override
    public Iterator<Throwable> infoMessages()
    {
        if( theCurrentInfoMessages != null ) {
            return theCurrentInfoMessages.iterator();
        } else {
            return null;
        }
    }

    /**
     * Set the MIME type of the StructuredResponse.
     *
     * @param newValue the new value
     */
    public void setContentType(
            String newValue )
    {
        theContentType = newValue;
    }

    /**
     * Obtain the MIME type of the StructuredResponse.
     *
     * @return the MIME type
     */
    @Override
    public String getContentType()
    {
        return theContentType;
    }

    /**
     * Add a cookie to the response.
     *
     * @param newCookie the new cookie
     */
    public void addCookie(
            Cookie newCookie )
    {
        if( theOutgoingCookies == null ) {
            theOutgoingCookies = new HashMap<>();
        }
        Cookie found = theOutgoingCookies.put( newCookie.getName(), newCookie );

        if( found != null ) {
            log.error( "Setting the same cookie again: " + newCookie + " vs. " + found );
        }
    }

    /**
     * Convenience method to delete a cookie with this response.
     *
     * @param name name of the Cookie to delete
     */
    public void addDeleteCookie(
            String name )
    {
        Cookie newCookie = new Cookie( name, "**deleted**" );
        newCookie.setMaxAge( 0 );

        if( theOutgoingCookies == null ) {
            theOutgoingCookies = new HashMap<>();
        }

        Cookie found = theOutgoingCookies.put( name, newCookie );

        if( found != null ) {
            log.error( "Setting the same cookie again: " + name + " vs. " + found );
        }
    }

    /**
     * Obtain the getCookies.
     *
     * @return the getCookies. For efficiency, may return null
     */
    @Override
    public Collection<Cookie> getCookies()
    {
        if( theOutgoingCookies != null ) {
            return theOutgoingCookies.values();
        } else {
            return null;
        }
    }

    /**
     * Set the location header.
     *
     * @param newValue the new value
     */
    public void setLocation(
            String newValue )
    {
        theLocation = newValue;
    }

    /**
     * Obtain the location header.
     *
     * @return the currently set location header
     */
    @Override
    public String getLocation()
    {
        return theLocation;
    }

    /**
     * Set the HTTP response code.
     *
     * @param code the HTTP response code
     */
    public void setStatus(
            int code )
    {
        theStatus = code;
    }

    /**
     * Obtain the HTTP response code.
     *
     * @return the HTTP response code
     */
    @Override
    public int getStatus()
    {
        return theStatus;
    }

    /**
     * Set the locale.
     *
     * @param newValue the new value
     */
    public void setLocale(
            Locale newValue )
    {
        theLocale = newValue;
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
     * Set the character encoding.
     *
     * @param newValue the new value
     */
    public void setCharacterEncoding(
            String newValue )
    {
        theCharacterEncoding = newValue;
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
     * Set an additional header.
     *
     * @param name name of the header to add
     * @param value value of the header to add
     */
    public void setHeader(
            String name,
            String value )
    {
        if( theOutgoingHeaders == null ) {
            theOutgoingHeaders = new HashMap<>();
        }
        ArrayList<String> list = new ArrayList<>();
        list.add( value );
        theOutgoingHeaders.put( name, list );
    }
    /**
     * Add an additional header.
     *
     * @param name name of the header to add
     * @param value value of the header to add
     */
    public void addHeader(
            String name,
            String value )
    {
        if( theOutgoingHeaders == null ) {
            theOutgoingHeaders = new HashMap<>();
        }
        Collection<String> list = theOutgoingHeaders.get( name );
        if( list == null ) {
            list = new ArrayList<>();
            theOutgoingHeaders.put( name, list );
        }
        list.add( value );
    }

    /**
     * Obtain the single value of an additional header
     * 
     * @param name name of the header
     * @return the value or null
     */
    public String getHeader(
            String name )
    {
        if( theOutgoingHeaders == null ) {
            return null;
        }
        Collection<String> list = theOutgoingHeaders.get( name );
        if( list == null || list.isEmpty() ) {
            return null;
        }
        return list.iterator().next();
    }

    /**
     * Obtain the value(s) of an additional header.
     * 
     * @param name name of the header
     * @return the values, or null
     */
    public Collection<String> getHeaders(
            String name )
    {
        if( theOutgoingHeaders == null ) {
            return null;
        }
        Collection<String> list = theOutgoingHeaders.get( name );
        return list;
    }

    /**
     * Obtain the names of the additional headers.
     * 
     * @return the names
     */
    public Collection<String> getHeaderNames()
    {
        if( theOutgoingHeaders == null ) {
            return null;
        }
        return theOutgoingHeaders.keySet();
    }

    /**
     * Obtain the additional headers.
     *
     * @return the headers, as Map. May return null for efficiency.
     */
    @Override
    public Map<String,Collection<String>> getFullHeaders()
    {
        return theOutgoingHeaders;
    }

    /**
     * Determine whether an additional header with this name exists
     * 
     * @param name the name
     * @return true or false
     */
    public boolean containsHeader(
            String name )
    {
        return theOutgoingHeaders != null && theOutgoingHeaders.containsKey( name );
    }

    /**
     * Obtain the current text content of this section.
     *
     * @return the current text content of this section, or null
     */
    public String getTextContent()
    {
        if( theTextWriter != null ) {
            thePrintWriter.flush();
            return theTextWriter.toString();
        } else {
            return null;
        }
    }

    /**
     * Set the text content of this section.
     *
     * @param newValue the new content of this section
     */
    public void setTextContent(
            String newValue )
    {
        if( theBinaryStream != null ) {
            throw new IllegalStateException( "StructuredResponseSection has binary content already, cannot set text content" );
        }
        theTextWriter = new CharArrayWriter(); // throw the old one away
        thePrintWriter = new PrintWriter( theTextWriter );
        theTextWriter.append( newValue );
    }

    /**
     * Append to the content of this section.
     *
     * @param toAppend the content to append to this section
     */
    public void appendTextContent(
            String toAppend )
    {
        if( theBinaryStream != null ) {
            throw new IllegalStateException( "StructuredResponseSection has binary content already, cannot append text content" );
        }
        if( theTextWriter == null ) {
            theTextWriter = new CharArrayWriter();
            thePrintWriter = new PrintWriter( theTextWriter );
        }
        theTextWriter.append( toAppend );
    }

    /**
     * Determine whether this section contains this content fragment already.
     *
     * @param testContent the content fragment to test
     * @return true if this section containsContent the testContent already
     */
    public boolean containsTextContent(
            String testContent )
    {
        if( theTextWriter == null ) {
            return false;
        } else {
            thePrintWriter.flush();
            int found = theTextWriter.toString().indexOf( testContent );
            return found >= 0;
        }
    }

    /**
     * Obtain the current binary content of this section.
     * 
     * @return the current content of this section, or null
     */
    public byte [] getBinaryContent()
    {
        if( theBinaryStream != null ) {
            return theBinaryStream.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * Set the binary content of this section.
     * 
     * @param newValue the new content of this section
     */
    public void setBinaryContent(
            byte [] newValue )
    {
        if( theTextWriter != null ) {
            throw new IllegalStateException( "StructuredResponseSection has text content already, cannot set binary content" );
        }
        if( theBinaryStream != null ) {
            throw new IllegalStateException( "StructuredResponseSection has binary content already, cannot set text content" );
        }
        theBinaryStream = new ByteArrayOutputStream(); // throw the old one away
        try {
            theBinaryStream.write( newValue );
        } catch( IOException ex ) {
            // cannot happen
            log.error( ex );
        }
    }
    
    /**
     * Append to the content of this section.
     * 
     * @param toAppend the content to append to this section
     */
    public void appendBinaryContent(
            byte [] toAppend )
    {
        appendBinaryContent( toAppend, toAppend.length );
    }

    /**
     * Append to the content of this section.
     * 
     * @param toAppend the content to append to this section
     * @param len   the number of bytes
     */
    public void appendBinaryContent(
            byte [] toAppend,
            int     len )
    {
        if( theTextWriter != null ) {
            throw new IllegalStateException( "StructuredResponseSection has text content already, cannot append binary content" );
        }
        if( theBinaryStream == null ) {
            theBinaryStream = new ByteArrayOutputStream();
        }
        theBinaryStream.write( toAppend, 0, len );
    }

    /**
     * Obtain a ServletOutputStream to write to this section.
     * 
     * @return the ServletOutputStream
     */
    public ServletOutputStream getOutputStream()
    {
        if( theTextWriter != null ) {
            throw new IllegalStateException( "StructuredResponseSection has text content already, cannot append binary content" );
        }
        if( theBinaryStream == null ) {
            theBinaryStream = new ByteArrayOutputStream();
        }
        return new MyServletOutputStream( theBinaryStream );
    }

    /**
     * Obtain a PrintWriter to write to this section.
     * 
     * @return the PrintWriter
     */
    public PrintWriter getWriter()
    {
        if( theBinaryStream != null ) {
            throw new IllegalStateException( "StructuredResponseSection has binary content already, cannot append text content" );
        }
        if( theTextWriter == null ) {
            theTextWriter  = new CharArrayWriter();
            thePrintWriter = new PrintWriter( theTextWriter );
        }
        return thePrintWriter;
    }

    /**
     * Convenience method to append text directly without going through Writer.
     */
    public void appendText(
            String toAppend )
    {
        getWriter().append( toAppend );
    }

    /**
     * Clears any data that exists in any buffers.
     */
    public void resetBuffer()
    {
        theTextWriter   = null;
        theBinaryStream = null;
    }

    /**
     * Copy the buffer into this StructuredResponse.
     * 
     * @param destination the HttpServletResponse to copy to
     * @throws IOException thown if an input/output error occurred
     */
    public void copyAllTo(
            StructuredResponseSection destination )
        throws
            IOException
    {
        copyHeaderItemsTo( destination );
        copyContentTo(     destination );
    }

    public void copyHeaderItemsTo(
            StructuredResponseSection destination )
        throws
            IOException
    {
        if( theStatus > 0 ) {
            destination.setStatus( theStatus );
        }
        if( theContentType != null ) {
            destination.setContentType( theContentType );
        }
        if( theLocale != null ) {
            destination.setLocale( theLocale );
        }
        if( theOutgoingHeaders != null ) {
            for( String key : theOutgoingHeaders.keySet() ) {
                for( String value : theOutgoingHeaders.get( key )) {
                    destination.addHeader( key, value );
                }
            }
        }
        if( theOutgoingCookies != null ) {
            for( Cookie c : theOutgoingCookies.values() ) {
                destination.addCookie( c );
            }
        }
    }

    public void copyContentTo(
            StructuredResponseSection destination )
        throws
            IOException
    {
        if( theTextWriter != null ) {
            thePrintWriter.flush();
            OutputStreamWriter delegate = new OutputStreamWriter( destination.getOutputStream() );
            theTextWriter.writeTo( delegate );
            delegate.flush();

        } else if( theBinaryStream != null ) {
            theBinaryStream.writeTo( destination.getOutputStream() );
        } else {
            // do nothing
        }
        destination.getOutputStream().flush();
    }

    /**
     * Copy the buffer into this HttpServletResponse.
     * 
     * @param destination the HttpServletResponse to copy to
     * @throws IOException thown if an input/output error occurred
     */
    public void copyAllTo(
            HttpServletResponse destination )
        throws
            IOException
    {
        copyHeaderItemsTo( destination );
        copyContentTo(     destination );
    }
    
    public void copyHeaderItemsTo(
            HttpServletResponse destination )
        throws
            IOException
    {
        if( theStatus > 0 ) {
            destination.setStatus( theStatus );
        }
        if( theContentType != null ) {
            destination.setContentType( theContentType );
        }
        if( theLocale != null ) {
            destination.setLocale( theLocale );
        }
        if( theOutgoingHeaders != null ) {
            for( String key : theOutgoingHeaders.keySet() ) {
                for( String value : theOutgoingHeaders.get( key )) {
                    destination.addHeader( key, value );
                }
            }
        }
        if( theOutgoingCookies != null ) {
            for( Cookie c : theOutgoingCookies.values() ) {
                destination.addCookie( c );
            }
        }
    }

    public void copyContentTo(
            HttpServletResponse destination )
        throws
            IOException
    {
        if( theTextWriter != null ) {
            thePrintWriter.flush();
            OutputStreamWriter delegate = new OutputStreamWriter( destination.getOutputStream() );
            theTextWriter.writeTo( delegate );
            delegate.flush();

        } else if( theBinaryStream != null ) {
            theBinaryStream.writeTo( destination.getOutputStream() );
        } else {
            // do nothing
        }
        destination.getOutputStream().flush();
    }

    /**
     * The mime type of this section. null is default.
     */
    protected String theContentType = null;

    /**
     * The location header, if any.
     */
    protected String theLocation = null;

    /**
     * The getCookies to be sent. This is represented as a HashMap in order to easily be
     * able to detect that the same cookie has been set again.
     */
    protected HashMap<String,Cookie> theOutgoingCookies = new HashMap<>();

    /**
     * The outgoing HTTP response code. -1 stands for "not set".
     */
    protected int theStatus = -1;

    /**
     * The outgoing Locale.
     */
    protected Locale theLocale = null;

    /**
     * The outgoing character encoding.
     */
    protected String theCharacterEncoding = null;

    /**
     * The outgoing headers.
     */
    protected HashMap<String,Collection<String>> theOutgoingHeaders;

    /**
     * The current problems, in sequence of occurrence.
     */
    protected ArrayList<Throwable> theCurrentProblems;

    /**
     * The current informational messages, in sequence of occurrence.
     */
    protected ArrayList<Throwable> theCurrentInfoMessages;

    /**
     * The maximum number of problems to store in this section.
     */
    protected int theMaxProblems;

    /**
     * The maximum number of informational messages to store in this section.
     */
    protected int theMaxInfoMessages;

    /**
     * Text content of this section, if any.
     * This is mutually exclusive with theBinaryContent.
     */
    protected CharArrayWriter theTextWriter;

    /**
     * Delegates to theTextWriter.
     */
    protected PrintWriter thePrintWriter;

    /**
     * Binary content of this section, if any.
     * This is mutually exclusive with theTextWriter.
     */
    protected ByteArrayOutputStream theBinaryStream;


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
    }}
