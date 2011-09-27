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
// Copyright 1998-2011 by R-Objects Inc. dba NetMesh Inc., Johannes Ernst
// All rights reserved.
//

package org.infogrid.comm.smtp;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javax.smartcardio.CommandAPDU;
import org.infogrid.comm.AbstractFireAndForgetSendingMessageEndpoint;
import org.infogrid.util.ExternalCommand;
import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.Log;

/**
 * A message endpoint for sending messages via SMTP.
 * 
 * @param <T> the message type
 */
public class SmtpSendingMessageEndpoint<T extends SmtpSendableMessage>
        extends
            AbstractFireAndForgetSendingMessageEndpoint<T>
{
    private static final Log log = Log.getLogInstance( SmtpSendingMessageEndpoint.class ); // our own, private logger

    /**
     * Factory method.
     *
     * @param exec the ScheduledExecutorService to schedule timed tasks
     * @return the created SmtpSendingMessageEndpoint
     * @param <T> the message type
     */
    public static <T extends SmtpSendableMessage> SmtpSendingMessageEndpoint<T> create(
            ScheduledExecutorService exec )
    {
        SmtpSendingMessageEndpoint<T> ret = new SmtpSendingMessageEndpoint<T>(
                null,
                DEFAULT_RANDOM_VARIATION,
                exec,
                new ArrayList<T>() );
        return ret;
    }

    /**
     * Factory method.
     *
     * @param name the name of the MessageEndpoint (for debugging only)
     * @param randomVariation the random component to add to the various times
     * @param exec the ScheduledExecutorService to schedule timed tasks
     * @param messagesToBeSent outgoing message queue (may or may not be empty)
     * @return the created SmtpSendingMessageEndpoint
     * @param <T> the message type
     */
    public static <T extends SmtpSendableMessage> SmtpSendingMessageEndpoint<T> create(
            String                   name,
            double                   randomVariation,
            ScheduledExecutorService exec,
            List<T>                  messagesToBeSent )
    {
        SmtpSendingMessageEndpoint<T> ret = new SmtpSendingMessageEndpoint<T>(
                name,
                randomVariation,
                exec,
                messagesToBeSent );
        return ret;
    }

    /**
     * Constructor for subclasses only, use factory method.
     * 
     * @param name the name of the MessageEndpoint (for debugging only)
     * @param randomVariation the random component to add to the various times
     * @param exec the ScheduledExecutorService to schedule timed tasks
     * @param messagesToBeSent outgoing message queue (may or may not be empty)
     */
    protected SmtpSendingMessageEndpoint(
            String                   name,
            double                   randomVariation,
            ScheduledExecutorService exec,
            List<T>                  messagesToBeSent )
    {
        super( name, randomVariation, exec, messagesToBeSent );
    }

    /**
     * Attempt to send one message.
     * 
     * @param msg the Message to send.
     * @throws IOException the message send failed
     */
    protected void attemptSend(
            T msg )
        throws
            IOException
    {
        if( msg.getRemainingSendingAttempts() <= 0 ) {
            if( log.isDebugEnabled() ) {
                log.debug( "Giving up on", msg );
            }
            return; // pretend it worked
        }
        msg.setRemainingSendingAttempts( msg.getRemainingSendingAttempts()-1 );

        String [] commandLine = new String[ SEND_MAIL_COMMAND.length ] ;
        for( int i=0 ; i<commandLine.length ; ++i ) {
            commandLine[i] = MessageFormat.format( SEND_MAIL_COMMAND[i], msg.getSenderString(), msg.getReceiverString(), msg.getSubject() );
        }

        ProcessBuilder pb = new ProcessBuilder( commandLine );

        int status = ExternalCommand.execute( pb, msg.getPayload(), null, null );
        if( status != 0 ) {
            log.error( "Cannot send mail", status, pb );
            throw new IOException( "Failed to execute mail-sending command" );
        }

    }

    /**
     * Our ResourceHelper.
     */
    private static final ResourceHelper theResourceHelper = ResourceHelper.getInstance( SmtpSendingMessageEndpoint.class );

    /**
     * Default random variation in message sending times.
     */
    public static final double DEFAULT_RANDOM_VARIATION = theResourceHelper.getResourceDoubleOrDefault(  "RandomVariation", 0.02 ); // 2%

    /**
     * Executable to run to actually send the message.
     * This should usually be overridden
     */
    public static final String [] SEND_MAIL_COMMAND = theResourceHelper.getResourceStringArrayOrDefault(
            "SendMailCommand",
            new String [] {
                    "sh",
                    "-c",
                    "( cat && echo \"\\n.\" ) | mail -s '{2}' '{1}'"
            } ); // make sure there's always a single-period line at the end
}
