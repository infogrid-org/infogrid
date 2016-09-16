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

package org.infogrid.admin.igck;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.MultiplicityException;
import org.infogrid.meshbase.IterableMeshBase;
import org.infogrid.meshbase.MeshBaseError;
import org.infogrid.meshbase.MeshBaseErrorListener;
import org.infogrid.meshbase.store.IterableStoreMeshBase;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.PropertyType;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.store.IterableStore;
import org.infogrid.store.sql.mysql.MysqlStore;
import org.infogrid.util.ArrayHelper;
//import org.infogrid.store.sql.postgresql.PostgresqlStore;
import org.infogrid.util.HasIdentifier;
import org.infogrid.util.logging.Log;
//import org.postgresql.ds.PGSimpleDataSource;

/**
 * The InfoGrid checker.
 */
public class Igck
    implements
        MeshBaseErrorListener
{
    private static final Log log = Log.getLogInstance( Igck.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param dbConnectString the JDBC database connection string
     * @param dbTable the table in the database containing the MeshObjects
     * @param dbUser the database user to use
     * @param dbPassword the database password to use
     * @return the created Igck
     * @throws IOException thrown if an input/output problem occurred
     */
    public static Igck create(
            String dbConnectString,
            String dbTable,
            String dbUser,
            String dbPassword )
        throws
            IOException
    {
        // example: jdbc:mysql://localhost/test
        
        Pattern jdbcPattern = Pattern.compile( "(jdbc:)?([a-z0-9]+)://([^/:]+(:\\d+)?)(/([^/]+))?" );
        Matcher matcher     = jdbcPattern.matcher( dbConnectString );
        
        if( !matcher.matches()) {
            throw new IOException( "Cannot parse JDBC connection string: " + dbConnectString );
        }
        String dbType = matcher.group( 2 );
        String dbHost = matcher.group( 3 );
        String dbPort = matcher.group( 4 );
        String dbName = matcher.group( 6 );

        IterableStore store;        
        if( "mysql".equals( dbType) ) {
            MysqlDataSource dataSource = new MysqlDataSource();
            if( dbHost != null && !dbHost.isEmpty() ) {
                dataSource.setServerName(   dbHost );
            }
            if( dbPort != null && !dbPort.isEmpty() ) {
                dataSource.setPortNumber(   Integer.parseInt( dbPort ));
            }
            if( dbName != null && !dbName.isEmpty() ) {
                dataSource.setDatabaseName( dbName );
            }
            if( dbUser != null && !dbUser.isEmpty() ) {
                dataSource.setUser(         dbUser );
            }
            if( dbPassword != null && !dbPassword.isEmpty() ) {
                dataSource.setPassword(     dbPassword );
            }

            store = MysqlStore.create( dataSource, dbTable );
        
//        } else if( "postgresql".equals( dbType )) {
//            PGSimpleDataSource dataSource = new PGSimpleDataSource();
//            dataSource.setServerName(   dbHost );
//            dataSource.setPortNumber(   Integer.parseInt( dbPort ));
//            dataSource.setDatabaseName( dbName );
//            dataSource.setUser(         dbUser );
//            dataSource.setPassword(     dbPassword );
//
//            store = PostgresqlStore.create( dataSource, dbTable );
//            
        } else {
            throw new IOException( "Cannot identify database engine for connection string " + dbConnectString );
        }
        
        IterableStoreMeshBase mb = IterableStoreMeshBase.create( store );

        return new Igck( mb );
    }
    
    /**
     * Private constructor, use factory method.
     * 
     * @param mb the MeshBase to be tested
     */
    protected Igck(
            IterableMeshBase mb )
    {
        theMeshBase = mb;
    }
    
    /**
     * Set the checkMissingNeighbors flag.
     * 
     * @param newValue the new value
     */
    public void setCheckMissingNeighbors(
            boolean newValue )
    {
        theCheckMissingNeighbors = newValue;
    }
    
    /**
     * Set the checkMissingTypes flag.
     * 
     * @param newValue the new value
     */
    public void setCheckMissingTypes(
            boolean newValue )
    {
        theCheckMissingTypes = newValue;
    }
    
    /**
     * Set the checkMultiplicities flag.
     * 
     * @param newValue the new value
     */
    public void setCheckMultiplicities(
            boolean newValue )
    {
        theCheckMultiplicities = newValue;
    }
    
    /**
     * Set the checkValues flag.
     * 
     * @param newValue the new value
     */
    public void setCheckValues(
            boolean newValue )
    {
        theCheckValues = newValue;
    }

    /**
     * Set the removeMissingNeighbors flag.
     * 
     * @param newValue the new value
     */
    public void setRemoveMissingNeighbors(
            boolean newValue )
    {
        theRemoveMissingNeighbors = newValue;
    }
    
    /**
     * Set the removeMissingTypes flag.
     * 
     * @param newValue the new value
     */
    public void setRemoveMissingTypes(
            boolean newValue )
    {
        theRemoveMissingTypes = newValue;
    }
    
    /**
     * Set the verbosity level. Higher means more verbose.
     * 
     * @param newValue the new level
     */
    public void setVerbose(
            int newValue )
    {
        theVerbose = newValue;
    }

    /**
     * Run the operation.
     */
    public void run()
    {
        theMeshBase.addDirectErrorListener( this );

        theMeshObjectCount = 0;
        theErrorCount      = 0;

        theMeshBase.iterator().batchForEach(
                512,
                (MeshObject current) -> runOne( current ));

        if( theErroneousCount == 0 ) {
            System.out.println( "Congratulations, no errors found." );
        } else {
            System.out.printf("Found %d erroneous out of %d MeshObjects (%02.1f%%), %d errors total\n",
                    theErroneousCount,
                    theMeshObjectCount,
                    100.f * theErroneousCount / theMeshObjectCount,
                    theErrorCount );
        }
        theMeshBase.removeErrorListener( this );
    }

    /**
     * Check a single MeshObject.
     * 
     * @param current the MeshObject to check
     */    
    protected void runOne(
            MeshObject current )
    {
        ++theMeshObjectCount;
        theErrorFlag = 0;

        if( theCheckMissingNeighbors ) {
            MeshObjectIdentifier [] neighborIds = current.getNeighborMeshObjectIdentifiers();
            for( int i=0 ; i<neighborIds.length ; ++i ) {
                if( neighborIds[i] == null ) {
                    ++theErrorFlag;
                    error(  current,
                            "null neighbor identifier (" + i + "/" + neighborIds.length + ")",
                            "(type " + ArrayHelper.arrayToString( current.getTypes(), (EntityType t) -> t.getIdentifier().toExternalForm() ) + ")" );
                } else {
                    MeshObject neighbor = theMeshBase.findMeshObjectByIdentifier( neighborIds[i] );
                    if( neighbor == null ) {
                        ++theErrorFlag;
                        error(  current,
                                "neighbor (" + i + "/" + neighborIds.length + ") cannot be found:",
                                neighborIds[i].toExternalForm(),
                                "(type " + ArrayHelper.arrayToString( current.getTypes(), (EntityType t) -> t.getIdentifier().toExternalForm() ) + ")" );
                    }
                }
            }
        }
        if( theCheckMultiplicities ) {
            for( EntityType entityType : current.getTypes()) {
                for( RoleType roleType : entityType.getAllRoleTypes()) {
                    MeshObjectIdentifier [] others = current.traverseToIdentifiers( roleType );

                    try {
                        roleType.checkMultiplicity( current, others );

                    } catch( MultiplicityException ex ) {
                        ++theErrorFlag;
                        error(  current,
                                "RoleType " + roleType.getIdentifier().toExternalForm() + " (" + roleType.getMultiplicity().toString() + ") has " + others.length,
                                "(type " + ArrayHelper.arrayToString( current.getTypes(), (EntityType t) -> t.getIdentifier().toExternalForm() ) + ")" );
                    }
                }
            }            
        }
        if( theErrorFlag > 0 ) {
            theErrorCount += theErrorFlag;
            ++theErroneousCount;
            theErrorFlag = 0;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unresolveableEntityType(
            MeshBaseError.UnresolvableEntityType event )
    {
        ++theErrorFlag;
        if( theCheckMissingTypes ) {
            error(  event.getMeshObject(),
                    "unknown EntityType " + event.getMeshTypeIdentifier().toExternalForm() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unresolveableRoleType(
            MeshBaseError.UnresolvableRoleType event )
    {
        ++theErrorFlag;
        if( theCheckMissingTypes ) {
            error(  event.getMeshObject(),
                    "unknown RoleType " + event.getMeshTypeIdentifier().toExternalForm() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unresolveablePropertyType(
            MeshBaseError.UnresolvablePropertyType event )
    {
        ++theErrorFlag;
        if( theCheckMissingTypes ) {
            error(  event.getMeshObject(),
                    "unknown PropertyType " + event.getMeshTypeIdentifier().toExternalForm() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incompatibleDataType(
            MeshBaseError.IncompatibleDataType event )
    {
        ++theErrorFlag;
        if( theCheckValues ) {
            error(  event.getMeshObject(),
                    "value " + event.getPropertyValue() + " incompatible with type " + event.getPropertyType().getDataType() + " of PropertyType " + event.getPropertyType() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyNotOptional(
            MeshBaseError.PropertyNotOptional event )
    {
        ++theErrorFlag;
        if( theCheckValues ) {
            error(  event.getMeshObject(),
                    "PropertyType " + event.getPropertyType() + " does not allow null values" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void otherError(
            MeshBaseError.OtherError event )
    {
        log.error( event );
    }

    /**
     * Report an error.
     * 
     * @param obj the affected MeshObject
     * @param msgs the messages in increasing verbosity
     */
    protected void error(
            HasIdentifier obj,
            Object ... msgs )
    {
        if( theVerbose == 0 ) {
            System.err.println( obj.getIdentifier().toExternalForm() );
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append( "Error: " );
            msg.append( obj.getIdentifier().toExternalForm() );
            for( int i=0 ; i<theVerbose && i<msgs.length; ++i ) {
                msg.append( ' ' );
                msg.append( msgs[i] );
            }
            System.err.println( msg );
        }
    }

    /**
     * The MeshBase to be tested.
     */
    protected IterableMeshBase theMeshBase;
    
    /**
     * If true, check for missing neighbors.
     */
    protected boolean theCheckMissingNeighbors;
    
    /**
     * If true, check for missing types.
     */
    protected boolean theCheckMissingTypes;
    
    /**
     * If true, check for invalid multiplicities.
     */
    protected boolean theCheckMultiplicities;

    /**
     * If true, check PropertyValues.
     */
    protected boolean theCheckValues;

    /**
     * If true, remove missing neighbors.
     */
    protected boolean theRemoveMissingNeighbors;
    
    /**
     * If true, remove missing types.
     */
    protected boolean theRemoveMissingTypes;
    
    /**
     * The verbosity level.
     */
    protected int theVerbose;
    
    /**
     * Running counter for examined MeshObjects.
     */
    protected int theMeshObjectCount;
    
    /**
     * Running counter for errors.
     */
    protected int theErrorCount;

    /**
     * Running counter for MeshObjects with at least one error.
     */
    protected int theErroneousCount;
    
    /**
     * Temporary flag for error counting that gets reset on each MeshObject.
     */
    protected int theErrorFlag;
}
