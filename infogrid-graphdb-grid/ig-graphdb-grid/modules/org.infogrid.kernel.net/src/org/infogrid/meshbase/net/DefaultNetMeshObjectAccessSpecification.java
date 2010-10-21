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

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.text.StringRepresentation;
import org.infogrid.util.text.StringRepresentationParameters;
import org.infogrid.util.text.StringifierException;

/**
 * Default implementation of NetMeshObjectAccessSpecification.
 */
public class DefaultNetMeshObjectAccessSpecification
        implements
            NetMeshObjectAccessSpecification,
            CanBeDumped
{
    private final static long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor. Use factory class to instantiate.
     *
     * @param factory the factory that created this object
     * @param accessPath the sequence of network locations to traverse to find one where we can access the MeshObject
     * @param remoteIdentifier the identifier of the MeshObject there, if different from the default
     * @param asEnteredByUser String form as entered by the user, if any. This helps with error messages.
     */
    protected DefaultNetMeshObjectAccessSpecification(
            NetMeshObjectAccessSpecificationFactory factory,
            NetMeshBaseAccessSpecification []       accessPath,
            NetMeshObjectIdentifier                 remoteIdentifier,
            String                                  asEnteredByUser )
    {
        theFactory          = factory;
        theAccessPath       = accessPath != null ? accessPath : new NetMeshBaseAccessSpecification[0];
        theRemoteIdentifier = remoteIdentifier;
        theAsEntered        = asEnteredByUser;
        
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            if( theAccessPath[i] == null ) {
                throw new IllegalArgumentException( "No AccessPath component in NetMeshObjectAccessSpecification must be null" );
            }
        }
        if( remoteIdentifier == null ) {
            throw new NullPointerException();
        }
    }

    /**
     * Obtain the factory that created this object.
     *
     * @return the factory
     */
    public NetMeshObjectAccessSpecificationFactory getFactory()
    {
        return theFactory;
    }

    /**
     * Obtain the NetMeshBaseAccessSpecification path.
     * 
     * @return the path we traverse to the MeshObject we want to access. May be of length 0.
     */
    public NetMeshBaseAccessSpecification [] getAccessPath()
    {
        return theAccessPath;
    }

    /**
     * Obtain the Identifier of the NetMeshObject that we are looking for in the remote NetMeshBase.
     * Calculate it if it is the default.
     *
     * @return the Identifier of the NetMeshObject that we are looking for
     */
    public NetMeshObjectIdentifier getNetMeshObjectIdentifier()
    {
        return theRemoteIdentifier;
    }

    /**
     * Determine the String form of this object as entered by the user, if any.
     * This may be null if the user did not enter anything, or it is not known.
     *
     * @return String form, as entered by the user, if any
     */
    public String getAsEntered()
    {
        return theAsEntered;
    }

    /**
     * Obtain an externalized version of this NetMeshObjectAccessSpecification.
     * 
     * @return external form of this NetMeshObjectAccessSpecification similar to URL.toExternalForm()
     */
    public String toExternalForm()
    {
        if( theAccessPath.length == 0 ) {
            return theRemoteIdentifier.toExternalForm();
        }
        if(    theAccessPath.length == 1
            && theRemoteIdentifier != null
            && theAccessPath[0].getNetMeshBaseIdentifier().equals( theRemoteIdentifier.getNetMeshBaseIdentifier() ))
        {
            // special rule for 1-element access path
            return theRemoteIdentifier.toExternalForm();
        }

        StringBuilder almostRet = new StringBuilder( 100 ); // fudge number

        String sep = "";
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            almostRet.append( sep );
            almostRet.append( theAccessPath[i].toExternalForm() );
            sep = "!";
        }
        if( theRemoteIdentifier != null ) {
            almostRet.append( sep );
            almostRet.append( theRemoteIdentifier.toExternalForm() );
        }

        return almostRet.toString();
    }

    /**
     * Obtain an external form for this Identifier, similar to
     * <code>java.net.URL.toExternalForm()</code>. This is provided
     * to make invocation from JSPs easier.
     *
     * @return external form of this Identifier
     */
    public String getExternalForm()
    {
        return toExternalForm();
    }

    /**
     * Helper method to escape the hash sign.
     *
     * @param s String with hash
     * @return String with escaped hash
     */
    protected static String escapeHash(
            String s )
    {
        int           len = s.length();
        StringBuilder ret = new StringBuilder( len + 10 ); // fudge
        for( int i=0 ; i<len ; ++i ) {
            char c = s.charAt( i );
            switch( c ) {
                case '#':
                    ret.append( ESCAPED_HASH );
                    break;
                default:
                    ret.append( c );
                    break;
            }            
        }
        return ret.toString();
    }

    /**
     * Helper method to descape the hash site.
     *
     * @param s String with escaped hash
     * @return String with regular hash
     */
    protected static String descapeHash(
            String s )
    {
        int           len = s.length();
        StringBuilder ret = new StringBuilder( len );
        
        int startAt = 0;
        int foundAt;
        while( ( foundAt = s.indexOf( ESCAPED_HASH, startAt )) >= 0 ) {
            String sub = s.substring( startAt, foundAt );
            ret.append( sub );
            ret.append( '#' );
            foundAt += ESCAPED_HASH.length();
        }
        String sub = s.substring( startAt );
        ret.append( sub );

        return ret.toString();
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
        if( !( other instanceof NetMeshObjectAccessSpecification )) {
            return false;
        }
        NetMeshObjectAccessSpecification realOther = (NetMeshObjectAccessSpecification) other;
        
        if( theAccessPath.length != realOther.getAccessPath().length ) {
            return false;
        }
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            if( !theAccessPath[i].equals( realOther.getAccessPath()[i] )) {
                return false;
            }
        }
        if( getNetMeshObjectIdentifier() != null ) {
            if( !getNetMeshObjectIdentifier().equals( realOther.getNetMeshObjectIdentifier() )) {
                return false;
            }
        } else if( realOther.getNetMeshObjectIdentifier() != null ) {
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
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            ret ^= theAccessPath[i].hashCode() >> i;
        }
        if( theRemoteIdentifier != null ) {
            ret ^= theRemoteIdentifier.hashCode();
        }
        return ret;
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
        String contextPath = null;

        if( pars != null ) {
            contextPath = (String) pars.get(  StringRepresentationParameters.WEB_RELATIVE_CONTEXT_KEY );
        }

        String ret = rep.formatEntry(
                getClass(), // dispatch to the right subtype
                StringRepresentation.DEFAULT_ENTRY,
                pars,
        /* 0 */ this,
        /* 1 */ contextPath );

        return ret;
    }

    /**
     * Obtain the start part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    public String toStringRepresentationLinkStart(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return "";
    }

    /**
     * Obtain the end part of a String representation of this object that acts
     * as a link/hyperlink and can be shown to the user.
     *
     * @param rep the StringRepresentation
     * @param pars collects parameters that may influence the String representation
     * @throws StringifierException thrown if there was a problem when attempting to stringify
     * @return String representation
     */
    public String toStringRepresentationLinkEnd(
            StringRepresentation           rep,
            StringRepresentationParameters pars )
        throws
            StringifierException
    {
        return "";
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
                    "theAccessPath",
                    "theRemoteIdentifier"
                },
                new Object[] {
                    theAccessPath,
                    theRemoteIdentifier
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
     * The factory that created this object.
     */
    protected NetMeshObjectAccessSpecificationFactory theFactory;

    /**
     * The NetMeshBaseIdentifier path.
     */
    protected NetMeshBaseAccessSpecification [] theAccessPath;

    /**
     * If a non-default MeshObject shall be accessed, this captures the Identifier
     * of that MeshObject at the remote location.
     */
    protected NetMeshObjectIdentifier theRemoteIdentifier;

    /**
     * String form as entered by the user.
     */
    protected String theAsEntered;

    /**
     * The escaped hash sign.
     */
    private static final String ESCAPED_HASH = "%23"; // URL escape, not HTML escape. "&#35;";
}
