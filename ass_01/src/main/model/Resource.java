package main.model;
/**
 * Class used for synchronization
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class Resource {
	
	private boolean run ;
	private int steps;
	
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
       setSteps( 0 );
       notify( );
    }
    
    public synchronized void stop() {
    	setRun( false );
     }
    
    public synchronized void step() {
    	setRun( true );
        setSteps( 1 );
        notify( );
    }
}
