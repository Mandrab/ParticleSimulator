package jpf.view;

import gov.nasa.jpf.vm.Verify;
import jpf.controller.Controller;

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
