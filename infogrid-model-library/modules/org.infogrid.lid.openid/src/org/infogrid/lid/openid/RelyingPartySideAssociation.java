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

package org.infogrid.lid.openid;

/**
 * An association, as held by a RelyingParty.
 */
public class RelyingPartySideAssociation
        extends
            EitherSideAssociation
{
    /**
     * Factory method.
     *
     * @param serverUrl URL of the identity server with which we have this Association
     * @return the created RelyingPartySideAssociation
     */
    public static RelyingPartySideAssociation create(
            String  serverUrl,
            String  associationHandle,
            byte [] sharedSecret,
            long    issuedTime,
            long    expiryTime )
    {
        return new RelyingPartySideAssociation( serverUrl, associationHandle, sharedSecret, issuedTime, expiryTime );
    }

    /**
     * Constructor.
     */
    protected RelyingPartySideAssociation(
            String  serverUrl,
            String  associationHandle,
            byte [] sharedSecret,
            long    issuedTime,
            long    expiryTime )
    {
        super( associationHandle, sharedSecret, issuedTime, expiryTime );

        theServerUrl = serverUrl;
    }

    /**
     * Obtain the URL of the server.
     *
     * @return the URL of the server
     */
    public String getServerUrl()
    {
        return theServerUrl;
    }

    /**
     * Helper method to make sure we have a complete association.
     *
     * @throws IllegalStateException if the association is not complete
     */
    public void checkCompleteness()
        throws
            IllegalStateException
    {
        StringBuffer error = new StringBuffer();

        if( theServerUrl == null ) {
            error.append( "Have no serverUrl. " );
        }
        if( theAssociationHandle == null ) {
            error.append( "Have no associationHandle. " );
        }
        if( theSharedSecret == null ) {
            error.append( "Have no shared secret. " );
        } else if( theSharedSecret.length != 20 ) {
            error.append( "Have shared secret with wrong length (" ).append( theSharedSecret.length ).append( "). " );
        }
        if( theIssuedTime < 0L ) {
            error.append( "Have no issuedTime. " );
        }
        if( theExpiryTime < 0L ) {
            error.append( "Have no expiryTime. " );
        }

        if( error.length() > 0 ) {
            String errorString = error.toString();

            throw new IllegalStateException( errorString );
        }
    }
    
    /**
     * The URL of our server.
     */
    protected String theServerUrl;
}
