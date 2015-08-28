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

package org.infogrid.probe.store.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.net.externalized.ExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObject;
import org.infogrid.mesh.net.externalized.ParserFriendlyExternalizedNetMeshObjectFactory;
import org.infogrid.mesh.net.externalized.SimpleExternalizedNetMeshObject;
import org.infogrid.meshbase.net.DefaultNetMeshBaseAccessSpecificationFactory;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.DefaultNetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshObjectAccessSpecificationFactory;
import org.infogrid.meshbase.net.NetMeshObjectIdentifierFactory;
import org.infogrid.mesh.net.a.DefaultAnetMeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.externalized.ExternalizedProxy;
import org.infogrid.modelbase.MeshTypeIdentifierFactory;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.modelbase.m.MMeshTypeIdentifierFactory;
import org.infogrid.probe.httpmapping.TraditionalInfoGridHttpMappingPolicy;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.probe.shadow.externalized.ExternalizedShadowMeshBase;
import org.infogrid.probe.shadow.externalized.xml.ExternalizedShadowMeshBaseXmlEncoder;
import org.infogrid.probe.shadow.m.MShadowMeshBase;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests ShadowMeshBase serialization.
 */
public class ShadowMeshBaseSerializationTest1
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
        ClassLoader    cl       = ShadowMeshBaseSerializationTest1.class.getClassLoader();
        ModuleRegistry registry = InClasspathModuleRegistry.instantiateOrGet( cl );

        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.probe.store" ))).activateRecursively();
        
        Log4jLog.configure( "org/infogrid/probe/store/test/Log.properties", cl );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/probe/store/test/ResourceHelper",
                Locale.getDefault(),
                cl ));

        theModelBase = ModelBaseSingleton.getSingleton();
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Test
    public void run()
        throws
            Exception
    {
        File thisDir = new File( "src/test/resources/org/infogrid/probe/store/test" );

        TestCase [] testCases = new TestCase[] {
            new TestCase(
                    "ShadowMeshBaseSerializationTest1_1.xml",
                    nmbid1,
                    null,
                    new ExternalizedNetMeshObject[] {
                        SimpleExternalizedNetMeshObject.create(
                                theNetMeshObjectIdentifierFactory.fromExternalForm( "" ),
                                null,
                                12L,
                                34L,
                                56L,
                                78L,
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                false,
                                null,
                                -1,
                                -1,
                                null )
                    }
            )
        };
        
        for( int i=0 ; i<testCases.length ; ++i ) {
            runTest( thisDir, testCases[i] );
        }
    }

    /**
     *  Run one test.
     * 
     * @param parentDir the parent directory
     * @param testCase the test case to run
     * @throws Exception all sorts of things may go wrong in tests
     */
    protected void runTest(
            File     parentDir,
            TestCase testCase )
        throws
            Exception
    {
        log.debug( "Now running testcase " + testCase.theInputFile );
            
        File theFile = new File( parentDir, testCase.theInputFile );

        ExternalizedShadowMeshBaseXmlEncoder test = new ExternalizedShadowMeshBaseXmlEncoder();
        
        ExternalizedShadowMeshBase mb = test.decodeShadowMeshBase(
                new FileInputStream( theFile ),
                theShadowMeshBase );

        checkEquals( mb.getNetworkIdentifier(), testCase.theNetworkIdentifier, testCase.theInputFile + ": NetworkIdentifier wrong" );
        
        checkEqualsOutOfSequence( mb.getExternalizedProxies(),        testCase.theProxies, testCase.theInputFile + ": Proxies wrong" );
        checkEqualsOutOfSequence( mb.getExternalizedNetMeshObjects(), testCase.theObjects, testCase.theInputFile + ": MeshObjects wrong" );
    }
    
    /**
     * Setup.
     * 
     * @throws Exception all sorts of things may go wrong in tests
     */
    @Before
    public void setup()
        throws
            Exception
    {
        nmbid1 = theMeshBaseIdentifierFactory.fromExternalForm( "https://foo.example.com/%27" );
        
        theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create();
        theExternalizedMeshObjectFactory
                = new ParserFriendlyExternalizedNetMeshObjectFactory() {
                        public ParserFriendlyExternalizedNetMeshObject createParserFriendlyExternalizedMeshObject() {
                            return new ParserFriendlyExternalizedNetMeshObject();
                        }
                };
        theNetMeshObjectIdentifierFactory
                = DefaultAnetMeshObjectIdentifierFactory.create( nmbid1, theMeshBaseIdentifierFactory );

        theMeshTypeIdentifierFactory = MMeshTypeIdentifierFactory.create();
    
        theNetMeshBaseAccessSpecificationFactory = DefaultNetMeshBaseAccessSpecificationFactory.create(
                    theMeshBaseIdentifierFactory );

        theNetMeshObjectAccessSpecificationFactory
                = DefaultNetMeshObjectAccessSpecificationFactory.create(
                        theNetMeshObjectIdentifierFactory,
                        theMeshBaseIdentifierFactory,
                        theNetMeshBaseAccessSpecificationFactory );

        theShadowMeshBase = MShadowMeshBase.create(
                nmbid1,
                theMeshBaseIdentifierFactory,
                theNetMeshObjectAccessSpecificationFactory,
                null,
                theModelBase,
                null,
                null,
                System.currentTimeMillis(),
                TraditionalInfoGridHttpMappingPolicy.SINGLETON,
                theApplicationContext );
    }
    
    /**
     * Tear-down.
     */
    @After
    public void cleanup()
    {
        theShadowMeshBase.die();
        theShadowMeshBase = null;
    }

    // Our Logger
    private static Log log = Log.getLogInstance( ShadowMeshBaseSerializationTest1.class );
    
    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create();
    
    /**
     * A NetMeshBaseIdentifier for the test.
     */
    protected NetMeshBaseIdentifier nmbid1;

    /**
     * A ExternalizedNetMeshObjectFactory for the test.
     */
    protected ParserFriendlyExternalizedNetMeshObjectFactory theExternalizedMeshObjectFactory;
    
    /**
     * A NetMeshObjectIdentifierFactory for the test.
     */
    protected NetMeshObjectIdentifierFactory theNetMeshObjectIdentifierFactory;

    /**
     * A MeshTypeIdentifierFactory for the test.
     */
    protected MeshTypeIdentifierFactory theMeshTypeIdentifierFactory;
    
    /**
     * a factory for NetMeshBaseAccessSpecifications for the test.
     */
    protected NetMeshBaseAccessSpecificationFactory theNetMeshBaseAccessSpecificationFactory;
    /**
     * A factory for NetMeshObjectAccessSpecifications for the test.
     */
    protected NetMeshObjectAccessSpecificationFactory theNetMeshObjectAccessSpecificationFactory;
    
    /**
     * Our ModelBase.
     */
    protected static ModelBase theModelBase;
    
    /**
     * Root application context.
     */
    protected static Context theApplicationContext = SimpleContext.createRoot( "root" );
    
    /**
     * ShadowMeshBase to be serialized into.
     */
    protected ShadowMeshBase theShadowMeshBase;
    
    /**
     * Represents one TestCase.
     */
    protected static class TestCase
    {
        TestCase(
                String                       inputFile,
                NetMeshBaseIdentifier            id,
                ExternalizedProxy []         proxies,
                ExternalizedNetMeshObject [] objects )

            throws
                Exception
        {
            theInputFile         = inputFile;
            theNetworkIdentifier = id;
            
            if( proxies != null ) {
                theProxies = proxies;
            } else {
                theProxies = new ExternalizedProxy[0];
            }
            if( objects != null ) {
                theObjects = objects;
            } else {
                theObjects = new ExternalizedNetMeshObject[0];
            }
        }

        public String                      theInputFile;
        public NetMeshBaseIdentifier       theNetworkIdentifier;
        public ExternalizedProxy[]         theProxies;
        public ExternalizedNetMeshObject[] theObjects;
    }
}
