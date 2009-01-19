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

package org.infogrid.meshbase.net;

import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifier;
import org.infogrid.util.StringHelper;

/**
 * Default implementation of NetMeshObjectAccessSpecification.
 */
public class DefaultNetMeshObjectAccessSpecification
        implements
            NetMeshObjectAccessSpecification
{
    private final static long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param accessPath the sequence of network locations to traverse to find one where we can access the MeshObject
     * @param extName   the identifier of the MeshObject there, if different from the default
     */
    protected DefaultNetMeshObjectAccessSpecification(
            NetMeshBaseAccessSpecification [] accessPath,
            NetMeshObjectIdentifier           extName )
    {
        theAccessPath       = accessPath != null ? accessPath : new NetMeshBaseAccessSpecification[0];
        theRemoteIdentifier = extName;
        
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            if( theAccessPath[i] == null ) {
                throw new IllegalArgumentException( "No AccessPath component in NetMeshObjectAccessSpecification must be null" );
            }
        }
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
     * Obtain the Identifier of the NetMeshObject that we are looking for in the remote MeshBase,
     * if different from the default.
     *
     * @return the Identifier of the NetMeshObject that we are looking for, if different from the default
     */
    public NetMeshObjectIdentifier getNoneDefaultNetMeshObjectIdentifier()
    {
        return theRemoteIdentifier;
    }

    /**
     * Obtain the Identifier of the NetMeshObject that we are looking for in the remote NetMeshBase.
     * Calculate it if it is the default.
     *
     * @return the Identifier of the NetMeshObject that we are looking for
     */
    public NetMeshObjectIdentifier getNetMeshObjectIdentifier()
    {
        NetMeshObjectIdentifier ret;
        if( theRemoteIdentifier != null ) {
            ret = theRemoteIdentifier;

        } else  if( theAccessPath == null || theAccessPath.length == 0 ) {
            // FIXME -- not sure this should reference an "A" implementation here
            ret = DefaultAnetMeshObjectIdentifier.create( null, null );

        } else {
            ret = DefaultAnetMeshObjectIdentifier.create( theAccessPath[ theAccessPath.length-1 ].getNetMeshBaseIdentifier(), null );
        }
        return ret;
    }

    /**
     * Obtain an externalized version of this NetMeshObjectAccessSpecification.
     * 
     * @return external form of this NetMeshObjectAccessSpecification similar to URL.toExternalForm()
     */
    public String toExternalForm()
    {
        StringBuilder almostRet = new StringBuilder( 100 ); // fudge number

        String sep = "";
        for( int i=0 ; i<theAccessPath.length ; ++i ) {
            almostRet.append( sep );
            almostRet.append( theAccessPath[i].toExternalForm() );
            sep = "!";
        }
        if( theRemoteIdentifier != null ) {
            almostRet.append( "#" );
            almostRet.append( escapeHash( theRemoteIdentifier.toExternalForm() ));
        }

        return almostRet.toString();
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
     * Convert to String, for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "theAccessPath",
                    "theNonDefaultRemoteIdentifier"
                },
                new Object[] {
                    theAccessPath,
                    theRemoteIdentifier
                });
    }

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
     * The escaped hash sign.
     */
    private static final String ESCAPED_HASH = "&#35;";
}