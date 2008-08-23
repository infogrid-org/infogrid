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

package org.infogrid.lid.openid;

import org.infogrid.jee.templates.StructuredResponse;
import org.infogrid.lid.AbortProcessingException;
import org.infogrid.lid.AbstractLidService;
import org.infogrid.lid.LidService;
import org.infogrid.util.context.Context;
import org.infogrid.util.http.SaneRequest;

/**
 * Processes OpenID SSO requests on the IdP side.
 */
public class OpenIdSsoProcessor
        extends
            AbstractLidService
        implements
            LidService
{
    /**
     * Factory method.
     *
     * @param c the context in which this <code>ObjectInContext</code> runs.
     * @return the created OpenIdSsoProcessor
     */
    public static OpenIdSsoProcessor create(
            Context c )
    {
        OpenIdSsoProcessor ret = new OpenIdSsoProcessor( c );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param c the context in which this <code>ObjectInContext</code> runs.
     */
    protected OpenIdSsoProcessor(
            Context c )
    {
        super( LID_PROFILE_NAME, LID_PROFILE_VERSION, YADIS_FRAGMENT, c );
    }

    /**
     * Process any relevant requests.
     * 
     * @param lidRequest the incoming request
     * @param lidResponse the outgoing response
     * @throws AbortProcessingException thrown if processing is complete
     */
    public void processRequest(
            SaneRequest        lidRequest,
            StructuredResponse lidResponse )
        throws
            AbortProcessingException
    {
        throw new UnsupportedOperationException(); // FIXME
    }

    /**
     * Name of the LID profile.
     */
    public static final String LID_PROFILE_NAME = "http://openid.net/signon/";
    
    /**
     * Name of the LID version.
     */
    public static final String LID_PROFILE_VERSION = "1.0";
    
    /**
     * Yadis service fragment.
     */
    public static final String YADIS_FRAGMENT
            = "<xrd:Type>" + LID_PROFILE_NAME + LID_PROFILE_VERSION + "</xrd:Type>\n"
            + "<xrd:URI>{0}</xrd:URI>\n"
            + "<openid:Delegate xmlns:openid=\"http://openid.net/xmlns/1.0\">{0}</openid:Delegate>\n";
}
