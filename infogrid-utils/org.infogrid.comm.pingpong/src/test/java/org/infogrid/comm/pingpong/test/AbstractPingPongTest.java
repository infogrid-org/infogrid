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

package org.infogrid.comm.pingpong.test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import org.infogrid.util.NamedThreadFactory;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;
import org.infogrid.util.logging.Log;

/**
 * Factors out functionality common to ping-pong tests.
 */
public abstract class AbstractPingPongTest
{
    /**
     * This starts a relative timer.
     * 
     * @return the time at which the clock was started, in System.currentTimeMillis() format
     */
    protected final long startClock()
    {
        startTime = System.currentTimeMillis();
        getLog().info( "Starting clock -- it is now " + startTime );
        
        return startTime;
    }

    /**
     * Obtain the absolute start time.
     *
     * @return the time at which the clock was started, in System.currentTimeMillis() format
     */
    public final long getStartTime()
    {
        return startTime;
    }

    /**
     * Sleep for a specified number of milliseconds.
     *
     * @param delta the number of milliseconds to wait
     * @throws InterruptedException thrown when another thread interrupts this Thread while sleeping
     */
    protected final static void sleepFor(
            long delta )
        throws
            InterruptedException
    {
        Thread.sleep( delta );
    }

    /**
     * This puts the invoking Thread to sleep until a specified relative time
     * (in milli-seconds) from the time startClock() was called.
     *
     * @param relativeTimeInMillis the relative time by which this Thread is supposed to wake up
     * @throws InterruptedException thrown if the Thread was interrupted
     */
    protected final void sleepUntil(
            long relativeTimeInMillis )
        throws
            InterruptedException
    {
        long current = System.currentTimeMillis();

        getLog().info( "sleeping until relative time: " + relativeTimeInMillis + " (it is now: relative: " + (current-startTime) + " / absolute: " + current + " )" );

        long delta = relativeTimeInMillis - (current - startTime);

        try {
            Thread.sleep( delta );
        } catch( IllegalArgumentException ex ) {
            getLog().error(
                    "Wait time has passed already: current relative time: "
                    + (current-startTime)
                    + " vs. "
                    + relativeTimeInMillis
                    + " absolute: "
                    + current );
        }
    }

    /**
     * Obtain the current time relative to the time startClock() was called.
     *
     * @return the current time relative to the time startClock() was called
     */
    protected final long getRelativeTime()
    {
        long current = System.currentTimeMillis();

        return current - startTime;
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
     * Obtain a Thread pool for a test.
     * 
     * @param testName name of the test, used to label the Threads created by the ThreadFactory
     * @param nThreads the number of threads in the thread pool
     * @return the created ScheduledExecutorService
     */
    protected ScheduledExecutorService createThreadPool(
            String testName,
            int    nThreads )
    {
        NamedThreadFactory factory = new NamedThreadFactory( testName );

        return new MyScheduledThreadPoolExecutor( nThreads, factory, testName );
    }

    /**
     * Obtain a Thread pool for a test.
     * 
     * @param nThreads the number of threads in the thread pool
     * @return the created ScheduledExecutorService
     */
    protected ScheduledExecutorService createThreadPool(
            int nThreads )
    {
        Class<?> testClass = getClass();
        
        return createThreadPool( testClass.getName(), nThreads );
    }
    // Our Logger
    private static Log log;

    /**
     * The absolute time in millis when the timer was started.
     */
    protected long startTime;

    /**
     * Our local subclass of ScheduledThreadPoolExecutor.
     */
    static class MyScheduledThreadPoolExecutor
            extends
                ScheduledThreadPoolExecutor
            implements
                CanBeDumped
    {
        /**
         * Constructor.
         *
         * @param corePoolSize number of Threads
         * @param threadFactory factory for Threads
         * @param name name of this MyScheduledThreadPoolExecutor, for debugging purposes
         */
        MyScheduledThreadPoolExecutor(
                int           corePoolSize,
                ThreadFactory threadFactory,
                String        name )
        {
            super( corePoolSize, threadFactory );

            theName = name;

            setContinueExistingPeriodicTasksAfterShutdownPolicy( false );
            setExecuteExistingDelayedTasksAfterShutdownPolicy( false );
        }

        /**
         * Dump this object.
         *
         * @param d the Dumper to dump to
         */
        public void dump(
                Dumper d )
        {
            d.dump( this,
                    new String[] {
                        "name"
                    },
                    new Object[] {
                        "ThreadPoolExecutor " + theName + " (" + getCorePoolSize() + " threads)"
                    });
        }

        /**
         *
         */
        protected String theName;
    }
}
