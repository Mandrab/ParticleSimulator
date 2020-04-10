package jpf;

import java.util.Arrays;
import java.util.List;

import gov.nasa.jpf.vm.Verify;
import jpf.model.Model;
import jpf.model.simulators.Simulator;

public class Resource {
	
	private Model model;

	private boolean run ;
	private boolean steps;
	
	public Resource( Model model ) {
		this.model = model;
		run = false;
		steps = false;
	}

	public synchronized void manage( ) throws InterruptedException {
		while ( ! run ) {
			Verify.beginAtomic( );
			List<Simulator> states = Arrays.asList( model.getSimulators( ) );
			assert states.stream( ).filter( s -> s.getState( ) == Thread.State.RUNNABLE ).count( ) == 1 : "There should be only a thread running this runnable (the last one to come to the barrier)";
	    	assert states.stream( ).filter( s -> s.getState( ) == Thread.State.WAITING ).count( ) == states.size( ) -1 : "Threads are not waiting on stop!";
	    	assert states.stream( ).allMatch( s -> s.getIteration( ) == states.get( 0 ).getIteration( ) ) : "Thread isn't at the same iteration of others!";
	    	Verify.endAtomic( );

			wait( );
		}

		if ( steps ) run = false;
	}

	public synchronized void start( ) {
       run = true;
       steps = false;
       notify( );
    }

    public synchronized void stop( ) {
    	run = false;
    	steps = false;
     }
    
    public synchronized void step( ) {
    	run = true;
        steps = true;
        notify( );
    }
}
