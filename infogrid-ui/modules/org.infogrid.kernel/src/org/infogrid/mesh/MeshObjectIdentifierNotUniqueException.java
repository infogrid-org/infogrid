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

package org.infogrid.mesh;

import org.infogrid.util.AbstractLocalizedException;

/**
  * <p>This Exception is thrown if an attempt is made to create a new MeshObject with an
  *    Identifier that is used already for a different MeshObject. This typically indicates a
  *    programming error by the application programmer.</p>
  */
public class MeshObjectIdentifierNotUniqueException
    extends
        AbstractLocalizedException
{
    /**
      * Constructor.
      *
      * @param existing the MeshObject that exists already with the same Identifier
      */
    public MeshObjectIdentifierNotUniqueException(
            MeshObject existing )
    {
        super( existing != null
                 ? "MeshObject with Identifier " + existing.getIdentifier().toExternalForm() + " exists already, use different Identifier when creating a new MeshObject"
                 : "MeshObject exists already, use different Identifier when creating a new MeshObject" );

        theExisting     = existing;
        theExistingName = existing != null ? existing.getIdentifier() : null;
    }

    /**
     * Obtain the MeshObject that existed already with the same Identifier. This method
     * may return null if this Exception was serialized/deserialized.
     *
     * @return the MeshObject that existed already with the same Identifier, or null if not available.
     */
    public final MeshObject getExistingMeshObject()
    {
        return theExisting;
    }

    /**
     * Obtain the Identifier of the MeshObject that existed already with the same Identifier.
     * This value will always be available, even after serialization/deserialization.
     *
     * @return the Identifier of the MeshObject that existed already with the same Identifier.
     */
    public final MeshObjectIdentifier getExistingMeshObjectIdentifier()
    {
        return theExistingName;
    }

    /**
     * Obtain parameters for the internationalization.
     *
     * @return the parameters
     */
    public Object [] getLocalizationParameters()
    {
        return new Object[] { theExisting, theExistingName != null ? theExistingName.toExternalForm() : null };
    }

    /**
     * The already-existing MeshObject.
     */
    protected transient MeshObject theExisting;

    /**
     * The Identifier of the already-existing MeshObject.
     */
    protected MeshObjectIdentifier theExistingName;
}
