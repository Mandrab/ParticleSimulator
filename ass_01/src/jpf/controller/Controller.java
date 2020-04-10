package jpf.controller;

import gov.nasa.jpf.vm.Verify;
import jpf.model.Model;
import jpf.view.ViewThread;

public class Controller {

	private static final int N_BODIES = 5;
	private static final int N_STEPS = 2;
	private static final int N_STEPS_VIEW = 2;
	private static final int N_SIMULATOR = 2;

	private Model model;

    public void run( boolean gui ) {

    	model = new Model( );
    	int steps = N_STEPS;

    	if ( gui ) {
    		Verify.println( "simulators: " + N_SIMULATOR + " steps: " + N_STEPS_VIEW );

    		model.initialize( N_SIMULATOR );
    		model.start( );

    		new ViewThread( this ).start( );
    		new ViewUpdater( ).start( );

    		steps = N_STEPS_VIEW;
    	} else {
    		Verify.println( "bodies: " + N_BODIES + " simulators: " + N_SIMULATOR + " steps: " + steps );

    		model.initialize( N_BODIES, N_SIMULATOR );
    		model.start( );
    	}

    	try {
    		model.execute( steps );
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }
    }

    public void start( ) {
    	model.start( );
    }

    public void stop( ) {
    	model.stop( );
    }

    private class ViewUpdater extends Thread {

    	@Override
    	public void run( ) {

			/*
			 * Thank to JPF there is no need of the while cycle. Indeed, it will
			 * try all the possible combination of this action
			 */
			assert model.getState( ) != null : "model state should not be null";
			assert model.getState( ) != null : "model state should not be null";
    	}
    }
}