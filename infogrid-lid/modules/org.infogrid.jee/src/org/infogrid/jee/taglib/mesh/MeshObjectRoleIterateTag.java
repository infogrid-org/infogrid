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

package org.infogrid.jee.taglib.mesh;

import org.infogrid.jee.taglib.AbstractInfoGridBodyTag;
import org.infogrid.jee.taglib.InfoGridJspUtils;
import org.infogrid.jee.taglib.IgnoreException;

import org.infogrid.mesh.MeshObject;
import org.infogrid.model.primitives.RoleType;

import org.infogrid.util.ArrayCursorIterator;

import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Tag that iterates over the <code>RoleTypes</code> in which a start <code>MeshObject</code>
 * participates with a destination <code>MeshObject</code>.
 */
public class MeshObjectRoleIterateTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public MeshObjectRoleIterateTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theStartMeshObjectName       = null;
        theDestinationMeshObjectName = null;
        theRoleTypeLoopVar           = null;
        theIterator                  = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the startMeshObjectName property.
     *
     * @return value of the startMeshObjectName property
     * @see #setStartMeshObjectName
     */
    public final String getStartMeshObjectName()
    {
        return theStartMeshObjectName;
    }

    /**
     * Set value of the meshObjectName property.
     *
     * @param newValue new value of the meshObjectName property
     * @see #getStartMeshObjectName
     */
    public final void setStartMeshObjectName(
            String newValue )
    {
        theStartMeshObjectName = newValue;
    }

    /**
     * Obtain value of the destinationMeshObjectName property.
     *
     * @return value of the destinationMeshObjectName property
     * @see #setDestinationMeshObjectName
     */
    public final String getDestinationMeshObjectName()
    {
        return theDestinationMeshObjectName;
    }

    /**
     * Set value of the destinationMeshObjectName property.
     *
     * @param newValue new value of the destinationMeshObjectName property
     * @see #getDestinationMeshObjectName
     */
    public final void setDestinationMeshObjectName(
            String newValue )
    {
        theDestinationMeshObjectName = newValue;
    }

    /**
     * Obtain value of the roleTypeLoopVar property.
     *
     * @return value of the roleTypeLoopVar property
     * @see #setRoleTypeLoopVar
     */
    public final String getRoleTypeLoopVar()
    {
        return theRoleTypeLoopVar;
    }

    /**
     * Set value of the roleTypeLoopVar property.
     *
     * @param newValue new value of the roleTypeLoopVar property
     * @see #getRoleTypeLoopVar
     */
    public final void setRoleTypeLoopVar(
            String newValue )
    {
        theRoleTypeLoopVar = newValue;
    }

    /**
     * Process the start tag.
     *
     * @return evaluate or skip body
     * @throws JspException if a JSP exception has occurred
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        MeshObject start       = (MeshObject) lookupOrThrow( theStartMeshObjectName );
        MeshObject destination = (MeshObject) lookupOrThrow( theDestinationMeshObjectName );

        RoleType [] types = start.getRoleTypes( destination );
        theIterator = new ArrayCursorIterator<RoleType>( types );

        int ret = iterateOnce();
        return ret;
    }
    

    /**
     * Invoked after the Body tag has been invoked.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an error occurred
     */
    @Override
    protected int realDoAfterBody()
        throws
            JspException
    {
        if( super.bodyContent != null ) {

            InfoGridJspUtils.printPrevious( pageContext, InfoGridJspUtils.isTrue( getFilter()), bodyContent.getString() );
            bodyContent.clearBody();
        }

        int ret = iterateOnce();
        return ret;
    }

    /**
     * Factors out common code for doStartTag and doAfterBody.
     */
    protected int iterateOnce()
    {
        if( theIterator.hasNext() ) {
            RoleType current = theIterator.next();

            if( theRoleTypeLoopVar != null ) {
                pageContext.setAttribute( theRoleTypeLoopVar, current );
            }

            return EVAL_BODY_AGAIN;

        } else {
            return SKIP_BODY;
        }
    }

   /**
     * Process the end tag.
     *
     * @return evaluate or skip page
     * @throws JspException thrown if an error occurred
     */
    @Override
    protected int realDoEndTag()
    {
        if( theRoleTypeLoopVar != null ) {
            pageContext.removeAttribute( theRoleTypeLoopVar );
        }
        return EVAL_PAGE;
    }

    /**
     * Name of the bean that contains the start MeshObject.
     */
    protected String theStartMeshObjectName;
    
    /**
     * Name of the bean that contains the destination MeshObject.
     */
    protected String theDestinationMeshObjectName;
    
    /**
     * String containing the name of the loop variable that contains the current RoleType.
     */
    protected String theRoleTypeLoopVar;

    /**
     * Iterator over the RoleTypes.
     */
    protected Iterator<RoleType> theIterator;
}
        
