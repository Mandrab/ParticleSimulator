package main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import main.controller.Controller;

public class BodiesSimulator {

	public static void main( String[] args ) throws InterruptedException {

    	List<String> argsList = Arrays.asList( args );
    	Controller controller = new Controller( );

    	getValueOf( argsList, "-bodies" ).ifPresent( value -> controller.setBodiesCount( Integer.parseInt( value ) ) );
    	getValueOf( argsList, "-steps" ).ifPresent( value -> controller.setSteps( Integer.parseInt( value ) ) );
    	getValueOf( argsList, "-simulators" ).ifPresent( value -> controller.setSimulators( Integer.parseInt( value ) ) );

    	controller.initialize( );
    	
    	boolean launchGui = argsList.stream( ).anyMatch( s -> s.equals( "-gui" ) );
    	if ( launchGui ) {
    		controller.setGraphicMode( );
    		controller.stop( );
    	}

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
}
