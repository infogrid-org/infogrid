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

package org.infogrid.module;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A software installation, i.e. a number of Modules and activation meta-data.
 * Subclasses implement this differently, depending on the run-time environment
 * (e.g. JEE vs. JME).
 */
public abstract class SoftwareInstallation
{
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
    protected SoftwareInstallation(
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
        thePlatform = platform;

        theRootModuleRequirement              = ModuleRequirement.create1( rootModuleName );
        theActivationClassName                = activationClassName;
        theActivationMethodName               = activationMethodName;

        theIsDeveloper          = isDeveloper;
        theIsDemo               = isDemo;
        theIsShowModuleRegistry = isShowModuleRegistry;

        if( theRootModuleRequirement == null ) {
            throw new SoftwareInstallationException( "Root Module cannot be determined" );
        }
        if( moduleDebugStream != null ) {
            ModuleErrorHandler.setDebugStream( moduleDebugStream );
        }
    }

    /**
     * Obtain one of the enumerated values in RECOGNIZED_PLATFORMS indicating what platform we
     * are running on.
     *
     * @return the platform we are running on
     */
    public final String getPlatform()
    {
        return thePlatform;
    }

    /**
     * Determine whether we are running this in developer mode.
     *
     * @return if true, we are running this in developer mode
     */
    public final boolean isDeveloperMode()
    {
        return theIsDeveloper;
    }

    /**
     * Determine whether we are running this in demo mode.
     *
     * @return if true, we are running this in demo mode
     */
    public final boolean isDemo()
    {
        return theIsDemo;
    }

    /**
     * Determine whether we are supposed to dump the ModuleRegistry to the terminal.
     *
     * @return if true, dump to the terminal.
     */
    public final boolean isShowModuleRegistry()
    {
        return theIsShowModuleRegistry;
    }

    /**
     * Obtain the ModuleRequirement for the root Module as specified on the command-line.
     *
     * @return the ModuleRequirement for the root Module as specified on the command-line
     */
    public final ModuleRequirement getRootModuleRequirement()
    {
        return theRootModuleRequirement;
    }

    /**
     * Obtain the activation class specified in the command-line, if any.
     *
     * @return name of the class
     */
    public final String getOverriddenActivationClassName()
    {
        return theActivationClassName;
    }

    /**
     * Obtain the activation method specified in the command-line, if any.
     *
     * @return name of the method (without class name)
     */
    public final String getOverriddenActivationMethodName()
    {
        return theActivationMethodName;
    }

    /**
     * Obtain the deactivation method specified in the command-line, if any.
     *
     * @return name of the method (without class name)
     */
    public final String getOverriddenDeactivationMethodName()
    {
        return theDeactivationMethodName;
    }

    /**
     * Determine our host name as well as we can. Otherwise return null.
     *
     * @return the host name
     */
    public final synchronized String getHostName()
    {
        if( theHostName == null ) {
            try {
                // we first try to get the numeric address. Then we try to turn
                // it into a DNS name, but we'll use that only if we can actually
                // find the IP address again from the DNS name

                InetAddress localHost = InetAddress.getLocalHost();
                theHostName = localHost.getHostAddress(); // best guess right now

                String dnsName = localHost.getHostName();
                // if we get localhost, or we get a too-short name we just use leave it at the numeric address
                if( ! "localhost".equals( dnsName ) && dnsName.indexOf( '.' ) > 0 ) {
                    // this doesn't help us

                    InetAddress [] reverse = InetAddress.getAllByName( theHostName );
                    for( int i=0 ; i<reverse.length ; ++i ) {
                        if( localHost.equals( reverse[i] )) {
                            theHostName = dnsName;
                            break;
                        }
                    }
                }

            } catch( UnknownHostException ex ) {
                return theHostName;
            }
        }
        return theHostName;
    }

    /**
     * Create a temp file at a suitable place. This can be used by any Module.
     *
     * @param suffix suffix to use
     * @return the created temp file
     * @throws IOException if the temp file could not be created
     */
    public final File createTempFile(
            String suffix )
        throws
            IOException
    {
        return File.createTempFile( "infogrid-", suffix ); // FIXME this sounds a little too trivial
    }

    /**
     * Determine the platform on which we are running.
     *
     * @return the platform on which we are running
     */
    protected static String determinePlatform()
    {
        String ret = null;

        String osName  = System.getProperty( "os.name" );
        String lowerOs = osName.toLowerCase();

        // we check for N-1 because the Nth is the "other" category
        for( int i=0 ; i<RECOGNIZED_PLATFORMS.length-1 ; ++i ) {
            if( lowerOs.indexOf( RECOGNIZED_PLATFORMS[i] ) >= 0 ) {
                ret = RECOGNIZED_PLATFORMS[i];
                break;
            }
        }
        if( ret == null ) {
            ret = OTHER_PLATFORM;
        }
        return ret;
    }

    /**
     * The platform -- one of the values in RECOGNIZED_PLATFORMS.
     */
    protected String thePlatform;

    /**
     * The ModuleRequirement for our root Module.
     */
    protected ModuleRequirement theRootModuleRequirement;

    /**
     * The name of the activation class in the root Module, if specified on the command-line.
     */
    protected String theActivationClassName;

    /**
     * the name of the activation method in the root Module, if specified on the command-line.
     */
    protected String theActivationMethodName;

    /**
     * the name of the deactivation method in the root Module, if specified on the command-line.
     */
    protected String theDeactivationMethodName;

    /**
     * Our host name, once we have determined it.
     */
    protected String theHostName;

    /**
     * Indicates whether we are running in developer mode.
     */
    protected boolean theIsDeveloper;

    /**
     * Indicates whether we are running in demo mode.
     */
    protected boolean theIsDemo;

    /**
     * Indicates whether we are dumping the content of the ModuleRegistry to the terminal.
     */
    protected boolean theIsShowModuleRegistry;

    /**
     * Identifies the Mac OSX platform.
     */
    public static final String MAC_OSX_PLATFORM = "mac os x";

    /**
     * Identifies the Windows platform.
     */
    public static final String WINDOWS_PLATFORM = "windows";

    /**
     * Identifies any other platform.
     */
    public static final String OTHER_PLATFORM   = "other";

    /**
     * The list of platforms that we distinguish currently.
     */
    public static final String [] RECOGNIZED_PLATFORMS = {
            MAC_OSX_PLATFORM,
            WINDOWS_PLATFORM,
            OTHER_PLATFORM
    };
}
