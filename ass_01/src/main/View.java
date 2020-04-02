package main;

import java.awt.BorderLayout;
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

import main.Model.State;

/**
 * Simulation view
 * @author aricci
 *
 */
public class View extends JFrame implements ActionListener{

	private static final long serialVersionUID = -5516015410790143416L;

	private VisualiserPanel panel;
	private JButton buttonStart;
	private JButton buttonStop;
	private JPanel buttonPanel;
    
    /**
     * Creates a view of the specified size (in pixels)
     * @param w
     * @param h
     */
    public View( int w, int h ){
        setTitle( "Bodies Simulation" );
        
        getContentPane( ).setLayout( new BorderLayout( ) );
        setSize( w, h );
        setResizable( false );
        this.panel = new VisualiserPanel( w, h );
        getContentPane( ).add( panel, BorderLayout.CENTER );
        addWindowListener( new WindowAdapter( ) {
			public void windowClosing( WindowEvent ev ) {
				System.exit( -1 );
			}
			public void windowClosed( WindowEvent ev ) {
				System.exit( -1 );
			}
		} );
        setVisible( true );
        
        this.buttonStart = new JButton( "start" );
        this.buttonStop = new JButton( "stop" );
        this.buttonPanel = new JPanel( );
        this.buttonPanel.add( this.buttonStart );
        this.buttonPanel.add( this.buttonStop );
        getContentPane( ).add( this.buttonPanel,BorderLayout.NORTH );
        this.buttonStart.addActionListener( this );
        this.buttonStop.addActionListener( this );
    }
    
    public void display( List<Position> bodies, double vt, long iter ){
        try {
	    	SwingUtilities.invokeAndWait( ( ) -> {
	        	this.panel.display( bodies, vt, iter );
	        } );
        } catch ( Exception ex ) { }
    }
        
    public static class VisualiserPanel extends JPanel {

		private static final long serialVersionUID = -165761220960106001L;

		private List<Position> bodies = new ArrayList<Position>( );
    	private long nIter;
    	private double vt;
    	
        private long dx;
        private long dy;
        
        public VisualiserPanel( int w, int h ) {
            setSize( w, h );
            dx = w/2 - 20;
            dy = h/2 - 20;
        }

        public void paint( Graphics g ){
    		Graphics2D g2 = ( Graphics2D ) g;

    		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON );
    		g2.setRenderingHint( RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY );
    		g2.clearRect( 0, 0, this.getWidth( ), this.getHeight( ) );
	        
    		this.bodies.forEach( b -> {
	            double rad = 0.01;
	            int x0 = (int)(dx + b.getX()*dx);
		        int y0 = (int)(dy - b.getY()*dy);
		        g2.drawOval(x0,y0, (int)(rad*dx*2), (int)(rad*dy*2));
		    });
    		String time = String.format("%.2f", vt);
    		g2.drawString( "Bodies: " + bodies.size( ) + " - vt: " + time + " - nIter: " + nIter, 2, 20 );
        }
        
        public void display( List<Position> bodies, double vt, long iter){
            this.bodies = bodies;
            this.vt = vt;
            this.nIter = iter;
        	repaint();
        }
    }

	@Override
	public void actionPerformed(ActionEvent action) {

		  Object src = action.getSource();
	        if (src==this.buttonStart){
	            //model start 
	        } else {
	            //model stop
	        }
	}
	
	public void updateView( State state ) {
		display( state.getBallsPositions( ), state.getVirtualTime( ), state.getIterations( ) );
	}
}
