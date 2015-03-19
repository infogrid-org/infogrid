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

package org.infogrid.httpd.filesystem.defaultserver;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A simple executable that can run the HttpFilesystemServer, with a primitive Swing GUI
 * to start and stop the server.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class SwingServerRunner
{
    /**
     * Main program.
     *
     * @param args command-line arguments
     */
    public static void main(
            String [] args )
    {
        try {
            main0( args );
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Real main program.
     *
     * @param args command-line arguments
     * @throws Exception any Exception
     */
    protected static void main0(
            String [] args )
        throws
            Exception
    {
        HttpFilesystemServer server = new HttpFilesystemServer( new File( System.getProperty( "user.home" )));
        JFrame f = createFrame( server );
        f.setVisible( true );
    }

    /**
     * Create the GUI.
     *
     * @param server the HttpFilesystemServer to activate/deactivate
     * @return the JFrame containing the GUI
     */
    protected static JFrame createFrame(
            final HttpFilesystemServer server )
    {
        JFrame f = new JFrame( "ServerRunner" );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel        p     = new JPanel( new BorderLayout() );
        final JButton act   = new JButton( "activate!" );
        final JButton deact = new JButton( "deactivate!" );

        deact.setEnabled( false );
        p.add( act,   BorderLayout.WEST );
        p.add( deact, BorderLayout.EAST );

        act.addActionListener((ActionEvent e) -> {
            act.setEnabled( false );
            deact.setEnabled( true );
            
            try {
                server.start();
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        });
        deact.addActionListener((ActionEvent e) -> {
            act.setEnabled( true );
            deact.setEnabled( false );
            
            try {
                server.stop();
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        });

        f.getContentPane().add( p );
        f.pack();

        return f;
    }
}
