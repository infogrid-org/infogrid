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

package org.infogrid.lid.gpg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.lid.nonce.LidNonceManager;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.ExternalCommand;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Identifier;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.HTTP;

/**
 * Implementation of LidGpg as wrapper around the GPG commandline tool.
 */
public class CommandLineWrapperLidGpg
        extends
            LidGpg
{
    /**
     * Factory method.
     *
     * @param nonceManager the LidNonceManager to use
     * @throws IOException GPG's home directory could not be created
     * @return the created instance
     */
    public static CommandLineWrapperLidGpg create(
            LidNonceManager nonceManager )
        throws
            IOException
    {
        CommandLineWrapperLidGpg ret = new CommandLineWrapperLidGpg( nonceManager );
        return ret;
    }

    /**
     * Constructor.
     *
     * @param nonceManager the LidNonceManager to use
     * @throws IOException GPG's home directory could not be created
     */
    protected CommandLineWrapperLidGpg(
            LidNonceManager nonceManager )
        throws
            IOException
    {
        super( nonceManager );

        // make sure the home directory exists
        theGpgHomeDir = null;
        if( theGpgHomeDirChoices != null ) {
            for( String choice : theGpgHomeDirChoices ) {
                File homeDir = new File( choice );
                if( !homeDir.exists() ) {
                    homeDir.mkdirs();
                }
                if( homeDir.isDirectory() ) {
                    theGpgHomeDir = homeDir;
                    break;
                }
            }
            if( theGpgHomeDir == null ) {
                // if something is specified but cannot be created, throw
                throw new IOException( "Cannot create GPG working directory, tried: " + ArrayHelper.join( theGpgHomeDirChoices ));
            }
        }
    }

    /**
     * Generate private/public key pair for this LID.
     *
     * @param key the LID URL for which a key pair needs to be generated
     * @param arg ignored
     * @return the generated LidKeyPair
     * @throws FactoryException thrown if the LidKeyPair could not be created
     */
    public LidKeyPair obtainFor(
            Identifier key,
            Void       arg )
        throws
            FactoryException
    {
        synchronized( SYNC ) {

            File batchFile = null;
            try {
                // This follows the example from the gpg doc/DETAILS file
                String batchFileContent =
                          "Key-Type: DSA\n"
                        + "Key-Length: 1024\n"
                        + "Subkey-Type: ELG-E\n"
                        + "Subkey-Length: 1024\n"
                        + "Name-Real: " + key.toExternalForm() + "\n"
                        + "Expire-Date: 0\n"
                        + "%commit\n";

                batchFile = File.createTempFile( getClass().getName(), "" );

                FileWriter batchFileWriter = new FileWriter( batchFile );

                batchFileWriter.write( batchFileContent );
                batchFileWriter.close();

                exec( new ProcessBuilder( theExecutable, "--batch", "--gen-key", batchFile.getAbsolutePath()));

                // now construct return values

                StringBuilder output = new StringBuilder();
                exec( new ProcessBuilder( theExecutable, "--export", "--armor", "=" + key.toExternalForm()), null, output, null );

                String publicKey = output.toString();

                output = new StringBuilder();
                exec( new ProcessBuilder( theExecutable, "--export-secret-keys", "--armor", "=" + key.toExternalForm()), null, output, null );

                String privateKey = output.toString();

                LidKeyPair ret = new LidKeyPair( key, publicKey, privateKey );
                return ret;

            } catch( IOException ex ) {
                throw new FactoryException( this, ex );
            } finally {
                if( CLEANUP ) {
                    batchFile.delete();
                    cleanupHomedir();
                }
            }
        }
    }

    /**
     * Validate a piece of signed text against a LID.
     *
     * @param lid the LID URL
     * @param signedText the signed text
     * @param publicKey the public key to use
     * @return true if the validation was successful
     * @throws IOException an I/O error occurred
     */
    public boolean validateSignedText(
            String lid,
            String signedText,
            String publicKey )
        throws
            IOException
    {
        if( log.isDebugEnabled() ) {
            log.debug( "Signed text:\n" + signedText );
        }

        synchronized( SYNC ) {
            try {
                exec( new ProcessBuilder( theExecutable, "--import" ), publicKey, null, null );

                StringBuilder output = new StringBuilder();

                int result = exec( new ProcessBuilder( theExecutable, "--verify" ), signedText, null, output );
                // Note that result == 0 means *good*, not bad

                if( result>0 ) {
                    String stringOutput = output.toString();
                    log.warn( "Bad electronic signature on signed '" + signedText + "', output is '" + stringOutput + "'"  );

                    return false;
                }
                boolean ret;
                Matcher m = theLidInGpgOutputPattern.matcher( output );
                if( m.matches() ) {
                    String group1 = m.group( 1 );
                    if( lid.equals( group1 )) {
                        ret = true;
                    } else {
                        ret = false;
                    }
                } else {
                    ret = false;
                }

                if( !ret && log.isDebugEnabled() ) {
                    log.debug( "gpg --verify responded with " + output + ", but we were looking for \"" + lid + "\".\n" );
                }

                return ret;

            } finally {
                if( CLEANUP ) {
                    cleanupHomedir();
                }
            }
        }
    }

    /**
     * Sign a URL.
     *
     * @param lid the LID that will sign the URL
     * @param url the URL to be signed
     * @param privateKey the private key to use
     * @param lidVersion the LID protocol version to use
     * @return the signed URL
     * @throws IOException an I/O error occurred
     */
    public String signUrl(
            String lid,
            String url,
            String privateKey,
            String lidVersion )
        throws
            IOException
    {
        synchronized( SYNC ) {
            try {
                // amazingly enough, there does not seem to be a difference between importing
                // public and private keys in GPG.
                StringBuilder errorData = new StringBuilder();

                exec( new ProcessBuilder( theExecutable, "--import" ), privateKey, null, errorData );

                StringBuilder append = new StringBuilder();
                append.append( url );
                if( url.indexOf( '?' ) >= 0 ) {
                    append.append( '&' );
                } else {
                    append.append( '?' );
                }

                append.append( "lid=" );
                append.append( HTTP.encodeToValidUrlArgument( lid ));
                append.append( "&" ).append( LidCredentialType.LID_CREDTYPE_PARAMETER_NAME ).append( "=" ).append( HTTP.encodeToValidUrlArgument( LidGpgClearSignCredentialType.LID_GPG_CLEARSIGN_PARAMETER_VALUE ));

                String nonce = theNonceManager.generateNewNonce();
                append.append( "&" ).append( LidNonceManager.LID_NONCE_PARAMETER_NAME ).append(  "=" );
                append.append( HTTP.encodeToValidUrlArgument( nonce ));

                StringBuilder output = new StringBuilder( 256 );
                exec( new ProcessBuilder( theExecutable, "--clearsign", "-u", "=" + lid ), append.toString(), output, null );

                String outputString = output.toString();

                Matcher m = thePgpSignedPattern.matcher( outputString );
                if( m.matches() ) {
                    String hash = m.group( 1 );
                    String sig  = m.group( 3 );

                    if( lidVersion != null && lidVersion.startsWith( "1." )) {
                        append.append( "&credential=" ).append( HTTP.encodeToValidUrlArgument( hash + "\n" + sig ));
                    } else {
                        append.append( "&" ).append( LidCredentialType.LID_CREDENTIAL_PARAMETER_NAME ).append( "=" ).append( HTTP.encodeToValidUrlArgument( hash + "\n" + sig ));
                    }
                    return append.toString();

                }
                throw new RuntimeException(
                        "Our regular expression did not match gpg output '"
                                + outputString
                                + "', lid is '"
                                + lid
                                + "', gpg is "
                                + theExecutable
                                + ", gpgHomeDir is "
                                + theGpgHomeDir
                                + ", thePgpSignedPattern is '"
                                + thePgpSignedPattern
                                + "'" );
            } finally {
                if( CLEANUP ) {
                    cleanupHomedir();
                }
            }
        }
    }

    /**
     * Helper method to cleanup Gpg's home directory.
     * This must be invoked at the end of each operation, not at the beginning:
     * this way, it is clean upon application re-start (which may be run with
     * a different user id).
     */
    protected void cleanupHomedir()
    {
        if( theGpgHomeDir != null ) {
            File contained [] = theGpgHomeDir.listFiles();
            if( contained != null ) {
                // If this is null, something else might be wrong (e.g. homeDir does not exist),
                // but the cleanup method is not the place where to test that
                for( File current : contained ) {
                    current.delete();
                }
            }
        }
    }
    
    /**
     * Execute the command in the correct environment.
     * 
     * @param builder the ProcessBuilder holding the command, arguments, environment etc.
     * @return exit code
     * @throws IOException thrown if an I/O problem occurred
     */
    protected int exec(
            ProcessBuilder builder )
        throws
            IOException
    {
        return ExternalCommand.execute( builder );
    }

    /**
     * Execute the command in the correct environment.
     *
     * @param builder the ProcessBuilder holding the command, arguments, environment etc.
     * @param stdinContent if given, contains input data that shall be piped into command
     * @param stdoutBuf if given, command's standard output will be redirected into this buffer
     * @param stderrBuf if given, command's error output will be redirected into this buffer
     * @return exit code
     * @throws IOException thrown if an I/O problem occurred
     */
    protected int exec(
            ProcessBuilder builder,
            String         stdinContent,
            StringBuilder  stdoutBuf,
            StringBuilder  stderrBuf )
        throws
            IOException
    {
        if( theGpgHomeDir != null ) {
            builder.environment().put( "GNUPGHOME", theGpgHomeDir.getAbsolutePath() );
        }
        return ExternalCommand.execute( builder, stdinContent, stdoutBuf, stderrBuf );
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( CommandLineWrapperLidGpg.class );

    /**
     * Name of the executable.
     */
    protected static final String theExecutable = theResourceHelper.getResourceStringOrDefault( "GpgPath", "/usr/bin/gpg" );

    /**
     * The Gpg home directory choices
     */
    protected static final String [] theGpgHomeDirChoices = theResourceHelper.getResourceStringArrayOrDefault(
            "GpgHomedir",
            new String[] { "/tmp/org.infogrid.lid.gpg.CommandLineWrapperLidGpg" } ); // applications should set this to something else

    /**
     * The actual Gpg home directory.
     */
    protected File theGpgHomeDir;

    /**
     * The pattern in the gpg output that contains our LID.
     */
    protected static final Pattern theLidInGpgOutputPattern = Pattern.compile(
            "[\\s\\S]*\"(.*)\"[\\s\\S]*" );

    /**
     * The pattern in the gpg output that matches a signed message.
     */
    protected static final Pattern thePgpSignedPattern = Pattern.compile(
            "-----BEGIN PGP SIGNED MESSAGE-----\\s"
                    + "Hash: (\\S+)\\s\\s"
                    + "(.*)\n"
                    + "-----BEGIN PGP SIGNATURE-----\\s"
                    + "([\\s\\S]*)-----END PGP SIGNATURE-----\\s*",
            Pattern.MULTILINE | Pattern.DOTALL );

    /**
     * If true, each operation will delete files after it is done. This is good for production.
     * Set to false in debugging situations.
     */
    protected boolean CLEANUP = theResourceHelper.getResourceBooleanOrDefault( "DeleteFiles", true );

    /**
     * Synchronization flag across all threads.
     */
    protected static final Object SYNC = new Object();
}
