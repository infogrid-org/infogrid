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

package org.infogrid.jee.taglib.candy;

import org.infogrid.jee.taglib.AbstractInfoGridBodyTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;

/**
 * Tag that collects the content of a single content row within
 * the <code>TabbedCursorIteratorTag</code> and thus distinguishes it from header
 * and other rows.
 */
public class TabbedCursorIteratorContentRowTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public TabbedCursorIteratorContentRowTag()
    {
        // noop
    }
    
    /**
     * Do the start tag operation.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    protected int realDoStartTag()
        throws
            JspException
    {
        Tag parentTag = getParent();
        if( parentTag == null || !( parentTag instanceof TabbedCursorIteratorTag )) {
            throw new JspException( "TabbedCursorIteratorContentRowTag tag must be directly contained in an TabbedCursorIteratorTag tag" );
        }

        TabbedCursorIteratorTag realParentTag = (TabbedCursorIteratorTag) parentTag;

        if( realParentTag.processesEmptySet() ) {
            return SKIP_BODY;
        }
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Do the end tag operation.
     *
     * @return evaluate or skip page
     * @throws JspException thrown if an evaluation error occurred
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    @Override
    protected int realDoEndTag()
        throws
            JspException
    {
        TabbedCursorIteratorTag realParentTag = (TabbedCursorIteratorTag) getParent();

        if( realParentTag.processesEmptySet() ) {
            return EVAL_PAGE;
        }
        return EVAL_PAGE;
    }
}

