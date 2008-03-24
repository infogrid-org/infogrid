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

package org.infogrid.viewlet;

import org.infogrid.context.Context;
import org.infogrid.mesh.MeshObject;
import org.infogrid.model.primitives.EntityType;

import org.infogrid.module.Module;
import org.infogrid.module.ModuleCapability;
import org.infogrid.module.ModuleException;
import org.infogrid.module.ModuleNotFoundException;
import org.infogrid.module.ModuleRegistry;
import org.infogrid.module.ModuleResolutionException;
import org.infogrid.module.StandardModuleAdvertisement;

import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

import java.net.MalformedURLException;

/**
 * ViewletFactoryChoice referencing a ModuleCapability in a Module.
 */
public class InModuleViewletFactoryChoice
        extends
            ViewletFactoryChoice
{
    private static final Log log = Log.getLogInstance( InModuleViewletFactoryChoice.class ); // our own, private logger

    /**
     * Constructor.
     *
     * @param reg the ModuleRegistry against which to resolve the StandardModuleAdvertisement
     * @param ad the StandardModuleAdvertisement that contains the candidate
     * @param cap the ModuleCapability that was found
     */
    public InModuleViewletFactoryChoice(
            ModuleRegistry              reg,
            StandardModuleAdvertisement ad,
            ModuleCapability            cap )
    {
        theModuleRegistry = reg;
        theAd             = ad;
        theCapability     = cap;
    }

    /**
     * Obtain a user-visible String to display to the user for this ViewletFactoryChoice.
     *
     * @return the user-visible String
     */
    public String getUserVisibleName()
    {
        try {
            Module m = theModuleRegistry.resolve( theAd );

            ResourceHelper rh = ResourceHelper.getInstance( theCapability.getImplementationName(), m.getClassLoader() );
            String         className = getImplementationName();

            String ret = rh.getResourceStringOrDefault( className, className );
            return ret;

        } catch( MalformedURLException ex ) {
            log.warn( ex );
            return getImplementationName();

        } catch( ModuleException ex ) {
            log.warn( ex );
            return getImplementationName();
        }
    }
    /**
      * Obtain the names of the interfaces provided by this ViewletFactoryChoice.
      *
      * @return the names of the interfaces provided by this ViewletFactoryChoice.
      */
    public String [] getInterfaceNames()
    {
        return theCapability.getInterfaceNames();
    }

    /**
     * Obtain the name of the Viewlet's implementation.
     *
     * @return the implementation name
     */
    public String getImplementationName()
    {
        return theCapability.getImplementationName();
    }

    /**
     * Obtain the advertisement of the Module that contained a candidate.
     *
     * @return the ModuleAdvertisement
     */
    public StandardModuleAdvertisement getModuleAdvertisement()
    {
        return theAd;
    }

    /**
     * Obtain the found ModuleCapability.
     *
     * @return the found ModuleCapability
     */
    public ModuleCapability getModuleCapability()
    {
        return theCapability;
    }

    /**
     * Obtain a measure of the match quality. 0 means &quot;perfect match&quot;,
     * while larger numbers mean increasingly worse match quality.
     *
     * @param toView the MeshObjectsToView to match against
     * @return the match quality
     * @see ViewletFactoryChoice#PERFECT_MATCH_QUALITY
     * @see ViewletFactoryChoice#AVERAGE_MATCH_QUALITY
     * @see ViewletFactoryChoice#WORST_MATCH_QUALITY
     */
    public double getMatchQualityFor(
            MeshObjectsToView toView )
    {
        MeshObject                             subject = toView.getSubject();
        ModuleCapability.ArgumentCombination[] combo   = theCapability.getSupportedArgumentCombinations();

        if( combo == null || combo.length == 0 ) {
           // treat it as if it was one level supertypes away (FIXME?)
            return 1;

        } else {
            int bestDistance = Integer.MAX_VALUE;

            for( int j = 0; j<combo.length; ++j ) {
                String[] args = combo[j].getArguments();

                if( args.length != 1 ) {
                    continue;
                }
                
                for( EntityType type : subject.getTypes() ) {
                    int distance = type.getDistanceToSupertype( args[0] );

                    if( distance < bestDistance ) {
                        bestDistance  = distance;
                    }
                }
            }
            return bestDistance;
        }
    }

    /**
     * Instantiate a ViewletFactoryChoice into a Viewlet.
     * 
     * @param toView the MeshObjectsToView; only used for error reporting
     * @param c the Context to use
     * @return the instantiated Viewlet
     */
    public Viewlet instantiateViewlet(
            MeshObjectsToView toView,
            Context           c )
        throws
            CannotViewException
    {
        try {
            Module      viewletModule = theModuleRegistry.resolve( theAd, true );
            ClassLoader viewletClassLoader = viewletModule.getClassLoader();
            Class       viewletClass = Class.forName( theCapability.getImplementationName(), true, viewletClassLoader );

            return instantiateViewlet( toView, viewletClass, c );

        } catch( ClassNotFoundException ex ) {
            throw new org.infogrid.viewlet.CannotViewException.InternalError( null, toView, ex );
            
        } catch( ModuleResolutionException ex ) {
            log.error( ex );
            throw new org.infogrid.viewlet.CannotViewException.InternalError( null, toView, ex );
            
        } catch( ModuleNotFoundException ex ) {
            log.error( ex );
            throw new org.infogrid.viewlet.CannotViewException.InternalError( null, toView, ex );
            
        } catch( MalformedURLException ex ) {
            log.error( ex );
            throw new org.infogrid.viewlet.CannotViewException.InternalError( null, toView, ex );
            
        }
    }

    /**
     * The ModuleRegistry.
     */
    protected ModuleRegistry theModuleRegistry;

    /**
     * The advertisement of the Module that contained a candidate.
     */
    protected StandardModuleAdvertisement theAd;

    /**
     * The capability that indicated a candidate.
     */
    protected ModuleCapability theCapability;
}
