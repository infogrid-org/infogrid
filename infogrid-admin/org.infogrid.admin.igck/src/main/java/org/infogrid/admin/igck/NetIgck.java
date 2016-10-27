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
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.mesh.MeshObject;
import org.infogrid.mesh.MeshObjectIdentifier;
import org.infogrid.mesh.NotPermittedException;
import org.infogrid.mesh.net.NetMeshObjectIdentifier;
import org.infogrid.meshbase.net.NetMeshBase;
import org.infogrid.meshbase.net.NetMeshObjectAccessException;
import org.infogrid.store.IterableStore;
import org.infogrid.store.sql.mysql.MysqlStore;
import org.infogrid.util.logging.Log;
//import org.postgresql.ds.PGSimpleDataSource;

/**
 * The "Net" InfoGrid checker.
 */
public class NetIgck
    extends
        Igck
{
    private static final Log log = Log.getLogInstance( NetIgck.class ); // our own, private logger

    /**
     * Factory method.
     * 
     * @param netMeshBaseId the NetMeshBaseIdentifier for the NetMeshBase
     * @param dbConnectString the JDBC database connection string
     * @param dbTable the table in the database containing the MeshObjects
     * @param proxyTable the table in the database containing the Proxies
     * @param shadowTable the table in the database containing the Shadow MeshBases
     * @param shadowProxyTable the table in the database containing all the Proxies of the Shadow MeshBases
     * @param dbUser the database user to use
     * @param dbPassword the database password to use
     * @return the created Igck
     * @throws IOException thrown if an input/output problem occurred
     * @throws ParseException thrown if the NetMeshBaseIdentifier could not be parsed
     */
    public static Igck create(
            String netMeshBaseId,
            String dbConnectString,
            String dbTable,
            String proxyTable,
            String shadowTable,
            String shadowProxyTable,
            String dbUser,
            String dbPassword )
        throws
            IOException,
            ParseException
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
        IterableStore proxyStore;
        IterableStore shadowStore;
        IterableStore shadowProxyStore;
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

            store            = MysqlStore.create( dataSource, dbTable );
            proxyStore       = MysqlStore.create( dataSource, proxyTable );
            shadowStore      = MysqlStore.create( dataSource, shadowTable );
            shadowProxyStore = MysqlStore.create( dataSource, shadowProxyTable );
            
        
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
        
        InstrumentedNetStoreMeshBase mb = InstrumentedNetStoreMeshBase.create( netMeshBaseId, store, proxyStore, shadowStore, shadowProxyStore );

        return new NetIgck( mb );
    }

    /**
     * Private constructor, use factory method.
     * 
     * @param mb the MeshBase to be tested
     */
    protected NetIgck(
            InstrumentedNetStoreMeshBase mb )
    {
        super( mb );
    }

    /**
     * Overridden method to find a MeshObject by identifier.
     * 
     * @param id the identifier
     * @return the found MeshObject, or null
     */
    @Override
    protected MeshObject findMeshObject(
            MeshObjectIdentifier id )
    {
        try {
            NetMeshBase realMb = (NetMeshBase) theMeshBase;

            MeshObject ret = realMb.accessLocally( id );
            return ret;

        } catch( NetMeshObjectAccessException ex ) {
            return null;

        } catch( NotPermittedException ex ) {
            log.error( ex );
        }
        return null;
    }

    /**
     * Overridable method to test whether the MeshObject with this identifier exists,
     * and if it does not, but should exist, return true.
     * 
     * @param id the identifier
     * @return true indicates error
     */
    protected boolean meshObjectShouldExist(
            MeshObjectIdentifier id )
    {
        NetMeshBase             realMb = (NetMeshBase) theMeshBase;
        NetMeshObjectIdentifier realId = (NetMeshObjectIdentifier) id;
        
        if( !realMb.getIdentifier().equals( realId.getNetMeshBaseIdentifier() )) {
            return false; // we don't worry about shadows
        }
        MeshObject ret = theMeshBase.findMeshObjectByIdentifier( id );
        return ret == null;
    }
}