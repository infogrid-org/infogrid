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
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.model.traversal.TraversalSpecification;

import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Tag that iterates over the set of <code>MeshObjects</code> found by traversing a
 * <code>TraversalSpecification</code> from a given start <code>MeshObject</code>.
 */
public class MeshObjectRelatedIterateTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public MeshObjectRelatedIterateTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theMeshObjectName             = null;
        theTraversalSpecificationName = null;
        theRelatedLoopVar            = null;
        theIterator                   = null;

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
     * Obtain value of the traversalSpecificationName property.
     *
     * @return value of the traversalSpecificationName property
     * @see #setTraversalSpecificationName
     */
    public final String getTraversalSpecificationName()
    {
        return theTraversalSpecificationName;
    }

    /**
     * Set value of the traversalSpecificationName property.
     *
     * @param newValue new value of the traversalSpecificationName property
     * @see #getTraversalSpecificationName
     */
    public final void setTraversalSpecificationName(
            String newValue )
    {
        theTraversalSpecificationName = newValue;
    }

    /**
     * Obtain value of the relatedLoopVar property.
     *
     * @return value of the relatedLoopVar property
     * @see #setRelatedLoopVar
     */
    public final String getRelatedLoopVar()
    {
        return theRelatedLoopVar;
    }

    /**
     * Set value of the relatedLoopVar property.
     *
     * @param newValue new value of the relatedLoopVar property
     * @see #getRelatedLoopVar
     */
    public final void setRelatedLoopVar(
            String newValue )
    {
        theRelatedLoopVar = newValue;
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
        MeshObject             obj       = (MeshObject) lookupOrThrow( theMeshObjectName );
        TraversalSpecification traversal = (TraversalSpecification) lookupOrThrow( theTraversalSpecificationName );
        
        MeshObjectSet found = obj.traverse( traversal );
        theIterator = found.iterator();

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
            MeshObject current = theIterator.next();

            if( theRelatedLoopVar != null ) {
                pageContext.setAttribute( theRelatedLoopVar, current );
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
        if( theRelatedLoopVar != null ) {
            pageContext.removeAttribute( theRelatedLoopVar );
        }
        return EVAL_PAGE;
    }

    /**
     * Name of the bean that contains the MeshObject to start the traversal from.
     */
    protected String theMeshObjectName;
    
    /**
     * Name of the bean that contains the TraversalSpecification.
     */
    protected String theTraversalSpecificationName;

    /**
     * String containing the name of the loop variable that contains the current neighbor.
     */
    protected String theRelatedLoopVar;

    /**
     * Iterator over the MeshObjects.
     */
    protected Iterator<MeshObject> theIterator;
}
