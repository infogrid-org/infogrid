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

import java.util.ArrayList;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.util.ArrayHelper;

/**
 * Thrown if access to one or more remote NetMeshObjects failed. This
 * subclass adds information to MeshObjectAccessException relevant only
 * to NetMeshBases.
 */
public class NetMeshObjectAccessException
        extends
            MeshObjectAccessException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param mb the NetMeshBase in which the Exception occurred
     * @param attemptedPaths the NetMeshObjectAccessSpecification that were attempted to be accessed
     * @param results a partial result, available at the time the Exception occurred
     * @param seeOtherPaths NetMeshObjectAccessSpecification of the NetMeshObjects to be accessed instead (aka redirect)
     * @param causes the underlying causes for the Exception, in the same order as the attemptedPaths
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshObjectAccessSpecification [] attemptedPaths,
            NetMeshObject []                    results,
            NetMeshObjectAccessSpecification [] seeOtherPaths,
            Throwable []                        causes )
    {
        this( mb, mb.getIdentifier(), attemptedPaths, results, seeOtherPaths, causes, null );
    }

    /**
     * Constructor.
     *
     * @param mb the NetMeshBase in which the Exception occurred
     * @param attemptedPaths the NetMeshObjectAccessSpecification that were attempted to be accessed
     * @param results a partial result, available at the time the Exception occurred
     * @param seeOtherPaths NetMeshObjectAccessSpecification of the NetMeshObjects to be accessed instead (aka redirect)
     * @param causes the underlying causes for the Exception, in the same order as the attemptedPaths
     * @param cause the underlying cause for this Exception, if there is one unrelated to the individual causes
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshObjectAccessSpecification [] attemptedPaths,
            NetMeshObject []                    results,
            NetMeshObjectAccessSpecification [] seeOtherPaths,
            Throwable []                        causes,
            Throwable                           cause )
    {
        this( mb, mb.getIdentifier(), attemptedPaths, results, seeOtherPaths, causes, cause );
    }

    /**
     * Constructor.
     *
     * @param mb the NetMeshBase in which the Exception occurred
     * @param mbIdentifier the NetMeshBaseIdentifier of the NetMeshBase in which the Exception occurred
     * @param attemptedPaths the NetMeshObjectAccessSpecification that were attempted to be accessed
     * @param results a partial result, available at the time the Exception occurred
     * @param seeOtherPaths NetMeshObjectAccessSpecification of the NetMeshObjects to be accessed instead (aka redirect)
     * @param causes the underlying causes for the Exception, in the same order as the attemptedPaths
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshBaseIdentifier               mbIdentifier,
            NetMeshObjectAccessSpecification [] attemptedPaths,
            NetMeshObject []                    results,
            NetMeshObjectAccessSpecification [] seeOtherPaths,
            Throwable []                        causes )
    {
        this( mb, mbIdentifier, attemptedPaths, results, seeOtherPaths, causes, null );
    }

     /**
     * Constructor.
     *
     * @param mb the NetMeshBase in which the Exception occurred
     * @param mbIdentifier the NetMeshBaseIdentifier of the NetMeshBase in which the Exception occurred
     * @param attemptedPaths the NetMeshObjectAccessSpecification that were attempted to be accessed
     * @param results a partial result, available at the time the Exception occurred
     * @param seeOtherPaths NetMeshObjectAccessSpecification of the NetMeshObjects to be accessed instead (aka redirect)
     * @param causes the underlying causes for the Exception, in the same order as the attemptedPaths
     * @param cause the underlying cause for this Exception, if there is one unrelated to the individual causes
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshBaseIdentifier               mbIdentifier,
            NetMeshObjectAccessSpecification [] attemptedPaths,
            NetMeshObject []                    results,
            NetMeshObjectAccessSpecification [] seeOtherPaths,
            Throwable []                        causes,
            Throwable                           cause )
    {
        super(  mb,
                mbIdentifier,
                identifiersOf( attemptedPaths ),
                results,
                identifiersOf( seeOtherPaths ),
                causes,
                cause );
        
        theAttemptedPaths = attemptedPaths;
        theSeeOtherPaths  = seeOtherPaths;

    }

    /**
     * Obtain the NetMeshBase in which the Exception occurred.
     *
     * @return the NetMeshBase in which the Exception occurred
     */
    @Override
    public NetMeshBase getMeshBase()
    {
        return (NetMeshBase) super.getMeshBase();
    }

    /**
     * Obtain the identifier of the NetMeshBase in which the Exception occurred.
     *
     * @return the identifier of the NetMeshBase in which the Exception occurred
     */
    @Override
    public NetMeshBaseIdentifier getMeshBaseIdentifier()
    {
        return (NetMeshBaseIdentifier) super.getMeshBaseIdentifier();
    }

    /**
     * Obtain the partial result, if any.
     *
     * @return the partial result, if any
     */
    @Override
    public NetMeshObject [] getBestEffortResult()
    {
        return (NetMeshObject []) super.getBestEffortResult();
    }

    /**
     * Obtain the see others, if any. (aka redirects)
     *
     * @return the see others, if any.
     */
    @Override
    public NetMeshObjectIdentifier [] getSeeOther()
    {
        return (NetMeshObjectIdentifier []) super.getSeeOther();
    }

    /**
     * Obtain the see others, if any. (aka redirects)
     *
     * @return the see others, if any.
     */
    public NetMeshObjectAccessSpecification [] getSeeOtherPaths()
    {
        return theSeeOtherPaths;
    }

    /**
     * Obtain the see other for a given NetMeshObjectIdentifier.
     *
     * @param key the NetMeshObjectIdentifier
     * @return the redirect value, if any
     */
    public NetMeshObjectAccessSpecification getSeeOtherPathFor(
            NetMeshObjectIdentifier key )
    {
        for( int i=0 ; i<theAttemptedIdentifiers.length ; ++i ) {
            if( key.equals( theAttemptedIdentifiers[i] )) {
                return theSeeOtherPaths[i];
            }
        }
        throw new IllegalArgumentException( "Unknown key: " + key.toExternalForm() );
    }

    /**
     * Obtain the attempted NetMeshObjectAccessSpecifications.
     *
     * @return the NetMeshObjectAccessSpecifications
     */
    public NetMeshObjectAccessSpecification [] getAttemptedAccessSpecifications()
    {
        return theAttemptedPaths;
    }

    /**
     * Determine the cause for this NetMeshObjectAccessSpecification.
     *
     * @param key the NetMeshObjectAccessSpecification
     * @return the cause, if any
     */
    public Throwable getCauseFor(
            NetMeshObjectAccessSpecification key )
    {
        return super.getCauseFor( key.getNetMeshObjectIdentifier() );
    }

    /**
     * Obtain the parameters with which the internationalized string
     * will be parameterized.
     *
     * @return the parameters with which the internationalized string will be parameterized
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        ArrayList<NetMeshObjectIdentifier> notFoundIdentifiers      = new ArrayList<NetMeshObjectIdentifier>( theAttemptedIdentifiers.length );
        ArrayList<NetMeshObjectIdentifier> somewhereElseIdentifiers = new ArrayList<NetMeshObjectIdentifier>( theAttemptedIdentifiers.length );
        ArrayList<NetMeshObjectIdentifier> foundIdentifiers         = new ArrayList<NetMeshObjectIdentifier>( theAttemptedIdentifiers.length );

        for( int i=0 ; i<theAttemptedIdentifiers.length ; ++i ) {
            if( theResults[i] != null ) {
                if( theResults[i].getIdentifier().equals( theAttemptedIdentifiers[i] )) {
                    foundIdentifiers.add( (NetMeshObjectIdentifier) theAttemptedIdentifiers[i] );
                } else {
                    somewhereElseIdentifiers.add( (NetMeshObjectIdentifier) theAttemptedIdentifiers[i] );
                }
            } else {
                notFoundIdentifiers.add( (NetMeshObjectIdentifier) theAttemptedIdentifiers[i] );
            }
        }

        return new Object[] {
                theAttemptedIdentifiers,
                theResults,
                theSeeOthers,
                theCauses,
                ArrayHelper.copyIntoNewArray( foundIdentifiers,         NetMeshObjectIdentifier.class ),
                ArrayHelper.copyIntoNewArray( somewhereElseIdentifiers, NetMeshObjectIdentifier.class ),
                ArrayHelper.copyIntoNewArray( notFoundIdentifiers,      NetMeshObjectIdentifier.class ),
                getCause(),
                theAttemptedPaths,
                theSeeOtherPaths
        };
    }

    /**
     * Helper method to obtain the remote Identifiers of an array of NetMeshObjectAccessSpecifications.
     *
     * @param specs the NetMeshObjectAccessSpecifications
     * @return the NetMeshObjectIdentifiers contained therein
     */
    public static NetMeshObjectIdentifier [] identifiersOf(
            NetMeshObjectAccessSpecification [] specs )
    {
        NetMeshObjectIdentifier [] ret = new NetMeshObjectIdentifier[ specs.length ];
        for( int i=0 ; i<ret.length ; ++i ) {
            if( specs[i] != null ) {
                ret[i] = specs[i].getNetMeshObjectIdentifier();
            } // else null
        }
        return ret;
    }

    /**
     * The paths to the MeshObjects(s) whose access threw this Exception.
     */
    protected NetMeshObjectAccessSpecification [] theAttemptedPaths;

    /**
     * The paths to the redirected NetMeshObjects.
     */
    protected NetMeshObjectAccessSpecification [] theSeeOtherPaths;
}
