package main.controller;

import java.util.logging.Level;

import main.GlobalLogger;
import main.builders.SimulatorsPoolBuilder;
import main.model.Model;
import main.view.View;

/**
 * This class implement Controller.
 * This class contains the initialize method to initialize the Model.
 * The run method is the main method of the class because it starts the execution of the program. 
 * It also contains the start stop and step methods with the respective calls to the model and all 
 * the methods for setting the view, the number of bodies and the number of steps.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Controller {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private final long REFRESH_RATE = 10;	// GUI refresh period

	private View view;						// system GUI
	private ViewUpdater viewUpdater;		// a thread to periodically refresh the GUI
	private Model model;					// system model

	private int nBodies;					// number of bodies to run
	private int nSteps;						// number of iterations to run
	private int nSimulators;				// number of simulator/thread to run
	private boolean launchGui;				// flag to know if the GUI should be launched

    public Controller( ) {

    	nBodies = 1000;						// default bodies quantity
    	nSteps = 1000;						// default step
    	nSimulators = SimulatorsPoolBuilder.getOptimizedNum( nBodies, true );
    	launchGui = false;					// launch cli by default
	}

    /**
     * Initialize the controller (needed to run)
     */
    public void initialize( ) {
    	model = new Model( );
    	model.initialize( nBodies, nSimulators );
    }

    /**
     * Effectively start the system
     */
    public void run( ) {

    	logger.log( Level.INFO, "Simulation will evaluate \t" + nBodies + " bodies" );
    	logger.log( Level.INFO, "Simulation will run for \t" + nSteps + " steps" );
    	if ( launchGui ) logger.log( Level.INFO, "Simulation will run in \t\tGUI mode\n" );
    	else logger.log( Level.INFO, "Simulation will run in \t\tCLI mode\n" );

    	if ( launchGui ) viewUpdater.start( );			// if started in GUI mode, launch GUI refresher

    	long startTime = System.currentTimeMillis( );

    	try {
    		model.execute( nSteps );					// execute the simulation
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }

    	long stopTime = System.currentTimeMillis( );

    	logger.log( Level.INFO, "Elapsed time " + ( stopTime - startTime ) );
    }

    /**
     * Start the system (model)
     */
    public void start( ) {
    	model.start( );
    }

    /**
     * Stop the system (model)
     */
    public void stop( ) {
    	model.stop( );
    }

    /**
     * Make a step in the simulation
     */
    public void step( ) {
    	model.step( );
    }

    /**
     * Set number of bodies to simulate
     * 
     * @param nBodies
     * 		the number of bodies
     */
    public void setBodiesCount( int nBodies ) {
    	this.nBodies = nBodies;
    }

    /**
     * Set number of steps to iterate
     * 
     * @param nSteps
     * 		the number of steps
     */
    public void setSteps( int nSteps ) {
    	this.nSteps = nSteps;
    }

    /**
     * Set number of simulators to run
     * 
     * @param nSimulators
     * 		the number of simulators in the simulation
     */
    public void setSimulators( int nSimulators ) {
    	this.nSimulators = nSimulators;
    }

    /**
     * Setup the system to start in graphic mode
     */
    public void setGraphicMode( ) {
    	launchGui = true;

    	view = new View( this );
    	view.updateView( model.getState( ) );
    	viewUpdater = new ViewUpdater( );
    }

    /**
     * A class intended to refresh the GUI periodically
     * 
     * @author Baldini Paolo, Battistini Ylenia
     */
    private class ViewUpdater extends Thread {

    	@Override
    	public void run( ) {

			try {
				// while the system is running, keep refresh
	    		while( ! model.isTerminated( ) ) {

	    			view.updateView( model.getState( ) );

					Thread.sleep( REFRESH_RATE );
	    		}
			} catch ( InterruptedException ignored ) { }
    	}
    }
}