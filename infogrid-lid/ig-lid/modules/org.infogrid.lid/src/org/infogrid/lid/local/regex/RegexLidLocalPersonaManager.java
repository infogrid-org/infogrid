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

package org.infogrid.lid.local.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.lid.AbstractLidPersonaManager;
import org.infogrid.lid.LidPersona;
import org.infogrid.lid.LidPersonaUnknownException;
import org.infogrid.lid.credential.LidCredentialType;
import org.infogrid.lid.SimpleLidPersona;
import org.infogrid.util.Identifier;

/**
 * A LidPersonaManager that compares user name and password against regular expressions.
 * This is not likely to be useful in a production scenario, but can help during development
 * or testing.
 */
public class RegexLidLocalPersonaManager
        extends
            AbstractLidPersonaManager
{
    /**
     * Factory method.
     * 
     * @param userNameRegex the user name regular expression
     * @param passwordRegex the password regular expression
     * @return the created RegexLidLocalPersonaManager
     */
    public static RegexLidLocalPersonaManager create(
            String userNameRegex,
            String passwordRegex )
    {
        RegexLidLocalPersonaManager ret = new RegexLidLocalPersonaManager(
                Pattern.compile( userNameRegex ),
                Pattern.compile( passwordRegex ));
        
        return ret;
    }

    /**
     * Factory method.
     * 
     * @param userNameRegex the user name regular expression
     * @param passwordRegex the password regular expression
     * @return the created RegexLidLocalPersonaManager
     */
    public static RegexLidLocalPersonaManager create(
            Pattern userNameRegex,
            Pattern passwordRegex )
    {
        RegexLidLocalPersonaManager ret = new RegexLidLocalPersonaManager(
                userNameRegex,
                passwordRegex );
        
        return ret;
    }

    /**
     * Private constructor, use factory method.
     * 
     * @param userNameRegex the user name regular expression
     * @param passwordRegex the password regular expression
     */
    protected RegexLidLocalPersonaManager(
            Pattern userNameRegex,
            Pattern passwordRegex )
    {
        theUserNameRegex = userNameRegex;

        theCredentialTypes = new LidCredentialType[] {
                RegexLidPasswordCredentialType.create( passwordRegex )
        };
        theCredentials = new String[] {
                null
        };
    }

    /**
     * Obtain a LidPersona, given its Identifier.
     *
     * @param identifier the Identifier for which the LidPersona will be retrieved
     * @return the found LidPersona
     * @throws LidPersonaUnknownException thrown if no LidPersona exists with this Identifier
     */
    public LidPersona find(
            Identifier identifier )
        throws
            LidPersonaUnknownException
    {
        if( isUser( identifier )) {
            HashMap<String,String> attributes  = new HashMap<String,String>();
            attributes.put( LidPersona.IDENTIFIER_ATTRIBUTE_NAME, identifier.toExternalForm() );
            attributes.put( "FirstName",  "John" );
            attributes.put( "LastName",   "Doe" );
            attributes.put( "Profession", "Mythical Man" );
            
            LidPersona ret = SimpleLidPersona.create( identifier, null, attributes, theCredentialTypes, theCredentials );
        
            return ret;

        } else {
            throw new LidPersonaUnknownException( identifier );
        }
    }

    /**
     * Determine whether a record with the given username exists.
     * 
     * @param userName the user name
     * @return true if a record exists
     */
    public boolean isUser(
            Identifier userName )
    {
        if( userName == null ) {
            return false;
        }
        if( theUserNameRegex == null ) {
            return false; // no parameter, always say no
        }
        Matcher userNameMatcher = theUserNameRegex.matcher( userName.toExternalForm() );
        if( !userNameMatcher.matches() ) {
            return false;
        }
        return true;
    }
    
    /**
     * The user name regular expression.
     */
    protected Pattern theUserNameRegex;

    /**
     * The credential types accepted.
     */
    protected LidCredentialType [] theCredentialTypes;

    /**
     * The credential values (not used).
     */
    protected String [] theCredentials;
}
