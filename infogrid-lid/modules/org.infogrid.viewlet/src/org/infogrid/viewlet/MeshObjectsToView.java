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

package org.infogrid.viewlet;

import org.infogrid.mesh.MeshObject;
import org.infogrid.util.StringHelper;

import java.util.Map;

/**
 * Instances of this class are being used to tell a Viewlet which MeshObjects it is supposed
 * to view, in which context, with which parameters etc.
 */
public class MeshObjectsToView
{
    /**
     * Factory method. Used when only the subject shall be specified
     * 
     * @param subject the subject for the Viewlet
     * @return the created MeshObjectsToView
     */
    public static MeshObjectsToView create(
            MeshObject subject )
    {
        return new MeshObjectsToView(
                subject,
                null,
                null,
                null );
    }

    /**
     * Factory method. Used when the subject shall be specified, and the name of Viewlet type to use.
     * 
     * @param subject the subject for the Viewlet
     * @param viewletTypeName the type of Viewlet to use
     * @return the created MeshObjectsToView
     */
    public static MeshObjectsToView create(
            MeshObject         subject,
            String             viewletTypeName )
    {
        return new MeshObjectsToView(
                subject,
                null,
                viewletTypeName,
                null );
    }


    /**
     * Factory method, specifying all parameters.
     * 
     * @param subject the subject for the Viewlet
     * @param subjectParameters the parameters for the Viewlet
     * @param viewletTypeName the type of Viewlet to use
     * @param viewletParameters the Viewlet parameters (eg size, zoom, ...) to use
     * @return the created MeshObjectsToView
     */
    public static MeshObjectsToView create(
            MeshObject         subject,
            Map<String,Object> subjectParameters,
            String             viewletTypeName,
            Map<String,Object> viewletParameters )
    {
        return new MeshObjectsToView(
                subject,
                subjectParameters,
                viewletTypeName,
                viewletParameters );
    }

    /**
     * Private constructor, use factory method.
     * 
     * @param subject the subject for the Viewlet
     * @param subjectParameters the parameters for the Viewlet
     * @param viewletTypeName the type of Viewlet to use
     * @param viewletParameters the Viewlet parameters (eg size, zoom, ...) to use
     */
    protected MeshObjectsToView(
            MeshObject         subject,
            Map<String,Object> subjectParameters,
            String             viewletTypeName,
            Map<String,Object> viewletParameters )
    {
        theSubject           = subject;
        theSubjectParameters = subjectParameters;
        theViewletTypeName   = viewletTypeName;
        theViewletParameters = viewletParameters;
    }

    /**
     * Obtain the subject that the Viewlet is supposed to view.
     *
     * @return the subject
     */
    public MeshObject getSubject()
    {
        return theSubject;
    }

    /**
     * Obtain the parameters for the subject.
     *
     * @return the parameters for the subject, if any
     */
    public Map<String,Object> getSubjectParameters()
    {
        return theSubjectParameters;
    }

    /**
     * Obtain the name representing the Viewlet type that the Viewlet is supposed to support.
     *
     * @return the name representing the Viewlet type that the Viewlet is supposed to support
     */
    public String getViewletTypeName()
    {
        return theViewletTypeName;
    }

    /**
     * Obtain the parameters that the Viewlet is supposed to use.
     *
     * @return the parameters that the Viewlet is supposed to use
     */
    public Map<String,Object> getViewletParameters()
    {
        return theViewletParameters;
    }

    /**
     * For debugging.
     *
     * @return debugging string
     */
    @Override
    public String toString()
    {
        return StringHelper.objectLogString(
                this,
                new String[] {
                    "subject",
                    "subjectPars",
                    "viewletTypeName",
                    "viewletPars"
                },
                new Object[] {
                    theSubject,
                    theSubjectParameters,
                    theViewletTypeName,
                    theViewletParameters
        });
    }

    /**
     * The subject to view.
     */
    protected MeshObject theSubject;

    /**
     * The parameters for the subject, if any.
     */
    protected Map<String,Object> theSubjectParameters;

    /**
     * The type of Viewlet we would like to view the subject.
     */
    protected String theViewletTypeName;

    /**
     * The parameters that we would like the Viewlet to use when viewing the selected objects.
     */
    protected Map<String,Object> theViewletParameters;
}
