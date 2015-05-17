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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid.otp;

import org.infogrid.lid.credential.AbstractLidCredentialType;
import org.infogrid.lid.credential.LidExpiredCredentialException;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.Identifier;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.util.logging.Log;

/**
 * A one-time password type of credential.
 * There's no need to use a NonceManager because this CredentialType has nonce-like functionality
 * built in.
 */
public class LidOtpCredentialType
        extends
            AbstractLidCredentialType
{
    private static final Log log = Log.getLogInstance( LidOtpCredentialType.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param otpManager the LidOtpManager to use
     * @return the created LidOtpCredentialType
     */
    public static LidOtpCredentialType create(
            LidOtpManager otpManager )
    {
        LidOtpCredentialType ret = new LidOtpCredentialType( otpManager );

        return ret;
    }

    /**
     * Constructor, for subclasses only.
     *
     * @param otpManager the LidOtpManager to use
     */
    protected LidOtpCredentialType(
            LidOtpManager   otpManager )
    {
        theOtpManager = otpManager;
    }

    /**
     * Determine whether this LidCredentialType is contained in this request.
     *
     * @param request the request
     * @return true if this LidCredentialType is contained in this request
     */
    public boolean isContainedIn(
            SaneRequest request )
    {
        if( request.matchUrlArgument( LID_CREDTYPE_PARAMETER_NAME, LID_OTP_PARAMETER_VALUE )) {
            return true;
        }

        return false;
    }

    /**
     * Determine whether the request contains a valid LidCredentialType of this type
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @param siteIdentifier identifies the site
     * @throws LidExpiredCredentialException thrown if the contained LidCredentialType has expired
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    public void checkCredential(
            SaneRequest   request,
            HasIdentifier subject,
            Identifier    siteIdentifier )
        throws
            LidExpiredCredentialException,
            LidInvalidCredentialException
    {
        SaneRequest originalRequest = request.getOriginalSaneRequest();

        String credential = originalRequest.getUrlArgument( LID_CREDENTIAL_PARAMETER_NAME );
        if( credential == null || credential.length() == 0 ) {
            throw new LidWrongOtpException( subject.getIdentifier(), siteIdentifier, this );
        }

        theOtpManager.validateOtp( subject.getIdentifier(), siteIdentifier, credential, this );
    }

    /**
     * Determine whether this LidCredentialType is a one-time token credential, e.g.
     * a one-time password.
     *
     * @return true if this is a one-time token credential
     */
    public boolean isOneTimeToken()
    {
        return true; // it is because of the nonce
    }

    /**
     * Determine equality.
     *
     * @param other the objects to compare against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( other instanceof LidOtpCredentialType ) {
            LidOtpCredentialType realOther = (LidOtpCredentialType) other;

            if( !theOtpManager.equals( realOther.theOtpManager )) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return theOtpManager.hashCode();
    }

    /**
     * The LidOtpManager to use.
     */
    protected LidOtpManager theOtpManager;

    /**
     * Name of the LidCredentialType.
     */
    public static final String LID_OTP_PARAMETER_VALUE = "otp";
}
