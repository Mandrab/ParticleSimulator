package main.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import main.controller.Controller;
import main.Position;
import main.model.Model.State;

/**
 * This class deals with the views, creates the panel for the view with the related buttons.
 * The view contains three buttons: start, stop and step.
 * Button start-> the view starts its execution, 
 * Button stop-> the execution stops and the progress is shown on the screen.
 * Button step-> step by step you execute the mode.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class View extends JFrame implements ActionListener {

	private static final long serialVersionUID = -5516015410790143416L;
	private static final int WIDTH = 620;
	private static final int HEIGHT = 620; 

	private Controller controller;
	
	private VisualiserPanel panel;
	private JPanel buttonPanel;
	private JTextField textField;
	private JButton buttonStart;
	private JButton buttonStop;
	private JButton buttonStep;
    
    public View( Controller controller ) {
    	
    	this.controller = controller;

        setTitle( "Bodies Simulation" );

        setLayout( new BorderLayout( ) );

        setResizable( false );

        panel = new VisualiserPanel( WIDTH, HEIGHT );
        panel.setPreferredSize( new Dimension( WIDTH, HEIGHT ) );

        addWindowListener( new WindowAdapter( ) {
			public void windowClosing( WindowEvent ev ) {
				System.exit( -1 );
			}
			public void windowClosed( WindowEvent ev ) {
				System.exit( -1 );
			}
		} );

        textField = new JTextField( "Bodies: " + 0 + " - virtual: " + 0.0 + " - numberIter: " + 0.0 );
		textField.setEditable( false );

        buttonStart = new JButton( "start" );
        buttonStart.addActionListener( this );

        buttonStop = new JButton( "stop" );
        buttonStop.addActionListener( this );
        
        buttonStep = new JButton( "step" );
        buttonStep.addActionListener( this );

        buttonPanel = new JPanel( );
        buttonPanel.add( textField );
        buttonPanel.add( buttonStart );
        buttonPanel.add( buttonStop );
        buttonPanel.add( buttonStep );
        buttonPanel.setPreferredSize( new Dimension( WIDTH, buttonPanel.getPreferredSize( ).height ) );

        add( buttonPanel, BorderLayout.NORTH );
        add( panel, BorderLayout.CENTER );

        pack( );
        setVisible( true );
    }
    
    public void display( List<Position> bodies, double vt, long iter ){
        try {
	    	SwingUtilities.invokeAndWait( ( ) -> {
	        	this.panel.display( bodies, vt, iter );
	        } );
        } catch ( Exception ex ) { }
    }
        
    public class VisualiserPanel extends JPanel {

		private static final long serialVersionUID = -165761220960106001L;

		private List<Position> bodies = new ArrayList<Position>( );
    	private long nIter;
    	
        private long dx;
        private long dy;
        
        public VisualiserPanel( int WIDTH, int HEIGHT ) {
            setSize( WIDTH, HEIGHT );
            dx = WIDTH/2 - 20;
            dy = HEIGHT/2 - 20;
        }

        public void paint( Graphics g ) {
    		Graphics2D g2 = ( Graphics2D ) g;

    		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON );
    		g2.setRenderingHint( RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY );
    		g2.clearRect( 0, 0, this.getWidth( ), this.getHeight( ) );
	        
    		bodies.forEach( b -> {
	            double rad = 0.01;
	            int x0 = ( int )( dx + b.getX( ) * dx );
		        int y0 = ( int )( dy - b.getY( ) * dy );
		        g2.drawOval( x0, y0, ( int )( rad * dx * 2 ), ( int )( rad * dy * 2 ) );
		    } );
        }

        public void display( List<Position> bodies, double vt, long iter ) {
        	this.bodies = bodies;
            this.nIter = iter;
            String time = String.format( "%.2f", vt );
    		textField.setText( "Bodies: " + bodies.size( ) + " - vt: " + time + " - nIter: " + nIter);
        	repaint( );
        }
    }

	@Override
	public void actionPerformed(ActionEvent action) {

		Object src = action.getSource();
        if ( src == buttonStart ) {
        	SwingUtilities.invokeLater( ( ) -> controller.start( ) );
        } else if ( src == buttonStop ) {
        	SwingUtilities.invokeLater( ( ) -> controller.stop( ) );
        } else if ( src == buttonStep ) {
        	SwingUtilities.invokeLater( ( ) -> controller.step( ) );
        }
	}

	public void updateView( State state ) {
		display( state.getBallsPositions( ), state.getVirtualTime( ), state.getIterations( ) );
	}
}
