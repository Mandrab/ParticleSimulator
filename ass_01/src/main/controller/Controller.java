package main.controller;

import java.util.Optional;
import java.util.logging.Level;

import main.builders.SimulatorsPoolBuilder;
import main.model.Model;
import main.view.View;

public class Controller {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private final long REFRESH_RATE = 10;

	private View view;
	private ViewUpdater viewUpdater;
	private Model model;

	private int nBodies;
	private int nSteps;
	private int nSimulators;
	private boolean launchGui;
	
	private boolean run;

    public Controller( ) {

    	nBodies = 1000;			// default bodies quantity
    	nSteps = 1000;			// default step
    	nSimulators = SimulatorsPoolBuilder.getOptimizedNum( nBodies, true );
    	launchGui = false;		// launch cli by default
	}
    
    public void initialize( ) {
    	model = new Model( );
    	model.initialize( nBodies, nSimulators );
    }

    public void run( ) {

    	run = true;

    	logger.log( Level.INFO, "Simulation will evaluate \t" + nBodies + " bodies" );
    	logger.log( Level.INFO, "Simulation will run for \t" + nSteps + " steps" );
    	if ( launchGui ) logger.log( Level.INFO, "Simulation will run in \t\tGUI mode\n" );
    	else logger.log( Level.INFO, "Simulation will run in \t\tCLI mode\n" );

    	if ( launchGui ) viewUpdater.start( );

    	long startTime = System.currentTimeMillis( );

    	try {
    		if ( launchGui )
    			model.execute( nSteps, Optional.of( ( ) -> logger.log( Level.INFO,
    				"Elapsed time " + ( System.currentTimeMillis( ) - startTime ) ) ) );
    		else model.execute( nSteps, Optional.empty( ) );
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }

    	logger.log( Level.INFO, "Elapsed time " + ( System.currentTimeMillis( ) - startTime ) );
    }

    public void start( ) {
    	if ( ! run ) run( );
    	model.start( );
    }

    public void stop( ) {
    	model.stop( );
    }
    
    public void step( ) {
    	if ( ! run ) run( );
    	model.step( );
    }
    
    public void setBodiesCount( int nBodies ) {
    	this.nBodies = nBodies;
    }

    public void setSteps( int nSteps ) {
    	this.nSteps = nSteps;
    }
    
    public void setSimulators( int nSimulators ) {
    	this.nSimulators = nSimulators;
    }

    public void setGraphicMode( ) {
    	launchGui = true;

    	view = new View( this );
    	view.updateView( model.getState( ) );
    	viewUpdater = new ViewUpdater( );
    }

    private class ViewUpdater extends Thread {

    	@Override
    	public void run( ) {

			try {
	    		while( ! model.isTerminated( ) ) {

	    			view.updateView( model.getState( ) );

					Thread.sleep( REFRESH_RATE );
	    		}
			} catch ( InterruptedException ignored ) { }
    	}
    }
}