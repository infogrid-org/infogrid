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
import org.infogrid.model.primitives.EntityType;

import org.infogrid.util.ArrayCursorIterator;

import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Tag that iterates over the <code>MeshTypes</code> with which a <code>MeshObject</code> is blessed.
 */
public class MeshObjectBlessedByIterateTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public MeshObjectBlessedByIterateTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theMeshObjectName   = null;
        theBlessedByLoopVar = null;
        theIterator         = null;

        super.initializeToDefaults();
    }
    
    /**
     * Obtain value of the meshObjectName property.
     *
     * @return value of the meshObjectName property
     * @see #setMeshObjectName
     */
    public final String getMeshObjectName()
    {
        return theMeshObjectName;
    }

    /**
     * Set value of the meshObjectName property.
     *
     * @param newValue new value of the meshObjectName property
     * @see #getMeshObjectName
     */
    public final void setMeshObjectName(
            String newValue )
    {
        theMeshObjectName = newValue;
    }

    /**
     * Obtain value of the blessedByLoopVar property.
     *
     * @return value of the blessedByLoopVar property
     * @see #setBlessedByLoopVar
     */
    public final String getBlessedByLoopVar()
    {
        return theBlessedByLoopVar;
    }

    /**
     * Set value of the blessedByLoopVar property.
     *
     * @param newValue new value of the blessedByLoopVar property
     * @see #getBlessedByLoopVar
     */
    public final void setBlessedByLoopVar(
            String newValue )
    {
        theBlessedByLoopVar = newValue;
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
        MeshObject obj = (MeshObject) lookupOrThrow( theMeshObjectName );

        theIterator = new ArrayCursorIterator<EntityType>( obj.getTypes() );

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
            EntityType current = theIterator.next();

            if( theBlessedByLoopVar != null ) {
                pageContext.setAttribute( theBlessedByLoopVar, current );
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
        if( theBlessedByLoopVar != null ) {
            pageContext.removeAttribute( theBlessedByLoopVar );
        }
        return EVAL_PAGE;
    }

    /**
     * Name of the bean that contains the MeshObject to render.
     */
    protected String theMeshObjectName;
    
    /**
     * String containing the name of the loop variable that contains the current EntityType.
     */
    protected String theBlessedByLoopVar;

    /**
     * Iterator over the EntityTypes.
     */
    protected Iterator<EntityType> theIterator;
}
