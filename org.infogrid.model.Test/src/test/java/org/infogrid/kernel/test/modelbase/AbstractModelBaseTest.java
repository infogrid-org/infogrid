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

package org.infogrid.kernel.test.modelbase;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.kernel.test.differencer.AbstractDifferencerTest;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.modelbase.SubjectAreaNotFoundException;
import org.infogrid.modelbase.externalized.xml.XmlModelExporter;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * Factors out common behaviors of ModelBaseTests.
 */
public abstract class AbstractModelBaseTest
        extends
            AbstractTest
{
    /**
     * Initialize Module Framework, and initialize statics.
     * 
     * @throws Exception all sorts of things may go wrong in tests
     */
    @BeforeClass
    public static void initialize()
        throws
            Exception
    {
        ClassLoader    cl       = AbstractModelBaseTest.class.getClassLoader();
        ModuleRegistry registry = InClasspathModuleRegistry.instantiateOrGet( cl );

        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.model.Test" ))).activateRecursively();

        Log4jLog.configure( "org/infogrid/kernel/test/modelbase/Log.properties", cl );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/kernel/test/modelbase/ResourceHelper",
                Locale.getDefault(),
                cl ));
    }

    /**
     * Populate the ModelBase.
     */
    protected void populateModelBase()
        throws
            SubjectAreaNotFoundException,
            IOException
    {
        String [] theSubjectAreas = { "org.infogrid.model.Test" };

        for( int i=0 ; i<theSubjectAreas.length ; ++i ) {
            theModelBase.findSubjectArea( theSubjectAreas[i] );
        }

        //

        if( getLog().isDebugEnabled() ) {
            XmlModelExporter theExporter = new XmlModelExporter();
            theExporter.exportToXML( theModelBase, System.err );
        }
    }

    /**
     * Clean up after the test.
     */
    @After
    public void cleanup()
    {
        theModelBase.die();
    }

    /**
     * The ModelBase to test.
     */
    protected ModelBase theModelBase = ModelBaseSingleton.getSingleton();
}
