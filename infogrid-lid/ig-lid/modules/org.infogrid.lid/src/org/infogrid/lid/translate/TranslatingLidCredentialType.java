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

package org.infogrid.lid.translate;

import org.infogrid.lid.LidPersona;
import org.infogrid.lid.LidPersonaUnknownException;
import org.infogrid.lid.credential.AbstractLidCredentialType;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.credential.LidInvalidCredentialException;
import org.infogrid.lid.credential.TranslatingLidInvalidCredentialException;
import org.infogrid.util.InvalidIdentifierException;
import org.infogrid.util.http.SaneRequest;

/**
 * A LidCredentialType that implements the parallel to TranslatingLidPersonaManager.
 */
public class TranslatingLidCredentialType
        extends
            AbstractLidCredentialType
{
    /**
     * Factory method.
     *
     * @param bridge the corresponding TranslatingLidPersonaManager
     * @param delegate the delegate LidCredentialType
     * @return the created TranslatingLidCredentialType
     */
    public static TranslatingLidCredentialType create(
            TranslatingLidPersonaManager bridge,
            LidCredentialType                 delegate )
    {
        return new TranslatingLidCredentialType( bridge, delegate );
    }

    /**
     * Constructor.
     *
     * @param bridge the corresponding TranslatingLidPersonaManager
     * @param delegate the delegate LidCredentialType
     */
    protected TranslatingLidCredentialType(
            TranslatingLidPersonaManager bridge,
            LidCredentialType                 delegate )
    {
        theBridge   = bridge;
        theDelegate = delegate;
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
        return theDelegate.isContainedIn( request );
    }

    /**
     * Determine whether the request contains a valid LidCredentialType of this type
     * for the given subject.
     *
     * @param request the request
     * @param subject the subject
     * @throws LidInvalidCredentialException thrown if the contained LidCdedentialType is not valid for this subject
     */
    public void checkCredential(
            SaneRequest request,
            LidPersona  subject )
        throws
            LidInvalidCredentialException
    {
        LidPersona delegateSubject;
        try {
            delegateSubject = theBridge.translatePersonaForward( subject );
        } catch( LidPersonaUnknownException ex ) {
            throw new TranslatingLidInvalidCredentialException( null, subject.getIdentifier(), this, ex );

        } catch( InvalidIdentifierException ex ) {
            throw new TranslatingLidInvalidCredentialException( null, subject.getIdentifier(), this, ex );
        }

        try {
            theDelegate.checkCredential( request, delegateSubject );

        } catch( LidInvalidCredentialException ex ) {
            throw new TranslatingLidInvalidCredentialException( delegateSubject.getIdentifier(), subject.getIdentifier(), this, ex );

        }
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
        if( other instanceof TranslatingLidCredentialType ) {
            TranslatingLidCredentialType realOther = (TranslatingLidCredentialType) other;

            if( !theBridge.equals( realOther.theBridge )) {
                return false;
            }

            return theDelegate.equals( realOther.theDelegate );
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
        return theBridge.hashCode() ^ theDelegate.hashCode();
    }

    /**
     * The corresponding TranslatingLidPersonaManager.
     */
    protected TranslatingLidPersonaManager theBridge;

    /**
     * The delegate.
     */
    protected LidCredentialType theDelegate;
}
