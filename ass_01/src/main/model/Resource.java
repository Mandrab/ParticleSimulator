package main.model;

import java.util.logging.Level;

import main.GlobalLogger;

/**
 * Class used for synchronization
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class Resource {
	
	private static final GlobalLogger logger = GlobalLogger.get( );
	
	private boolean run ;
	private int steps;
	private long startTime;
	private long stopTime;
	
	public synchronized void setRun( boolean obj ) {
		run = obj;
	}
	
	public synchronized boolean getRun( ) {
		return run;
	}
	
	public synchronized int getSteps( ) {
		return steps;
	}
	
	public synchronized void setSteps( int obj ) {
		steps = obj;
	}

	public synchronized void manage( ) {
		while ( !getRun( ) )
			try {
				wait( );
			} catch (InterruptedException e) { e.printStackTrace( ); }
		if ( getSteps( ) > 0 )
			setRun( false );
		}

	public synchronized void start() {
       setRun( true );
       startTime = System.currentTimeMillis();
       setSteps( 0 );
       notify( );
    }
    
    public synchronized void stop() {
    	setRun( false );
    	stopTime = System.currentTimeMillis();
    	logger.log( Level.INFO, "Elapsed user time " + ( stopTime - startTime ) );
     }
    
    public synchronized void step() {
    	setRun( true );
        setSteps( 1 );
        notify( );
    }
}
