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

package org.infogrid.probe.test.writableprobe;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import org.diet4j.core.ModuleException;
import org.infogrid.mesh.EntityBlessedAlreadyException;
import org.infogrid.mesh.EntityNotBlessedException;
import org.infogrid.mesh.IllegalPropertyTypeException;
import org.infogrid.mesh.IllegalPropertyValueException;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.NotRelatedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.mesh.net.NetMeshObject;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.meshbase.net.CoherenceSpecification;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshBaseIdentifier;
import org.infogrid.meshbase.transaction.ChangeSet;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.probe.ApiProbe;
import org.infogrid.probe.ProbeException;
import org.infogrid.probe.StagingMeshBase;
import org.infogrid.probe.StagingMeshBaseLifecycleManager;
import org.infogrid.probe.WritableProbe;
import org.infogrid.probe.shadow.ShadowMeshBase;
import org.infogrid.util.logging.Log;

/**
 * Tests creating MeshObjects with WritableProbe.
 */
public class WritableProbeTest9
        extends
            AbstractWritableProbeTest
{
    /**
     * Run the test.
     *
     * @throws Exception all sorts of things can go wrong during a test
     */
    // FIXME BROKEN: need new API call to "createAndPush" to avoid triggering the non-local NetMeshObjectIdentifier exception

    public void run()
        throws
            Exception
    {
        WritableProbeTestCase [] cases = new WritableProbeTestCase[] {
            new WritableProbeTestCase( NonWritableTestProbe.class ) {
                    public void afterFirstRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Attempting to create OBJ1 in main NetMeshBase and pushing it to shadow, should fail" );

                        Transaction tx     = null;
                        Throwable   thrown = null;
                        try {
                            tx = mainBase.createTransactionAsap();
                            
                            NetMeshObject obj = mainBase.getMeshBaseLifecycleManager().createMeshObject(
                                    mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                            shadow.getIdentifier(),
                                            OBJ1 ));
                            boolean success = obj.tryToPushHomeReplica( mainBase.getProxyFor( shadow.getIdentifier() ));
                            
                            checkCondition( !success, "Pushing new object should have failed" );

                        } catch( Throwable ex ) {
                            thrown = ex;

                        } finally {
                            if( tx != null ) {
                                tx.commitTransaction();
                            }
                        }
                    }

                    public void afterSecondRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Waiting for replication to happen" );

                        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );

                        NetMeshObject obj1 = mainBase.accessLocally(
                                mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                    shadow.getIdentifier(),
                                    OBJ1 ));

                        checkCondition( obj1 == null, "OBJ1 is still here" );
                    }
            },
            new WritableProbeTestCase( WritableIgnoringTestProbe.class ) {
                    public void afterFirstRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Attempting to create OBJ1 in main NetMeshBase and pushing it to shadow, should work" );

                        Transaction tx = null;
                        try {
                            tx = mainBase.createTransactionAsap();

                            NetMeshObject obj = mainBase.getMeshBaseLifecycleManager().createMeshObject(
                                    mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                            shadow.getIdentifier(),
                                            OBJ1 ));
                            boolean success = obj.tryToPushHomeReplica( mainBase.getProxyFor( shadow.getIdentifier() ));

                            checkCondition( success, "Pushing new object should have succeeded" );

                        } catch( NotPermittedException ex ) {
                            reportError( "Was not permitted to create", ex );

                        } finally {
                            if( tx != null ) {
                                tx.commitTransaction();
                            }
                        }
                    }
                    public void afterSecondRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Should be a ChangeSet of size 1" );

                        checkObject( theChangeSet, "ChangeSet is still null" );
                        if( theChangeSet != null ) {
                            checkEquals( theChangeSet.size(), 1, "Wrong size ChangeSet" );
                        }

                        //

                        log.debug( "Waiting for replication to happen" );

                        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );

                        NetMeshObject obj1 = mainBase.accessLocally(
                                mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                    shadow.getIdentifier(),
                                    OBJ1 ));

                        checkCondition( obj1 == null, "OBJ1 should not exist" );
                    }
            },
            new WritableProbeTestCase( WritableProcessingTestProbe.class ) {
                    public void afterFirstRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Attempting to create OBJ1 in main NetMeshBase and pushing it to shadow, should work" );

                        Transaction tx = null;
                        try {
                            tx = mainBase.createTransactionAsap();

                            NetMeshObject obj = mainBase.getMeshBaseLifecycleManager().createMeshObject(
                                    mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                            shadow.getIdentifier(),
                                            OBJ1 ));
                            boolean success = obj.tryToPushHomeReplica( mainBase.getProxyFor( shadow.getIdentifier() ));

                            checkCondition( success, "Pushing new object should have succeeded" );

                        } catch( NotPermittedException ex ) {
                            reportError( "Was not permitted to delete object", ex );

                        } finally {
                            if( tx != null ) {
                                tx.commitTransaction();
                            }
                        }
                    }

                    public void afterSecondRun(
                            NetMeshBase    mainBase,
                            ShadowMeshBase shadow,
                            NetMeshObject  shadowHomeInMain )
                        throws
                            Exception
                    {
                        log.debug( "Should be a ChangeSet of size 1" );

                        checkObject( theChangeSet, "ChangeSet is still null" );
                        checkEquals( theChangeSet.size(), 1, "Wrong size ChangeSet" );

                        //

                        log.debug( "Waiting for replication to happen" );

                        Thread.sleep( PINGPONG_ROUNDTRIP_DURATION );

                        NetMeshObject obj1 = mainBase.findMeshObjectByIdentifier(
                                mainBase.getMeshObjectIdentifierFactory().fromExternalForm(
                                    shadow.getIdentifier(),
                                    OBJ1 ));

                        checkObject( obj1, "OBJ1 not not found" );
                    }
            }
            // we don't do a NoWritableProcessingTestProbe here: makes no difference for the creation of MeshObjects
            // we may or may not want to have a mechanism that makes a difference in the future (FIXME?)
        };
        
        super.run( cases );
    }
    
    // Our Logger
    private static Log log = Log.getLogInstance( WritableProbeTest9.class  );

    protected static final String OBJ1 = "#one";

    /**
     * Once the WritableProbe runs, it deposits the found ChangeSet here.
     */
    protected static ChangeSet theChangeSet;
    
    /**
     * Abstract Probe class for this set of tests
     */
    public abstract static class AbstractWritableTestProbe
        implements
            ApiProbe
    {
        public void readFromApi(
                NetMeshBaseIdentifier  networkId,
                CoherenceSpecification coherence,
                StagingMeshBase        freshMeshBase )
            throws
                IsAbstractException,
                EntityBlessedAlreadyException,
                EntityNotBlessedException,
                RelatedAlreadyException,
                NotRelatedException,
                MeshObjectIdentifierNotUniqueException,
                IllegalPropertyTypeException,
                IllegalPropertyValueException,
                TransactionException,
                NotPermittedException,
                ProbeException,
                IOException,
                ModuleException,
                URISyntaxException,
                ParseException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: readFromApi()" );
            }

            MeshObjectIdentifierFactory     idf  = freshMeshBase.getMeshObjectIdentifierFactory();
            StagingMeshBaseLifecycleManager life = freshMeshBase.getMeshBaseLifecycleManager();

            boolean giveUpLock = getGiveUpLock();

            for( int i=0 ; i<theObjectNames.length ; ++i ) {
                NetMeshObject obj = life.createMeshObject( idf.fromExternalForm( theObjectNames[i] ));

                if( giveUpLock ) {
                    obj.setWillGiveUpLock( giveUpLock );
                }
            }
        }

        /**
         * Enable subclasses to say whether to give up lock.
         * 
         * @return true if give up lock
         */
        protected boolean getGiveUpLock()
        {
            return true;
        }
        
        /**
         * Next value for A_X.
         */
        protected String [] theObjectNames = {};
    }

    /**
     * The non-Writable Probe class.
     */
    public static class NonWritableTestProbe
        extends
            AbstractWritableTestProbe
    {
        @Override
        protected boolean getGiveUpLock()
        {
            return false;
        }
    }

    /**
     * The Writable Probe class that ignores the changes.
     */
    public static class WritableIgnoringTestProbe
        extends
            AbstractWritableTestProbe
        implements
            WritableProbe
    {
        public void write(
                NetMeshBaseIdentifier networkId,
                ChangeSet             updateSet,
                StagingMeshBase       previousMeshBaseWithUpdates )
            throws
                ProbeException,
                IOException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: write( " + updateSet + " ), but ignoring" );
            }
            
            theChangeSet = updateSet;
        }
    }
    
    /**
     * The Writable Probe class that allows and processes the changes.
     */
    public static class WritableProcessingTestProbe
        extends
            AbstractWritableTestProbe
        implements
            WritableProbe
    {
         public void write(
                NetMeshBaseIdentifier networkId,
                ChangeSet             updateSet,
                StagingMeshBase       previousMeshBaseWithUpdates )
            throws
                ProbeException,
                IOException
        {
            if( log.isDebugEnabled() ) {
                log.debug( "Probe: write( " + updateSet + " )" );
            }
            
            theObjectNames = new String [] { OBJ1 };

            theChangeSet = updateSet;
        }
    }
}
