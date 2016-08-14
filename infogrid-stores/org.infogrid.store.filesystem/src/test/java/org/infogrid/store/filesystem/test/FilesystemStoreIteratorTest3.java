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

package org.infogrid.store.filesystem.test;

import java.io.File;
import org.infogrid.store.test.AbstractStoreIteratorTest3;
import org.infogrid.store.filesystem.FilesystemStore;
import org.junit.Before;

/**
 * Tests the FilesystemStoreIterator.
 */
public class FilesystemStoreIteratorTest3
        extends
            AbstractStoreIteratorTest3
{
    @Before
    public void setup()
    {
        File subdir = new File( AbstractFilesystemStoreTest.test_SUBDIR_NAME );
        deleteFile( subdir );
        subdir.mkdirs();

        theTestStore = FilesystemStore.create( subdir );
    }
}
