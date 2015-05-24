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

package org.infogrid.probe.test.yadis;

import java.util.Arrays;
import java.util.Collection;
import org.infogrid.meshbase.net.local.LocalNetMeshBase;
import org.junit.Test;
import org.junit.runners.Parameterized;

/**
 * Factors out more functionality for Yadis discovery tests.
 */
public abstract class AbstractYadisDiscoveryTest
    extends
        AbstractYadisTest
{
    /**
     * Test parameters.
     * 
     * @return test parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList( new Object[][]
            {
                {
                    0
                },
                {
                    2000
                }
            });
    }

    /**
     * Run the test.
     *
     * @throws Exception all sorts of things may happen during a test
     */
    @Test
    public void run()
        throws
            Exception
    {
        run( theMeshBase1, true );
        run( theMeshBase2, false );
    }

    /**
     * Run one test scenario.
     *
     * @param mb the NetMeshBase to use for this scenario
     * @param mode the mode
     * @throws Exception all sorts of things may happen during a test
     */
    protected abstract void run(
            LocalNetMeshBase mb,
            boolean          mode )
        throws
            Exception;
}
