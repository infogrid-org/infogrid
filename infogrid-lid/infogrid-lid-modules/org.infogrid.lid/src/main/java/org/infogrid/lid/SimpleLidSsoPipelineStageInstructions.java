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

package org.infogrid.lid;

/**
 * Extends SimpleLidPipelineStageInstructions for SSO.
 */
public class SimpleLidSsoPipelineStageInstructions
        extends
            SimpleLidPipelineStageInstructions
        implements
            LidSsoPipelineStageInstructions
{
    /**
     * Factory method for a status code only.
     *
     * @param status the HTTP status to be returned
     * @return the created instructions
     */
    public static SimpleLidSsoPipelineStageInstructions create(
            int status )
    {
        return new SimpleLidSsoPipelineStageInstructions( null, null, status, null, null, null, true, false );
    }

    /**
     * Factory method for a simple redirect.
     *
     * @param location the URL to redirect to
     * @param status the HTTP status to be returned
     * @return the created instructions
     */
    public static SimpleLidSsoPipelineStageInstructions create(
            String location,
            int    status )
    {
        return new SimpleLidSsoPipelineStageInstructions( null, null, status, location, null, null, true, false );
    }

    /**
     * Factory method for content to be sent.
     *
     * @param content the content to return
     * @param contentType the MIME type of the content to return
     * @return the created instructions
     */
    public static SimpleLidSsoPipelineStageInstructions createContentOnly(
            String content,
            String contentType )
    {
        return new SimpleLidSsoPipelineStageInstructions( content, contentType, null, null, null, null, true, false );
    }

    /**
     * Factory method that only indicates authentication is needed.
     *
     * @return the created instructions
     */
    public static SimpleLidSsoPipelineStageInstructions createNeedAuth()
    {
        return new SimpleLidSsoPipelineStageInstructions( null, null, null, null, null, null, false, true );
    }

    /**
     * Constructor.
     *
     * @param content the content to return
     * @param contentType the MIME type of the content to return
     * @param location the URL to redirect to
     * @param status the HTTP status to be returned
     * @param headerNames names of additional HTTP headers to be sent
     * @param headerValues values of additional HTTP headers to be sent
     * @param applyAsRecommendedReturnValue return value from the applyAsRecommended method.
     * @param needsAuth if true, the SSO request needs authentication before it can proceed
     */
    protected SimpleLidSsoPipelineStageInstructions(
            String    content,
            String    contentType,
            Integer   status,
            String    location,
            String [] headerNames,
            String [] headerValues,
            boolean   applyAsRecommendedReturnValue,
            boolean   needsAuth )
    {
        super( content, contentType, status, location, headerNames, headerValues, applyAsRecommendedReturnValue );

        theNeedsAuth = needsAuth;
    }

    /**
     * If true, the SSO request needs authentication before it can proceed.
     *
     * @return if true, the SSO request needs authentication before it can proceed.
     */
    public boolean getNeedsAuthentication()
    {
        return theNeedsAuth;
    }

    /**
     * If true, the SSO request needs authentication before it can proceed.
     */
    protected boolean theNeedsAuth;
}
