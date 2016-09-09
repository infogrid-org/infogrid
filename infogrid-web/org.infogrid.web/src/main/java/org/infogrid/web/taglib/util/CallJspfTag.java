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

package org.infogrid.web.taglib.util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.taglib.AbstractInfoGridTag;
import org.infogrid.web.taglib.IgnoreException;

/**
 * <p>Allows the inclusion of JSP fragments as subroutines with parameters.</p>
 * @see <a href="package-summary.html">Details in package documentation</a>
 */
public class CallJspfTag
    extends
        AbstractInfoGridTag

{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public CallJspfTag()
    {
        // noop
    }

    /**
     * Release all of our resources.
     */
    @Override
    protected void initializeToDefaults()
    {
        theName          = null;
        theOldCallRecord = null;

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
     * Our implementation of doStartTag().
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     * @throws IOException thrown if an I/O Exception occurred
     */
    @Override
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        ServletRequest request    = pageContext.getRequest();
        theOldCallRecord          = (CallJspXRecord) request.getAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME );
        CallJspXRecord callRecord = new CallJspXRecord( theName );
        request.setAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME, callRecord );

        return EVAL_BODY_INCLUDE; // contains parameter declarations
    }

    /**
     * Our implementation of doEndTag(), to be provided by subclasses.
     *
     * @return evaluate or skip body
     * @throws JspException thrown if an evaluation error occurred
     * @throws IgnoreException thrown to abort processing without an error
     * @throws IOException thrown if an I/O Exception occurred
     */
    @Override
    protected int realDoEndTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        InfoGridWebApp app = getInfoGridWebApp();
        
        ServletRequest request = pageContext.getRequest();

        try {
            app.getResourceManager().processJspf( theName, pageContext );

            return EVAL_PAGE;

        } catch( ServletException ex ) {
            throw new JspException( ex ); // why in the world are these two differnt types of exceptions?

        } finally {
            request.setAttribute( CallJspXRecord.CALL_JSPX_RECORD_ATTRIBUTE_NAME, theOldCallRecord );
        }
    }

    /**
     * Name
     */
    protected String theName;

    /**
     * The CallJspXRecord to restore.
     */
    CallJspXRecord theOldCallRecord;
}
