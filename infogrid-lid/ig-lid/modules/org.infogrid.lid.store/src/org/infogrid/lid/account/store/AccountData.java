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

package org.infogrid.lid.account.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.infogrid.lid.account.LidAccount;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.Identifier;
import org.infogrid.util.logging.Log;

/**
 * Helper class to package attributes and credentials into the same instance.
 */
public class AccountData
{
    private static final Log log = Log.getLogInstance( AccountData.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param status the status of the Account
     */
    public AccountData(
            LidAccount.LidAccountStatus status )
    {
        theStatus            = status;
        theRemoteIdentifiers = new ArrayList<Identifier>();
        theAttributes        = new HashMap<String,String>();
        theCredentialTypes   = new ArrayList<LidCredentialType>();
        theCredentialValues  = new ArrayList<String>();
        theGroupIdentifiers  = new ArrayList<Identifier>();
    }

    /**
     * Constructor.
     *
     * @param status the status of the Account
     * @param remoteIdentifiers identifiers of the remote personas associated with this Account
     * @param attributes the attributes
     * @param credentials the credentials
     * @param groupIdentifiers identifiers of the groups the Account belongs to
     */
    public AccountData(
            LidAccount.LidAccountStatus   status,
            ArrayList<Identifier>         remoteIdentifiers,
            Map<String,String>            attributes,
            Map<LidCredentialType,String> credentials,
            ArrayList<Identifier>         groupIdentifiers )
    {
        theStatus            = status;
        theRemoteIdentifiers = remoteIdentifiers;
        theAttributes        = attributes;

        theCredentialTypes  = new ArrayList<LidCredentialType>();
        theCredentialValues = new ArrayList<String>();

        if( credentials != null ) {
            for( LidCredentialType key : credentials.keySet() ) {
                String value = credentials.get(  key );

                theCredentialTypes.add( key );
                theCredentialValues.add(  value );
            }
        }
        theGroupIdentifiers = groupIdentifiers;
    }

    /**
     * Obtain the account status.
     *
     * @return the account status
     */
    public LidAccount.LidAccountStatus getStatus()
    {
        return theStatus;
    }

    /**
     * Obtain the identifiers of the remote personas, if any.
     *
     * @return the identifiers of the remote personas
     */
    public List<Identifier> getRemoteIdentifiers()
    {
        return theRemoteIdentifiers;
    }

    /**
     * Obtain the attributes of the LidAccount.
     * 
     * @return the attributes
     */
    public Map<String,String> getAttributes()
    {
        return theAttributes;
    }
    
    /**
     * Obtain the LidCredentialTypes of the LidAccount.
     *
     * @return the LidCredentialTypes
     */
    public LidCredentialType [] getCredentialTypes()
    {
        return ArrayHelper.copyIntoNewArray( theCredentialTypes, LidCredentialType.class );
    }

    /**
     * Obtain the values of the LidCredentialTypes of the LidAccount.
     *
     * @return the value
     */
    public String [] getCredentialValues()
    {
        return ArrayHelper.copyIntoNewArray( theCredentialValues, String.class );
    }

    /**
     * Obtain the Identifiers of the set of groups that this LidAccount is a member of.
     *
     * @return the Identifiers
     */
    public Identifier [] getGroupIdentifiers()
    {
        return ArrayHelper.copyIntoNewArray( theGroupIdentifiers, Identifier.class );
    }

    /**
     * Add an attribute.
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    protected void addAttribute(
            String name,
            String value )
    {
        String ret = theAttributes.put( name, value );
        if( ret != null ) {
            log.error( "Overwriting attribute " + name + " with new value " + value + ", was " + ret );
        }
    }

    /**
     * Add a credential.
     * 
     * @param credentialType the credential type
     * @param value the value of the attribute
     */
    protected void addCredential(
            LidCredentialType credentialType,
            String            value )
    {
        theCredentialTypes.add( credentialType );
        theCredentialValues.add( value );
    }

    /**
     * Add a group identifier.
     *
     * @param id the identifier
     */
    protected void addGroupIdentifier(
            Identifier id )
    {
        theGroupIdentifiers.add( id );
    }

    /**
     * The account status.
     */
    protected LidAccount.LidAccountStatus theStatus;

    /**
     * Identifiers of the remote personas, if any.
     */
    protected ArrayList<Identifier> theRemoteIdentifiers;

    /**
     * Attributes of the LidAccount.
     */
    protected Map<String,String> theAttributes;

    /**
     * LidCredentialTypes of the LidAccount.
     */
    protected ArrayList<LidCredentialType> theCredentialTypes;

    /**
     * Values of the LidCredentialTypes, in the same sequence.
     */
    protected ArrayList<String> theCredentialValues;

    /**
     * Identifiers of the groups a LidAccount belongs to.
     */
    protected ArrayList<Identifier> theGroupIdentifiers;
}
