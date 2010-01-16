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
 * Thrown if LID processing is aborted because the response content is known already.
 */
public class LidAbortProcessingPipelineWithContentException
    extends
        LidAbortProcessingPipelineException
{
    private static final long serialVersionUID = 1L; // helps with serialization

    /**
     * Constructor.
     *
     * @param content the content to be returned
     * @param contentType the content type to be returned
     * @param source the LidProcessingPipelineStage that threw this exception
     */
    public LidAbortProcessingPipelineWithContentException(
            String                     content,
            String                     contentType,
            LidProcessingPipelineStage source )
    {
        super( source, null, null, null );

        theContent     = content;
        theContentType = contentType;
    }

    /**
     * Obtain the content to be returned.
     *
     * @return the content
     */
    public String getContent()
    {
        return theContent;
    }

    /**
     * Obtain the content type to be returned.
     *
     * @return the content type
     */
    public String getContentType()
    {
        return theContentType;
    }

    /**
     * The content to be returned.
     */
    protected String theContent;

    /**
     * The content type to be returned.
     */
    protected String theContentType;
}
