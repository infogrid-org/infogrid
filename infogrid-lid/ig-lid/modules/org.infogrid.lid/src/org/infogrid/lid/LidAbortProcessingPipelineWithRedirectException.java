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
// Copyright 1998-2010 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid;

/**
 * Thrown if LID processing is aborted because the response is already known to be an
 * HTTP redirect.
 */
public class LidAbortProcessingPipelineWithRedirectException
    extends
        LidAbortProcessingPipelineException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param location the URL to redirect to
     * @param status the HTTP status to be returned
     * @param source the LidProcessingPipelineStage that threw this exception
     */
    public LidAbortProcessingPipelineWithRedirectException(
            String                     location,
            int                        status,
            LidProcessingPipelineStage source )
    {
        super( source, null, null, null );

        theLocation = location;
        theStatus   = status;
    }

    /**
     * Obtain the location URL.
     *
     * @return the location URL
     */
    public String getLocation()
    {
        return theLocation;
    }

    /**
     * Obtain the status to be returned.
     *
     * @return the status
     */
    public int getStatus()
    {
        return theStatus;
    }

    /**
     * The redirect location.
     */
    protected String theLocation;

    /**
     * The HTTP status to be returned.
     */
    protected int theStatus;
}
