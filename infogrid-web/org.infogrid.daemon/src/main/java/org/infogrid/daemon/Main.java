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

package org.infogrid.daemon;

import org.infogrid.web.app.InfoGridWebApp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.diet4j.core.Module;
import org.diet4j.core.ModuleActivationException;
import org.diet4j.core.ModuleDeactivationException;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.infogrid.util.BufferingInputStream;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.infogrid.web.app.InfoGridWebAccessory;
import org.infogrid.web.taglib.AbstractInfoGridTag;

/**
 * Main program of the InfoGrid daemon. Activate using diet4j.
 */
public class Main
{
    /**
     * Diet4j module activation.
     * 
     * @param thisModule the Module being activated
     * @throws ModuleActivationException thrown if module activation failed
     */
    public static void moduleActivate(
            Module thisModule )
        throws
            ModuleActivationException
    {
        System.err.println( "Activating " + thisModule );

        activateLogging( thisModule );

        theDaemonModule = thisModule;
    }

    /**
     * Main program.
     * 
     * @param args command-line arguments
     * @throws Exception something went wrong
     */
    public static void main(
            String [] args )
        throws
            Exception
    {
        System.err.println( "main()" );

        String configFile = null;
        for( int i=0 ; i<args.length ; ++i ) {
            if( "--config".equals( args[i] )) {
                configFile = args[++i];
            }
        }

        theConfig = DaemonConfiguration.create( configFile );

        JspFactory.setDefaultFactory( new org.apache.jasper.runtime.JspFactoryImpl() );

        ModuleRegistry reg = theDaemonModule.getModuleRegistry();

        theAppModule = reg.resolve( reg.determineSingleResolutionCandidate( theConfig.getAppModuleRequirement() ));

        ModuleRequirement [] accReqs = theConfig.getAccessoryModuleRequirements();
        theAccessoryModules = new Module[ accReqs.length ];
        theAccessories      = new InfoGridWebAccessory[ accReqs.length ];

        for( int i=0 ; i<accReqs.length ; ++i ) {
            theAccessoryModules[i] = reg.resolve( reg.determineSingleResolutionCandidate( accReqs[i] ) );
        }
        
        theAppModule.activateRecursively();
        theApp = (InfoGridWebApp) theAppModule.getContextObject();
        theApp.initialize( theConfig );

        for( int i=0 ; i<accReqs.length ; ++i ) {
            theAccessoryModules[i].activateRecursively();
            Object co = theAccessoryModules[i].getContextObject();
            if( co instanceof InfoGridWebAccessory ) {
                theAccessories[i] = (InfoGridWebAccessory) co;
                theAccessories[i].initialize( theConfig, theApp );
            }
        }

        theJettyServer = new Server( new QueuedThreadPool( theConfig.getHttpServerThreads() ));

        ServerConnector connector = new ServerConnector( theJettyServer );
        connector.setPort( theConfig.getHttpServerPort() );
        theJettyServer.setConnectors( new Connector[] { connector } );

        ServletContextHandler contextHandler = new ServletContextHandler();

        // setClassLoader does not seem to be required if we add the Servlet
        // class via the ServletHolder instead of directly (which incorrectly
        // goes through Class.getName() per 
        // https://github.com/eclipse/jetty.project/issues/894
        // contextHandler.setClassLoader( Main.class.getClassLoader() );

        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.getSessionManager().setSessionIdPathParameterName( null );
        // per http://stackoverflow.com/questions/7727534/how-do-you-disable-jsessionid-for-jetty-running-with-the-eclipse-jetty-maven-plu
        contextHandler.setSessionHandler( sessionHandler );

        String virtualHost = theConfig.getAppVirtualHost();
        if( virtualHost != null ) {
            contextHandler.setVirtualHosts( new String[] { virtualHost });
        }

        contextHandler.setContextPath( theConfig.getAppContextPath() + "/" ); // they want trailing slashes
        contextHandler.setAttribute(
                InstanceManager.class.getName(),
                new SimpleInstanceManager() );
        contextHandler.setAttribute(
                AbstractInfoGridTag.INFOGRID_APP_NAME,
                theApp );

        // This is a major hack: we need the CentralDaemonServlet to have access
        // to the InfoGrid app, but there seems to be no good API to do this.
        // So we hack it in.
        ServletHolder myHolder = new ServletHolder( CentralDaemonServlet.class ){
            {
                theInfoGridApp = theApp;
            }
            @Override
            protected Servlet newInstance() throws ServletException, IllegalAccessException, InstantiationException
            {
                Servlet instance = super.newInstance();
                if( instance instanceof CentralDaemonServlet ) {
                    ((CentralDaemonServlet)instance).setApp( theInfoGridApp );
                }
                return instance;
            }
            protected InfoGridWebApp theInfoGridApp;
        };

        contextHandler.addServlet( myHolder, "/*");

        theJettyServer.setHandler( contextHandler ); // handle everything ourselves, including 404's etc

        theJettyServer.start();
    }

