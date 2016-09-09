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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.web.templates;

import java.io.IOException;
import java.io.PrintWriter;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.web.ProblemReporter;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * Encapsulates the content of an HTTP response in structured form. The structure
 * consists of at least one StructuredResponseSection (the default) and any number
 * of additional named StructuredResponseSections.
 * 
 * Once assembled, a StructuredResponse is combined with a StructuredResponseTemplate
 * to produce the HTTP response.
 * 
 * It is also acts as a HttpServletResponse that buffers its content by writing
 * to the default StructuredResponseSection.
 */
public class StructuredResponse
        implements
            HasHeaderPreferences,
            ProblemReporter,
            HttpServletResponse,
            CanBeDumped
{
    /**
     * Factory method.
     *
     * @param servletContext the ServletContext in which the StructuredResponse is created
     * @return the created StructuredResponse
     */
    public static StructuredResponse create(
            ServletContext servletContext )
    {
        StructuredResponse ret = new StructuredResponse( servletContext );
        ret.setDefaultSection( ret.obtainSection(StructuredResponse.MAIN_SECTION ));

        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param servletContext the ServletContext in which the StructuredResponse is created
     */
    protected StructuredResponse(
            ServletContext servletContext )
    {
        theServletContext = servletContext;
    }

    /**
     * Obtain the current default section.
     *
     * @return the default section
     */
    public StructuredResponseSection getDefaultSection()
    {
        return theDefaultSection;
    }

    /**
     * Set the default section to something else.
     * 
     * @param newValue the new default section
     */
    public void setDefaultSection(
            StructuredResponseSection newValue )
    {
        theDefaultSection = newValue;
    }

    /**
     * Create a new section, make it the default, and return
     * the previously set default section.
     * 
     * @return the old default section
     */
    public StructuredResponseSection swapInNewDefaultSection()
    {
        StructuredResponseSection ret = theDefaultSection;
        
        theDefaultSection = obtainSection( String.format( "temp-default-%d", theUnnamedSectionIndex++ ));
        
        return ret;
    }

    /**
     * Obtain a section; if the section does not exist, create it.
     *
     * @param name the section name
     * @return the section
     */
    public StructuredResponseSection obtainSection(
            String name )
    {
        if( name == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        StructuredResponseSection ret = theSections.get( name );
        if( ret == null ) {
            ret = StructuredResponseSection.create( name, DEFAULT_MAX_PROBLEMS, DEFAULT_MAX_INFO_MESSAGES );
            theSections.put( name, ret );
        }
        return ret;
    }

    /**
     * Get a section; if the section does not exist, return null.
     *
     * @param name the section name
     * @return the section, or null
     */
    public StructuredResponseSection getSection(
            String name )
    {
        if( name == null ) {
            throw new NullPointerException( "Cannot obtain null section" );
        }
        StructuredResponseSection ret = theSections.get( name );
        return ret;
    }

    /**
     * Obtain an Iterator over the text section names currently used.
     *
     * @return the Iterator
     */
    public Iterator<String> sectionNameIterator()
    {
        return theSections.keySet().iterator();
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
     * Report a problem that should be shown to the user.
     *
     * @param t the Throwable indicating the problem
     */
    @Override
    public void reportProblem(
            Throwable t )
    {
        getDefaultSection().reportProblem( t );
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
        getDefaultSection().reportProblems( ts );
    }

    /**
     * Determine whether problems have been reported.
     *
     * @return true if at least one problem has been reported
     */
    @Override
    public boolean haveProblemsBeenReported()
    {
        return getDefaultSection().haveProblemsBeenReported();
    }

    /**
     * Determine whether problems have been reported here and in all contained sections.
     *
     * @return true if at least one problem has been reported
     */
    public boolean haveProblemsBeenReportedAggregate()
    {
        for( StructuredResponseSection current : theSections.values() ) {
            if( current.haveProblemsBeenReported() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the problems reported so far.
     *
     * @return problems reported so far, in sequence. For efficiency, may return null;
     */
    @Override
    public Iterator<Throwable> problems()
    {
        return getDefaultSection().problems();
    }

    /**
     * Obtain the problems reported so far here and in all contained sections.
     *
     * @return problems reported so far
     */
    public Iterator<Throwable> problemsAggregate()
    {
        ArrayList<Throwable> ret =  new ArrayList<>();

        for( StructuredResponseSection current : theSections.values() ) {
            Iterator<Throwable> problemIter = current.problems();
            if( problemIter != null ) {
                while( problemIter.hasNext() ) {
                    ret.add( problemIter.next() );
                }
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
        getDefaultSection().reportInfoMessage( t );
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
        getDefaultSection().reportInfoMessages( ts );
    }

    /**
     * Determine whether informational messages have been reported.
     *
     * @return true if at least one informational message has been reported
     */
    @Override
    public boolean haveInfoMessagesBeenReported()
    {
        return getDefaultSection().haveInfoMessagesBeenReported();
    }

    /**
     * Determine whether informational messages have been reported here and in all contained sections.
     *
     * @return true if at least one informational message has been reported
     */
    public boolean haveInfoMessagesBeenReportedAggregate()
    {
        for( StructuredResponseSection current : theSections.values() ) {
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
        return getDefaultSection().infoMessages();
    }

    /**
     * Obtain the informational messages reported so far here and in all contained sections.
     *
     * @return informational messages reported so far
     */
    public Iterator<Throwable> infoMessagesAggregate()
    {
        ArrayList<Throwable> ret = new ArrayList<>();

        for( StructuredResponseSection current : theSections.values() ) {
            Iterator<Throwable> infoMessageIter = current.infoMessages();
            while( infoMessageIter.hasNext() ) {
                ret.add( infoMessageIter.next() );
            }
        }

        return ret.iterator();
    }

    /**
     * Obtain the Cookies.
     *
     * @return the Cookies
     */
    @Override
    public Collection<Cookie> getCookies()
    {
        return getDefaultSection().getCookies();
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
        getDefaultSection().addCookie( toAdd );
    }

    /**
     * Obtain the location header.
     *
     * @return the currently set location header
     */
    @Override
    public String getLocation()
    {
        return getDefaultSection().getLocation();
    }

    /**
     * Set a redirect location.
     *
     * @param newValue the new value
     */
    public void setLocation(
            String newValue )
    {
        getDefaultSection().setLocation( newValue );
    }

    /**
     * Obtain the HTTP response code.
     *
     * @return the HTTP response code
     */
    @Override
    public int getStatus()
    {
        return getDefaultSection().getStatus();
    }

    /**
     * Set the desired HTTP response code.
     *
     * @param newValue the new value
     */
    public void setHttpResponseCode(
            int newValue )
    {
        getDefaultSection().setStatus( newValue );
    }

    /**
     * Obtain the locale.
     *
     * @return the locale
     */
    @Override
    public Locale getLocale()
    {
        return getDefaultSection().getLocale();
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
        getDefaultSection().setLocale( newValue );
    }

    /**
     * Obtain the character encoding.
     *
     * @return the character encoding
     */
    @Override
    public String getCharacterEncoding()
    {
        return getDefaultSection().getCharacterEncoding();
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
        getDefaultSection().setCharacterEncoding( newValue );
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
        getDefaultSection().addHeader( name, value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,Collection<String>> getFullHeaders()
    {
        return getDefaultSection().getFullHeaders();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsHeader(
            String name )
    {
        return getDefaultSection().containsHeader( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType()
    {
        return getDefaultSection().getContentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream()
        throws
            IOException
    {
        return getDefaultSection().getOutputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter()
        throws
            IOException
    {
        return getDefaultSection().getWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(
            int len )
    {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLengthLong(
            long len )
    {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(
            String type )
    {
        getDefaultSection().setContentType( type );
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
        // no op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetBuffer()
    {
        getDefaultSection().resetBuffer();
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
        getDefaultSection().resetBuffer();
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
        getDefaultSection().setStatus( sc );
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
        getDefaultSection().setStatus( sc );
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

        getDefaultSection().setLocation( location );
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
        getDefaultSection().setHeader( name, value );
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
        getDefaultSection().setStatus( sc );
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
        getDefaultSection().setStatus( sc );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(
            String name )
    {
        return getDefaultSection().getHeader( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaders(
            String name )
    {
        return getDefaultSection().getHeaders( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaderNames()
    {
        return getDefaultSection().getHeaderNames();
    }

    /**
     * Determine whether this StructuredResponse is empty.
     *
     * @return true if the structure is empty
     */
    public boolean isEmpty()
    {
        for( StructuredResponseSection value : theSections.values() ) {
            if( !value.isEmpty() ) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Copy the buffer into this HttpServletResponse.
     * 
     * @param destination the HttpServletResponse to copy to
     * @throws IOException thown if an input/output error occurred
     */
    public void copyTo(
            HttpServletResponse destination )
        throws
            IOException
    {
        getDefaultSection().copyAllTo( destination );
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
                    "theRequestedTemplateName"
                },
                new Object [] {
                    theRequestedTemplateName
                });
    }

    /**
     * The sections of the response.
     */
    protected Map<String,StructuredResponseSection> theSections = new HashMap<>();

    /**
     * The section currently identified as the default section.
     */
    protected StructuredResponseSection theDefaultSection;

    /**
     * The ServletContext within which this response is assembled.
     */
    protected ServletContext theServletContext;

    /**
     * Name of the template that is being requested, if any.
     */
    protected String theRequestedTemplateName = null;

    /**
     * Counts up for naming otherwise unnamed sections.
     */
    protected int theUnnamedSectionIndex;

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
     * Name of the main content section. By default, output will be written into this
     * section unless a different section has been specified.
     */
    public static final String MAIN_SECTION = "main";

    /**
     * Name of the section representing the head of an HTML document.
     */
    public static final String HTML_HEAD_SECTION = "html-head";

    /**
     * Name of the section representing the title of an HTML document. While this could be considered
     * a part of the head of the HTML document, in practice it has turned out to be useful if
     * it is kept separate.
     */
    public static final String HTML_TITLE_SECTION = "html-title";

    /**
     * Name of the section representing the app icon and top-headline of an HTML document.
     */
    public static final String HTML_APP_HEADER_SECTION = "html-app-header";

    /**
     * Name of the section representing the messages section of an HTML document.
     */
    public static final String HTML_MESSAGES_SECTION = "html-messages";

    /**
     * Name of the section representing the main menu in an HTML document. Many HTML
     * documents don't have such a section, but it is common enough that we make it
     * explicit here.
     */
    public static final String HTML_MAIN_MENU_SECTION = "html-main-menu";
    
    /**
     * Name of the section representing the copyright or other footer in an HTML document.
     */
    public static final String HTML_FOOTER_SECTION = "html-footer";

    /**
     * Name of the section that represents the final assembly of the output.
     */
    public static final String FINAL_ASSEMBLY_SECTION = "final-assembly";
}
