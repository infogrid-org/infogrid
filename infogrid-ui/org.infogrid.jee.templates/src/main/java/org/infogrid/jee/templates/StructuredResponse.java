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

package org.infogrid.jee.templates;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.ProblemReporter;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequestUtils;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;

/**
 * Encapsulates the content of an HTTP response in structured form.
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
     * @param delegate the underlying HttpServletResponse
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @return the created StructuredResponse
     */
    public static StructuredResponse create(
            HttpServletResponse delegate,
            ServletContext      servletContext )
    {
        StructuredResponse ret = new StructuredResponse( delegate, servletContext, DEFAULT_MAX_PROBLEMS, DEFAULT_MAX_INFO_MESSAGES );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param delegate the underlying HttpServletResponse
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @param maxProblems the maxmimum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     * @return the created StructuredResponse
     */
    public static StructuredResponse create(
            HttpServletResponse delegate,
            ServletContext      servletContext,
            int                 maxProblems,
            int                 maxInfoMessages )
    {
        StructuredResponse ret = new StructuredResponse( delegate, servletContext, maxProblems, maxInfoMessages );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param delegate the underlying HttpServletResponse
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @param maxProblems the maximum number of problems to report in this StructuredResponse
     * @param maxInfoMessages the maximum number of informational messages to report in this StructuredResponse
     */
    protected StructuredResponse(
            HttpServletResponse delegate,
            ServletContext      servletContext,
            int                 maxProblems,
            int                 maxInfoMessages )
    {
        theDelegate        = delegate;
        theServletContext  = servletContext;
        theMaxProblems     = maxProblems;
        theMaxInfoMessages = maxInfoMessages;
    }

    /**
     * Obtain the underlying HttpServletResponse.
     *
     * @return the delegate
     */
    public HttpServletResponse getDelegate()
    {
        return theDelegate;
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
            TextStructuredResponseSectionTemplate template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        TextStructuredResponseSection ret = theTextSections.get( template );
        if( ret == null ) {
            ret = TextStructuredResponseSection.create( template );
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
            BinaryStructuredResponseSectionTemplate template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        BinaryStructuredResponseSection ret = theBinarySections.get( template );
        if( ret == null ) {
            ret = BinaryStructuredResponseSection.create( template );
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
            TextStructuredResponseSectionTemplate template )
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
            BinaryStructuredResponseSectionTemplate template )
    {
        if( template == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        BinaryStructuredResponseSection ret = theBinarySections.get( template );
        return ret;
    }

    /**
     * Obtain a text section by name; if the section does not exist, create it.
     *
     * @param name the name of the section
     * @return the section
     */
    public TextStructuredResponseSection obtainTextSectionByName(
            String name )
    {
        TextStructuredResponseSectionTemplate template = obtainTextSectionTemplateByName( name );
        if( template == null ) {
            return null;
        }
        TextStructuredResponseSection ret = obtainTextSection( template );
        return ret;
    }

    /**
     * Obtain a binary section by name; if the section does not exist, create it.
     *
     * @param name the name of the section
     * @return the section
     */
    public BinaryStructuredResponseSection obtainBinarySectionByName(
            String name )
    {
        BinaryStructuredResponseSectionTemplate template = obtainBinarySectionTemplateByName( name );
        if( template == null ) {
            return null;
        }
        BinaryStructuredResponseSection ret = obtainBinarySection( template );
        return ret;
    }

    /**
     * Obtain a text section template by name; if it does not exist, create it.
     *
     * @param name the name of the section
     * @return the section template
     */
    public TextStructuredResponseSectionTemplate obtainTextSectionTemplateByName(
            String name )
    {
        for( TextStructuredResponseSectionTemplate current : theTextSections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        TextStructuredResponseSectionTemplate ret
                = createTextStructuredResponseSectionTemplate( name );
        return ret;
    }

    /**
     * Obtain a binary section template by name; if it does not exist, create it.
     *
     * @param name the name of the section
     * @return the section template
     */
    public BinaryStructuredResponseSectionTemplate obtainBinarySectionTemplateByName(
            String name )
    {
        for( BinaryStructuredResponseSectionTemplate current : theBinarySections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        BinaryStructuredResponseSectionTemplate ret
                = createBinaryStructuredResponseSectionTemplate( name );
        return ret;
    }

    /**
     * Get a text section template by name; if it does not exist, return null.
     *
     * @param name the name of the section
     * @return the section template, if any
     */
    public TextStructuredResponseSectionTemplate getTextSectionTemplateByName(
            String name )
    {
        for( TextStructuredResponseSectionTemplate current : theTextSections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        return null;
    }

    /**
     * Get a binary section template by name; if it does not exist, return null.
     *
     * @param name the name of the section
     * @return the section template, if any
     */
    public BinaryStructuredResponseSectionTemplate getBinarySectionTemplateByName(
            String name )
    {
        for( BinaryStructuredResponseSectionTemplate current : theBinarySections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        return null;
    }

    /**
     * Obtain an Iterator over the text section templates currently used.
     *
     * @return the Iterator
     */
    public Iterator<TextStructuredResponseSectionTemplate> textSectionTemplateIterator()
    {
        return theTextSections.keySet().iterator();
    }

    /**
     * Obtain an Iterator over the binary section templates currently used.
     *
     * @return the Iterator
     */
    public Iterator<BinaryStructuredResponseSectionTemplate> binarySectionTemplateIterator()
    {
        return theBinarySections.keySet().iterator();
    }

    /**
     * Create a TextStructuredResponseSectionTemplate for which only a name is known.
     *
     * @param name the name of the to-be-created TextStructuredResponseSectionTemplate
     * @return the created TextStructuredResponseSectionTemplate
     */
    protected TextStructuredResponseSectionTemplate createTextStructuredResponseSectionTemplate(
            String name )
    {
        TextStructuredResponseSectionTemplate ret;
        if( TEXT_DEFAULT_SECTION.getSectionName().equals( name )) {
            ret = TEXT_DEFAULT_SECTION;
        } else {
            ret = TextStructuredResponseSectionTemplate.create( name );
        }
        return ret;
    }

    /**
     * Create a BinaryStructuredResponseSectionTemplate for which only a name is known.
     *
     * @param name the name of the to-be-created BinaryStructuredResponseSectionTemplate
     * @return the created BinaryStructuredResponseSectionTemplate
     */
    protected BinaryStructuredResponseSectionTemplate createBinaryStructuredResponseSectionTemplate(
            String name )
    {
        BinaryStructuredResponseSectionTemplate ret;
        if( BINARY_DEFAULT_SECTION.getSectionName().equals( name )) {
            ret = BINARY_DEFAULT_SECTION;
        } else {
            ret = BinaryStructuredResponseSectionTemplate.create( name );
        }
        return ret;
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
        return theLocation;
    }

    /**
     * Set a redirect location.
     *
     * @param newValue the new value
     */
    public void setLocation(
            String newValue )
    {
        theLocation = newValue;
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
        return theDelegate.containsHeader( name );
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType()
    {
        return theDelegate.getContentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream()
        throws
            IOException
    {
        return theDelegate.getOutputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter()
        throws
            IOException
    {
        return theDelegate.getWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(
            int len )
    {
        theDelegate.setContentLength( len );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLengthLong(
            long len )
    {
        theDelegate.setContentLengthLong( len );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(
            String type )
    {
        theDelegate.setContentType( type );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferSize(
            int size )
    {
        theDelegate.setBufferSize( size );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize()
    {
        return theDelegate.getBufferSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushBuffer()
        throws
            IOException
    {
        theDelegate.flushBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetBuffer()
    {
        theDelegate.resetBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted()
    {
        return theDelegate.isCommitted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        theDelegate.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeURL(
            String url )
    {
        return theDelegate.encodeURL( url );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeRedirectURL(
            String url )
    {
        return theDelegate.encodeRedirectURL( url );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeUrl(
            String url )
    {
        return theDelegate.encodeUrl( url );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeRedirectUrl(
            String url )
    {
        return theDelegate.encodeRedirectUrl( url );
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
        theDelegate.sendError( sc, msg );
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
        theDelegate.sendError( sc );
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
        theDelegate.sendRedirect( location );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateHeader(
            String name,
            long   date )
    {
        theDelegate.setDateHeader( name, date );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDateHeader(
            String name,
            long   date )
    {
        theDelegate.addDateHeader( name, date );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeader(
            String name,
            String value )
    {
        theDelegate.setHeader( name, value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntHeader(
            String name,
            int    value )
    {
        theDelegate.setIntHeader( name, value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIntHeader(
            String name,
            int    value )
    {
        theDelegate.addIntHeader( name, value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(
            int sc )
    {
        theDelegate.setStatus( sc );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(
            int    sc,
            String sm )
    {
        theDelegate.setStatus( sc, sm );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus()
    {
        return theDelegate.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(
            String name )
    {
        return theDelegate.getHeader( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaders(
            String name )
    {
        return theDelegate.getHeaders( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaderNames()
    {
        return theDelegate.getHeaderNames();
    }

    /**
     * Determine whether this StructuredResponse is empty.
     *
     * @return true if it is empty
     */
    public boolean isEmpty()
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
        for( TextStructuredResponseSectionTemplate key : theTextSections.keySet() ) {
            TextStructuredResponseSection value = theTextSections.get(  key );
            if( !value.isEmpty() ) {
                return false;
            }
        }
        for( BinaryStructuredResponseSectionTemplate key : theBinarySections.keySet() ) {
            BinaryStructuredResponseSection value = theBinarySections.get(  key );
            if( !value.isEmpty() ) {
                return false;
            }
        }
        return true;
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
                    "theLocation",
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
                    theLocation,
                    theHttpResponseCode,
                    theLocale,
                    theCharacterEncoding,
                    theOutgoingHeaders
                });
    }

    /**
     * The underlying servlet response.
     */
    protected HttpServletResponse theDelegate;

    /**
     * The sections of the response that are represented as text.
     */
    protected HashMap<TextStructuredResponseSectionTemplate,TextStructuredResponseSection> theTextSections
            = new HashMap<>();

    /**
     * The sections of the response that are represented as binary.
     */
    protected HashMap<BinaryStructuredResponseSectionTemplate,BinaryStructuredResponseSection> theBinarySections
            = new HashMap<>();

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
     * The desired location header.
     */
    protected String theLocation;

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
     * The outgoing headers.
     */
    protected HashMap<String,String[]> theOutgoingHeaders = new HashMap<>();

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
     * The single default section for text content. Output will be written into this section
     * unless otherwise specified.
     */
    public static final TextStructuredResponseSectionTemplate TEXT_DEFAULT_SECTION
            = TextStructuredResponseSectionTemplate.create( "text-default" );

    /**
     * The single default section for binary content. Binary output will be written into this section
     * unless otherwise specified.
     */
    public static final BinaryStructuredResponseSectionTemplate BINARY_DEFAULT_SECTION
            = BinaryStructuredResponseSectionTemplate.create( "binary-default" );

    /**
     * The section representing the head of an HTML document.
     */
    public static final TextHtmlStructuredResponseSectionTemplate HTML_HEAD_SECTION
            = TextHtmlStructuredResponseSectionTemplate.create( "html-head" );

    /**
     * The section representing the title of an HTML document. While this could be considered
     * a part of the head of the HTML document, in practice it has turned out to be useful if
     * it is kept separate.
     */
    public static final TextHtmlStructuredResponseSectionTemplate HTML_TITLE_SECTION
            = TextHtmlStructuredResponseSectionTemplate.create( "html-title" );

    /**
     * The section representing the messages section of an HTML document.
     */
    public static final TextHtmlStructuredResponseSectionTemplate HTML_MESSAGES_SECTION
            = TextHtmlStructuredResponseSectionTemplate.create( "html-messages" );

    /**
     * The section representing the main menu in an HTML document. Many HTML documents don't have
     * such a section, but it is common enough that we make it explicit here.
     */
    public static final TextHtmlStructuredResponseSectionTemplate HTML_MAIN_MENU_SECTION
            = TextHtmlStructuredResponseSectionTemplate.create( "html-main-menu" );}