    /**
     * Diet4j module deactivation.
     * 
     * @param thisModule the Module being deactivated
     * @throws ModuleDeactivationException thrown if module deactivation failed
     */
    public static void moduleDeactivate(
            Module thisModule )
        throws
            ModuleDeactivationException
    {
        System.err.println( "Deactivating " + thisModule );

        try {
            theJettyServer.stop();

        } catch( Exception ex ) {
            throw new ModuleDeactivationException( thisModule.getModuleMeta(), ex );

        } finally {
            for( Module accModule : theAccessoryModules ) {
                accModule.deactivateRecursively();
            }
            theAppModule.deactivateRecursively();
        }
    }

    /**
     * Helper method to activate logging.
     * 
     * @param thisModule the Module being activated
     * @throws ModuleActivationException a problem occurred during activation
     */
    private static void activateLogging(
            Module thisModule )
        throws
            ModuleActivationException
    {
        Properties logProperties = new Properties();
        boolean    initialized   = false;

        if( DAEMON_LOG4J_CONFIG_FILESYSTEM.exists() ) {
            try {
                if( DAEMON_LOG4J_CONFIG_FILESYSTEM.canRead() ) {
                    logProperties.load( new BufferingInputStream( new FileInputStream( DAEMON_LOG4J_CONFIG_FILESYSTEM )));
                    initialized = true;
                }
            } catch( Throwable t) {
                System.err.println( "Failed to read log4j configuration from: " + DAEMON_LOG4J_CONFIG_FILESYSTEM );
                // ignore
            }
        }
        if( !initialized ) {
            try {
                logProperties.load( new BufferedInputStream( thisModule.getClassLoader().getResourceAsStream( DAEMON_LOG4J_CONFIG_RESOURCE )));

            } catch( Throwable ex ) {
                throw new ModuleActivationException(
                        thisModule.getModuleMeta(),
                        "Default Log4j configuration file could not be loaded from " + DAEMON_LOG4J_CONFIG_RESOURCE,
                        ex );
            }
        }
            
        try {
            Log4jLog.configure( logProperties );
        } catch( Throwable ex ) {
            // This can happen, for example, when a file could not be written
            throw new ModuleActivationException(
                    thisModule.getModuleMeta(),
                    ex );
        }
        Log.setLogFactory( new Log4jLogFactory() );
    }

    /**
     * dietj4 Module representing the daemon.
     */
    protected static Module theDaemonModule;
    
    /**
     * diet4j Module representing the main application.
     */
    protected static Module theAppModule;
    
    /**
     * diet4j Modules representing the accessories for the main application.
     */
    protected static Module [] theAccessoryModules;

    /**
     * The main application.
     */
    protected static InfoGridWebApp theApp;
    
    /**
     * The accessories for the main application.
     * Same sequence as theAccessoryModules, possibly containing nulls,
     */
    protected static InfoGridWebAccessory [] theAccessories;

    /**
     * The configuration provided to the daemon by means of a config file.
     */
    protected static DaemonConfiguration theConfig;

    /**
     * The HTTP server.
     */
    protected static Server theJettyServer;
    
    /**
     * The log4j configuration file in the file system. This is optional and will
     * be loaded in preference to 
     */
    public static final File DAEMON_LOG4J_CONFIG_FILESYSTEM = new File( "/etc/infogrid/daemon-log4j.properties" );
    
    /**
     * The log4j configuration resource in this module. This is the fallback if
     * DAEMON_LOG4J_CONFIG_FILESYSTEM could not be loaded.
     */
    public static final String DAEMON_LOG4J_CONFIG_RESOURCE = "org/infogrid/daemon/Log.properties";
}
