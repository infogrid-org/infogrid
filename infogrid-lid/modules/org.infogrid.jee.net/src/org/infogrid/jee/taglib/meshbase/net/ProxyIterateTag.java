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

package org.infogrid.jee.taglib.meshbase.net;

import org.infogrid.jee.taglib.AbstractInfoGridBodyTag;
import org.infogrid.jee.taglib.IgnoreException;
import org.infogrid.jee.taglib.net.InfoGridNetJspUtils;

import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.Proxy;

import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.Iterator;

/**
 * <p>Iterates over the Proxies of a NetMeshObject.</p>
 */
public class ProxyIterateTag
    extends
        AbstractInfoGridBodyTag
{
    /**
     * Constructor.
     */
    public ProxyIterateTag()
    {
        // noop
    }

    /**
     * Initialize all default values. To be invoked by subclasses.
     */
    @Override
    protected void initializeToDefaults()
    {
        theMeshObjectName = null;
        theMeshBaseName   = null;
        theLoopVar        = null;

        theProxyIterator  = null;
        
        super.initializeToDefaults();
    }
    
    /**
     * Obtain value of the meshObjectName property.
     *
     * @return value of the meshObjectName property
     * @see #setMeshObjectName
     */
    public final String getMeshObjectName()
    {
        return theMeshObjectName;
    }

    /**
     * Set value of the meshObjectName property.
     *
     * @param newValue new value of the meshObjectName property
     * @see #getMeshObjectName
     */
    public final void setMeshObjectName(
            String newValue )
    {
        theMeshObjectName = newValue;
    }

    /**
     * Obtain value of the meshBaseName property.
     *
     * @return value of the meshBaseName property
     * @see #setMeshBaseName
     */
    public final String getMeshBaseName()
    {
        return theMeshBaseName;
    }

    /**
     * Set value of the meshBaseName property.
     *
     * @param newValue new value of the meshBaseName property
     * @see #getMeshBaseName
     */
    public final void setMeshBaseName(
            String newValue )
    {
        theMeshBaseName = newValue;
    }

    /**
     * Obtain value of the loopVar property.
     *
     * @return value of the loopVar property
     * @see #setLoopVar
     */
    public final String getLoopVar()
    {
        return theLoopVar;
    }

    /**
     * Set value of the loopVar property.
     *
     * @param newValue new value of the loopVar property
     * @see #getLoopVar
     */
    public final void setLoopVar(
            String newValue )
    {
        theLoopVar = newValue;
    }

    /**
     * Process the start tag.
     *
     * @return evaluate or skip body
     * @throws JspException if a JSP exception has occurred
     */
    protected int realDoStartTag()
        throws
            JspException,
            IgnoreException
    {
        NetMeshObject theMeshObject = ( theMeshObjectName != null ) ? (NetMeshObject) lookup( theMeshObjectName ) : null;
        NetMeshBase   theMeshBase   = ( theMeshBaseName   != null ) ? (NetMeshBase)   lookupOrThrow( theMeshBaseName ) : null;

        if( theMeshObject != null ) {
            if( theMeshBase != null ) {
                throw new JspException( "Must not set both MeshObjectName and MeshBaseName" );
            } else {
                theProxyIterator = theMeshObject.proxyIterator();
            }
        } else if( theMeshBase != null ) {
            theProxyIterator = theMeshBase.proxies();
        } else {
            throw new JspException( "Must set one of MeshObjectName and MeshBaseName" );
        }

        if( theProxyIterator.hasNext() ) {
            Proxy found = theProxyIterator.next();

            if( theLoopVar != null ) {
                pageContext.setAttribute( theLoopVar, found );
            }
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }        
    }

    /**
     * Our implementation of doAfterBody().
     */
    @Override
    protected int realDoAfterBody()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        if( super.bodyContent != null ) {

            InfoGridNetJspUtils.printPrevious( pageContext, InfoGridNetJspUtils.isTrue( getFilter()), bodyContent.getString() );
            bodyContent.clearBody();
        }

        if( theProxyIterator.hasNext() ) {
            Proxy found = theProxyIterator.next();

            if( theLoopVar != null ) {
                pageContext.setAttribute( theLoopVar, found );
            }
            return EVAL_BODY_AGAIN;
        } else {
            return SKIP_BODY;
        }        
    }

    /**
     * Our implementation of doEndTag().
     */
    @Override
    protected int realDoEndTag()
        throws
            JspException,
            IgnoreException,
            IOException
    {
        if( theLoopVar != null ) {
            pageContext.removeAttribute( theLoopVar );
        }

        return EVAL_PAGE;
    }
    
    /**
     * String containing the name of the bean that is the MeshObject whose Proxies we render.
     */
    protected String theMeshObjectName;

    /**
     * String containing the name of the bean that is the NetMeshBase whose Proxies we render.
     */
    protected String theMeshBaseName;

    /**
     * Iterator over the found Proxies.
     */
    protected Iterator<Proxy> theProxyIterator;

    /**
     * Name of the loop variable.
     */
    protected String theLoopVar;
}
