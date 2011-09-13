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

package org.infogrid.comm.smtp;

import org.infogrid.util.ResourceHelper;
import org.infogrid.util.logging.CanBeDumped;
import org.infogrid.util.logging.Dumper;

/**
 * A simple implementation of SmtpSendableMessage.
 */
public class SimpleSmtpSendableMessage
        implements
            SmtpSendableMessage,
            CanBeDumped
{
    /**
     * Factory method.
     * 
     * @param sender the sender identifier as a String
     * @param receiver the receiver identifier as a String
     * @param subject the message subject
     * @param payload the message payload
     * @return the created SimpleSmtpSendableMessage
     */
    public static SimpleSmtpSendableMessage create(
            String sender,
            String receiver,
            String subject,
            String payload )
    {
        return new SimpleSmtpSendableMessage( sender, receiver, subject, payload, DEFAULT_ATTEMPTS );
    }

    /**
     * Factory method.
     *
     * @param sender the sender identifier as a String
     * @param receiver the receiver identifier as a String
     * @param subject the message subject
     * @param payload the message payload
     * @param remainingSendingAttempts the number of (remaining) attempts to send until giving up
     * @return the created SimpleSmtpSendableMessage
     */
    public static SimpleSmtpSendableMessage create(
            String sender,
            String receiver,
            String subject,
            String payload,
            int    remainingSendingAttempts )
    {
        return new SimpleSmtpSendableMessage( sender, receiver, subject, payload, remainingSendingAttempts );
    }

    /**
     * Constructor.
     * 
     * @param sender the sender identifier as a String
     * @param receiver the receiver identifier as a String
     * @param subject the message subject
     * @param payload the message payload
     * @param remainingSendingAttempts the number of (remaining) attempts to send until giving up
     */
    protected SimpleSmtpSendableMessage(
            String sender,
            String receiver,
            String subject,
            String payload,
            int    remainingSendingAttempts )
    {
        theSender   = sender;
        theReceiver = receiver;
        theSubject  = subject;
        thePayload  = payload;
        theRemainingSendingAttempts = remainingSendingAttempts;
    }

    /**
     * Obtain the sender identifier as a String.
     * 
     * @return the sender identifier as a String
     */
    public String getSenderString()
    {
        return theSender;
    }
    
    /**
     * Obtain the receiver identifier as a String.
     * 
     * @return the receiver identifier as a String
     */
    public String getReceiverString()
    {
        return theReceiver;
    }
    
    /**
     * Obtain the subject of the message.
     * 
     * @return the subject of the message
     */
    public String getSubject()
    {
        return theSubject;
    }
    
    /**
     * Obtain the payload of the message.
     * 
     * @return the payload of the message
     */
    public String getPayload()
    {
        return thePayload;
    }
    
    /**
     * Obtain the remaining number of sending attempts until giving up.
     *
     * @return the number of sending attempts
     */
    public int getRemainingSendingAttempts()
    {
        return theRemainingSendingAttempts;
    }

    /**
     * Set the remaining number of sending attempts until giving up.
     *
     * @param newValue the new value
     */
    public void setRemainingSendingAttempts(
            int newValue )
    {
        theRemainingSendingAttempts = newValue;
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
                        "sender",
                        "receiver",
                        "subject",
                        "payload",
                        "remainingSendingAttempts"
                },
                new Object[] {
                        theSender,
                        theReceiver,
                        theSubject,
                        thePayload,
                        theRemainingSendingAttempts
                });
    }

    /**
     * Sender identifier.
     */
    protected String theSender;
    
    /**
     * Receiver identifier.
     */
    protected String theReceiver;
    
    /**
     * Message subject, if any.
     */
    protected String theSubject;
    
    /**
     * Message payload, if any.
     */
    protected String thePayload;

    /**
     * Remaining number of attempts to send.
     */
    protected int theRemainingSendingAttempts;

    /**
     * The default number of sending attempts.
     */
    public static final int DEFAULT_ATTEMPTS = ResourceHelper.getInstance( SimpleSmtpSendableMessage.class ).getResourceIntegerOrDefault(
            "DefaultAttempts",
            4 );
}
