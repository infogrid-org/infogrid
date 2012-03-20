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
// Copyright 1998-2012 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module.commandline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.infogrid.module.ModuleErrorHandler;
import org.infogrid.module.SoftwareInstallation;
import org.infogrid.module.SoftwareInstallationException;

/**
 * A local software installation.
 * Use this to obtain all paths, for example.
 */
public class CommandlineSoftwareInstallation
    extends
        SoftwareInstallation
{
    /**
     * Factory method to create a new instance of SoftwareInstallation from the command-line arguments passed
     * into this method.
     *
     * @param args the command-line arguments to this invocation
     * @return a newly created SoftwareInstallation
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     */
    public static CommandlineSoftwareInstallation createFromCommandLine(
            String [] args )
        throws
            SoftwareInstallationException
    {
        ArrayList<String> moduleAdPaths                     = new ArrayList<String>();
        String            homeObjectUrlName                  = null;
        String            rootModuleName                     = null;
        String            activationClassName                = null;
        String            activationMethodName               = null;
        String            runClassName                       = null;
        String            runMethodName                      = null;
        String            platform                           = null;
        PrintStream       moduleDebugStream                  = null;
        String []         remainingArguments                 = null;
        boolean           showSplash                         = false;
        boolean           isDeveloper                        = false;
        boolean           isDemo                             = false;
        boolean           isShowModuleRegistry               = false;
        long              sleepPeriodBeforeExit              = 0L;

        for( int i = 0; i < args.length; ++i ) {

            if( "-help".equalsIgnoreCase( args[i] )) {
                usageCommandLineExit( null );
                
            } else if ( "-homeurl".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    homeObjectUrlName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -homeurl" );
                }

            } else if ( "-moduleadvertisementpath".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    for( String dir : args[i].split( ":" )) {
                        moduleAdPaths.add( dir );
                    }
                } else {
                    usageCommandLineThrow( "argument missing after -moduleadvertisementpath" );
                }

            } else if ( "-rootmodule".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    rootModuleName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -rootmodule" );
                }

            } else if ( "-activationClass".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    activationClassName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -activationclass" );
                }

            } else if ( "-activationMethod".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    activationMethodName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -activationmethod" );
                }

            } else if ( "-runclass".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    runClassName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -runclass" );
                }

            } else if ( "-runMethodName".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    runMethodName = args[i];
                } else {
                    usageCommandLineThrow( "argument missing after -runMethodName" );
                }

            } else if ( "-moduledebug".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    String moduleDebugStreamName = args[i];
                    if( "System.err".equalsIgnoreCase( moduleDebugStreamName )) {
                        moduleDebugStream = System.err;
                    } else if( "System.out".equalsIgnoreCase( moduleDebugStreamName )) {
                        moduleDebugStream = System.out;
                    } else {
                        try {
                            moduleDebugStream = new PrintStream( new FileOutputStream( moduleDebugStreamName ));
                        } catch( IOException ex ) {
                            usageCommandLineThrow( "file " + moduleDebugStreamName + " cannot be opened for writing" );
                        }
                    }
                } else {
                    usageCommandLineThrow( "argument missing after -moduledebug" );
                }

            } else if ( "-splash".equalsIgnoreCase( args[i] )) {
                showSplash = true;
            } else if ( "-demo".equalsIgnoreCase( args[i] )) {
                isDemo = true;
            } else if ( "-developer".equalsIgnoreCase( args[i] )) {
                isDeveloper = true;
            } else if( "-showmoduleregistry".equalsIgnoreCase( args[i] )) {
                isShowModuleRegistry = true;
            } else if( "-debug".equalsIgnoreCase( args[i] )) {
                ModuleErrorHandler.setDebugStream( System.err );
            } else if( "-sleepbeforeexit".equalsIgnoreCase( args[i] )) {
                if( ++i < args.length ) {
                    sleepPeriodBeforeExit = Long.parseLong( args[i] );
                } else {
                    usageCommandLineThrow( "argument missing after -sleepbeforeexit" );
                }
                
            } else {
                // all other arguments are remaining arguments
                remainingArguments = new String[ args.length - i ];
                System.arraycopy( args, i, remainingArguments, 0, remainingArguments.length );
                break;
            }
        }
        if( rootModuleName == null ) {
            usageCommandLineThrow( "no -rootmodule argument given" );
        }

        if( moduleAdPaths.isEmpty() ) {
            usageCommandLineThrow( "at least one -moduleadvertisementpath argument must be given" );
        }

        // determine platform
        platform = determinePlatform();

        // fix remaining arguments
        if( remainingArguments == null ) {
            remainingArguments = new String[0];
        }
        return new CommandlineSoftwareInstallation(
                platform,
                moduleAdPaths,
                homeObjectUrlName,
                rootModuleName,
                activationClassName,
                activationMethodName,
                runClassName,
                runMethodName,
                showSplash,
                isDeveloper,
                isDemo,
                isShowModuleRegistry,
                sleepPeriodBeforeExit,
                moduleDebugStream,
                remainingArguments );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param platform the platform on which we run, one of the pre-defined values in this class
     * @param moduleAdPaths paths, possibly with wildcards, to the ModuleAdvertisements available to this application
     * @param homeObjectUrlName the start object, if any
     * @param rootModuleName the name of the root Module to run
     * @param activationClassName the name of the class to invoke to activate the root Module (overrides default specified in ModuleAdvertisement)
     * @param activationMethodName the name of the method to invoke to activate the root Module (overrides default specified in ModuleAdvertisement)
     * @param runClassName the name of the class to run in the root Module (overrides default specified in ModuleAdvertisement)
     * @param runMethodName the name of the method to run in the root Module (overrides default specified in ModuleAdvertisement)
     * @param showSplash if true, show splash screen
     * @param isDeveloper if true, run in developer mode
     * @param isDemo if true, run in demo mode
     * @param isShowModuleRegistry if true, print the content of the ModuleRegistry to the terminal
     * @param sleepPeriodBeforeExit the number of milliseconds to sleep prior to exiting the VM
     * @param moduleDebugStream a stream for Module-related debug information (may be null)
     * @param remainingArguments the arguments on the command line not used by SoftwareInstallation itself
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     */
    protected CommandlineSoftwareInstallation(
            String       platform,
            List<String> moduleAdPaths,
            String       homeObjectUrlName,
            String       rootModuleName,
            String       activationClassName,
            String       activationMethodName,
            String       runClassName,
            String       runMethodName,
            boolean      showSplash,
            boolean      isDeveloper,
            boolean      isDemo,
            boolean      isShowModuleRegistry,
            long         sleepPeriodBeforeExit,
            PrintStream  moduleDebugStream,
            String []    remainingArguments )
        throws
            SoftwareInstallationException
    {
        super(  platform,
                rootModuleName,
                activationClassName,
                activationMethodName,
                isDeveloper,
                isDemo,
                isShowModuleRegistry,
                moduleDebugStream );

        theModuleAdPaths     = moduleAdPaths;

        theHomeObjectUrlName = homeObjectUrlName;

        theRunClassName      = runClassName;
        theRunMethodName     = runMethodName;

        theShowSplash        = showSplash;

        theSleepPeriodBeforeExit = sleepPeriodBeforeExit;
        theRemainingArguments    = remainingArguments;
    }

    /**
     * Obtain the paths where the ModuleAdvertisements can be found.
     *
     * @return the paths, potentially with wildcards
     */
    public Iterable<String> getModuleAdvertisementPaths()
    {
        return theModuleAdPaths;
    }

    /**
     * Obtain the run class specified in the command-line, if any.
     *
     * @return name of the class
     */
    public final String getOverriddenRunClassName()
    {
        return theRunClassName;
    }

    /**
     * Obtain the run method specified in the command-line, if any.
     *
     * @return name of the method (without class name)
     */
    public final String getOverriddenRunMethodName()
    {
        return theRunMethodName;
    }

    /**
     * Obtain the number of milliseconds to sleep before exiting the main method.
     * This can be very convenient for profiling, for example.
     * 
     * @return the time before exit, in milliseconds
     */
    public final long getSleepPeriodBeforeExit()
    {
        return theSleepPeriodBeforeExit;
    }

    /**
     * Obtain the arguments passed when running the root Module.
     *
     * @return the arguments passed when running the root Module
     */
    public final String [] getRemainingArguments()
    {
        return theRemainingArguments;
    }

    /**
     * Prints usage message to the terminal for command-line invocation.
     *
     * @param msg the message to print to the terminal
     */
    protected static void usageCommandLine(
            String msg )
    {
        StringBuilder fullMsg = new StringBuilder( 512 );
        fullMsg.append( "Command line: " );

        if( msg != null ) {
            fullMsg.append( msg ).append( '\n' );
        }
        fullMsg.append( "Usage:\n" );
        fullMsg.append( "    [ -help ]                       prints out this synopsys\n" );
        fullMsg.append( "    [ -userdir <dir> ]              directory for user data instead of the default\n" );
        fullMsg.append( "    [ -homeurl <url> ]              url of an object to be opened instead of the default\n" );
        fullMsg.append( "    [ -moduledebug <stream> ]       debug module loading to System.out or System.err\n" );
        fullMsg.append( "    -moduleadvertisementpath <path> path where ModuleAdvertisements can be found. May be repeated and may contain wildcards.\n" );
        fullMsg.append( "    -rootmodule <name>              name of the root module to start\n" );
        fullMsg.append( "    [ -activationclass <class> ]    override the activation class specified for the root module\n" );
        fullMsg.append( "    [ -activationmethod <method> ]  override the activation method specified for the root module\n" );
        fullMsg.append( "    [ -runclass <class> ]           override the run class specified for the root module\n" );
        fullMsg.append( "    [ -runmethod <method> ]         override the run method specified for the root module\n" );
        // fullMsg.append( "    [ -errortextonly ]             errors shall only be printed onto the terminal\n" );
        fullMsg.append( "    [ -showmoduleregistry ]         print the content of the ModuleRegistry to the terminal\n" );

        ModuleErrorHandler.print( fullMsg.toString() );
    }

    /**
     * Prints usage message to the terminal and exits.
     *
     * @param msg the message to print to the terminal
     */
    protected static void usageCommandLineExit(
            String msg )
    {
        usageCommandLine( msg );
        System.exit( 1 );
    }

    /**
     * Prints usage message to the terminal and throws Exception.
     *
     * @param msg the message to print to the terminal
     */
    protected static void usageCommandLineThrow(
            String msg )
    {
        usageCommandLine( msg );
        throw new IllegalArgumentException( msg );
    }

    /**
     * Convert to String, for debugging purposes.
     *
     * @return String representation
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append( super.toString() );
        buf.append( "{\n" );
        buf.append( "    thePlatform:              " ).append( thePlatform ).append( "\n" );
        buf.append( "    theRootModuleRequirement: " ).append( theRootModuleRequirement ).append( "\n" );
        buf.append( "    theActivationClassName:   " ).append( theActivationClassName ).append( "\n" );
        buf.append( "    theActivationMethodName:  " ).append( theActivationMethodName ).append( "\n" );
        buf.append( "    theHostName:              " ).append( theHostName ).append( "\n" );
        buf.append( "    theIsDeveloper:           " ).append( theIsDeveloper ).append( "\n" );
        buf.append( "    theIsDemo:                " ).append( theIsDemo ).append( "\n" );
        buf.append( "    theIsShowModuleRegistry:  " ).append( theIsShowModuleRegistry ).append( "\n" );
        buf.append( "    theRemainingArguments: {\n" );
        for( int i=0 ; i<theRemainingArguments.length ; ++i ) {
            buf.append( "        " ).append( theRemainingArguments[i] ).append( "\n" );
        }
        buf.append( "    }\n" );
        buf.append( "    thePlatform:              " ).append( thePlatform ).append( "\n" );
        buf.append( "    theModuleAdPaths:     " );
        if( theModuleAdPaths != null && !theModuleAdPaths.isEmpty() ) {
            buf.append( " {\n" );
            for( String current : theModuleAdPaths ) {
                buf.append( "        " ).append( current ).append( "\n" );
            }
            buf.append( "    }\n" );
        } else {
            buf.append( " null\n" );
        }
        buf.append( "    theHomeObjectUrlName:     " ).append( theHomeObjectUrlName ).append( "\n" );
        buf.append( "    theRunClassName:          " ).append( theRunClassName ).append( "\n" );
        buf.append( "    theRunMethodName:         " ).append( theRunMethodName ).append( "\n" );
        buf.append( "}" );
        return buf.toString();
    }

    /**
     * The paths to the Module Advertisements.
     */
    protected List<String> theModuleAdPaths;

    /**
     * The URL of the object that we want to open up first (if any).
     */
    protected String theHomeObjectUrlName;

    /**
     * The name of the run class in the root Module, if specified on the command-line.
     */
    protected String theRunClassName;

    /**
     * The name of the run method in the root Module, if specified on the command-line.
     */
    protected String theRunMethodName;

    /**
     * Indicates whether we want to show the splash screen or not.
     */
    protected boolean theShowSplash;

    /**
     * Time, in milliseconds, to sleep prior to exiting the main method. This can be very
     * useful for profiling, for example.
     */
    protected long theSleepPeriodBeforeExit;
    
    /**
     * The arguments not used by SoftwareInstallation itself.
     */
    protected String [] theRemainingArguments;
}
