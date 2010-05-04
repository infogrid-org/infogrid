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

package org.infogrid.lid.account.prepostfix;

import org.infogrid.lid.account.LidAccountManager;
import org.infogrid.lid.account.translate.TranslatingLidAccountManager;
import org.infogrid.util.Identifier;
import org.infogrid.util.SimpleStringIdentifier;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * Maps local usernames into identity URLs by prefixing or postfixing
 * a constant character string.
 */
public class PrePostfixTranslatingAccountManager
        extends
            TranslatingLidAccountManager
        implements
            CanBeDumped
{
    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param prefix the prefix
     * @param delegate the delegate LidLocalPersonaManager
     * @return the created PrePostfixTranslatingAccountManager
     */
    public static PrePostfixTranslatingAccountManager create(
            Identifier        siteIdentifier,
            String            prefix,
            LidAccountManager delegate )
    {
        PrePostfixTranslatingAccountManager ret = new PrePostfixTranslatingAccountManager(
                siteIdentifier,
                prefix,
                null,
                delegate );

        return ret;
    }

    /**
     * Factory method.
     *
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param prefix the prefix, if any
     * @param postfix the postfix, if any
     * @param delegate the delegate LidLocalPersonaManager
     * @return the created PrePostfixTranslatingAccountManager
     */
    public static PrePostfixTranslatingAccountManager create(
            Identifier        siteIdentifier,
            String            prefix,
            String            postfix,
            LidAccountManager delegate )
    {
        PrePostfixTranslatingAccountManager ret = new PrePostfixTranslatingAccountManager(
                siteIdentifier,
                prefix,
                postfix,
                delegate );

        return ret;
    }

    /**
     * Constructor for subclasses only.
     * 
     * @param siteIdentifier identifier of the site at which the accounts are managed
     * @param prefix the prefix, if any
     * @param postfix the postfix, if any
     * @param delegate the delegate LidLocalPersonaManager
     */
    protected PrePostfixTranslatingAccountManager(
            Identifier        siteIdentifier,
            String            prefix,
            String            postfix,
            LidAccountManager delegate )
    {
        super( siteIdentifier, delegate );
        
        thePrefix  = prefix;
        thePostfix = postfix;
    }

    /**
     * Translate the identifier as used by this class into the identifier as used by the delegate.
     * 
     * @param identifier input parameter
     * @return translated identifier
     */
    protected Identifier translateIdentifierForward(
            Identifier identifier )
    {
        if( identifier == null ) {
            return null;
        }
        String s = identifier.toExternalForm();

        if( thePrefix != null && !s.startsWith( thePrefix )) {
            throw new IllegalArgumentException( "identifier " + identifier + " does not start with prefix " + thePrefix );
        }
        if( thePostfix != null && !s.endsWith( thePostfix )) {
            throw new IllegalArgumentException( "identifier " + identifier + " does not end with postfix " + thePostfix );
        }
        
        String almost;
        if( thePrefix != null ) {
            if( thePostfix != null ) {
                almost = s.substring( thePrefix.length(), s.length() - thePostfix.length() );
            } else {
                almost = s.substring( thePrefix.length() );
            }
        } else if( thePostfix != null ) {
            almost = s.substring( 0, s.length() - thePostfix.length() );
        } else {
            almost = s;
        }
        Identifier ret = SimpleStringIdentifier.create( almost.toString());
        return ret;
    }

    /**
     * Translate the identifier as used by the delegate into the identifier as used by this class.
     * 
     * @param identifier input parameter
     * @return translated identifier
     */
    protected Identifier translateIdentifierBackward(
            Identifier identifier )
    {
        if( identifier == null ) {
            return null;
        }
        StringBuilder almost = new StringBuilder();
        if( thePrefix != null ) {
            almost.append( thePrefix );
        }
        almost.append( identifier.toExternalForm() );
        if( thePostfix != null ) {
            almost.append( thePostfix );
        }
        Identifier ret = SimpleStringIdentifier.create( almost.toString());
        return ret;
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "thePrefix",
                    "thePostfix",
                    "theDelegate"
                },
                new Object[] {
                    thePrefix,
                    thePostfix,
                    theDelegate
                } );
    }

    /**
     * The prefix.
     */
    protected String thePrefix;
    
    /**
     * The postfix.
     */
    protected String thePostfix;
}
