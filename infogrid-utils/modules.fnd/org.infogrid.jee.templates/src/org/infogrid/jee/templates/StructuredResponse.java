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

package org.infogrid.jee.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.infogrid.jee.sane.SaneServletRequest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

/**
 * Encapsulates the content of an HTTP response in structured form.
 */
public class StructuredResponse
        implements
            HasHeaderPreferences
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
     * Obtain the default text section.
     * 
     * @return the default text section
     */
    public TextStructuredResponseSection getDefaultTextSection()
    {
        return getTextSection( TextStructuredResponseSectionTemplate.DEFAULT_SECTION );
    }

    /**
     * Obtain the default binary section.
     * 
     * @return the default binary section
     */
    public BinaryStructuredResponseSection getDefaultBinarySection()
    {
        return getBinarySection( BinaryStructuredResponseSectionTemplate.DEFAULT_SECTION );
    }

    /**
     * Obtain a text section.
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
        if( ret == null ) {
            ret = TextStructuredResponseSection.create( template );
            theTextSections.put( template, ret );
        }
        return ret;
    }

    /**
     * Obtain a binary section.
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
        if( ret == null ) {
            ret = BinaryStructuredResponseSection.create( template );
            theBinarySections.put( template, ret );
        }
        return ret;
    }

    /**
     * Find a text section by name.
     * 
     * @param name the name of the section
     * @return the section template, if any
     */
    public TextStructuredResponseSection findTextSectionByName(
            String name )
    {
        TextStructuredResponseSectionTemplate template = findTextSectionTemplateByName( name );
        if( template == null ) {
            return null;
        }
        TextStructuredResponseSection ret = getTextSection( template );
        return ret;
    }

    /**
     * Find a binary section by name.
     * 
     * @param name the name of the section
     * @return the section template, if any
     */
    public BinaryStructuredResponseSection findBinarySectionByName(
            String name )
    {
        BinaryStructuredResponseSectionTemplate template = findBinarySectionTemplateByName( name );
        if( template == null ) {
            return null;
        }
        BinaryStructuredResponseSection ret = getBinarySection( template );
        return ret;
    }

    /**
     * Find a text section template by name.
     * 
     * @param name the name of the section
     * @return the section template, if any
     */
    public TextStructuredResponseSectionTemplate findTextSectionTemplateByName(
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
     * Find a binary section template by name.
     * 
     * @param name the name of the section
     * @return the section template, if any
     */
    public BinaryStructuredResponseSectionTemplate findBinarySectionTemplateByName(
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
    public void reportProblem(
            Throwable t )
    {
        if( theCurrentProblems.size() <= theMaxProblems ) {
            // make sure we aren't growing this indefinitely
            theCurrentProblems.add( t );

        } else {
            log.error( "Too many problems. Ignored ", t );
        }
    }

    /**
     * Determine whether problems have been reported.
     * 
     * @return true if at least one problem has been reported
     */
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
    public List<Throwable> problems()
    {
        ArrayList<Throwable> ret =  new ArrayList<Throwable>();
        ret.addAll( theCurrentProblems );

        return ret;
    }

    /**
     * Obtain the problems reported so far here and in all contained sections.
     * 
     * @return problems reported so far
     */
    public List<Throwable> problemsAggregate()
    {
        ArrayList<Throwable> ret =  new ArrayList<Throwable>();
        ret.addAll( theCurrentProblems );

        for( TextStructuredResponseSection current : theTextSections.values() ) {
            ret.addAll( current.problems() );
        }
        for( BinaryStructuredResponseSection current : theBinarySections.values() ) {
            ret.addAll( current.problems() );
        }

        return ret;
    }

    /**
     * Obtain the desired MIME type.
     * 
     * @return the desired MIME type
     */
    public String getMimeType()
    {
        return theMimeType;
    }

    /**
     * Obtain the Cookies.
     * 
     * @return the Cookies
     */
    public Collection<Cookie> getCookies()
    {
        return theCookies;
    }

    /**
     * Obtain the location header.
     * 
     * @return the currently set location header
     */
    public String getLocation()
    {
        return theLocation;
    }

    /**
     * Obtain the HTTP response code.
     * 
     * @return the HTTP response code
     */
    public int getHttpResponseCode()
    {
        return theHttpResponseCode;
    }
    
    /**
     * Obtain the locale.
     * 
     * @return the locale
     */
    public Locale getLocale()
    {
        return theLocale;
    }

    /**
     * Obtain the character encoding.
     * 
     * @return the character encoding
     */
    public String getCharacterEncoding()
    {
        return theCharacterEncoding;
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
     * Set the Yadis header.
     * 
     * @param value the value of the Yadis header.
     */
    public void setYadisHeader(
            String value )
    {
        theYadisHeader = value;
    }

    /**
     * Obtain the Yadis header, if any.
     * 
     * @return the Yadis header
     */
    public String getYadisHeader()
    {
        return theYadisHeader;
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
     * The underlying servlet response.
     */
    protected HttpServletResponse theDelegate;

    /**
     * The sections of the response that are represented as text.
     */
    protected HashMap<TextStructuredResponseSectionTemplate,TextStructuredResponseSection> theTextSections
            = new HashMap<TextStructuredResponseSectionTemplate,TextStructuredResponseSection>();

    /**
     * The sections of the response that are represented as binary.
     */
    protected HashMap<BinaryStructuredResponseSectionTemplate,BinaryStructuredResponseSection> theBinarySections
            = new HashMap<BinaryStructuredResponseSectionTemplate,BinaryStructuredResponseSection>();

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
    protected ArrayList<Throwable> theCurrentProblems = new ArrayList<Throwable>();

    /**
     * The maximum number of problems to store in this type of section.
     */
    protected int theMaxProblems;

    /**
     * The desired MIME type. Currently not used.
     */
    protected String theMimeType;
    
    /**
     * The desired cookies. Currently not used.
     */
    protected Collection<Cookie> theCookies = new ArrayList<Cookie>();

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
     * The Yadis header content, if any.
     */
    protected String theYadisHeader;
    
    /**
     * Name of the request attribute that contains the StructuredResponse. Make sure
     * this constant does not contain any characters that might make some processor
     * interpret it as being an expression.
     */
    public static final String STRUCTURED_RESPONSE_ATTRIBUTE_NAME
            = SaneServletRequest.classToAttributeName( StructuredResponse.class );

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( StructuredResponse.class );

    /**
     * The default maximum number of problems to store.
     */
    public static final int DEFAULT_MAX_PROBLEMS = theResourceHelper.getResourceIntegerOrDefault( "DefaultMaxProblems", 20 );
}
