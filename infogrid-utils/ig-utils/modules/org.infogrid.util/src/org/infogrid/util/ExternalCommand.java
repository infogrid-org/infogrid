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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.infogrid.util.logging.Log;

/**
 * Utility class to execute external commands. An external command can either be executed directly (by means of the
 * static execute method) or as a Runnable, which can be submitted to a thread pool etc.
 *
 */
public class ExternalCommand
        implements
            Runnable
{
    private static final Log log = Log.getLogInstance( ExternalCommand.class ); // our own, private logger

    /**
     * Constructor, for the case the ExternalCommand is run as a Runnable.
     *
     * @param builder the ProcessBuilder holding the command, arguments, environment etc.
     * @param stdinContent if given, contains input data that shall be piped into command
     */
    public ExternalCommand(
            ProcessBuilder builder,
            String         stdinContent )
    {
        theBuilder      = builder;
        theStdinContent = stdinContent;
        theStdoutBuf    = new StringBuilder();
        theStderrBuf    = new StringBuilder();
    }

    /**
     * Enables this ExternalCommand to be submitted to a thread pool etc.
     */
    public void run()
    {
        if( log.isInfoEnabled() ) {
            log.info( this, "Running ", theBuilder, theStdinContent );
        }

        int returnValue;

        try {
            returnValue = execute( theBuilder, theStdinContent, theStdoutBuf, theStderrBuf );

            if( returnValue != 0 ) {
                log.error( "Returned", returnValue, theStdoutBuf, theStderrBuf );
            } else {
                log.info( "Returned", returnValue, theStdoutBuf, theStderrBuf );
            }

        } catch( IOException ex ) {
            log.error( ex );
        }
    }

    /**
     * Execute an external command that does not read from stdin or write to stdout.
     *
     * @param builder the ProcessBuilder holding the command, arguments, environment etc.
     * @return exit code of the child process
     * @throws IOException thrown if an input/output error occurred
     */
    public static int execute(
            ProcessBuilder builder )
        throws
            IOException
    {
        return execute( builder, null, null, null );
    }

    /**
     * Execute an external command with data that we provide and that we can get back.
     *
     * This is an implementation with a lot of overhead (3 Threads plus the
     * one Thread that the JDK creates itself!) but I can't quite think of
     * anything that is reasonably simple and likely to work.
     *
     * @param builder the ProcessBuilder holding the command, arguments, environment etc.
     * @param stdinContent if given, contains input data that shall be piped into command
     * @param stdoutBuf if given, command's standard output will be redirected into this buffer
     * @param stderrBuf if given, command's error output will be redirected into this buffer
     * @return exit code of the child process
     * @throws IOException thrown if an input/output error occurred
     */
    public static int execute(
            ProcessBuilder      builder,
            final String        stdinContent,
            final StringBuilder stdoutBuf,
            final StringBuilder stderrBuf )
        throws
            IOException
    {
        if( log.isDebugEnabled() ) {
            log.debug( "about to execute", builder.command(), stdinContent );
        }

        final Process p = builder.start();

        Thread inputThread  = null;
        Thread outputThread = null;
        Thread errorThread  = null;

        if( stdinContent != null ) {
            inputThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            OutputStreamWriter writer = new OutputStreamWriter( p.getOutputStream());
                            writer.write( stdinContent );
                            writer.close();
                        } catch( IOException ex ) {
                            log.error( ex );
                        }
                    }
            };
        }
        if( stdoutBuf != null ) {
            outputThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStreamReader reader = new InputStreamReader( p.getInputStream());
                            int cc = 0;
                            while( ( cc = reader.read()) >= 0 ) {
                                char c = (char) cc;
                                stdoutBuf.append( c );
                            }
                        } catch( IOException ex ) {
                            log.error( ex );
                        }
                    }
            };
        }
        if( stderrBuf != null ) {
            errorThread = new Thread() {
                    @Override
                    public void run()
                    {
                        try {
                            InputStreamReader reader = new InputStreamReader( p.getErrorStream());
                            int cc = 0;
                            while( ( cc = reader.read()) >= 0 )
                            {
                                char c = (char) cc;
                                stderrBuf.append( c );
                            }

                        } catch( IOException ex ) {
                            log.error( ex );
                        }
                    }
            };
        }
        else if( log.isDebugEnabled() ) {
            errorThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int cc = 0;
                            InputStreamReader errorReader = new InputStreamReader( p.getErrorStream());
                            StringBuilder buf = new StringBuilder();
                            while( ( cc = errorReader.read()) >= 0  ) {
                                char c = (char) cc;
                                buf.append( c );
                                if( c == '\n' ) {
                                    log.debug( "Process reported: " + buf );
                                    buf = new StringBuilder();
                                }
                            }
                            if( buf.length() > 0 ) {
                                log.debug( "Process reported: " + buf );
                            }
                        } catch( IOException ex ) {
                            log.error( ex );
                        }
                    }
                };
        }

        if( inputThread != null ) {
            inputThread.start();
        }
        if( outputThread != null ) {
            outputThread.start();
        }
        if( errorThread != null ) {
            errorThread.start();
        }

        try {
            int returnValue = p.waitFor();

            if( inputThread != null ) {
                inputThread.join();
            }
            if( outputThread != null ) {
                outputThread.join();
            }
            if( errorThread != null ) {
                errorThread.join();
            }

            if( log.isDebugEnabled() ) {
                log.debug( "execution returned " + returnValue );
            }
            return returnValue;
        } catch( InterruptedException ex ) {
            log.error( ex );
        }
        return -1;
    }

    /**
     * The ProcessBuilder holding all the arguments, environment etc.
     */
    protected ProcessBuilder theBuilder;

    /**
     * The data written to stdin.
     */
    protected String theStdinContent;

    /**
     * The buffer for data written to stdout.
     */
    protected StringBuilder theStdoutBuf;

    /**
     * The buffer for data written to stderr.
     */
    protected StringBuilder theStderrBuf;
}
