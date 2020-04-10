package jpf;

import gov.nasa.jpf.vm.Verify;
import jpf.controller.Controller;

public class BodiesSimulator {

	public static void main( String[] args ) throws InterruptedException {

		// verify first part of the assignment (i.e., no GUI)
		if ( args.length == 0 ) {		
			Verify.println( "Verifing simulators synchronization" );
	    	new Controller( ).run( false );

	    // verify first part's extension (i.e., the second part of the assignment: the GUI interactions)
	    // need to be started through CLI with flag '-gui'
		} else if ( args[ 0 ].equals( "-gui" ) ) {
			Verify.println( "Verifing GUI interactions" );
			new Controller( ).run( true );
		}
    }
}
