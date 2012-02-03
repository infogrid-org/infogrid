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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.module.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;
import org.infogrid.module.ModuleErrorHandler;
import org.infogrid.module.SoftwareInstallation;
import org.infogrid.module.SoftwareInstallationException;

/**
 * A SoftwareInstallation of a server-side J2EE installation.
 * Use this to obtain all paths, for example.
 */
public class ServletSoftwareInstallation
   extends
       SoftwareInstallation
{
    /**
     * Factory method to create a new instance of SoftwareInstallation from a default properties file.
     *
     * @return a newly created SoftwareInstallation
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     * @throws IOException if a needed file could not be loaded
     */
    public static ServletSoftwareInstallation createFromDefaultProperties()
        throws
            SoftwareInstallationException,
            IOException
    {
        ClassLoader loader     = SoftwareInstallation.class.getClassLoader();
        InputStream propStream = loader.getResourceAsStream( DEFAULT_PROPERTIES_FILE );

        return createFromProperties( propStream );
    }

    /**
     * Factory method to create a new instance of SoftwareInstallation from a properties file at a certain stream.
     *
     * @param propStream the InputStream from where to read the Properties
     * @return a newly created SoftwareInstallation
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     * @throws IOException if a needed file could not be loaded
     */
    public static ServletSoftwareInstallation createFromProperties(
            InputStream propStream )
        throws
            SoftwareInstallationException,
            IOException
    {
        if( propStream == null ) {
            throw new IOException( "Cannot read BootLoader properties from null stream" );
        }
        Properties props = new Properties();
        props.load( propStream );

        return createFromProperties( props );
    }

    /**
     * Factory method to create a new instance of SoftwareInstallation from java.util.Properties.
     *
     * @param props the Properties that we parse
     * @return a newly created SoftwareInstallation
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     */
    public static ServletSoftwareInstallation createFromProperties(
            Properties props )
        throws
            SoftwareInstallationException
    {
        String      rootModuleName             = null;
        String      activationClassName        = null;
        String      activationMethodName       = null;
        String      platform                   = null;
        PrintStream moduleDebugStream          = null;
        boolean     isDeveloper                = false;
        boolean     isDemo                     = false;
        boolean     isShowModuleRegistry       = false;

        Iterator iter = props.keySet().iterator();
        while( iter.hasNext() ) {
            String key   = (String) iter.next();
            String value = props.getProperty( key );

            if ( "rootmodule".equalsIgnoreCase( key )) {
                rootModuleName = value;

            } else if( "activationClassName".equalsIgnoreCase( key )) {
                activationClassName = value;

            } else if( "activationMethodName".equalsIgnoreCase( key )) {
                activationMethodName = value;

            // no run class here
            } else if ( "moduledebug".equalsIgnoreCase( key )) {
                String moduleDebugStreamName = value;
                if( "System.err".equalsIgnoreCase( moduleDebugStreamName )) {
                    moduleDebugStream = System.err;
                } else if( "System.out".equalsIgnoreCase( moduleDebugStreamName )) {
                    moduleDebugStream = System.out;
                } else {
                    try {
                        moduleDebugStream = new PrintStream( new FileOutputStream( moduleDebugStreamName ));
                    } catch( IOException ex ) {
                        usageJspThrow( "file " + moduleDebugStreamName + " cannot be opened for writing" );
                    }
                }
            } else if( "demo".equalsIgnoreCase( key )) {
                isDemo = true;
            } else if( "developer".equalsIgnoreCase( key )) {
                isDeveloper = true;
            } else if( "showmoduleregistry".equalsIgnoreCase( key )) {
                isShowModuleRegistry = true;
            } else {
                usageJspThrow( "Unknown property " + key );
            }
        }

        if( rootModuleName == null ) {
            usageJspThrow( "no rootmodule argument given" );
        }

        // determine platform
        platform = determinePlatform();

        return new ServletSoftwareInstallation(
                platform,
                rootModuleName,
                activationClassName,
                activationMethodName,
                isDeveloper,
                isDemo,
                isShowModuleRegistry,
                moduleDebugStream );
    }

    /**
     * Private constructor, use factory  method.
     *
     * @param platform the platform on which we run, one of the pre-defined values in this class
     * @param rootModuleName the name of the root Module to run
     * @param activationClassName the name of the class to invoke to activate the root Module (overrides default specified in ModuleAdvertisement)
     * @param activationMethodName the name of the method to invoke to activate the root Module (overrides default specified in ModuleAdvertisement)
     * @param isDeveloper if true, run in developer mode
     * @param isDemo if true, run in demo mode
     * @param isShowModuleRegistry if true, print the content of the ModuleRegistry to the terminal
     * @param moduleDebugStream a stream for Module-related debug information (may be null)
     * @throws SoftwareInstallationException if this software installation is incorrect, inconsistent or incomplete
     */
    protected ServletSoftwareInstallation(
            String      platform,
            String      rootModuleName,
            String      activationClassName,
            String      activationMethodName,
            boolean     isDeveloper,
            boolean     isDemo,
            boolean     isShowModuleRegistry,
            PrintStream moduleDebugStream )
        throws
            SoftwareInstallationException
    {
        super(  platform,
                rootModuleName,
                activationClassName,
                activationMethodName,
                false, // never use ModuleClassLoaders in J2EE mode
                false, // allowDefaultClassPathForRootModule
                isDeveloper,
                isDemo,
                isShowModuleRegistry,
                moduleDebugStream );
    }

    /**
     * Obtain an "About" text that describes this piece of software.
     *
     * @return the "about" text
     */
    public final String getAboutText()
    {
        return theAboutText;
    }

    /**
     * Prints usage message to the terminal for JSP invocation and throws RuntimeException.
     *
     * @param msg the message to print to the terminal
     */
    protected static void usageJspThrow(
            String msg )
    {
        StringBuilder fullMsg = new StringBuilder( 256 );

        if( msg != null ) {
            fullMsg.append( msg ).append( '\n' );
        }

        fullMsg.append( "Usage: set properties with the following names:\n" );
        fullMsg.append( "    moduledebug=<stream>                      debug module loading to System.out or System.err\n" );
        fullMsg.append( "    moduleadvertisementinstantiatorjars=<url> before running, load these ModuleAdvertisementInstantiator JARs (comma-separated)\n" );
        fullMsg.append( "    moduleadvertisementxmllistjars=<url>      before running, load the ModuleAdvertisment XMLs listed in these list files (comma-separated)\n" );
        fullMsg.append( "    rootmodule=<name>                         name of the root module to start\n" );
        fullMsg.append( "    activationclass=<class>                   override the activation class specified for the root module\n" );
        fullMsg.append( "    activationmethod=<method>                 override the activation method specified for the root module\n" );

        ModuleErrorHandler.print( fullMsg.toString() );

        throw new RuntimeException( "Incorrect configuration, check error log for more information" );
    }

    /**
     * The default location for our properties file.
     */
    public static final String DEFAULT_PROPERTIES_FILE = "BootLoader.properties";

    /**
     * The about text for this version. FIXME, needs to be internationalized.
     */
    protected static String theAboutText
            = "InfoGrid.org(tm)\n"
            + "\u00A9 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst\n"
            + "All rights reserved.\n"
            + "For more information about InfoGrid go to http://infogrid.org/";
}
