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

package org.infogrid.lid.gpg;

import org.infogrid.context.Context;

import org.infogrid.lid.AbstractLidClientSignature;
import org.infogrid.lid.LidSubjectArea;
import org.infogrid.lid.yadis.YadisSubjectArea;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.ByTypeMeshObjectSelector;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.mesh.set.m.ImmutableMMeshObjectSet;
import org.infogrid.util.FactoryException;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

/**
 * A LID GPG-based ClientSignature.
 */
public class LidGpgClientSignature
        extends
            AbstractLidClientSignature
{
    private static final Log log = Log.getLogInstance( LidGpgClientSignature.class ); // our own, private logger
    
    /**
     * Constructor. Use factory method.
     */
    public LidGpgClientSignature(
            SaneRequest request,
            String     identifier,
            String     credential,
            String     lidCookieString,
            String     sessionId,
            String     target,
            String     nonce,
            Context    context )
    {
        super( request, identifier, credential, lidCookieString, sessionId, target, nonce, context );
    }

    /**
     * Obtain the credential type provided by the client, if any.
     *
     * @return the credential type provided by the client, or null
     */
    public String getCredentialType()
    {
        return GPG_CREDENTIAL_TYPE;
    }

    /**
      * The internal method that determines whether or not a request was signed, and if so,
      * whether the signature is any good.
      *
      * @return true if the request was signed validly
      * @throws AbortProcessingException thrown if an error occurred
      */
    protected MeshObject determineSignedGoodRequest(
            MeshObject persona )
        throws
            IOException
    {
        MeshObjectSet lidServices = persona.getMeshBase().getMeshObjectSetFactory().createImmutableMeshObjectSet(
                persona.traverse( YadisSubjectArea.SITE_MAKESUSEOF_SERVICE.getSource() ),
                ByTypeMeshObjectSelector.create( LidSubjectArea.LIDGPGSSO ));

        if( lidServices.isEmpty() ) {
            return null;
        }

        MeshObjectSet endpoints = lidServices.traverse( YadisSubjectArea.SERVICE_ISPROVIDEDATENDPOINT_SITE.getSource() );

        LidGpgPublicKeyManager thePublicKeyManager = theContext.findContextObject( LidGpgPublicKeyManager.class );
        if( thePublicKeyManager == null ) {
            log.error( "Cannot find LidPublicKeyManager in the context: LID GPG validation not possible" );
            return null;
        }
        LidGpg theGpg            = LidGpg.create();
        String personaIdentifier = persona.getIdentifier().toExternalForm();

        String thePublicKey = null;
        try {
            thePublicKey = thePublicKeyManager.obtainFor( personaIdentifier );

        } catch( FactoryException ex ) {
            log.warn( ex );
        }
        if( thePublicKey == null ) {
            // can't do anything here
            return null;
        }

        if( theNonce == null ) {
            return null;
        }

        // we do the parsing ourselves to make sure there's no problem that's silently ignored by
        // other parsers
        Matcher m = theLidNoncePattern.matcher( theNonce );
        if( !m.matches() ) {
            if( log.isInfoEnabled() ) {
                log.info( "Invalid GpgClearsignRequest because LID V2 nonce has invalid format" );
            }
            return null;
        }
        int year   = Integer.parseInt( m.group( 1 ));
        int month  = Integer.parseInt( m.group( 2 ));
        int day    = Integer.parseInt( m.group( 3 ));
        int hour   = Integer.parseInt( m.group( 4 ));
        int minute = Integer.parseInt( m.group( 5 ));
        int second = Integer.parseInt( m.group( 6 ));
        double milli = Double.parseDouble( m.group( 7 ));

        if(    year   < 2000 || month  < 1  || month > 12 || day    < 1  || day > 31
            || hour   < 0    || hour   > 23 || minute < 0 || minute > 59
            || second < 0    || second > 62 || milli < 0  || milli  >= 1000 )
        {
            // leap seconds can go to 62, according to perldoc TimeDate
            if( log.isInfoEnabled() ) {
                log.info( "Invalid GpgClearsignRequest because LID V2 nonce is out of range" );
            }
            return null;
        }

        Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ));
        cal.set( year, month-1, day, hour, minute, second ); // month is 0-based
        long nonceTime = cal.getTimeInMillis();

        cal.clear();
        cal.setTimeInMillis( System.currentTimeMillis() );
        long now = cal.getTimeInMillis();

        long delta = now - nonceTime;
        delta /= 1000L;

        if( delta > 0 ) {
            if( delta > theMaxNonceAge ) {
                if( log.isInfoEnabled() ) {
                    log.info( "Invalid GpgClearsignRequest because LID V2 nonce is too old: " + delta );
                }
                return null;
            }
        } else {
            if( delta < -theMaxNonceFuture ) {
                if( log.isInfoEnabled() ) {
                    log.info( "Invalid GpgClearsignRequest because LID V2 nonce is in the future: " + (-delta));
               }
                return null;
            }
        }

        theGpg.importPublicKey( thePublicKey );

        String  fullUri    = theRequest.getAbsoluteFullUri();
        String  postString = theRequest.getPostData();

        String signedText = theGpg.reconstructSignedMessage( fullUri, postString, theCredential );

        boolean ret = theGpg.validateSignedText( personaIdentifier, signedText );
        if( ret ) {
            return persona;
        } else {
            return null;
        }
    }

    /**
     * The name of the GPG credential type.
     */
    public static final String GPG_CREDENTIAL_TYPE = "gpg --clearsign";
}
    
