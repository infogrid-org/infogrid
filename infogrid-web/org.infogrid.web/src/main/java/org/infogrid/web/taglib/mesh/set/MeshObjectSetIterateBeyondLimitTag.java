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

package org.infogrid.web.taglib.mesh.set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.infogrid.web.taglib.AbstractInfoGridBodyTag;

/**
 * Tag that is invoked once in an iteration that is limited and has more elements,
 * to indicate "there is more".
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class MeshObjectSetIterateBeyondLimitTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public MeshObjectSetIterateBeyondLimitTag()
    {
        // noop
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException
    {
        Tag parentTag = getParent();
        if( parentTag == null || !( parentTag instanceof AbstractMeshObjectSetIterateTag )) {
            throw new JspException( "BeyondLimit tag must be directly contained in an AbstractMeshObjectSetIterateTag tag" );
        }

        AbstractMeshObjectSetIterateTag realParentTag = (AbstractMeshObjectSetIterateTag) parentTag;
        if( realParentTag.displayBeyondLimit() ) {
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }
}