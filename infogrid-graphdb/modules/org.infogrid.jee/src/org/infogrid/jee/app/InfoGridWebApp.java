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

package org.infogrid.jee.app;

import org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet;
import org.infogrid.jee.viewlet.modelbase.AllMeshTypesViewlet;
import org.infogrid.jee.viewlet.propertysheet.PropertySheetViewlet;

import org.infogrid.context.Context;
import org.infogrid.mesh.MeshObject;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.model.traversal.TraversalDictionary;

import org.infogrid.viewlet.AbstractViewletFactory;
import org.infogrid.viewlet.DefaultViewletFactoryChoice;
import org.infogrid.viewlet.MeshObjectsToView;
import org.infogrid.viewlet.ViewletFactory;
import org.infogrid.viewlet.ViewletFactoryChoice;

import org.infogrid.util.LocalizedException;
import org.infogrid.util.LocalizedObjectFormatter;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.NameServer;
import org.infogrid.util.logging.Log;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * <p>An InfoGrid web application. This may be subclassed if needed.</p>
 * <p>The application developer must instantiate this class (or a subclass) exactly
 *    once per application and set the resulting instance with the
 *    {@link InfoGridWebApp#setSingleton InfoGridWebApp.setSingleton} method.</p>
 * <p>If the {@link org.infogrid.jee.servlet.InitializationFilter InitializationFilter}
 *    is used, this can be accomplished simply by declaring the name of the
 *    application class (e.g. <code>org.infogrid.jee.app.InfoGridWebApp</code>)
 *    as a parameter in the <code>web.xml</code> file. See documentation for
 *    {@link org.infogrid.jee.servlet.InitializationFilter InitializationFilter}.</p>
 */
public abstract class InfoGridWebApp
{
    /**
     * Set the singleton instance.
     *
     * @param single the singleton instance
     * @throws IllegalStateException if a singleton instance was set previously
     */
    public static void setSingleton(
            InfoGridWebApp single )
        throws
            IllegalStateException
    {
        if( theSingleton != null ) {
            throw new IllegalStateException( "Singleton set already: " + theSingleton );
        }
        theSingleton = single;
    }

    /**
     * Obtain the singleton instance.
     *
     * @return the singleton instance
     */
    public static InfoGridWebApp getSingleton()
    {
        return theSingleton;
    }

    /**
     * Constructor, for subclasses.
     *
     * @param meshBase the main MeshBase of the application
     * @param viewletFactory the ViewletFactory of the application
     * @param traversalDictionary the TraversalDictionary of the application
     * @param applicationContext the main application Context
     */
    protected InfoGridWebApp(
            MeshBase                                mainMeshBase,
            NameServer<MeshBaseIdentifier,MeshBase> meshBaseNameServer,
            ViewletFactory                          viewletFactory,
            TraversalDictionary                     traversalDictionary,
            Context                                 applicationContext )
    {        
        theMainMeshBase        = mainMeshBase;
        theMeshBaseNameServer  = meshBaseNameServer;
        theViewletFactory      = viewletFactory;
        theTraversalDictionary = traversalDictionary;
        theApplicationContext  = applicationContext;
        
        theApplicationContext.addContextObject( theViewletFactory );
        
        log = Log.getLogInstance( getClass() );
    }

    /**
     * Obtain the MeshBase name server for this application.
     *
     * @return the MeshBase name server
     */
    public NameServer<MeshBaseIdentifier,MeshBase> getMeshBaseNameServer()
    {
        return theMeshBaseNameServer;
    }

    /**
     * Obtain the ModelBase for this application.
     *
     * @return the ModelBase
     */
    public final ModelBase getModelBase()
    {
        return ModelBaseSingleton.getSingleton();
    }

    /**
     * Obtain the ViewletFactory for this application.
     *
     * @return the ViewletFactory
     */
    public ViewletFactory getViewletFactory()
    {
        return theViewletFactory;
    }

    /**
     * Obtain the main application Context for this application.
     *
     * @return the Context
     */
    public final Context getApplicationContext()
    {
        return theApplicationContext;
    }

    /**
     * Find the a RequestDispatcher for the request with this ServletPath, localized
     * according to the user's language preferences. This uses a similar algorithm as for Java's
     * <code>ResourceBundle.getBundle</code>. It can be overridden by subclasses.
     *
     * @param servletName the generic servlet path
     * @param localeIterator Iterator over the user's Locale preferences, in sequence
     * @param context the ServletContext to use
     * @return the found RequestDispatcher, or null.
     */
    public RequestDispatcher findLocalizedRequestDispatcher(
            String           servletName,
            Iterator<Locale> localeIterator,
            ServletContext   context )
    {
        String found = null;
        
        String servletBaseName;
        String servletExtension;
        int    period = servletName.lastIndexOf( "." );
        if( period >= 0 ) {
            servletBaseName  = servletName.substring( 0, period );
            servletExtension = servletName.substring( period+1 );
        } else {
            servletBaseName  = servletName;
            servletExtension = "";
        }
        
        if( localeIterator != null ) {
            ArrayList<Locale> consideredAlready        = new ArrayList<Locale>();
            ArrayList<Locale> countriesStillToConsider = new ArrayList<Locale>();
            ArrayList<Locale> languagesStillToConsider = new ArrayList<Locale>();
            
            while( localeIterator.hasNext() ) {
                Locale current = localeIterator.next();

                StringBuffer candidate = new StringBuffer();
                candidate.append( servletBaseName );
                
                String language = current.getLanguage();
                String country  = current.getCountry();
                String variant  = current.getVariant();
                if( language == null || language.length() == 0 ) {
                    continue; // just the default
                }
                candidate.append( '_' ).append( language );
                if( country != null && country.length() > 1 ) {
                    candidate.append( '_' ).append( country );
                    
                    if( variant != null && variant.length() > 1 ) {
                        countriesStillToConsider.add( new Locale( language, country ));
                        candidate.append( '_' ).append( variant );
                        
                    } else {
                        languagesStillToConsider.add( new Locale( language ));
                    }
                }

                String candidateString = candidate.toString();
                URL    resource        = null;

                try {
                    resource = context.getResource( candidateString );
                    
                } catch( MalformedURLException ex ) {
                    // in this case, skip
                }
                if( resource != null ) {
                    found = candidateString;
                    break; // found
                } else {
                    consideredAlready.add( current );
                }
            }
            
            // now saved countries
            if( found == null ) {
                Iterator<Locale> iter = countriesStillToConsider.iterator();
                while( iter.hasNext() ) {
                    Locale current = iter.next();

                    if( consideredAlready.contains( current )) {
                        continue; // did this already
                    }
                    StringBuffer candidate = new StringBuffer();
                    candidate.append( servletBaseName );
                
                    candidate.append( '_' ).append( current.getLanguage() );
                    candidate.append( '_' ).append( current.getCountry() );

                    String candidateString = candidate.toString();
                    URL    resource        = null;

                    try {
                        resource = context.getResource( candidateString );

                    } catch( MalformedURLException ex ) {
                        // in this case, skip
                    }
                    if( resource != null ) {
                        found = candidateString;
                        break; // found
                    } else {
                        consideredAlready.add( current );
                    }
                }
            }
            // now saved languages
            if( found == null ) {
                Iterator<Locale> iter = languagesStillToConsider.iterator();
                while( iter.hasNext() ) {
                    Locale current = iter.next();

                    if( consideredAlready.contains( current )) {
                        continue; // did this already
                    }
                    StringBuffer candidate = new StringBuffer();
                    candidate.append( servletBaseName );
                
                    candidate.append( '_' ).append( current.getLanguage() );

                    String candidateString = candidate.toString();
                    if( doesResourceExist( candidateString, context )) {
                        found = candidateString;
                        break;
                    } else {
                        consideredAlready.add( current );
                    }                    
                }
            }
        }

        if( found != null ) {
            return context.getRequestDispatcher( found );
        }
        // make sure the default exists.
        if( doesResourceExist( servletName, context )) {            
            return context.getRequestDispatcher( servletName );
        } else {
            return null;
        }
    }

    /**
     * Determine whether a resource exists.
     *
     * @param resourcePath path to the resource
     * @param context the ServletContext to use
     * @return true if a resource exists with this path in the ServletContext
     */
    protected boolean doesResourceExist(
            String         resourcePath,
            ServletContext context )
    {
        URL resource = null;

        try {
            resource = context.getResource( resourcePath );

        } catch( MalformedURLException ex ) {
            // in this case, skip
        }
        if( resource != null ) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Report a problem that should be shown to the user.
     *
     * @param t the Throwable indicating the problem
     */
    public void reportProblem(
            Throwable t )
    {
        Thread myThread = Thread.currentThread();

        ArrayList<Throwable> myCurrentProblems;
        synchronized( theCurrentProblems ) {
            myCurrentProblems = theCurrentProblems.get( myThread );
            if( myCurrentProblems == null ) {
                myCurrentProblems = new ArrayList<Throwable>();
                theCurrentProblems.put( myThread, myCurrentProblems );
            }
        }
        // don't need further synchronization: the only one modifying my ArrayList is me

        if( myCurrentProblems.size() < 20 ) {
            // make sure we aren't growing this indefinitely
            myCurrentProblems.add( t );
        } else {
            log.error( "Too many problems. Ignored ", t );
        }
    }

    /**
     * Return all reported problems for this Thread, in sequence, and delete
     * them here.
     *
     * @return all reported problems for this Thread, in sequence
     */
    public ArrayList<Throwable> clearReportedProblems()
    {
        Thread myThread = Thread.currentThread();

        ArrayList<Throwable> myCurrentProblems;
        synchronized( theCurrentProblems ) {
            myCurrentProblems = theCurrentProblems.put( myThread, new ArrayList<Throwable>() ); // get and erase
        }
        return myCurrentProblems;
    }

    /**
     * Return all reported problems for this Thread, in the way they should be
     * show to the user, and delete them here.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @return all reported problems for this Thread
     */
    public String clearErrorString(
            HttpServletRequest  request,
            HttpServletResponse response )
    {
        return clearErrorString( request, response, obtainLocalizedExceptionObjectFormatter( request, response ) );
    }
    
    /**
     * Return all reported problems for this Thread, in the way they should be
     * show to the user, and delete them here.
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @param formatter the LocalizedObjectFormatter to use to format error parameters
     * @return all reported problems for this Thread
     */
    public String clearErrorString(
            HttpServletRequest       request,
            HttpServletResponse      response,
            LocalizedObjectFormatter formatter )
    {
        ArrayList<Throwable> problemList = clearReportedProblems();
        if( problemList == null || problemList.isEmpty() ) {
            return "";
        }
        
        ResourceHelper rh = ResourceHelper.getInstance( getClass() );

        MessageFormat errorDiv  = new MessageFormat( rh.getResourceStringOrDefault( "ErrorDiv", "<div class=\"errors\"><h2>Errors:</h2><ul>{0}</ul></div>" ));
        MessageFormat errorItem = new MessageFormat( rh.getResourceStringOrDefault( "ErrorItem", "<li>{0}</li>" ));
        int           maxErrors = rh.getResourceIntegerOrDefault( "MaxErrors", Integer.MAX_VALUE );

        StringBuilder content = new StringBuilder();

        Iterator<Throwable> iter = problemList.iterator();
        for( int i=0 ; i<maxErrors && iter.hasNext() ; ++i ) {

            Throwable problem = iter.next();

            String problemString;
            if( problem instanceof LocalizedException ) {
                problemString = ((LocalizedException)problem).getLocalizedMessage( formatter );
                
            } else if( problem.getClass().getName().indexOf( "JasperException" ) >= 0 ) {
                // needs special formatting. We check Strings rather that instanceof, so this works on non-Tomcat app servers as well
                String message = problem.getLocalizedMessage();
                if( message != null ) {
                    problemString = "<pre>" + quoteAngleBrackets( message ) + "</pre>";
                } else {
                    problemString = null;
                }
                
            } else {
                problemString = problem.getLocalizedMessage();
                
            }
            if( problemString == null || problemString.length() == 0 ) {
                problemString = problem.getClass().getName();
            }
            
            String stacktrace = createStackTrace( problem, "" );
            
            // these String arrays into the format seem to be necessary
            String formattedProblemString = errorItem.format( new Object[] { problemString, stacktrace != null ? stacktrace : "" } );
            
            content.append( formattedProblemString );
        }
        String ret = errorDiv.format( new String[] { content.toString() } );
        
        return ret;
    }
    
    /**
     * Create a String that represents the Stacktrace. Subclasses may override this and return an
     * empty String.
     *
     * @param t the Throwable for the stack trace
     * @param indent the String to use for indentation
     * @return the String
     */
    protected String createStackTrace(
            Throwable t,
            String    indent )
    {
        StringBuilder        buf   = new StringBuilder();
        StackTraceElement [] trace = t.getStackTrace();
        for( int i=0 ; i<trace.length ; ++i ) {
            buf.append( indent ).append( "at " ).append( trace[i] ).append( '\n' );
        }

        // now causes.
        for( Throwable cause = findCause( t ) ; cause != null ; cause = findCause( cause )) {
            buf.append( "Cause: " ).append( cause.toString() ).append( '\n' );
            
            StackTraceElement [] causeTrace = cause.getStackTrace();
            for( int i=0 ; i<causeTrace.length ; ++i ) {
                buf.append( indent ).append( "at " ).append( causeTrace[i] ).append( '\n' );
            }
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
     * @return the LocalizedObjectFormatter to use with this application
     */
    protected LocalizedObjectFormatter obtainLocalizedExceptionObjectFormatter(
            HttpServletRequest  request,
            HttpServletResponse response )
    {
        if( theExceptionObjectFormatter == null ) {
            theExceptionObjectFormatter = new InfoGridWebAppObjectFormatter( request.getContextPath() + "/" );
        }
        return theExceptionObjectFormatter;
    }

    /**
     * Obtain the TraversalDictionary for this application.
     *
     * @return the TraversalDictionary
     */
    public TraversalDictionary getTraversalDictionary()
    {
        return theTraversalDictionary;
    }

    /**
     *  Factory method to create the right subtype MeshBaseIdentifier.
     * 
     * @param stringForm the String representation of the MeshBaseIdentifier
     * @return suitable subtype of MeshBaseIdentifier
     * @throws URISyntaxException thrown if a syntax error occurred
     */
    public abstract MeshBaseIdentifier createMeshBaseIdentifier(
            String stringForm )
        throws
            URISyntaxException;


    /**
     * Obtain the default MeshBase.
     * 
     * @return the default MeshBase
     */
    public MeshBase getDefaultMeshBase()
    {
        return theMainMeshBase;
    }

    /**
     * The NameServer that knows the Meshbase(s) in this application.
     */
    protected NameServer<MeshBaseIdentifier,MeshBase> theMeshBaseNameServer;
    
    /**
     * The ViewletFactory for this application.
     */
    protected ViewletFactory theViewletFactory;

    /**
     * The application context.
     */
    protected Context theApplicationContext;

    /**
     * The singleton instance of this class.
     */
    private static InfoGridWebApp theSingleton;
    
    /**
     * The current problems, in sequence of occurrence, hashed by Thread.
     */
    protected HashMap<Thread,ArrayList<Throwable>> theCurrentProblems = new HashMap<Thread,ArrayList<Throwable>>();
    
    /**
     * Our logger. Allocated in the constructor.
     */
    private Log log;
    
    /**
     * The LocalizedObjectFormatter to use for error reporting.
     */
    protected LocalizedObjectFormatter theExceptionObjectFormatter;

    /**
     * The TraversalDictionary for this application, if one was given.
     */
    protected TraversalDictionary theTraversalDictionary;
        
    /**
     * The main MeshBase.
     */
    protected MeshBase theMainMeshBase;

    /**
     * Simple, bootstrap ViewletFactory.
     */
    private static class MyViewletFactory
            extends
                AbstractViewletFactory
    {
        /**
         * Constructor.
         */
        public MyViewletFactory()
        {
            super( "org.infogrid.jee.viewlet.JeeViewlet" );
        }

        /**
         * Find the ViewletFactoryChoices that apply to these MeshObjectsToView, but ignore the specified
         * viewlet type. If none are found, return an emtpy array.
         *
         * @param theObjectsToView the MeshObjectsToView
         * @return the found ViewletFactoryChoices, if any
         */
        public ViewletFactoryChoice [] determineFactoryChoicesIgnoringType(
                MeshObjectsToView theObjectsToView )
        {
            MeshObject subject = theObjectsToView.getSubject();
            if( subject.getMeshBase().getHomeObject() == subject ) {
                return new ViewletFactoryChoice[] {
                    DefaultViewletFactoryChoice.create( AllMeshObjectsViewlet.class, ViewletFactoryChoice.GOOD_MATCH_QUALITY ),
                    DefaultViewletFactoryChoice.create( AllMeshTypesViewlet.class, ViewletFactoryChoice.AVERAGE_MATCH_QUALITY ),
                    DefaultViewletFactoryChoice.create( PropertySheetViewlet.class, ViewletFactoryChoice.BAD_MATCH_QUALITY )
                };
            } else {
                return new ViewletFactoryChoice[] {
                    DefaultViewletFactoryChoice.create( PropertySheetViewlet.class, ViewletFactoryChoice.BAD_MATCH_QUALITY )
                };
            }            
        }
    }
}
