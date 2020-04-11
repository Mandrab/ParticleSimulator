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
 *
 */
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

    public Controller( ) {

    	nBodies = 5000;			// default bodies quantity
    	nSteps = 5000;			// default step
    	nSimulators = SimulatorsPoolBuilder.getOptimizedNum( nBodies, true );
    	launchGui = false;		// launch cli by default
	}
    
    public void initialize( ) {
    	model = new Model( );
    	model.initialize( nBodies, nSimulators );
    }

    public void run( ) {

    	logger.log( Level.INFO, "Simulation will evaluate \t" + nBodies + " bodies" );
    	logger.log( Level.INFO, "Simulation will run for \t" + nSteps + " steps" );
    	if ( launchGui ) logger.log( Level.INFO, "Simulation will run in \t\tGUI mode\n" );
    	else logger.log( Level.INFO, "Simulation will run in \t\tCLI mode\n" );

    	if ( launchGui ) viewUpdater.start( );

    	long startTime = System.currentTimeMillis( );

    	try {
    		model.execute( nSteps );
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }

    	long stopTime = System.currentTimeMillis( );

    	logger.log( Level.INFO, "Elapsed time " + ( stopTime - startTime ) );
    }

    public void start( ) {
    	model.start( );
    }

    public void stop( ) {
    	model.stop( );
    }
    
    public void step( ) {
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