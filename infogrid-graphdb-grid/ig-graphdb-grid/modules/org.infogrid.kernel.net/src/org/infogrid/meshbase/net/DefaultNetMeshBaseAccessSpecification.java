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

package org.infogrid.meshbase.net;

import org.infogrid.util.AbstractIdentifier;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * Default implementation of NetMeshBaseAccessSpecification.
 */
public class DefaultNetMeshBaseAccessSpecification
        extends
             AbstractIdentifier
        implements
            NetMeshBaseAccessSpecification,
            CanBeDumped
{
    /**
     * Constructor.
     *
     * @param netMeshBase identifies the NetMeshBase to access
     * @param coherence the CoherenceSpecification for the access
     * @param scopeSpecification the ScopeSpecification for the access
     * @param asEnteredByUser String form as entered by the user, if any. This helps with error messages.
     */
    protected DefaultNetMeshBaseAccessSpecification(
            NetMeshBaseIdentifier  netMeshBase,
            ScopeSpecification     scopeSpecification,
            CoherenceSpecification coherence,
            String                 asEnteredByUser )
    {
        super( asEnteredByUser );
        
        theNetMeshBaseIdentifier  = netMeshBase;
        theScopeSpecification     = scopeSpecification;
        theCoherenceSpecification = coherence;
    }
    
    /**
     * Obtain the NetMeshBaseIdentifier.
     *
     * @return the NetMeshBaseIdentifier
     */
    public NetMeshBaseIdentifier getNetMeshBaseIdentifier()
    {
        return theNetMeshBaseIdentifier;
    }
    
    /**
     * Obtain the ScopeSpecification, if any.
     *
     * @return the ScopeSpecification
     */
    public ScopeSpecification getScopeSpecification()
    {
        return theScopeSpecification;
    }

    /**
     * Obtain the CoherenceSpecification, if any.
     *
     * @return the CoherenceSpecification
     */
    public CoherenceSpecification getCoherenceSpecification()
    {
        return theCoherenceSpecification;
    }

    /**
     * Convert NetMeshBaseAccessSpecification into an external form.
     *
     * @return the external form
     */
    public String toExternalForm()
    {
        if( theNetMeshBaseIdentifier == null ) {
            return "<not initialized yet>"; // can happen in the debugger
        }
        StringBuilder ret = new StringBuilder();
        ret.append( theNetMeshBaseIdentifier.toExternalForm() );

        char sep = '?';
        if( theScopeSpecification != null ) {
            ret.append( sep );
            ret.append( SCOPE_KEYWORD ).append( "=" );
            ret.append( HTTP.encodeToValidUrlArgument( theScopeSpecification.toExternalForm() ));
            sep = '&';
        }
        if( theCoherenceSpecification != null ) {
            ret.append( sep );
            ret.append( COHERENCE_KEYWORD ).append( "=" );
            ret.append( HTTP.encodeToValidUrlArgument( theCoherenceSpecification.toExternalForm() ));
            sep = '&';
        }
        return ret.toString();
    }

    /**
     * Obtain a String representation of this instance that can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentation(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                StringRepresentation.DEFAULT_ENTRY,
                pars,
                this );

        return ret;

    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String contextPath         = null;
        String additionalArguments = null;
        String target              = null;
        String title               = null;

        if( pars != null ) {
            contextPath         = (String) pars.get(  StringRepresentationParameters.WEB_RELATIVE_CONTEXT_KEY );
            target              = (String) pars.get( StringRepresentationParameters.LINK_TARGET_KEY );
            title               = (String) pars.get( StringRepresentationParameters.LINK_TITLE_KEY );
            additionalArguments = (String) pars.get( StringRepresentationParameters.HTML_URL_ADDITIONAL_ARGUMENTS );
        }

        String externalForm = toExternalForm();

        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                DEFAULT_LINK_START_ENTRY,
                null,
        /* 0 */ contextPath,
        /* 1 */ externalForm,
        /* 2 */ additionalArguments,
        /* 3 */ target,
        /* 4 */ title );

        return ret;
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation
     * @return String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        String contextPath = null;
        String externalForm = toExternalForm();

        if( pars != null ) {
            contextPath = (String) pars.get( StringRepresentationParameters.WEB_RELATIVE_CONTEXT_KEY );
        }

        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                DEFAULT_LINK_END_ENTRY,
                null,
                contextPath,
                externalForm );
        return ret;
    }

    /**
     * Determine equality.
     *
     * @param other the Object to compare against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(
            Object other )
    {
        if( !( other instanceof NetMeshBaseAccessSpecification )) {
            return false;
        }
        NetMeshBaseAccessSpecification realOther = (NetMeshBaseAccessSpecification) other;

        if( !theNetMeshBaseIdentifier.equals( realOther.getNetMeshBaseIdentifier() )) {
            return false;
        }
        if( theScopeSpecification != null ) {
            if( !theScopeSpecification.equals( realOther.getScopeSpecification() )) {
                return false;
            }
        } else if( realOther.getScopeSpecification() != null ) {
            return false;
        }
        if( theCoherenceSpecification != null ) {
            if( !theCoherenceSpecification.equals( realOther.getCoherenceSpecification() )) {
                return false;
            }
        } else if( realOther.getCoherenceSpecification() != null ) {
            return false;
        }
        return true;
    }

    /**
     * Determine hash code.
     * 
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        int ret = 0;
        if( theNetMeshBaseIdentifier != null ) {
            ret ^= theNetMeshBaseIdentifier.hashCode();
        }
        if( theScopeSpecification != null ) {
            ret ^= theScopeSpecification.hashCode();
        }
        if( theCoherenceSpecification != null ) {
            ret ^= theCoherenceSpecification.hashCode();
        }
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
                        "netMeshBase",
                        "scope",
                        "coherence"
                },
                new Object[] {
                        theNetMeshBaseIdentifier,
                        theScopeSpecification,
                        theCoherenceSpecification
                });
    }

    /**
     * Convert to String form, for debugging.
     *
     * @return String form
     */
    @Override
    public String toString()
    {
        return toExternalForm();
    }

    /**
     * The Identifier of the NetMeshBase.
     */
    protected NetMeshBaseIdentifier theNetMeshBaseIdentifier;
    
    /**
     * The Scope of access.
     */
    protected ScopeSpecification theScopeSpecification;

    /**
     * The requested Coherence.
     */
    protected CoherenceSpecification theCoherenceSpecification;

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_ENTRY = "String";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_LINK_START_ENTRY = "LinkStartString";

    /**
     * Entry in the resource files, prefixed by the StringRepresentation's prefix.
     */
    public static final String DEFAULT_LINK_END_ENTRY = "LinkEndString";
}
