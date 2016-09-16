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
// Copyright 1998-2015 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh;

import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseIdentifier;
import org.infogrid.meshbase.MeshObjectAccessException;
import org.infogrid.model.primitives.MeshTypeIdentifier;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.modelbase.MeshTypeWithIdentifierNotFoundException;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * This Exception indicates a violation in the multiplicity of a RoleType. In other
 * words, this Exception is thrown if a relationship between MeshObjects A and B is
 * supposed to be blessed with a RelationshipType X, as a result of which A would
 * participate in more or fewer RoleTypes of RelationshipType type X than allowed per
 * the definition of the RoleType.
 */
public class MultiplicityException
        extends
            MeshObjectGraphModificationException
        implements
            CanBeDumped
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param mb the MeshBase in which this Exception was created
     * @param originatingMeshBaseIdentifier the MeshBaseIdentifier of the MeshBase in which this Exception was created
     * @param meshObject the MeshObject where the multiplicity violation was discovered, if available
     * @param meshObjectIdentifier the MeshObjectIdentifier for the MeshObject where the multiplicity violation was discovered
     * @param roleType the RoleType whose multiplicity was violated, if available
     * @param roleTypeIdentifier the MeshTypeIdentifier for the RoleType whose multiplicity was violated
     * @param others the MeshObjectIdentifiers of the other MeshObjects participating in this Role
     */
    public MultiplicityException(
            MeshBase             mb,
            MeshBaseIdentifier   originatingMeshBaseIdentifier,
            MeshObject           meshObject,
            MeshObjectIdentifier meshObjectIdentifier,
            RoleType             roleType,
            MeshTypeIdentifier   roleTypeIdentifier,
            MeshObjectIdentifier [] others )
    {
        super( mb, originatingMeshBaseIdentifier );

        theMeshObject           = meshObject;
        theMeshObjectIdentifier = meshObjectIdentifier;
        theRoleType             = roleType;
        theRoleTypeIdentifier   = roleTypeIdentifier;
        
        theOthers = others;
    }

    /**
     * More convenient simple constructor for the most common case.
     *
     * @param meshObject the MeshObject where the multiplicity violation was discovered, if available
     * @param roleType the RoleType whose multiplicity was violated, if available
     * @param others the MeshObjectIdentifiers of the other MeshObjects participating in this Role
     */
    public MultiplicityException(
            MeshObject              meshObject,
            RoleType                roleType,
            MeshObjectIdentifier [] others )
    {
        this(   meshObject.getMeshBase(),
                meshObject.getMeshBase().getIdentifier(),
                meshObject,
                meshObject.getIdentifier(),
                roleType,
                roleType.getIdentifier(),
                others );
    }

    /**
     * Obtain the MeshObject where the multiplicity violation was discovered.
     *
     * @return the MeshObject
     * @throws MeshObjectAccessException thrown if the MeshObject could not be found
     * @throws NotPermittedException thrown if the caller is not authorized to perform this operation
     * @throws IllegalStateException thrown if no resolving MeshBase is available
     */
    public synchronized MeshObject getMeshObject()
        throws
            MeshObjectAccessException,
            NotPermittedException,
            IllegalStateException
    {
        if( theMeshObject == null ) {
            theMeshObject = resolve( theMeshObjectIdentifier );
        }
        return theMeshObject;
    }

    /**
      * Obtain the MeshObjectIdentifier of the MeshObject where the multiplicity violation was discovered.
      *
      * @return the MeshObjectIdentifier
      */
    public MeshObjectIdentifier getMeshObjectIdentifier()
    {
        return theMeshObjectIdentifier;
    }

    /**
     * Obtain the RoleType whose multiplicity was violated.
     *
     * @return the RoleType
     * @throws MeshTypeWithIdentifierNotFoundException thrown if the RoleType could not be found
     * @throws IllegalStateException thrown if no resolving MeshBase is available
     * @throws ClassCastException thrown if the type identifier identified a MeshType which is not a RoleType
     */
    public synchronized RoleType getRoleType()
        throws
            MeshTypeWithIdentifierNotFoundException,
            IllegalStateException
    {
        if( theRoleType == null ) {
            theRoleType = (RoleType) resolve( theRoleTypeIdentifier );
        }
        return theRoleType;
    }

    /**
      * Obtain the MeshTypeIdentifier of the RoleType whose multiplicity was violated.
      *
      * @return the MeshTypeIdentifier
      */
    public MeshTypeIdentifier getMeshTypeIdentifier()
    {
        return theRoleTypeIdentifier;
    }

    /**
     * Obtain the MeshObjectIdentifiers of the other MeshObjects participating in
     * this role.
     * 
     * @return the MeshObjectIdentifiers
     */
    public MeshObjectIdentifier [] getOthers()
    {
        return theOthers;
    }

    /**
     * Dump this object.
     *
     * @param d the Dumper to dump to
     */
    @Override
    public void dump(
            Dumper d )
    {
        d.dump( this,
                new String[] {
                    "theMeshObject",
                    "theMeshObjectIdentifier",
                    "theRoleType",
                    "theRoleTypeIdentifier",
                    "theOthers"
                },
                new Object[] {
                    theMeshObject,
                    theMeshObjectIdentifier,
                    theRoleType,
                    theRoleTypeIdentifier,
                    theOthers
                });
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    @Override
    public Object [] getLocalizationParameters()
    {
        return new Object[] {
            theMeshObjectIdentifier,
            theRoleTypeIdentifier,
            theRoleType != null ? theRoleType.getMultiplicity() : null,
            theOthers.length
        };
    }

    /**
     * The MeshObject for which we discovered a violation.
     */
    protected transient MeshObject theMeshObject;

    /**
     * The MeshObjectIdentifier of the MeshObject for which we discovered a violation.
     */
    protected MeshObjectIdentifier theMeshObjectIdentifier;

    /**
     * The RoleType for which we discovered a violation.
     */
    protected transient RoleType theRoleType;

    /**
     * The MeshTypeIdentifier for the RoleType for which we discovered a violation.
     */
    protected MeshTypeIdentifier theRoleTypeIdentifier;
    
    /**
     * The MeshObjectIdentifiers of the other MeshObjects participating in this role.
     */
    protected MeshObjectIdentifier [] theOthers;
}
