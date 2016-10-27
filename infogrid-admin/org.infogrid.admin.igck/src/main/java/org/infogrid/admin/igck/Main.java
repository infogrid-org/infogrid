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

package org.infogrid.admin.igck;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.infogrid.model.primitives.text.ModelPrimitivesStringRepresentationDirectorySingleton;
import org.infogrid.util.logging.log4j.Log4jLog;

/**
 * The equivalent of fsck for InfoGrid.
 */
public class Main {
    /**
     * Main program
     * 
     * @param args the command line arguments
     */
    public static void main(
            String [] args )
    {
        boolean checkMissingNeighbors                = false;
        boolean checkMissingTypes                    = false;
        boolean checkMultiplicities                  = false;
        boolean checkValues                          = false;
        boolean checkReferentialIntegrity            = false;
        boolean removeMissingNeighbors               = false;
        boolean removeMissingTypes                   = false;
        boolean assignDefaultsToMandatoryNulls       = false;
        boolean assignDefaultsIfIncompatibleDataType = false;
        String  meshBaseId                           = null;
        String  dbTable                              = null;
        String  proxyTable                           = null;
        String  shadowTable                          = null;
        String  shadowProxyTable                     = null;
        String  dbUser                               = null;
        String  dbPassword                           = null;
        String  logfile                              = null;
        String  dbConnectString                      = null;
        boolean isNetMeshBase                        = false;
        int     verbose                              = 1;
        
        try {
            for( int i = 0; i < args.length ; ++i ) {
                switch( args[i].toLowerCase() ) {
                    case "--checkmissingneighbors":
                        checkMissingNeighbors = true;
                        break;
                    case "--checkmissingtypes":
                        checkMissingTypes = true;
                        break;
                    case "--checkmultiplicities":
                        checkMultiplicities = true;
                        break;
                    case "--checkvalues":
                        checkValues = true;
                        break;
                    case "--checkreferentialintegrity":
                        checkReferentialIntegrity = true;
                        break;
                    case "--removemissingneighbors":
                        removeMissingNeighbors = true;
                        break;
                    case "--removemissingtypes":
                        removeMissingTypes = true;
                        break;
                    case "--assigndefaultstomandatorynulls":
                        assignDefaultsToMandatoryNulls = true;
                        break;
                    case "--assigndefaultsifincompatibledatatype":
                        assignDefaultsIfIncompatibleDataType = true;
                        break;
                    case "--logfile":
                        if( logfile == null ) {
                            logfile = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-t":
                    case "--table":
                        if( dbTable == null ) {
                            dbTable = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "--id":
                        if( meshBaseId == null ) {
                            meshBaseId = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-pt":
                    case "--proxytable":
                        if( proxyTable == null ) {
                            proxyTable = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-st":
                    case "--shadowtable":
                        if( shadowTable == null ) {
                            shadowTable = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-spt":
                    case "--shadowproxytable":
                        if( shadowProxyTable == null ) {
                            shadowProxyTable = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-u":
                    case "--user":
                        if( dbUser == null ) {
                            dbUser = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-p":
                    case "--password":
                        if( dbPassword == null ) {
                            dbPassword = args[++i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                    case "-n":
                    case "--netmeshbase":
                        isNetMeshBase = true;
                        break;
                    case "-v":
                    case "--verbose":
                        ++verbose;
                        break;
                    case "-q":
                    case "--quiet":
                        --verbose;
                        break;
                    default:
                        if( args[i].startsWith( "--" ) ) {
                            // unknown option
                            synopsisQuit();
                        }
                        if( dbConnectString == null ) {
                            dbConnectString = args[i];
                        } else {
                            synopsisQuit();
                        }
                        break;
                }
            }
        } catch( ArrayIndexOutOfBoundsException ex ) {
            synopsisQuit();
        }
        if( dbConnectString == null ) {
            synopsisQuit();
        }
        
        if( removeMissingNeighbors ) {
            checkMissingNeighbors = true;
        }
        if( removeMissingTypes ) {
            checkMissingTypes = true;
        }
        if( assignDefaultsToMandatoryNulls || assignDefaultsIfIncompatibleDataType ) {
            checkValues = true;
        }

        if( !checkMissingNeighbors && !checkMissingTypes && !checkMultiplicities && !checkValues && !checkReferentialIntegrity ) {
            // default is to check all
            checkMissingNeighbors     = true;
            checkMissingTypes         = true;
            checkMultiplicities       = true;
            checkValues               = true;
            checkReferentialIntegrity = true;
        }
        
        if( dbTable == null ) {
            dbTable = "MeshObjects";
        }
        if( proxyTable == null ) {
            proxyTable = "Proxies";
        }
        if( shadowTable == null ) {
            shadowTable = "Shadows";
        }
        if( shadowProxyTable == null ) {
            shadowProxyTable = "ShadowProxies";
        }

        try {
            if( logfile != null ) {
                Log4jLog.configure( new File( logfile ));
            } else {
                Log4jLog.configure( "Log.properties", Main.class.getClassLoader() );
            }
            
            ModelPrimitivesStringRepresentationDirectorySingleton.initialize();

            Igck theObj;
            if( isNetMeshBase ) {
                if( meshBaseId == null ) {
                    synopsisQuit();
                }
                theObj = NetIgck.create( meshBaseId, dbConnectString, dbTable, proxyTable, shadowTable, shadowProxyTable, dbUser, dbPassword );
            } else {
                theObj = Igck.create( meshBaseId, dbConnectString, dbTable, dbUser, dbPassword );
            }
            theObj.setCheckMissingNeighbors(                checkMissingNeighbors );
            theObj.setCheckMissingTypes(                    checkMissingTypes );
            theObj.setCheckMultiplicities(                  checkMultiplicities );
            theObj.setCheckValues(                          checkValues );
            theObj.setCheckReferentialIntegrity(            checkReferentialIntegrity );
            theObj.setRemoveMissingNeighbors(               removeMissingNeighbors );
            theObj.setRemoveMissingTypes(                   removeMissingTypes );
            theObj.setAssignDefaultsToMandatoryNulls(       assignDefaultsToMandatoryNulls );
            theObj.setAssignDefaultsIfIncompatibleDataType( assignDefaultsIfIncompatibleDataType );
            theObj.setVerbose(                              verbose );

            theObj.run();

        } catch( ParseException ex ) {
            System.err.println( "ERROR: the provided MeshBaseIdentifier could not be parsed: " + meshBaseId );

        } catch( IOException ex ) {
            ex.printStackTrace( System.err );
        }
    }
    
    static void synopsisQuit()
    {
        System.err.println( "Arguments:" );
        System.err.println( "    [--checkmissingneighbors]                : check for missing neighbor MeshObjects" );
        System.err.println( "    [--checkmissingtypes]                    : check for MeshTypes used that cannot be resolved" );
        System.err.println( "    [--checkmultiplicities]                  : check that all RoleType multiplicities are obeyed" );
        System.err.println( "    [--checkvalues]                          : check that all PropertyValues are allowed" );
        System.err.println( "    [--checkreferentialintegrity]            : check referential integrity" );
        System.err.println( "    [--removemissingneighbors]               : remove references to missing neighbor MeshObjects" );
        System.err.println( "    [--removemissingtypes]                   : remove references to MeshTypes used that cannot be resolved" );
        System.err.println( "    [--assigndefaultstomandatorynulls]       : assign default values to non-optional properties that are null" );
        System.err.println( "    [--assigndefaultsifincompatibledatatype] : assign default values to properties with values not conforming with the property's data type" );
        
        System.err.println( "    [--id <identifier>]                      : identifier of the MeshBase; required for NetMeshBases" );
        System.err.println( "    [--table <table>]                        : the database table containing the MeshObjects (default: MeshObjects)" );
        System.err.println( "    [--proxytable <table>]                   : the database table containing the Proxies (default: Proxies)" );
        System.err.println( "    [--shadowtable <table>]                  : the database table containing the Shadow MeshBases (default: Shadows)" );
        System.err.println( "    [--shadowproxytable <table>]             : the database table containing the Shadow MeshBases' Proxies (default: ShadowProxies)" );
        System.err.println( "    [--user <user>]                          : the database username to use" );
        System.err.println( "    [--password <pass>]                      : the database password to use" );
        System.err.println( "    [--netmeshbase]                          : instantiate a NetMeshBase instead of a MeshBase" );
        System.err.println( "    [--logfile <log4jconfig>]                : alternate log4j config file" );
        System.err.println( "    [--verbose] | [--quiet]                  : increase or decrease verbosity level" );
        System.err.println( "    jdbc:<engine>://<host>/<database         : the JDBC database connection string" );
        
        System.exit( 1 );
    }
    
    public static final String DEFAULT_TABLE = "MeshObjects";
}
