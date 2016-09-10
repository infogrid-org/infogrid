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
        boolean preen           = false; // automatic repair
        String  logfile         = null;
        String  dbConnectString = null;
        String  dbTable         = null;
        String  dbUser          = null;
        String  dbPassword      = null;
        
        try {
            for( int i = 0; i < args.length ; ++i ) {
                switch( args[i] ) {
                    case "-p":
                        preen = true;
                        break;
                    case "--logfile":
                        if( logfile == null ) {
                            logfile = args[++i];
                        } else {
                            synopsisQuit( 1 );
                        }
                        break;
                    case "-t":
                    case "--table":
                        if( dbTable == null ) {
                            dbTable = args[++i];
                        } else {
                            synopsisQuit( 2 );
                        }
                        break;
                    case "-u":
                    case "--user":
                        if( dbUser == null ) {
                            dbUser = args[++i];
                        } else {
                            synopsisQuit( 3 );
                        }
                        break;
                    case "--password":
                        if( dbPassword == null ) {
                            dbPassword = args[++i];
                        } else {
                            synopsisQuit( 4 );
                        }
                        break;
                    default:
                        if( dbConnectString == null ) {
                            dbConnectString = args[i];
                        } else {
                            synopsisQuit( 5 );
                        }
                        break;
                }
            }
        } catch( ArrayIndexOutOfBoundsException ex ) {
            synopsisQuit( 6 );
        }
        if( dbConnectString == null ) {
            synopsisQuit( 7 );
        }
        
        try {
            if( logfile != null ) {
                Log4jLog.configure( new File( logfile ));
            } else {
                Log4jLog.configure( "Log.properties", Main.class.getClassLoader() );
            }

            Igck theObj = Igck.create( dbConnectString, dbTable, dbUser, dbPassword );
            theObj.setPreen( preen );
            theObj.run();

        } catch( IOException ex ) {
            ex.printStackTrace( System.err );
        }
    }
    
    static void synopsisQuit( int i )
    {
        System.err.println( "Synopsis:" );
        System.err.println( i );
        System.err.println( "    [ -p ] [--table <table>] [--user <user>] [--password <pass>] [--logfile <log4jconfig>] <jdbcString>" );
        System.err.println();
        System.err.println( "Example: -p --table MeshObjects jdbc:mysql://localhost/test" );
        
        System.exit( 1 );
    }
    
    public static final String DEFAULT_TABLE = "MeshObjects";
}
