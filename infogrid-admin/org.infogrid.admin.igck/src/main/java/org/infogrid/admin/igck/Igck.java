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
import org.infogrid.meshbase.store.IterableStoreMeshBase;
import org.infogrid.model.primitives.EntityType;
import org.infogrid.model.primitives.RoleType;
import org.infogrid.store.IterableStore;
import org.infogrid.store.sql.mysql.MysqlStore;
//import org.infogrid.store.sql.postgresql.PostgresqlStore;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;
//import org.postgresql.ds.PGSimpleDataSource;

/**
 * The InfoGrid checker.
 */
public class Igck
{
    private static final Log log = Log.getLogInstance( Igck.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param dbConnectString the JDBC database connection string
     * @param dbTable the table in the database containing the MeshObjects
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
     * Set the preen flag.
     * 
     * @param newValue the new value
     */
    public void setPreen(
            boolean newValue )
    {
        thePreen = newValue;
    }
    
    /**
     * Get the preen flag.
     * 
     * @return the flag
     */
    public boolean getPreen()
    {
        return thePreen;
    }

    /**
     * Run the operation.
     */
    public void run()
    {
        runNeighbors();
        runMultiplicities();
    }
    
    /**
     * Access all the neighbors in each MeshObject, and make sure they exist.
     */
    protected void runNeighbors()
    {
        log.info( "Checking MeshObjects for neighbors" );

        int meshObjectCount = 0;
        int neighborCount   = 0;
        CursorIterator<MeshObject> iter = theMeshBase.iterator();
        
        while( iter.hasNext() ) {
            MeshObject current = iter.next();
            log.debug( current.getIdentifier().toExternalForm() );
            
            MeshObjectIdentifier [] neighborIds = current.getNeighborMeshObjectIdentifiers();
            for( int i=0 ; i<neighborIds.length ; ++i ) {
                if( neighborIds[i] == null ) {
                    log.error(
                            "MeshObject",
                            current.getIdentifier().toExternalForm(),
                            "has null neighbor identifier (" + i + "/" + neighborIds.length + ")" );
                } else {
                    MeshObject neighbor = theMeshBase.findMeshObjectByIdentifier( neighborIds[i] );
                    if( neighbor == null ) {
                        log.error(
                                "MeshObject",
                                current.getIdentifier().toExternalForm(),
                                ", neighbor (" + i + "/" + neighborIds.length + ") cannot be found:",
                                neighborIds[i] );
                    }
                }
            }
            ++meshObjectCount;
            neighborCount += neighborIds.length;
        }
        log.info( "Done checking", meshObjectCount, "MeshObjects for neighbors (" + neighborCount + ")" );
    }
    
    /**
     * Check the Multiplicities of all relationships.
     */
    protected void runMultiplicities()
    {
        log.info( "Checking multiplicities" );
        
        int meshObjectCount = 0;
        CursorIterator<MeshObject> iter = theMeshBase.iterator();
        
        while( iter.hasNext() ) {
            MeshObject current = iter.next();
            log.debug( current.getIdentifier().toExternalForm() );

            for( EntityType entityType : current.getTypes()) {
                for( RoleType roleType : entityType.getAllRoleTypes()) {
                    MeshObjectIdentifier [] others = current.traverseToIdentifiers( roleType );
                    
                    try {
                        roleType.checkMultiplicity( current, others );

                    } catch( MultiplicityException ex ) {
                        log.error(
                                "MeshObject",
                                current.getIdentifier().toExternalForm(),
                                "violates multiplicity of RoleType",
                                roleType.getIdentifier().toExternalForm(),
                                ": has ",
                                others.length,
                                "related MeshObjects, multiplicity is",
                                roleType.getMultiplicity());
                    }
                }
            }
        }
        log.info( "Done chechking", meshObjectCount, "MeshObjects for multiplicities" );
    }

    /**
     * The MeshBase to be tested.
     */
    protected IterableMeshBase theMeshBase;
    
    /**
     * The preen flag. If true, automatically repair everything.
     */
    protected boolean thePreen;
}
