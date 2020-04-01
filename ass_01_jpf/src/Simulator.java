import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Simulator {

	private List<Body> bodies;		// bodies in the field
	private CyclicBarrier barrier;
	
	public Simulator( ) {
		bodies = new ArrayList<Body>( );
	}

	public void start( int nSteps, CyclicBarrier barrier ) {
		
		this.barrier = barrier;

		new Thread( new Executor( nSteps ) ).start( );
	}
	
	public void setBodies( List<Body> bodies ) {
		this.bodies = bodies;
	}
	
	private class Executor implements Runnable {

		private final int steps;

		public Executor( int nSteps ) {
			steps = nSteps;
		}

		@Override
		public void run( ) {
			/* simulation loop */
	        for ( int step = 0; step < steps; step++ ) {

	        	/* NON CRITICAL SECTION */

	        	try {
					barrier.await( );
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}

	        	/* START OF CRITICAL SECTION */

			    for ( int i = 0; i < bodies.size() - 1; i++ ) {

			    	Body b1 = bodies.get(i);

			    	synchronized ( b1 ) {

				        for ( int j = i + 1; j < bodies.size(); j++ ) {

				        	Body b2 = bodies.get(j);
				        	synchronized ( b2 ) {
								
							}
				        }
			    	}
		        }
			    
			    /* END OF CRITICAL SECTION */
	        }
		}
	}
}
