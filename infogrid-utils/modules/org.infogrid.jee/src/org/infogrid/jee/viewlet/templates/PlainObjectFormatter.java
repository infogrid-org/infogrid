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

package org.infogrid.jee.viewlet.templates;

import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;

import org.infogrid.model.primitives.MeshType;
import org.infogrid.model.primitives.ModelPrimitivesStringRepresentation;
import org.infogrid.model.primitives.PropertyValue;

import org.infogrid.util.LocalizedObjectFormatter;
import org.infogrid.util.ResourceHelper;

/**
 * A default LocalizedObjectFormatter for InfoGrid Web Apps.
 */
public class PlainObjectFormatter
        implements
            LocalizedObjectFormatter
{
    /**
     * Constructor.
     */
    public PlainObjectFormatter()
    {
        // no op
    }
    
    /**
     * Convert an Object to a String representation.
     *
     * @param o the Object
     * @return String representation of the Object
     */
    public String asLocalizedString(
            Object o )
    {
        if( o == null ) {
            return theResourceHelper.getResourceStringOrDefault( "NullString", "null" );

        } else if( o instanceof MeshObject || o instanceof MeshObjectIdentifier ) {
            MeshObjectIdentifier ref = ( o instanceof MeshObjectIdentifier ) ? ((MeshObjectIdentifier)o) : ((MeshObject)o).getIdentifier();

            String ret = ref.toStringRepresentation( ModelPrimitivesStringRepresentation.TEXT_PLAIN );
            return ret;

        } else if( o instanceof MeshType ) {
            MeshType realO = (MeshType) o;

            String ret = PropertyValue.toStringRepresentation( realO.getUserVisibleName(), ModelPrimitivesStringRepresentation.TEXT_PLAIN );
            return ret;
            
        } else if( o instanceof Class ) {
            Class realO = (Class) o;

            String ret = realO.getName();
            return ret;
           
        } else {
            return o.toString();
        }
    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( PlainObjectFormatter.class  );
}