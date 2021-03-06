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

package org.infogrid.model.primitives;

/**
  * CollectableMeshType is the abstract supertype of all MeshTypes that are
  * directly collected by SubjectAreas.
  */
public interface CollectableMeshType
        extends
            MeshType
{
    /**
      * Obtain the SubjectArea in which this CollectableMeshType is defined.
      *
      * @return the SubjectArea in which this CollectableMeshType is defined
      */
    public SubjectArea getSubjectArea();
}
