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

package org.infogrid.meshbase;

import org.infogrid.mesh.externalized.ExternalizedMeshObject;
import org.infogrid.mesh.externalized.ParserFriendlyExternalizedMeshObjectFactory;

import org.infogrid.meshbase.transaction.TransactionException;

import org.infogrid.modelbase.MeshTypeIdentifierFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Defines a type to load bulk information into a MeshBase.
 */
public interface BulkLoader
{
    /**
     * Bulk-load data into this MeshBase.
     *
     * @param inStream the Stream from which to read the data
     * @param bestEffort if true, the bulk tries to work around errors to the maximum extent possible
     * @return the iterator over the ExternalizedMeshObjects
     */
    public Iterator<? extends ExternalizedMeshObject> bulkLoad(
            InputStream                                 inStream,
            ParserFriendlyExternalizedMeshObjectFactory externalizedMeshObjectFactory,
            MeshObjectIdentifierFactory                 meshObjectIdentifierFactory,
            MeshTypeIdentifierFactory                   meshTypeIdentifierFactory )
        throws
            IOException,
            BulkLoadException,
            TransactionException;
}
