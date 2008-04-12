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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import org.infogrid.context.Context;
import org.infogrid.jee.rest.RestfulRequest;
import org.infogrid.jee.security.FormTokenService;
import org.infogrid.jee.viewlet.templates.StructuredResponse;
import org.infogrid.jee.viewlet.templates.StructuredResponseTemplate;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.model.traversal.TraversalDictionary;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.util.Factory;
import org.infogrid.util.NameServer;
import org.infogrid.viewlet.ViewletFactory;

/**
 * <p>An InfoGrid web application. This needs to be subclassed.</p>
 * <p>The application developer must instantiate this subclass exactly
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
            MeshBase                                                              mainMeshBase,
            NameServer<MeshBaseIdentifier,MeshBase>                               meshBaseNameServer,
            ViewletFactory                                                        viewletFactory,
            TraversalDictionary                                                   traversalDictionary,
            Factory<RestfulRequest,StructuredResponseTemplate,StructuredResponse> structuredResponseTemplateFactory,
            FormTokenService                                                      formTokenService,
            Context                                                               applicationContext )
    {        
        theMainMeshBase                      = mainMeshBase;
        theMeshBaseNameServer                = meshBaseNameServer;
        theViewletFactory                    = viewletFactory;
        theTraversalDictionary               = traversalDictionary;
        theApplicationContext                = applicationContext;
        theFormTokenService                  = formTokenService;
        theStructuredResponseTemplateFactory = structuredResponseTemplateFactory;
        
        theApplicationContext.addContextObject( theViewletFactory );
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
     * Obtain a factory for StructuredResponseTemplates.
     * 
     * @return the Factory
     */
    public Factory<RestfulRequest,StructuredResponseTemplate,StructuredResponse> getStructuredResponseTemplateFactory()
    {
        return theStructuredResponseTemplateFactory;
    }

    /**
     * Obtain a FormTokenService.
     * 
     * @return the FormTokenService
     */
    public FormTokenService getFormTokenService()
    {
        return theFormTokenService;
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
     * The FormTokenService to use.
     */
    protected FormTokenService theFormTokenService;

    /**
     * The application context.
     */
    protected Context theApplicationContext;

    /**
     * The singleton instance of this class.
     */
    private static InfoGridWebApp theSingleton;
    
    /**
     * The TraversalDictionary for this application, if one was given.
     */
    protected TraversalDictionary theTraversalDictionary;
        
    /**
     * The main MeshBase.
     */
    protected MeshBase theMainMeshBase;
    
    /**
     * The factory that determines the structure of our responses.
     */
    protected Factory<RestfulRequest,StructuredResponseTemplate,StructuredResponse> theStructuredResponseTemplateFactory;
}
