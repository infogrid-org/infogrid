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

package org.infogrid.jee.testapp;

import java.text.ParseException;
import org.infogrid.jee.rest.defaultapp.m.AbstractMRestfulAppInitializationFilter;
import org.infogrid.mesh.IsAbstractException;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifierNotUniqueException;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.RelatedAlreadyException;
import org.infogrid.meshbase.MeshBase;
import org.infogrid.meshbase.MeshBaseLifecycleManager;
import org.infogrid.meshbase.MeshObjectIdentifierFactory;
import org.infogrid.meshbase.transaction.Transaction;
import org.infogrid.meshbase.transaction.TransactionException;
import org.infogrid.model.Blob.BlobSubjectArea;
import org.infogrid.model.Test.TestSubjectArea;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.util.context.Context;
import org.infogrid.util.logging.Log;
import org.infogrid.viewlet.ViewletFactory;

/**
 * Initializes application-level functionality.
 */
public class TestAppInitializationFilter
        extends
            AbstractMRestfulAppInitializationFilter
{
    private static Log log; // because this is a filter, delay initialization

    /**
     * Constructor.
     */
    public TestAppInitializationFilter()
    {
        // nothing
    }

    /**
     * Initialize the initial content of the MeshBase.
     * 
     * @param mb the MeshBase to initialize
     */
    @Override
    protected void populateMeshBase(
            MeshBase mb )
    {
        MeshBaseLifecycleManager    life   = mb.getMeshBaseLifecycleManager();
        MeshObjectIdentifierFactory idFact = mb.getMeshObjectIdentifierFactory();
        MeshObject                  home   = mb.getHomeObject();
        
        EntityType [] typesToInstantiate = {
            TestSubjectArea.MANDATORYBLOBANY,
            TestSubjectArea.MANDATORYBLOBHTML,
            TestSubjectArea.MANDATORYBLOBIMAGE,
            TestSubjectArea.MANDATORYBLOBJPG,
            TestSubjectArea.MANDATORYBLOBPLAIN,
            TestSubjectArea.MANDATORYBLOBPLAINORHTML,
            TestSubjectArea.MANDATORYBOOLEAN,
            TestSubjectArea.MANDATORYCOLOR,
            TestSubjectArea.MANDATORYENUMERATED,
            TestSubjectArea.MANDATORYEXTENT,
            TestSubjectArea.MANDATORYFLOAT,
            TestSubjectArea.MANDATORYINTEGER,
            TestSubjectArea.MANDATORYMULTIPLICITY,
            TestSubjectArea.MANDATORYPOINT,
            TestSubjectArea.MANDATORYPROPERTIES,
            TestSubjectArea.MANDATORYSTRING,
            TestSubjectArea.MANDATORYTIMEPERIOD,
            TestSubjectArea.MANDATORYTIMESTAMP,
            TestSubjectArea.OPTIONALBLOBANY,
            TestSubjectArea.OPTIONALBLOBHTML,
            TestSubjectArea.OPTIONALBLOBIMAGE,
            TestSubjectArea.OPTIONALBLOBJPG,
            TestSubjectArea.OPTIONALBLOBPLAIN,
            TestSubjectArea.OPTIONALBLOBPLAINORHTML,
            TestSubjectArea.OPTIONALBOOLEAN,
            TestSubjectArea.OPTIONALCOLOR,
            TestSubjectArea.OPTIONALENUMERATED,
            TestSubjectArea.OPTIONALEXTENT,
            TestSubjectArea.OPTIONALFLOAT,
            TestSubjectArea.OPTIONALINTEGER,
            TestSubjectArea.OPTIONALMULTIPLICITY,
            TestSubjectArea.OPTIONALPOINT,
            TestSubjectArea.OPTIONALPROPERTIES,
            TestSubjectArea.OPTIONALSTRING,
            TestSubjectArea.OPTIONALTIMEPERIOD,
            TestSubjectArea.OPTIONALTIMESTAMP
        };

        Transaction tx = null;
        try {
            tx = mb.createTransactionNow();
            
            MeshObject image = life.createMeshObject(
                    idFact.fromExternalForm( "image" ), // testing is easier with well-known object
                    BlobSubjectArea.IMAGE );
            
            home.relate( image );
            
            for( int i=0 ; i<typesToInstantiate.length ; ++i ) {
                MeshObject current = life.createMeshObject(
                        idFact.fromExternalForm( typesToInstantiate[i].getName().value()),
                        typesToInstantiate[i] );
                home.relate( current );
            }

        } catch( ParseException ex ) {
            getLog().error( ex );
        } catch( MeshObjectIdentifierNotUniqueException ex ) {
            getLog().error( ex );
        } catch( IsAbstractException ex ) {
            getLog().error( ex );
        } catch( RelatedAlreadyException ex ) {
            getLog().error( ex );
        } catch( NotPermittedException ex ) {
            getLog().error( ex );
        } catch( TransactionException ex ) {
            getLog().error( ex );
        } finally {
            if( tx != null ) {
                tx.commitTransaction();
            }
        }
    }

    /**
     * Initialize the context objects. This may be overridden by subclasses.
     *
     * @param rootContext the root Context
     * @throws Exception initialization may fail
     */
    @Override
    protected void initializeContextObjects(
            Context rootContext )
        throws
            Exception
    {
        super.initializeContextObjects( rootContext );

        ViewletFactory vlFact = new TestAppViewletFactory();
        rootContext.addContextObject( vlFact );
    }

    /**
     * Initialize and get the log.
     *
     * @return the log
     */
    private static Log getLog()
    {
        if( log == null ) {
            log = Log.getLogInstance( TestAppInitializationFilter.class ); // our own, private logger
        }
        return log;
    }
}
