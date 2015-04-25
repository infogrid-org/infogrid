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
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the TreeFacadeCursorIterator.
 */
public class FileTreeFacadeCursorIteratorTest1
        extends
            AbstractCursorIteratorTest1
{
    @Test
    public void run()
        throws
            Exception
    {
        log.info( "create a test hierarchy, out of order" );
        
        File top = new File( "build/top" );
        deleteFile( top ); // cleanup

        top.mkdirs();
        
        File a = new File( top, "a" );
        File b = new File( top, "b" );
        File c = new File( top, "c" );

        File b1 = new File( b, "1" );
        File b2 = new File( b, "2" );
        File b3 = new File( b, "3" );
        
        File b3x = new File( b3, "x" );
        File b3y = new File( b3, "y" );
        
        c.createNewFile();
        a.createNewFile();
        b3.mkdirs();
        b3x.createNewFile();
        b1.createNewFile();
        b2.createNewFile();
        b3y.createNewFile();
        
        File [] testData = {
            top,
            a,
            b,
            b1,
            b2,
            b3,
            b3x,
            b3y,
            c
        };
        
        FileTreeFacade facade = FileTreeFacade.create( top );
        
        //
        
        log.info( "testing facade" );
        
        Assert.assertEquals("a->b wrong", facade.getForwardSiblingNode( a ), b);
        Assert.assertEquals("b->c wrong", facade.getForwardSiblingNode( b ), c);
        Assert.assertEquals("c->null wrong", facade.getForwardSiblingNode( c ), null);
        Assert.assertEquals("a<-null wrong", facade.getBackwardSiblingNode( a ), null);
        Assert.assertEquals("b<-a wrong", facade.getBackwardSiblingNode( b ), a);
        Assert.assertEquals("c<-b wrong", facade.getBackwardSiblingNode( c ), b);
        Assert.assertEquals("b1->b2 wrong", facade.getForwardSiblingNode( b1 ), b2);
        Assert.assertEquals("b2->b3 wrong", facade.getForwardSiblingNode( b2 ), b3);
        Assert.assertEquals("b3->null wrong", facade.getForwardSiblingNode( b3 ), null);
        Assert.assertEquals("b1<-null wrong", facade.getBackwardSiblingNode( b1 ), null);
        Assert.assertEquals("b2<-b1 wrong", facade.getBackwardSiblingNode( b2 ), b1);
        Assert.assertEquals("b3<-b2 wrong", facade.getBackwardSiblingNode( b3 ), b2);
        Assert.assertEquals("b3x->b3y wrong", facade.getForwardSiblingNode( b3x ), b3y);
        Assert.assertEquals("b3y->null wrong", facade.getForwardSiblingNode( b3y ), null);
        Assert.assertEquals("b3x<-null wrong", facade.getBackwardSiblingNode( b3x ), null);
        Assert.assertEquals("b3y<-b3y wrong", facade.getBackwardSiblingNode( b3y ), b3x);

        checkEqualsOutOfSequence( facade.getChildNodes( top ), new File[] { a, b, c    }, "top has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( a   ), new File[] {            }, "a has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b   ), new File[] { b1, b2, b3 }, "b has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( c   ), new File[] {            }, "c has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b1  ), new File[] {            }, "b1 has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b2  ), new File[] {            }, "b2 has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b3  ), new File[] { b3x, b3y   }, "b3 has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b3x ), new File[] {            }, "b3x has wrong children" );
        checkEqualsOutOfSequence( facade.getChildNodes( b3y ), new File[] {            }, "b3y has wrong children" );
        
        Assert.assertEquals("top has wrong parent", facade.getParentNode( top ), null);
        Assert.assertEquals("a has wrong parent", facade.getParentNode( a   ), top);
        Assert.assertEquals("b has wrong parent", facade.getParentNode( b   ), top);
        Assert.assertEquals("c has wrong parent", facade.getParentNode( c   ), top);
        Assert.assertEquals("b1 has wrong parent", facade.getParentNode( b1  ), b);
        Assert.assertEquals("b2 has wrong parent", facade.getParentNode( b2  ), b);
        Assert.assertEquals("b3 has wrong parent", facade.getParentNode( b3  ), b);
        Assert.assertEquals("b3x has wrong parent", facade.getParentNode( b3x ), b3);
        Assert.assertEquals("b3y has wrong parent", facade.getParentNode( b3y ), b3);
        
        //
        
        log.info( "testing iterator" );
        
        CursorIterator<File> iter1 = facade.iterator();
        
        super.runWith( testData, iter1, log );
    }

    private static final Log log = Log.getLogInstance( FileTreeFacadeCursorIteratorTest1.class  ); // our own, private logger
}
