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
// Copyright 1998-2008 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.lid;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Knows how to generate nonces.
 */
public class LidNonceGenerator
{
    /**
     * Generate a new nonce.
     *
     * @return the nonce
     */
    public static String generateNewNonce()
    {
        Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ));
        cal.setTimeInMillis( System.currentTimeMillis() );

        StringBuilder nonce = new StringBuilder( 64 );
        nonce.append( formatTime( cal.get( Calendar.YEAR ), 4 ));
        nonce.append( '-' );
        nonce.append( formatTime( cal.get( Calendar.MONTH )+1, 2 ));
        nonce.append( '-' );
        nonce.append( formatTime( cal.get( Calendar.DAY_OF_MONTH ), 2 ));
        nonce.append( 'T' );
        nonce.append( formatTime( cal.get( Calendar.HOUR_OF_DAY ), 2 ));
        nonce.append( ':' );
        nonce.append( formatTime( cal.get( Calendar.MINUTE ), 2 ));
        nonce.append( ':' );
        nonce.append( formatTime( cal.get( Calendar.SECOND ), 2 ));
        nonce.append( '.' );
        nonce.append( formatTime( cal.get( Calendar.MILLISECOND ), 3 ));
        nonce.append( 'Z' );

        return nonce.toString();
    }

    /**
     * Helper method to format an integer correctly.
     *
     * @param n the number to format
     * @param digits the number of digits in the format
     * @return formatted string
     */
    private static String formatTime(
            int n,
            int digits )
    {
        String ret = String.valueOf( n );
        if( ret.length() < digits ) {
            StringBuffer buf = new StringBuffer( digits );
            for( int i=ret.length() ; i<digits ; ++i ) {
                buf.append( "0" );
            }
            buf.append( ret );
            ret = buf.toString();
        }
        return ret;
    }
}
