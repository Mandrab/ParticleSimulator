package main.model;

/**
 * Class used for synchronization
 * Contains method start, stop and step.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Resource {
	
	private boolean run ;
	private boolean steps;

	/**
	 * Manage the flow of the program (start, stop, step)
	 * 
	 * @throws InterruptedException
	 * 		see Object.wait( )
	 */
	public synchronized void manage( ) throws InterruptedException {
		while ( ! run )					// if not run, block and wait to run again
			wait( );

		if ( steps ) run = false;		// if step, the next iteration should pause
	}

	/**
	 * Make the system run
	 */
	public synchronized void start( ) {
       run = true;
       steps = false;
       notify( );
    }

	/**
	 * Stop the system
	 */
    public synchronized void stop( ) {
    	run = false;
    	steps = false;
     }

    /**
     * Make a step and stop
     */
    public synchronized void step( ) {
    	run = true;
        steps = true;
        notify( );
    }
}