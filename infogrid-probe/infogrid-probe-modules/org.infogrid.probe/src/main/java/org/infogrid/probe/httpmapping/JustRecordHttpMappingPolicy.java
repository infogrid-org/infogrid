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

package org.infogrid.probe.httpmapping;

import java.io.IOException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.MeshObjectsNotFoundException;
import org.infogrid.probe.ProbeException;
import org.infogrid.util.http.HTTP;

/**
 * Just record the HTTP status code in the Home NetMeshObject.
 */
public class JustRecordHttpMappingPolicy
        implements
            HttpMappingPolicy
{
    /**
     * Given the home object that was attempted to be accessed by a Probe, and the HTTP response when
     * attempting to access it, perform what is necessary to correctly process the response.
     * This may be nothing -- in which case HTTP errors, for example, are silently ignored.
     * It may be throwing an exception. Subtypes may implement a large variety of behaviors.
     *
     * @param homeObject the home object of the Probe
     * @param response the HTTP response received
     * @return the HTTP response to use instead, if any
     * @throws MeshObjectsNotFoundException possible exception thrown
     * @throws NotPermittedException possible exception thrown
     * @throws IOException possible exception thrown
     */
    public HTTP.Response processHttpResponse(
            NetMeshObject homeObject,
            HTTP.Response response )
        throws
            MeshObjectsNotFoundException,
            NotPermittedException,
            IOException
    {
        return null;
    }

    /**
     * Given that the Probe or ProbeFramework threw this ProbeException, decide how to proceed.
     *
     * @param ex the thrown ProbeException
     * @param homeObject the home object of the Probe
     * @param haveYadis if true, the accessed URL has an associated Yadis resource
     * @throws ProbeException it may re-throw the ProbeException, for example
     */
    public void handleProbeException(
            ProbeException ex,
            NetMeshObject  homeObject,
            boolean        haveYadis )
        throws
            ProbeException
    {
        if( ex instanceof ProbeException.DontHaveProbe ) {
            return;
        } else {
            throw ex;
        }
    }

    /**
     * The singleton instance.
     */
    public static final JustRecordHttpMappingPolicy SINGLETON = new JustRecordHttpMappingPolicy();
}
