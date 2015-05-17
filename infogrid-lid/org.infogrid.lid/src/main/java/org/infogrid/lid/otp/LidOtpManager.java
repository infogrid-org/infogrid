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

import java.io.IOException;
import org.infogrid.util.FactoryException;
import org.infogrid.util.Identifier;

/**
 * Knows how to issue and validate one-time passwords.
 */
public interface LidOtpManager
{
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
            FactoryException;

    /**
     * Validate the one-time password.
     *
     * @param lid identifier of the user submitting the OTP
     * @param siteIdentifier identifier of the site to which the OTP was submitted
     * @param otpCandidate the submitted OTP
     * @param credentialType the LidCredentialType being checked
     * @throws LidWrongOtpException thrown if an invalid OTP was provided
     * @throws LidOtpExpiredException thrown if the OTP was correct but expired
     */
    public void validateOtp(
            Identifier           lid,
            Identifier           siteIdentifier,
            String               otpCandidate,
            LidOtpCredentialType credentialType )
        throws
            LidWrongOtpException,
            LidOtpExpiredException;
}

