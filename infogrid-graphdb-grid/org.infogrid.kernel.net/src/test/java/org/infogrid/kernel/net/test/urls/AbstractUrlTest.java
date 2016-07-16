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
// Copyright 1998-2016 by Johannes Ernst
// All rights reserved.
//

package org.infogrid.kernel.net.test.urls;

import java.util.Locale;
import java.util.ResourceBundle;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.ToStringDumper;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.BeforeClass;

/**
 * Superclass for all tests in this package.
 */
public abstract class AbstractUrlTest
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
        ClassLoader    cl       = AbstractUrlTest.class.getClassLoader();
        ModuleRegistry registry = InClasspathModuleRegistry.instantiateOrGet( cl );

        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.kernel" ))).activateRecursively();

        Log4jLog.configure( "org/infogrid/kernel/net/test/urls/Log.properties", cl );
        Log.setLogFactory( new Log4jLogFactory());

        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/kernel/net/test/urls/ResourceHelper",
                Locale.getDefault(),
                cl ));
    }

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );
}
