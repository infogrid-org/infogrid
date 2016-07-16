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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.mesh.set.m;

import java.util.ArrayList;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.set.ImmutableMeshObjectSet;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.mesh.set.MeshObjectSetFactory;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.model.traversal.TraversalSpecification;
import org.infogrid.util.ArrayHelper;

/**
  * A simple implementation of an immutable MeshObjectSet that keeps its content in
  * memory.
  */
public class ImmutableMMeshObjectSet
        extends
            AbstractMMeshObjectSet
        implements
            ImmutableMeshObjectSet
{
    /**
     * Constructor to be used by subclasses only.
     *
     * @param factory the MeshObjectSetFactory that created this MeshObjectSet
     * @param content the content of the MeshObjectSet
     */
    protected ImmutableMMeshObjectSet(
            MeshObjectSetFactory factory,
            MeshObject []        content )
    {
        super( factory );

        setInitialContent( content );
    }

    /**
     * {@inheritDoc}
     */
    public MeshObjectSet traverse(
            RoleType role )
    {
        ArrayList<MeshObject> almostRet = new ArrayList<>( currentContent.length * 3 ); // fudge
        for( int i = 0 ; i < currentContent.length ; ++i ) {
            MeshObject [] found = currentContent[i].traverse( role ).getMeshObjects();

            for( int j=0 ; j<found.length ; ++j ) {
                if( ! almostRet.contains( found[j] )) {
                    almostRet.add( found[j] );
                }
            }
        }

        MeshObjectSet ret = theFactory.createImmutableMeshObjectSet( ArrayHelper.copyIntoNewArray( almostRet, MeshObject.class ));
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeshObjectSet traverse(
            TraversalSpecification theSpec )
    {
        if( theSpec instanceof RoleType ) {
            return traverse( (RoleType) theSpec );

        } else {
            return theSpec.traverse( this );
        }
    }
}
