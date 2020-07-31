package main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import main.controller.Controller;
/**
 * Class that contains the main method.
 * It has two modes, in case it has been set on the command line
 * the "-gui" flag is launched in the GUI mode, if not, no.
 * From the command line the bodies, steps and number of simulators can also be specified.
 * 
 * @author Baldini Paolo, Battistini Ylenia 
 */
public class BodiesSimulator {

	/**
	 * Main method to start the system 
	 * 
	 * @param args
	 * 		contains optional flags such as: -gui, -bodies, -steps, -simulators
	 */
	public static void main( String[] args ) {

    	List<String> argsList = Arrays.asList( args );
    	Controller controller = new Controller( );

    	// read flags for run options (#simulator, #steps, #bodies)
    	getValueOf( argsList, "-bodies" ).ifPresent( value -> controller.setBodiesCount( Integer.parseInt( value ) ) );
    	getValueOf( argsList, "-steps" ).ifPresent( value -> controller.setSteps( Integer.parseInt( value ) ) );
    	getValueOf( argsList, "-simulators" ).ifPresent( value -> controller.setSimulators( Integer.parseInt( value ) ) );

    	// initialize the controller
    	controller.initialize( );
    	
    	// eventually start system with GUI
    	boolean launchGui = argsList.stream( ).anyMatch( s -> s.equals( "-gui" ) );
    	if ( launchGui ) {
    		controller.setGraphicMode( );	// set to start GUI
    	} else {
    		controller.start( );			// set the system to start at execute call
    	}

    	// run the system
    	controller.run( );
    }

	/**
	 * Get values from list of strings (searching the key)
	 * 
	 * @param list
	 * 		the list in which search
	 * @param name
	 * 		the key string
	 * 
	 * @return
	 * 		the optional value (if found)
	 */
    private static Optional<String> getValueOf( List<String> list, String name ) {

    	Iterator<String> listIterator = list.iterator( );

    	while ( listIterator.hasNext( ) ) {

    		if ( listIterator.next( ).equals( name ) && listIterator.hasNext( ) )
    			return Optional.of( listIterator.next( ) );
    	}

    	return Optional.empty( );
    }
}
