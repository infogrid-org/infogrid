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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.jee.taglib.util;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.infogrid.jee.taglib.AbstractInfoGridTag;
import org.infogrid.jee.taglib.IgnoreException;

/**
 * Declares that a JSP fragment accepts a named parameter.
 *
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class JspfParamTag
    extends
        AbstractInfoGridTag

{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public JspfParamTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theName = null;
        theType = null;

        super.initializeToDefaults();
    }

    /**
     * Obtain value of the name property.
     *
     * @return value of the name property
     * @see #setName
     */
    public String getName()
    {
        return theName;
    }

    /**
     * Set value of the name property.
     *
     * @param newValue new value of the name property
     * @see #getName
     */
    public void setName(
            String newValue )
    {
        theName = newValue;
    }

    /**
     * Obtain value of the type property.
     *
     * @return value of the type property
     * @see #setType
     */
    public String getType()
    {
        return theType;
    }

    /**
     * Set value of the type property.
     *
     * @param newValue new value of the type property
     * @see #getType
     */
    public void setType(
            String newValue )
    {
        theType = newValue;
    }

    /**
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     * @throws IOException thrown if an I/O Exception occurred
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        Tag parentTag = getParent();
        if( !( parentTag instanceof JspfTag )) {
            throw new JspException( "JspfParamTag must be directly contained in a JspfTag" );
        }
        CallJspfRecord record = (CallJspfRecord) pageContext.getRequest().getAttribute( CallJspfRecord.CALL_JSPF_RECORD_ATTRIBUTE_NAME );
        if( record == null ) {
            throw new JspException( "JspfParamTag cannot find JspfRecord for this call" );
        }
        if( theName == null || theName.length() == 0 ) {
            throw new JspException( "Attribute name of JspfParamTag must not be empty" );
        }
        record.processParameterValue( pageContext.getRequest(), theName, theType ); // may throw

        return SKIP_BODY;
    }

    /**
     * Name of the parameter.
     */
    protected String theName;

    /**
     * Type of the parameter, as the name of a class.
     */
    protected String theType;
}
