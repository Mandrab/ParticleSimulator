package jpf.model.simulators;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class StartStopSimulator implements Simulator {

	private Thread thread;

	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier ) {

		thread = new Thread( ( ) -> {

	        for ( int step = 0; step < nSteps; step++ ) {

	        	// UPDATE POSITIONS
	        	
	        	try {
	        		firstBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
   	
	        	// SOLVE CONFLICTS

			    try {
			    	secondBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
			    
			    // SOLVE BOUNDARY COLLISIONS
			    
	        }
		} );

		thread.start( );
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
