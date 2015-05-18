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

package org.infogrid.kernel.net.test.mesh.externalized;

import java.util.regex.Pattern;
import org.infogrid.meshbase.net.DefaultNetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.NetMeshBaseIdentifierFactory;
import org.infogrid.meshbase.net.schemes.AcctScheme;
import org.infogrid.meshbase.net.schemes.FileScheme;
import org.infogrid.meshbase.net.schemes.HttpScheme;
import org.infogrid.meshbase.net.schemes.HttpsScheme;
import org.infogrid.meshbase.net.schemes.Scheme;
import org.infogrid.meshbase.net.schemes.StrictRegexScheme;
import org.infogrid.meshbase.net.schemes.XriScheme;
import org.infogrid.modelbase.ModelBase;
import org.infogrid.modelbase.ModelBaseSingleton;
import org.infogrid.testharness.AbstractTest;
/**
 * Factors out common functionality of SerializerTests.
 */
public abstract class AbstractNetSerializerTest
        extends
            AbstractTest
{
    /**
     * The MeshBaseIdentifierFactory to be used.
     */
    protected final static NetMeshBaseIdentifierFactory theMeshBaseIdentifierFactory = DefaultNetMeshBaseIdentifierFactory.create(
            new Scheme [] {
                    new HttpScheme(),
                    new HttpsScheme(),
                    new FileScheme(),
                    new AcctScheme(),
                    new XriScheme(),
                    new StrictRegexScheme( "test", Pattern.compile( "test:.*" ))
             } );

    /**
     * The ModelBase.
     */
    protected static ModelBase theModelBase = ModelBaseSingleton.getSingleton();
}
