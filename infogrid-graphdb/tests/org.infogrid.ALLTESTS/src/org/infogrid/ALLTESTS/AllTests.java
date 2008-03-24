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

package org.infogrid.ALLTESTS;

import org.infogrid.module.Module;
import org.infogrid.module.ModuleAdvertisement;
import org.infogrid.module.ModuleRegistry;
import org.infogrid.module.ModuleRequirement;
import org.infogrid.module.StandardModule;
import org.infogrid.module.StandardModuleAdvertisement;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;

import java.io.BufferedInputStream;
import java.util.Properties;

/**
 * Invokes all of our tests.
 */
public class AllTests
{
    private static final Log log = Log.getLogInstance( AllTests.class ); // our own, private logger

    /**
     * List of Modules that we run.
     */
    public static final ModuleRequirement [] theModuleRequirements = {
            ModuleRequirement.create1( "org.infogrid.util.TEST" ),
            ModuleRequirement.create1( "org.infogrid.comm.pingpong.TEST" ),
            ModuleRequirement.create1( "org.infogrid.httpd.TEST" ),
            ModuleRequirement.create1( "org.infogrid.store.TEST" ),
            ModuleRequirement.create1( "org.infogrid.store.sql.TEST" ),
            ModuleRequirement.create1( "org.infogrid.kernel.TEST.modelbase" ),
            ModuleRequirement.create1( "org.infogrid.kernel.TEST.meshbase.m" ),
            ModuleRequirement.create1( "org.infogrid.kernel.TEST.meshbase.m.security" ),
            ModuleRequirement.create1( "org.infogrid.kernel.TEST.mesh.externalized" ),
            ModuleRequirement.create1( "org.infogrid.kernel.TEST.differencer" ),
            ModuleRequirement.create1( "org.infogrid.meshbase.store.TEST" ),
            ModuleRequirement.create1( "org.infogrid.meshbase.store.net.TEST" ),
            ModuleRequirement.create1( "org.infogrid.kernel.net.TEST.xpriso" ),
            ModuleRequirement.create1( "org.infogrid.kernel.net.TEST.urls" ),
            ModuleRequirement.create1( "org.infogrid.probe.TEST" ),
            ModuleRequirement.create1( "org.infogrid.probe.feeds.TEST" ),
            ModuleRequirement.create1( "org.infogrid.probe.vcard.TEST" ),
            ModuleRequirement.create1( "org.infogrid.probe.store.TEST" ),
            ModuleRequirement.create1( "org.infogrid.kernel.active.TEST" ),
            ModuleRequirement.create1( "org.infogrid.probe.TEST.active" ),
    };

    /**
     * The Module Framework's BootLoader activates this Module by calling this method.
     *
     * @param dependentModules the Modules that this Module depends on, if any
     * @param dependentContextObjects the context objects of the Modules that this Module depends on, if any, in same sequence as dependentModules
     * @param thisModule reference to the Module that is currently being initialized and to which we belong
     * @return a context object that is Module-specific, or null if none
     * @throws Exception may an Exception indicating that the Module could not be activated
     */
    public static Object activate(
            Module [] dependentModules,
            Object [] dependentContextObjects,
            Module    thisModule )
        throws
            Exception
    {
        theModuleRegistry = thisModule.getModuleRegistry();
        
        return null;
    }

    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        AllTests test = null;
        try {
            if( args.length > 0 ) { // well, not quite possible but to stay with the general outline
                System.err.println( "Synopsis: no arguments" );
                System.err.println( "aborting ..." );
                System.exit( 1 );
            }

            test = new AllTests( args );
            test.run();

        } catch( Throwable ex ) {
            log.error( ex );
            System.exit(1);
        }
        if( test != null ) {
            test.cleanup();
        }
        if( errorCount == 0 ) {
            log.info( "PASS" );
        } else {
            log.info( "FAIL (" + errorCount + " errors)" );
        }
        System.exit( errorCount );
    }

    /**
      * Constructor.
      *
      * @param args command-line arguments
      */
    public AllTests(
            String [] args )
        throws
            Exception
    {
        String nameOfLog4jConfigFile = "org/infogrid/ALLTESTS/Log.properties";

        try {
            Properties logProperties = new Properties();
            logProperties.load( new BufferedInputStream(
                    getClass().getClassLoader().getResourceAsStream( nameOfLog4jConfigFile )));

            Log4jLog.configure( logProperties );
            // which logger is being used is defined in the module dependency declaration through parameters

        } catch( Exception ex ) {
            System.err.println( "Unexpected Exception attempting to load " + nameOfLog4jConfigFile );
            ex.printStackTrace( System.err );
        }

        ResourceHelper.initializeLogging();
    }
    
    /**
     * Run method.
     */
    public void run()
    {
        for( ModuleRequirement req : theModuleRequirements ) {
            ModuleAdvertisement [] ads = theModuleRegistry.determineResolutionCandidates( req );
            if( ads == null || ads.length == 0 ) {
                log.error( "Could not resolve " + req );
                ++errorCount;
                continue;
            }
            for( ModuleAdvertisement currentAd : ads ) {
                if( currentAd instanceof StandardModuleAdvertisement ) {
                    try {
                        StringBuilder message = new StringBuilder();
                        message.append( "****************************************************************************\n" );
                        message.append( "** About to run test Module " ).append( currentAd.getModuleName()).append( "\n" );
                        message.append( "****************************************************************************" );
                        log.info( message.toString() );
                        StandardModule currentMod = (StandardModule) theModuleRegistry.resolve( currentAd );
                    
                        currentMod.activateRecursively();
                    
                        currentMod.run( new String[0] );

                    } catch( Exception ex ) {
                        log.error( ex );
                        ++errorCount;
                    }
                } else {
                    log.error( "Not a StandardModule: " + currentAd );
                }
            }
        }
    }
    
    /**
     * Cleanup.
     */
    public void cleanup()
    {
        // noop
    }

    /**
     * The ModuleRegistry.
     */
    protected static ModuleRegistry theModuleRegistry;

    /**
     * Error count so far.
     */
    protected static int errorCount = 0;
}
