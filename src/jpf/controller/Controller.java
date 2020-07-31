package jpf.controller;

import gov.nasa.jpf.vm.Verify;
import jpf.model.Model;
import jpf.view.ViewThread;

/**
 * Controller of MVC pattern
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Controller {

	private static final int N_BODIES = 5;
	private static final int N_STEPS = 2;
	private static final int N_STEPS_VIEW = 5;
	private static final int N_SIMULATOR = 2;

	private Model model;

	/**
	 * Run test system
	 * 
	 * @param gui
	 * 		true: run view test
	 * 		false: run basic test 
	 */
    public void run( boolean gui ) {

    	model = new Model( );
    	int steps = N_STEPS;

    	if ( gui ) {
    		Verify.println( "simulators: " + N_SIMULATOR + " steps: " + N_STEPS_VIEW );

    		// initialize model (create simulator pool) and set to immediately start it
    		// when execute is called
    		model.initialize( N_SIMULATOR );
    		model.start( );

    		// start view's simulator thread and view updater thread
    		new ViewThread( this ).start( );
    		new ViewUpdater( ).start( );

    		// run for different number of steps
    		steps = N_STEPS_VIEW;
    	} else {
    		Verify.println( "bodies: " + N_BODIES + " simulators: " + N_SIMULATOR + " steps: " + steps );

    		// initialize model (create simulator pool) and set to immediately start it
    		// when execute is called
    		model.initialize( N_BODIES, N_SIMULATOR );
    		model.start( );
    	}

    	// run the system effectively
    	try {
    		model.execute( steps );
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }
    }

    /**
	 * Make the system run
	 */
    public void start( ) {
    	model.start( );
    }

    /**
	 * Stop the system
	 */
    public void stop( ) {
    	model.stop( );
    }

    /**
     * Normally defines the refresh rate; now it only assert that the required data are not null
     * 
     * @author Baldini Paolo, Battistini Ylenia
     */
    private class ViewUpdater extends Thread {

    	@Override
    	public void run( ) {

			// Check that the model state is not null
			// Thank to JPF there is no need of the while cycle. Indeed, it will
			// try all the possible combination of this actions
			assert model.getState( ) != null : "model state should not be null";
			assert model.getState( ) != null : "model state should not be null";
    	}
    }
}