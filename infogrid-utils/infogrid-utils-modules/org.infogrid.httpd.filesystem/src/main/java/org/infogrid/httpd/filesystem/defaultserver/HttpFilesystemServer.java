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

package org.infogrid.httpd.filesystem.defaultserver;

import org.infogrid.httpd.DefaultHttpErrorHandler;
import org.infogrid.httpd.HttpResponseFactory;
import org.infogrid.httpd.filesystem.FilesystemHttpResponseFactory;
import org.infogrid.httpd.server.HttpServer;

import java.io.File;
import java.io.IOException;

/**
  * This is a very simple HTTPD server serving files from the file system.
  */
public class HttpFilesystemServer
    extends
        HttpServer
{
    /**
     * Constructor.
     *
     * @param documentRoot the document root directory
     * @throws IOException thrown if the documentRoot does not exist or isn't a directory
     */
    public HttpFilesystemServer(
            File documentRoot )
        throws
            IOException
    {
        this( documentRoot, DEFAULT_ACCEPT_PORT );
    }

    /**
     * Constructor.
     *
     * @param documentRoot the document root directory
     * @param acceptPort the port at which we accept incoming requests
     * @throws IOException thrown if the documentRoot does not exist or isn't a directory
     */
    public HttpFilesystemServer(
            File documentRoot,
            int  acceptPort )
        throws
            IOException
    {
        super( acceptPort );

        if( !documentRoot.exists() ) {
            throw new IOException( "Document root does not exist" );
        }
        if( !documentRoot.isDirectory() ) {
            throw new IOException( "Document root is not a directory" );
        }

        theDocumentRoot = documentRoot;

        HttpResponseFactory factory = new FilesystemHttpResponseFactory(
                documentRoot,
                new DefaultHttpErrorHandler(),
                null ); // no Default AccessManager -- everything is open

        theAcceptor.setResponseFactory( factory );
    }

    /**
     * Document root of this HttpFilesystemServer.
     */
    protected File theDocumentRoot;
}
