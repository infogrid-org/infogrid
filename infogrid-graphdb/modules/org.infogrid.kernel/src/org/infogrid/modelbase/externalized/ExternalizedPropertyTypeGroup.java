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

package org.infogrid.modelbase.externalized;

import org.infogrid.model.primitives.FloatValue;

import java.util.ArrayList;

/**
 * This is data wanting to become a PropertyTypeGroup, during reading.
 */
public class ExternalizedPropertyTypeGroup
    extends
        ExternalizedMeshType
{
    /**
     * Add to property.
     *
     * @param newValue the new value
     */
    public void addGroupMember(
            String newValue ) 
    {
        if( groupMembers == null ) {
            groupMembers = new ArrayList<String>();
        }
        groupMembers.add( newValue );
    }
    
    /**
     * Get property.
     *
     * @return the value
     */
    public ArrayList<String> getGroupMembers()
    {
        return groupMembers;
    }

    /**
     * Set property.
     *
     * @param newValue the new value
     */
    public void setSequenceNumber(
            FloatValue newValue ) 
    {
        sequenceNumber = newValue;
    }
    
    /**
     * Get property.
     *
     * @return the value
     */
    public FloatValue getSequenceNumber()
    {
        return sequenceNumber;
    }

    /**
     * The list of Identifiers of PropertyTypes that this PropertyTypeGroup contains.
     */
    protected ArrayList<String> groupMembers = null;

    /**
     * SequenceNumber.
     */
    protected FloatValue sequenceNumber = FloatValue.ZERO;
}
