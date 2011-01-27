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

package org.infogrid.probe.xrd;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.lid.model.xrd.XrdSubjectArea;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.RoleTypeBlessedAlreadyException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.module.ModuleException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.StagingMeshBaseLifecycleManager;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.http.HTTP;
import org.infogrid.util.logging.Log;

/**
 * A Probe for acct: objects according to the Webfinger protocol.
 * This Probe hard-codes (via Resource file) the host-meta information
 * to determine the XRD applicable to the acct: object. This makes the implementation
 * of this Probe substantially simpler and can be replaced with a more dynamic
 * version some time Webfinger is adopted more broadly.
 */
public class WebfingerAcctProbe
        implements
            ApiProbe
{
    private static final Log log = Log.getLogInstance( WebfingerAcctProbe.class ); // our own, private logger

    /**
     * {@inheritDoc }
     */
    public void readFromApi(
            NetMeshBaseIdentifier  dataSourceIdentifier,
            CoherenceSpecification coherenceSpecification,
            StagingMeshBase        freshMeshBase )
        throws
            EntityBlessedAlreadyException,
            EntityNotBlessedException,
            IllegalPropertyTypeException,
            IllegalPropertyValueException,
            IOException,
            IsAbstractException,
            MeshObjectIdentifierNotUniqueException,
            ModuleException,
            NotPermittedException,
            NotRelatedException,
            ProbeException,
            RelatedAlreadyException,
            RoleTypeBlessedAlreadyException,
            TransactionException,
            URISyntaxException,
            ParseException
    {
        if( HOST_META_URI_MAP == null ) {
            return; // be safe, we logged an error already
        }
        String fullyQualified = dataSourceIdentifier.toExternalForm();

        Matcher m = ACCT_PATTERN.matcher( fullyQualified );
        if( !m.matches() ) {
            throw new ProbeException.Other( dataSourceIdentifier, "Cannot run this Probe on a non-acct: data source: " + dataSourceIdentifier );
        }
        String user   = m.group( 1 );
        String domain = m.group( 2 );

        String uriTemplate = HOST_META_URI_MAP.get( domain );
        if( uriTemplate == null ) {
            // we know of nothing: do nothing
            return;
        }

        int    q = uriTemplate.indexOf( '?' );
        String xrdIdentifierString;
        
        // theoretically this pattern could be both before and after the ?
        if( q >= 0 ) {
            xrdIdentifierString
                    = uriTemplate.substring( 0, q+1 ).replace( VARIABLE_DOMAIN, domain ).replace( VARIABLE_ID, user )
                    + uriTemplate.substring( q+1 ).replace( VARIABLE_DOMAIN, HTTP.encodeToValidUrlArgument( domain )).replace( VARIABLE_ID, HTTP.encodeToValidUrlArgument( user ));
        } else {
            xrdIdentifierString
                    = uriTemplate.replace( VARIABLE_DOMAIN, domain ).replace( VARIABLE_ID, user );
        }

        StagingMeshBaseLifecycleManager life          = freshMeshBase.getMeshBaseLifecycleManager();
        NetMeshBaseIdentifier           xrdIdentifier = freshMeshBase.getMeshBaseIdentifierFactory().guessFromExternalForm( xrdIdentifierString );

        NetMeshObject xrd = life.createForwardReference( xrdIdentifier, XrdSubjectArea.XRD );

        xrd.relateAndBless( XrdSubjectArea.MESHOBJECT_DESCRIBES_MESHOBJECT.getSource(), freshMeshBase.getHomeObject() );
    }

    /**
     * Pattern to parse the user and domain from an acct: identifier.
     */
    private static final Pattern ACCT_PATTERN = Pattern.compile( "acct:([^@]+)@([^@]+)" );

    /**
     * The variable name in the URI template pattern for the domain portion of the URI.
     */
    public static final String VARIABLE_DOMAIN = "{domain}";

    /**
     * The variable name in the URI template pattern for the user portion of the URI.
     */
    public static final String VARIABLE_ID = "{id}";

    /**
     * Maps domains to URI template URLs as in host-meta XRDs.
     */
    protected static final Map<String,String> HOST_META_URI_MAP
            = ResourceHelper.getInstance( WebfingerAcctProbe.class ).getResourceStringMapOrNull( "HostMetaUriTemplates" );
    static {
        if( HOST_META_URI_MAP == null ) {
            log.error( "No HostMetaUriTemplates found" );
        }
    }
}
