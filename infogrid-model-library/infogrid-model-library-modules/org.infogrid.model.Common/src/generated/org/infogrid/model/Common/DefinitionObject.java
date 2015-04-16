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
// Copyright 1998-2013 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

//
// This file has been generated AUTOMATICALLY. DO NOT MODIFY.
// on Thu, 2015-04-16 22:40:56 +0000
//
// DO NOT MODIFY -- re-generate!

package org.infogrid.model.Common;

import org.infogrid.model.primitives.*;
import org.infogrid.mesh.*;
import org.infogrid.modelbase.ModelBaseSingleton;

/**
  * <p>Java interface for EntityType org.infogrid.model.Common/DefinitionObject.</p>
  * <p>To instantiate, use the factory methods provided by the <code>MeshBase</code>'s
  * <code>MeshObjectLifecycleManager</code>.</p>
  * <p><b>Note:</b> As an application programmer, you must never rely on any characteristic of any
  * class that implements this interface, only on the characteristics provided by this interface
  * and its supertypes.</p>
  *
  * <table>
  *  <tr><td>Identifier:</td><td><tt>org.infogrid.model.Common/DefinitionObject</tt></td></tr>
  *  <tr><td>Name:</td><td><tt>DefinitionObject</tt></td></tr>
  *  <tr><td>IsAbstract:</td><td><code>true</code></td></tr>
  *  <tr><td>UserVisibleName:</td><td><table><tr><td>default locale:</td><td>Object</td></tr></table></td></tr>
  *  <tr><td>UserVisibleDescription:</td><td><table><tr><td>default locale:</td><td>DefinitionObject serves as an abstract supertype for all definitions which may\nbe reused. A DefinitionObject may contain ComponentObjects, indicating that the definition is\nstructured. The ComponentObjects contained in a DefinitionObject form the components of\nthe structured definition. DefinitionObjects are referenced by ComponentObjects, indicating\nthe definition of the ComponentObject. In programming, for example, a class could be represented\nas a (subtype of) DefinitionObject. Its member variables would be (subtypes of) ComponentObject,\nwhich would be contained in the DefinitionObject. In turn, the contained ComponentObjects reference\nother DefinitionObjects (representing other classes, for example), to indicate their own structure.</td></tr></table></td></tr>
  * </table>
  */

public interface DefinitionObject
        extends
            org.infogrid.mesh.TypedMeshObjectFacade
{
    /**
      * Subject area in which this MeshObject is declared.
      */
    public static final SubjectArea _SUBJECTAREA = ModelBaseSingleton.findSubjectArea( "org.infogrid.model.Common" );

    /**
      * This MeshType.
      */
    public static final EntityType _TYPE = ModelBaseSingleton.findEntityType( "org.infogrid.model.Common/DefinitionObject" );

    /**
      * This MeshType.
      */
    public static final RoleType _DefinitionObject_Contains_ComponentObject_SOURCE = ModelBaseSingleton.findRoleType( "org.infogrid.model.Common/DefinitionObject_Contains_ComponentObject-S" );

    /**
      * This MeshType.
      */
    public static final RoleType _ComponentObject_References_DefinitionObject_DESTINATION = ModelBaseSingleton.findRoleType( "org.infogrid.model.Common/ComponentObject_References_DefinitionObject-D" );

}
