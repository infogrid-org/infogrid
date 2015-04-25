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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.infogrid.util.ArrayHelper;
import org.infogrid.util.logging.Log;
import org.junit.Assert;

/**
 * Factors out functionality common to tests in this package.
 */
public class AbstractTest
{
    /**
     * Report error if the arguments do not have the same content in any sequence.
     *
     * @param one first argument to compare
     * @param two second argument to compare
     * @param msg message to print when arguments aren't equal
     * @return true if check passed
     */
    public final boolean checkEqualsOutOfSequence(
            Object [] one,
            Object [] two,
            String    msg )
    {
        boolean ret = ArrayHelper.hasSameContentOutOfOrder( one, two, true );
        if( !ret ) {
            Assert.fail( msg );
        }
        return ret;
    }
    /**
     * Check that a String matches a regular expression.
     * 
     * @param regex the regular expression to match
     * @param candidate the candidate String
     * @param msg the message to print in case of an error
     * @return true if check passed
     */
    public final boolean checkRegex(
            String regex,
            String candidate,
            String msg )
    {
        if( candidate == null ) {
            Assert.fail( msg + ": cannot match null against regex '" + regex + "'" );
            return false;
        }
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( candidate );
        if( !m.matches() ) {
            Assert.fail( msg + ": regex '" + regex + "' did not match candidate '" + candidate + "'" );
            return false;
        }
        return true;
    }

    /**
     * Check that a String matches a regular expression.
     * 
     * @param regex the regular expression to match
     * @param flags the flags for the regular expression per Pattern.compile
     * @param candidate the candidate String
     * @param msg the message to print in case of an error
     * @return true if check passed
     */
    public final boolean checkRegex(
            String regex,
            int    flags,
            String candidate,
            String msg )
    {
        if( candidate == null ) {
            Assert.fail( msg + ": cannot match null against regex '" + regex + "'" );
            return false;
        }
        Pattern p = Pattern.compile( regex, flags );
        Matcher m = p.matcher( candidate );
        if( !m.matches() ) {
            Assert.fail( msg + ": regex '" + regex + "' did not match candidate '" + candidate + "'" );
            return false;
        }
        return true;
    }

    /**
     * Recursively delete a directory or file.
     *
     * @param f the file
     */
    public static void deleteFile(
            File f )
    {
        if( !f.exists() ) {
            return;
        }
        if( f.isDirectory() ) {
            File [] contained = f.listFiles();
            for( int i=0 ; i<contained.length ; ++i ) {
                deleteFile( contained[i] );
            }
        }
        f.delete();
    }

    /**
     * Obtain the Log for this subclass.
     *
     * @return the Log for this subclass
     */
    protected Log getLog()
    {
        return Log.getLogInstance( getClass() );
    }

    /**
     * Invoke the garbage collector.
     */
    protected final void collectGarbage()
    {
        try {
            for( int i=0 ; i<20 ; ++i ) {
                System.gc();
                Thread.sleep( 50L );
            }
        } catch( InterruptedException ex ) {
            Assert.fail( "unexpected interruption of thread: " + ex );
        }
    }
}
