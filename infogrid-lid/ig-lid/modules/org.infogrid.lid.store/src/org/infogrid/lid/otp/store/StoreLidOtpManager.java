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

package org.infogrid.lid.otp.store;

import java.io.IOException;
import java.util.Arrays;
import org.infogrid.lid.LidClientAuthenticationPipelineStage;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.otp.LidOtpCredentialType;
import org.infogrid.lid.otp.LidOtpManager;
import org.infogrid.store.Store;
import org.infogrid.store.StoreKeyDoesNotExistException;
import org.infogrid.store.StoreValue;
import org.infogrid.store.prefixing.PrefixingStore;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Identifier;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.UniqueStringGenerator;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

/**
 * LidOtpManager backed by a Store.
 */
public class StoreLidOtpManager
        implements
            LidOtpManager
{
    private static final Log log = Log.getLogInstance( StoreLidOtpManager.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param store the Store to use
     * @return the created StoreLidOtpManager
     */
    public static StoreLidOtpManager create(
            Store store )
    {
        return new StoreLidOtpManager( store, theDefaultExpirationDuration );
    }

    /**
     * Factory method.
     *
     * @param store the Store to use
     * @param expirationDuration the number of milliseconds until issued OTPs expire
     * @return the created StoreLidOtpManager
     */
    public static StoreLidOtpManager create(
            Store store,
            long  expirationDuration )
    {
        return new StoreLidOtpManager( store, expirationDuration );
    }

    /**
     * Private constructor, use factory method.
     *
     * @param store the Store to use
     * @param expirationDuration the number of milliseconds until issued OTPs expire
     */
    protected StoreLidOtpManager(
            Store store,
            long  expirationDuration )
    {
        theStore              = store;
        theExpirationDuration = expirationDuration;
    }

    /**
     * Attach a new one-time password to a URL.
     *
     * @param lid identifier of the user for which this OTP is issued
     * @param siteIdentifier identifier of the site at which the OTP is valid
     * @param url the URL to which the otp is attached
     * @return the URL with the attached OTP
     */
    public String attachOtp(
            Identifier lid,
            Identifier siteIdentifier,
            String     url )
        throws
            IOException,
            FactoryException
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "attachOtp", lid, siteIdentifier, url );
        }

        long   now       = System.currentTimeMillis();
        long   then      = now + theExpirationDuration;
        String lidString = lid.toExternalForm();
        Store  siteStore = PrefixingStore.create( siteIdentifier.toExternalForm(), theStore );
        String otp       = theGenerator.createUniqueToken();

        siteStore.putOrUpdate( lidString, ENCODING, now, now, -1L, then, otp.getBytes() );

        StringBuilder ret = new StringBuilder();
        ret.append( url );
        HTTP.appendArgumentToUrl( ret, LidClientAuthenticationPipelineStage.LID_PARAMETER_NAME, lidString );
        HTTP.appendArgumentToUrl( ret, LidCredentialType.LID_CREDTYPE_PARAMETER_NAME, LidOtpCredentialType.LID_OTP_PARAMETER_VALUE );
        HTTP.appendArgumentToUrl( ret, LidCredentialType.LID_CREDENTIAL_PARAMETER_NAME, otp );

        return ret.toString();
    }

    /**
     * Validate the one-time password.
     *
     * @param lid identifier of the user submitting the OTP
     * @param siteIdentifier identifier of the site to which the OTP was submitted
     * @param otpCandidate the submitted OTP
     * @return true if the OTP is valid
     */
    public boolean validateOtp(
            Identifier lid,
            Identifier siteIdentifier,
            String     otpCandidate )
    {
        if( log.isTraceEnabled() ) {
            log.traceMethodCallEntry( this, "validateOtp", lid, siteIdentifier, otpCandidate );
        }
        String lidString = lid.toExternalForm();
        Store  siteStore = PrefixingStore.create( siteIdentifier.toExternalForm(), theStore );

        try {
            StoreValue found = siteStore.get( lidString );
            if( found.isExpired() ) {
                return false;
            }
            if( !Arrays.equals( found.getData(), otpCandidate.getBytes() )) {
                return false;
            }
            // delete only if the OTP is valid
            siteStore.delete( lidString );
            return true;

        } catch( StoreKeyDoesNotExistException ex ) {
            return false;

        } catch( IOException ex ) {
            log.error( ex );
            return false;
        }
    }

    /**
     * The underlying Store.
     */
    protected Store theStore;

    /**
     * The number of milliseconds until an issued OTP expires.
     */
    protected long theExpirationDuration;

    /**
     * Generates the OTPs.
     */
    protected static UniqueStringGenerator theGenerator = UniqueStringGenerator.create( 16 );

    /**
     * The default number of milliseconds until an issued OTP expires.
     */
    protected static final long theDefaultExpirationDuration
            = ResourceHelper.getInstance( StoreLidOtpManager.class ).getResourceLongOrDefault(
                    "DefaultExpirationDuration", 8 * 60 * 60 * 1000L ); // 8 hours

    /**
     * The encoding.
     */
    protected static final String ENCODING = StoreLidOtpManager.class.getName();
}

