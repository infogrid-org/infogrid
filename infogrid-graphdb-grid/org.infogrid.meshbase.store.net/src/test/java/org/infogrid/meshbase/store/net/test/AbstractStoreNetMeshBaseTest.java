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

package org.infogrid.meshbase.store.net.test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import org.diet4j.core.ModuleRegistry;
import org.diet4j.core.ModuleRequirement;
import org.diet4j.inclasspath.InClasspathModuleRegistry;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.mesh.set.MeshObjectSet;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.m.NetMMeshBaseNameServer;
import org.infogrid.meshbase.net.proxy.Proxy;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.PropertyValue;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.store.sql.AbstractSqlStore;
import org.infogrid.store.sql.mysql.MysqlStore;
import org.infogrid.testharness.AbstractTest;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.context.Context;
import org.infogrid.util.context.SimpleContext;
import org.infogrid.util.logging.Log;
import org.infogrid.util.logging.log4j.Log4jLog;
import org.infogrid.util.logging.log4j.Log4jLogFactory;
import org.junit.Before;
import org.junit.BeforeClass;

/**`
 * Factors out common functionality of StoreNetMeshBaseTests.
 */
public abstract class AbstractStoreNetMeshBaseTest
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
        ClassLoader    cl       = AbstractStoreNetMeshBaseTest.class.getClassLoader();
        ModuleRegistry registry = InClasspathModuleRegistry.instantiateOrGet( cl );

        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.kernel" ))).activateRecursively();
        registry.resolve( registry.determineSingleResolutionCandidate( ModuleRequirement.create( "org.infogrid", "org.infogrid.model.Test" ))).activateRecursively();

        Log4jLog.configure( "org/infogrid/meshbase/store/net/test/Log.properties", cl );
        Log.setLogFactory( new Log4jLogFactory());
        
        ResourceHelper.setApplicationResourceBundle( ResourceBundle.getBundle(
                "org/infogrid/meshbase/store/net/test/ResourceHelper",
                Locale.getDefault(),
                cl ));
    }

    /**
     * Setup.
     * 
     * @throws Exception all sorts of things can go wrong in a test
     */
    @Before
    public void setup()
        throws
            Exception
    {
        theNameServer = NetMMeshBaseNameServer.create();

        theDataSource = new MysqlDataSource();
        theDataSource.setDatabaseName( test_DATABASE_NAME );
        
        theSqlStore = MysqlStore.create( theDataSource, test_TABLE_NAME );
    }
    
    /**
     * Report an error if the properties are not the same in both replicas.
     *
     * @param one first replica to compare
     * @param two second replica to compare
     * @param msg message to print when replicas' properties aren't equal
     * @throws Exception anything can go wrong in a test
     */
    protected void checkPropertiesReplication(
            NetMeshObject one,
            NetMeshObject two,
            String        msg )
        throws
            Exception
    {
        PropertyType [] oneTypes = one.getAllPropertyTypes();
        PropertyType [] twoTypes = two.getAllPropertyTypes();

        checkEqualsOutOfSequence( oneTypes, twoTypes, msg + " not the same PropertyTypes" );
        for( int i=0 ; i<oneTypes.length ; ++i ) {
            PropertyValue oneValue = one.getPropertyValue( oneTypes[i] );
            PropertyValue twoValue = two.getPropertyValue( oneTypes[i] );
            checkEquals( oneValue, twoValue, "different values for PropertyType " + oneTypes[i] );
        }
    }

    /**
     * Report an error if the types are not the same in both replicas.
     *
     * @param one first replica to compare
     * @param two second replica to compare
     * @param msg message to print when replicas' types aren't equal
     */
    protected void checkTypesReplication(
            NetMeshObject one,
            NetMeshObject two,
            String        msg )
    {
        EntityType [] oneTypes = one.getTypes();
        EntityType [] twoTypes = two.getTypes();

        checkEqualsOutOfSequence( oneTypes, twoTypes, msg + " not the same EntityTypes" );
    }

    /**
     * Report an error if the neighbors and the relationship types are not the same in both replicas.
     *
     * @param one first replica to compare
     * @param two second replica to compare
     * @param msg message to print when neighbors aren't equal
     * @throws NotRelatedException thrown if the two objects are not related
     */
    protected void checkNeighborsReplication(
            NetMeshObject one,
            NetMeshObject two,
            String        msg )
        throws
            NotRelatedException
    {
        MeshObjectSet oneNeighbors = one.traverseToNeighborMeshObjects( false );
        MeshObjectSet twoNeighbors = two.traverseToNeighborMeshObjects( false );

        checkEqualsOutOfSequence( oneNeighbors.getMeshObjects(), twoNeighbors.getMeshObjects(), msg + " not the same neighbors for " + one.getIdentifier() );

        for( final MeshObject currentOne : oneNeighbors ) {
            MeshObject currentTwo = twoNeighbors.find(
                     (MeshObject candidate) -> currentOne.getIdentifier().equals( candidate.getIdentifier() ));
            
            RoleType [] relatedOne = one.getRoleTypes( currentOne );
            RoleType [] relatedTwo = two.getRoleTypes( currentTwo );
            
            checkEqualsOutOfSequence( relatedOne, relatedTwo, msg + " not the same RoleTypes for " + one.getIdentifier() + " related to " + currentOne.getIdentifier() );
        }
    }
    
    /**
     * Report and error if the position of the Proxies is wrong.
     *
     * @param obj the NetMeshObject whose proxies are checked
     * @param proxiesTowards the NetMeshBases to which the proxies are supposed to be pointing
     * @param proxyTowardHome the NetMeshBase towards which the proxyTowardHome is supposed to be pointing, or null
     * @param proxyTowardLock the NetMeshBase towards which the proxyTowardsLock is supposed to be pointing, or null
     * @param msg the message to print when the proxies are not correct
     */
    protected void checkProxies(
            NetMeshObject  obj,
            NetMeshBase [] proxiesTowards,
            NetMeshBase    proxyTowardHome,
            NetMeshBase    proxyTowardLock,
            String         msg )
    {
        Proxy [] proxies = obj.getAllProxies();

        if( proxies == null || proxies.length == 0 ) {
            if( !( proxiesTowards == null || proxiesTowards.length == 0 )) {
                reportError( msg + ": object has no proxies, should have", proxiesTowards, obj.getIdentifier() );
                return;
            } else {
                return; // no proxies is correct
            }
        } else if( proxiesTowards == null || proxiesTowards.length == 0 ) {
            reportError( msg + ": object has proxies, should have none", proxiesTowards, obj.getIdentifier() );
            return;
        }
        if( proxies.length != proxiesTowards.length ) {
            reportError( msg + ": object has wrong number of proxies. Should have ", proxiesTowards, proxies );
        }
        
        NetMeshBaseIdentifier [] proxiesIdentifiers        = new NetMeshBaseIdentifier[ proxies.length ];
        NetMeshBaseIdentifier [] proxiesTowardsIdentifiers = new NetMeshBaseIdentifier[ proxiesTowards.length ];
        for( int i=0 ; i<proxies.length ; ++i ) {
            proxiesIdentifiers[i] = proxies[i].getPartnerMeshBaseIdentifier();
        }
        for( int i=0 ; i<proxiesTowards.length ; ++i ) {
            proxiesTowardsIdentifiers[i] = proxiesTowards[i].getIdentifier();
        }
        checkEqualsOutOfSequence( proxiesIdentifiers, proxiesTowardsIdentifiers, msg + ": same length, but not the same content" );

        if( proxyTowardLock == null ) {
            if( obj.getProxyTowardsLockReplica() != null ) {
                reportError( msg + ": has proxyTowardsLock but should not", obj.getIdentifier() );
            }

        } else if( obj.getProxyTowardsLockReplica() == null ) {
            reportError( msg + ": does not have proxyTowardsLock but should", obj.getIdentifier() );

        } else {
            checkEquals( proxyTowardLock.getIdentifier(), obj.getProxyTowardsLockReplica().getPartnerMeshBaseIdentifier(), msg + ": wrong proxyTorwardLock" );
        }
        if( proxyTowardHome == null ) {
            if( obj.getProxyTowardsHomeReplica() != null ) {
                reportError( msg + ": has proxyTowardHome but should not", obj.getIdentifier() );
            }

        } else if( obj.getProxyTowardsHomeReplica() == null ) {
            reportError( msg + ": does not have proxyTowardHome but should", obj.getIdentifier() );

        } else {
            checkEquals( proxyTowardHome.getIdentifier(), obj.getProxyTowardsHomeReplica().getPartnerMeshBaseIdentifier(), msg + ": wrong proxyTorwardLock" );
        }
    }

    /**
     * The root context for these tests.
     */
    protected static final Context rootContext = SimpleContext.createRoot( "root-context" );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();

    /**
     * The Database connection.
     */
    protected MysqlDataSource theDataSource;

    /**
     * The AbstractSqlStore to be tested.
     */
    protected AbstractSqlStore theSqlStore;

    /**
     * The name server.
     */
    protected NetMMeshBaseNameServer<NetMeshBaseIdentifier,NetMeshBase> theNameServer;

    /**
     * Factory for NetMeshBaseIdentifiers.
     */
    protected static NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new Scheme[] {
                    new HttpScheme(),
                    new StrictRegexScheme( "test", Pattern.compile( "test:.*" ))
            });

    /**
     * The name of the database that we use to store test data.
     */
    public static final String test_DATABASE_NAME = "test";

    /**
     * The name of the table that we use to store test data.
     */
    public static final String test_TABLE_NAME = "SqlStoreMeshBaseTest";

    /**
     * Expected duration within which at least one ping-pong round trip can be completed.
     * Milliseconds.
     */
    protected static final long PINGPONG_ROUNDTRIP_DURATION = 500L;
}
