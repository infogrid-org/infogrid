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

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;

/**
 * Thrown if access to a remote MeshObject was not possible.
 */
public class NetMeshObjectAccessException
        extends
            MeshObjectAccessException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param mb the MeshBase in which the Exception occurred
     * @param partialResult a partial result, if any, available at the time the Exception occurred
     * @param failedPaths the NetMeshObjectAccessSpecifications that were used
     * @param causes the underlying causes for the Exception, in the same order as the failedIdentifiers
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshObject []                    partialResult,
            NetMeshObjectAccessSpecification [] failedPaths,
            Throwable []                        causes )
    {
        this( mb, partialResult, failedPaths, causes, null );
    }

    /**
     * Constructor.
     *
     * @param mb the MeshBase in which the Exception occurred
     * @param partialResult a partial result, if any, available at the time the Exception occurred
     * @param failedPaths the NetMeshObjectAccessSpecifications that were used
     * @param causes the underlying causes for the Exception, in the same order as the failedIdentifiers
     * @param cause the cause, if any
     */
    public NetMeshObjectAccessException(
            NetMeshBase                         mb,
            NetMeshObject []                    partialResult,
            NetMeshObjectAccessSpecification [] failedPaths,
            Throwable []                        causes,
            Throwable                           cause )
    {
        super( mb, mb.getIdentifier(), partialResult, identifiersOf( failedPaths ), causes, cause );
        
        theFailedPaths = failedPaths;
    }

    /**
     * Obtain the MeshBase in which the Exception occurred.
     *
     * @return the MeshBase in which the Exception occurred
     */
    @Override
    public NetMeshBase getMeshBase()
    {
        return (NetMeshBase) super.getMeshBase();
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
     * Obtain the failed NetMeshObjectAccessSpecifications.
     *
     * @return the NetMeshObjectAccessSpecifications
     */
    public NetMeshObjectAccessSpecification [] getFailedAccessSpecifications()
    {
        return theFailedPaths;
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
        return new Object[] { theFailedPaths }; // single-element array
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
     * Determine the cause for this MeshObjectIdentifier.
     *
     * @param key the MeshObjectIdentifier
     * @return the cause, if any
     */
    public Throwable getCauseFor(
            NetMeshObjectAccessSpecification key )
    {
        return super.getCauseFor( key.getNetMeshObjectIdentifier() );
    }

    /**
     * The paths to the MeshObjects(s) whose access threw this Exception.
     */
    protected NetMeshObjectAccessSpecification [] theFailedPaths;
}
