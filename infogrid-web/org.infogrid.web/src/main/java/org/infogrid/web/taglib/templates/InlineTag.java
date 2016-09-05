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

package org.infogrid.web.taglib.templates;

import javax.servlet.jsp.JspException;
import org.infogrid.web.taglib.AbstractInfoGridTag;
import org.infogrid.web.taglib.IgnoreException;
import org.infogrid.web.templates.StructuredResponse;
import org.infogrid.web.templates.StructuredResponseSection;

/**
 * Inlines a named section of a StructuredResponse into the output.
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class InlineTag
        extends
             AbstractInfoGridTag
{
    /**
     * Constructor.
     */
    public InlineTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        theSectionName = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the sectionName property.
     *
     * @return value of the sectionName property
     * @see #setSectionName
     */
    public final String getSectionName()
    {
        return theSectionName;
    }

    /**
     * Set value of the sectionName property.
     *
     * @param newValue new value of the sectionName property
     * @see #getSectionName
     */
    public final void setSectionName(
            String newValue )
    {
        theSectionName = newValue;
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        StructuredResponse structured = (StructuredResponse) pageContext.getResponse();

        StructuredResponseSection section = structured.getSection( theSectionName );

        if( section != null ) {
            String content = section.getTextContent();
            if( content != null && content.length() > 0 ) {
                print( content );
            }

        } else if( getFormatter().isFalse( getIgnore() )) {
            throw new JspException( "Cannot find StructuredResponseSection named " + theSectionName );
        }

        return SKIP_BODY;
    }

    /**
     * The name of the section in the StructuredResponse that will be inlined.
     */
    protected String theSectionName;
}
