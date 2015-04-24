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

package org.infogrid.util.test;

import java.io.File;
import org.infogrid.util.CursorIterator;
import org.infogrid.util.logging.Log;
import org.infogrid.util.tree.FileTreeFacade;
import org.junit.Test;

/**
 * Tests the TreeFacadeCursorIterator.
 */
public class FileTreeFacadeCursorIteratorTest2
        extends
            AbstractCursorIteratorTest2
{
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "create a test hierarchy" );

        File top = new File( "build/top" );
        deleteFile( top ); // cleanup

        top.mkdirs();

        FileTreeFacade facade = FileTreeFacade.create( top );

        //

        log.info( "testing iterator" );

        CursorIterator<File> iter1 = facade.iterator();

        super.runWith( top, iter1, log );
    }

    private static final Log log = Log.getLogInstance( FileTreeFacadeCursorIteratorTest2.class  ); // our own, private logger
}
