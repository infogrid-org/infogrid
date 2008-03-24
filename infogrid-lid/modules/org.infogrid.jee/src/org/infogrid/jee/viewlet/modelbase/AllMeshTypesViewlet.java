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

package org.infogrid.jee.viewlet.modelbase;

import org.infogrid.context.Context;
import org.infogrid.jee.app.InfoGridWebApp;
import org.infogrid.jee.viewlet.AbstractJeeViewlet;
import org.infogrid.model.primitives.SubjectArea;
import org.infogrid.modelbase.ModelBase;

import java.util.Iterator;

/**
 * Displays all MeshTypes in the ModelBase.
 */
public class AllMeshTypesViewlet
        extends
            AbstractJeeViewlet
{
   /**
     * Factory method.
     *
     * @param c the application context
     * @return the created AllMeshObjectsViewlet
     */
    public static AllMeshTypesViewlet create(
            Context c )
    {
        return new AllMeshTypesViewlet( c );
    }

    /**
     * Constructor.
     *
     * @param c the application context
     */
    protected AllMeshTypesViewlet(
            Context c )
    {
        super( c );
    }

    /**
     * Obtain the SubjectAreas to display.
     *
     * @return Iterator over the SubjectAreas
     */
    public Iterator<SubjectArea> getSubjectAreas()
    {
        InfoGridWebApp app = InfoGridWebApp.getSingleton();
        ModelBase      mb  = app.getModelBase();
        
        return mb.subjectAreaIterator();
    }
}
