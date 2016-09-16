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

package org.infogrid.meshbase;

/**
 * Listener to errors encountered by a MeshBase
 */
public interface MeshBaseErrorListener
{
    /**
     * The identifier of an EntityType was encountered when reading a MeshObject
     * from disk that could not be resolved.
     * 
     * @param event the MeshBaseError
     */
    public void unresolveableEntityType(
            MeshBaseError.UnresolvableEntityType event );

    /**
     * The identifier of an RoleType was encountered when reading a MeshObject
     * from disk that could not be resolved.
     * 
     * @param event the MeshBaseError
     */
    public void unresolveableRoleType(
            MeshBaseError.UnresolvableRoleType event );

    /**
     * The identifier of an PropertyType was encountered when reading a MeshObject
     * from disk that could not be resolved.
     * 
     * @param event the MeshBaseError
     */
    public void unresolveablePropertyType(
            MeshBaseError.UnresolvablePropertyType event );

    /**
     * A PropertyValue did not match the DataType of the Property to which it was
     * supposed to be assigned when reading a MeshObject from disk.
     * 
     * @param event the MeshBaseError
     */
    public void incompatibleDataType(
            MeshBaseError.IncompatibleDataType event );

    /**
     * A null value was found on disk for a Property that was not optional.
     * 
     * @param event the MeshBaseError
     */
    public void propertyNotOptional(
            MeshBaseError.PropertyNotOptional event );

    /**
     * Some other uncategorized error occurred.
     * 
     * @param event the MeshBaseError
     */
    public void otherError(
            MeshBaseError.OtherError event );
}
