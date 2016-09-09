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

import javax.servlet.jsp.JspException;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.SaneRequest;
import org.infogrid.web.app.InfoGridWebApp;
import org.infogrid.web.security.CsrfMitigator;
import org.infogrid.web.taglib.AbstractInfoGridTag;
import org.infogrid.web.taglib.IgnoreException;

/**
 * Inserts the hidden input field with the token to make a safe form. This is only
 * needed for those HTML forms that are not generated with SafeFormTag.
 */
public class SafeFormHiddenInputTag
        extends
             AbstractInfoGridTag
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     */
    public SafeFormHiddenInputTag()
    {
        // noop
    }

    /**
     * Initialize.
     */
    @Override
    protected void initializeToDefaults()
    {
        super.initializeToDefaults();
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
        InfoGridWebApp app       = getInfoGridWebApp();
        CsrfMitigator  mitigator = app.getCsrfMitigator();
        
        if( mitigator != null ) {
            String toInsert = mitigator.getHtmlFormFragment( (SaneRequest) pageContext.getRequest() );
            if( toInsert != null ) {
                print( toInsert );
            }
        }
        return EVAL_PAGE;
    }

    /**
     * Our ResourceHelper, so field and cookie names are configurable.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( SafeFormHiddenInputTag.class );

    /**
     * Name of the cookie value as stored in the request attribute.
     */
    public static final String TOKEN_ATTRIBUTE_NAME
            = SafeFormHiddenInputTag.class.getName().replace( '.', '-' ) + "-value";

    /**
     * Name of the hidden field in the form.
     */
    public static final String INPUT_FIELD_NAME = theResourceHelper.getResourceStringOrDefault(
            "InputFieldName",
            SafeFormHiddenInputTag.class.getName().replace( '.', '-' ) + "-field" );
}
