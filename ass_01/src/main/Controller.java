package main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import main.Controller;
import main.GlobalLogger;
import main.Model;
import main.View;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author Baldini Paolo, Battstini Ylenia
 */
public class Controller {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private final long REFRESH_RATE = 15;

	private View view;
	private ViewUpdater viewUpdater;
	private Model model;

	private int nBodies;
	private int nSteps;
	private int iteration;
	private boolean launchGui;
	
	private boolean run;
	private boolean terminated;

    public static void main( String[] args ) throws InterruptedException {

    	List<String> argsList = Arrays.asList( args );
    	Controller controller = new Controller( );

    	getValueOf( argsList, "-bodies" ).ifPresent( value -> controller.setBodiesCount( Integer.parseInt( value ) ) );
    	getValueOf( argsList, "-steps" ).ifPresent( value -> controller.setSteps( Integer.parseInt( value ) ) );
    	
    	boolean launchGui = argsList.stream( ).anyMatch( s -> s.equals( "-gui" ) );
    	launchGui=true;
    	if ( launchGui ) {
    		controller.setGraphicMode( true );System.out.println("fsdvbsfdb " + controller.launchGui );
    		controller.run = false;
    	} else controller.run = true;

    	controller.run( );
    }

    private static Optional<String> getValueOf( List<String> list, String name ) {

    	Iterator<String> listIterator = list.iterator( );

    	while ( listIterator.hasNext( ) ) {

    		if ( listIterator.next( ).equals( name ) && listIterator.hasNext( ) )
    			return Optional.of( listIterator.next( ) );
    	}

    	return Optional.empty( );
    }
    
    public Controller( ) {

    	nBodies = 1000;			// default bodies quantity
    	nSteps = 1000;			// default step
    	launchGui = false;		// launch cli by default

    	// TODO : debug utility -> delete
    	//final int nBodies = 100;
    	//final int nBodies = 5000;

    	//final int nSteps = 500;
    	//final int nSteps = 5000;
	}

    public synchronized void run( ) throws InterruptedException {

    	logger.log( Level.INFO, "Simulation will evaluate \t" + nBodies + " bodies" );
    	logger.log( Level.INFO, "Simulation will run for \t" + nSteps + " steps" );
    	if ( launchGui ) logger.log( Level.INFO, "Simulation will run in \t\tGUI mode\n" );
    	else logger.log( Level.INFO, "Simulation will run in \t\tCLI mode\n" );

    	model = new Model( );
    	model.initialize( nBodies );
    	
    	iteration = 0;

    	if ( launchGui ) viewUpdater.start( );

    	while ( ! run ) Thread.sleep( REFRESH_RATE );

    	long startTime = System.currentTimeMillis( );

    	try { 
    		model.execute( nSteps );
    	} catch ( InterruptedException e ) { e.printStackTrace( ); }

    	long stopTime = System.currentTimeMillis( );
    	
    	terminated = true;

        logger.log( Level.INFO, "Elapsed time " + ( stopTime - startTime ) );
        view.terminate();
    }
    
    public void start( ) {
    	run = true;
    	model.start( );
    	iteration = this.nSteps;
    }
    
    public void step( ) {
    	run = true;
    	iteration = 1;
    	model.step( iteration );
    }
    
    public void pause( ) {
    	run = false;
    	model.pause( );
    }
    
    public void setBodiesCount( int nBodies ) {
    	this.nBodies = nBodies;
    }

    public void setSteps( int nSteps ) {
    	this.nSteps = nSteps;
    }
    
    public void setGraphicMode( boolean launchGui ) {
    	this.launchGui = launchGui;

    	view = new View( this );
    	viewUpdater = new ViewUpdater( );
    }

    private class ViewUpdater extends Thread {

    	@Override
    	public void run( ) {

			try {
				do {
		    		while( run && model.isRunning( ) ) {
		
		    			view.updateView( model.getState( ) );

						Thread.sleep( REFRESH_RATE );
		    		}
	
		    		Thread.sleep( REFRESH_RATE );
				} while ( ! terminated );
			} catch ( InterruptedException ignored ) { }
    	}
    }
}