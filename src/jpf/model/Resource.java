package jpf.model;

import java.util.Arrays;
import java.util.List;

import gov.nasa.jpf.vm.Verify;
import jpf.model.simulators.Simulator;

/**
 * Manages the control flow of the system (i.e., start, stop, pause)
 * 
 * @author baldini paolo, battistini ylenia
 */
public class Resource {
	
	private Model model;

	private boolean run ;
	private boolean steps;
	
	public Resource( Model model ) {
		this.model = model;
		run = false;
		steps = false;
	}

	/**
	 * Manages control flow of the concurrent part (i.e., start, stop, step)
	 * 
	 * @throws InterruptedException
	 * 		same cause as Object.wait( )
	 */
	public synchronized void manage( ) throws InterruptedException {
		// if not run, block and wait to run again
		while ( ! run ) {

			// verify without being interrupted by view or view-updater threads
			Verify.beginAtomic( );
			List<Simulator> states = Arrays.asList( model.getSimulators( ) );
			
			// check that only a thread (the one executing this runnable) is in run state
			assert states.stream( ).filter( s -> s.getState( ) == Thread.State.RUNNABLE ).count( ) == 1 : "There should be only a thread running this runnable (the last one to come to the barrier)";
	    	
			// check that the others threads are in waiting state
			assert states.stream( ).filter( s -> s.getState( ) == Thread.State.WAITING ).count( ) == states.size( ) -1 : "Threads are not waiting on stop!";
	    	
			// check that all thread are at the same iteration
			assert states.stream( ).allMatch( s -> s.getIteration( ) == states.get( 0 ).getIteration( ) ) : "Thread isn't at the same iteration of others!";
	    	Verify.endAtomic( );

			wait( );
		}

		// if step the next cycle should pause
		if ( steps ) run = false;
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
