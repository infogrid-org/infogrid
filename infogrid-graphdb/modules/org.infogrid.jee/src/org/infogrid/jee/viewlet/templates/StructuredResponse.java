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

package org.infogrid.jee.viewlet.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.util.LocalizedObjectFormatter;
import org.infogrid.util.logging.Log;

/**
 * Encapsulates the content of an HTTP response in structured form.
 */
public class StructuredResponse
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
        StructuredResponse ret = new StructuredResponse( delegate, servletContext );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     *
     * @param delegate the underlying HttpServletResponse
     * @param servletContext the ServletContext in which the StructuredResponse is created
     */
    protected StructuredResponse(
            HttpServletResponse delegate,
            ServletContext      servletContext )
    {
        theDelegate       = delegate;
        theServletContext = servletContext;
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
     * Set the MIME type of the StructuredResponse.
     * 
     * @param newValue the new value
     */
    public void setMimeType(
            String newValue )
    {
        theMimeType = newValue;
    }
    
    /**
     * Obtain the MIME type of the StructuredResponse.
     * 
     * @return the MIME type
     */
    public String getMimeType()
    {
        return theMimeType;
    }

    /**
     * Add a cookie to the response.
     * 
     * @param newCookie the new cookie
     */
    public void addNewCookie(
            Cookie newCookie )
    {
        Cookie found = theOutgoingCookies.put( newCookie.getName(), newCookie );
        
        if( found != null ) {
            log.error( "Setting the same cookie again: " + newCookie + " vs. " + found );
        }
    }

    /**
     * Obtain the cookies.
     * 
     * @return the cookies
     */
    public Collection<Cookie> cookies()
    {
        return theOutgoingCookies.values();
    }

    /**
     * Set the content of the default section.
     * 
     * @param newValue the new content of the section
     */
    public void setDefaultSectionContent(
            String newValue )
    {
        setSectionContent( TextStructuredResponseSection.DEFAULT_SECTION, newValue );
    }

    /**
     * Set the content of the default section.
     * 
     * @param newValue the new content of the section
     */
    public void setDefaultSectionContent(
            byte [] newValue )
    {
        setSectionContent( BinaryStructuredResponseSection.DEFAULT_SECTION, newValue );
    }

    /**
     * Set the content of a section.
     * 
     * @param section the section
     * @param newValue the new content of the section
     */
    public void setSectionContent(
            TextStructuredResponseSection section,
            String                        newValue )
    {
        theTextSections.put( section, newValue );
    }

    /**
     * Append to the content of a section.
     * 
     * @param section the section
     * @param toAppend the new content to append to the section
     */
    public void appendToSectionContent(
            TextStructuredResponseSection section,
            String                        toAppend )
    {
        String current = theTextSections.get( section );
        String newValue;
        if( current != null ) {
            newValue = current + toAppend;
        } else {
            newValue = toAppend;
        }
        theTextSections.put( section, newValue );
    }

    /**
     * Obtain the content of a section.
     * 
     * @param section the section
     * @return the content of the section, or null
     */
    public String getSectionContent(
            TextStructuredResponseSection section )
    {
        String ret = theTextSections.get( section );
        return ret;
    }

    /**
     * Set the content of a section.
     * 
     * @param section the section
     * @param newValue the new content of the section
     */
    public void setSectionContent(
            BinaryStructuredResponseSection section,
            byte []                         newValue )
    {
        theBinarySections.put( section, newValue );
    }

    /**
     * Obtain the content of a section.
     * 
     * @param section the section
     * @return the content of the section, or null
     */
    public byte [] getSectionContent(
            BinaryStructuredResponseSection section )
    {
        byte [] ret = theBinarySections.get( section );
        return ret;
    }

    /**
     * Find a section by name.
     * 
     * @param name the name of the section
     * @return the section, if any
     */
    public StructuredResponseSection findSectionByName(
            String name )
    {
        for( StructuredResponseSection current : theTextSections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        for( StructuredResponseSection current : theBinarySections.keySet() ) {
            if( name.equals( current.getSectionName() )) {
                return current;
            }
        }
        return null;
    }

    /**
     * Report a problem that should be shown to the user.
     *
     * @param t the Throwable indicating the problem
     */
    public void reportProblem(
            Throwable t )
    {
        if( theCurrentProblems.size() < 20 ) {
            // make sure we aren't growing this indefinitely
            theCurrentProblems.add( t );

            String report = createStackTrace( t );
            appendToSectionContent( TextStructuredResponseSection.ERROR_SECTION, report );

        } else {
            log.error( "Too many problems. Ignored ", t );
        }
    }

    /**
     * Create a String that represents the Stacktrace. Subclasses may override this and return an
     * empty String.
     *
     * @param t the Throwable for the stack trace
     * @return the String
     */
    protected String createStackTrace(
            Throwable t )
    {
        StringBuilder        buf   = new StringBuilder();
        StackTraceElement [] trace = t.getStackTrace();

        boolean isHtml = theMimeType != null && theMimeType.indexOf( "html" ) >= 0;
                // "text/html".equals( theMimeType ) || "application/xhtml+xml".equals( theMimeType );
        
        if( isHtml ) {
            buf.append( "<div class=\"error\">\n<h1>" );
        }
        buf.append( t.getMessage() );
        if( isHtml ) {
            buf.append( "</h1>" );
        }

        for( int i=0 ; i<trace.length ; ++i ) {
            buf.append( "\n" );
            if( isHtml ) {
                buf.append( "\n<div class=\"stacktracelement\">" );
            } else {
                buf.append( "    " );
            }
            buf.append( "at " ).append( trace[i] );
            if( isHtml ) {
                buf.append( "</div>" );
            }
        }

        // now causes.
        for( Throwable cause = findCause( t ) ; cause != null ; cause = findCause( cause )) {
            buf.append( "\n" );
            if( isHtml ) {
                buf.append( "<h2>" );
            }
            buf.append( cause.toString() );
            if( isHtml ) {
                buf.append( "</h2>" );
            }
            
            StackTraceElement [] causeTrace = cause.getStackTrace();
            for( int i=0 ; i<causeTrace.length ; ++i ) {
                buf.append( "\n" );
                if( isHtml ) {
                    buf.append( "\n<div class=\"stacktracelement\">" );
                } else {
                    buf.append( "    " );
                }
                buf.append( "at " ).append( causeTrace[i] );
                if( isHtml ) {
                    buf.append( "</div>" );
                }
            }
        }
        if( isHtml ) {
            buf.append( "</div>\n" );
        }
        return buf.toString();
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
     * Obtain a LocalizedObjectFormatter. This can be overridden by subclasses.
     * 
     * @param request the incoming HttpServletRequest
     * @param response the outgoing HttpServletResponse
     * @return the LocalizedObjectFormatter to use with this application
     */
    protected LocalizedObjectFormatter obtainLocalizedExceptionObjectFormatter(
            HttpServletRequest  request,
            HttpServletResponse response )
    {
        if( theExceptionObjectFormatter == null ) {
            theExceptionObjectFormatter = new HtmlObjectFormatter( request.getContextPath() + "/" );
        }
        return theExceptionObjectFormatter;
    }

    /**
     * Obtain the ServletContext whithin this response is being assembled.
     * 
     * @return the ServletContext
     */
    public ServletContext getServletContext()
    {
        return theServletContext;
    }

    /**
     * The underlying servlet response.
     */
    protected HttpServletResponse theDelegate;

    /**
     * The mime type of the response.
     */
    protected String theMimeType;

    /**
     * The cookies to be sent. This is represented as a HashMap in order to easily be
     * able to detect that the same cookie has been set again.
     */
    protected HashMap<String,Cookie> theOutgoingCookies = new HashMap<String,Cookie>();

    /**
     * The sections of the response that are represented as text.
     */
    protected HashMap<TextStructuredResponseSection,String> theTextSections
            = new HashMap<TextStructuredResponseSection,String>();

    /**
     * The sections of the response that are represented as binary.
     */
    protected HashMap<BinaryStructuredResponseSection,byte []> theBinarySections
            = new HashMap<BinaryStructuredResponseSection,byte []>();

    /**
     * The current problems, in sequence of occurrence.
     */
    protected ArrayList<Throwable> theCurrentProblems = new ArrayList<Throwable>();

    /**
     * The LocalizedObjectFormatter to use for error reporting.
     */
    protected LocalizedObjectFormatter theExceptionObjectFormatter;

    /**
     * The ServletContext within which this response is assembled.
     */
    protected ServletContext theServletContext;
}
