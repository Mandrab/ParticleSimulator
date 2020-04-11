package jpf.view;

import gov.nasa.jpf.vm.Verify;
import jpf.controller.Controller;
/**
 * This class represent View thread.
 * It contains a method run to execute view. 
 * The view is not real for this reason we used the JPF switch
 * to be able to execute start and stop
 * (simulating them as if they were user input) with a simple 
 * click while while and a generation of random numbers.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class ViewThread extends Thread {
	
	private Controller controller;
	
	public ViewThread( Controller controller ) {
		this.controller = controller;
	}

	public void run( ) {

		for ( int i = 0; i < 2; i++ ) {
			switch ( Verify.random( 1 ) ) {
				case 0:
					controller.start( );
				case 1:
					controller.stop( );
			}
		}
		
		controller.start( );
	}
}
