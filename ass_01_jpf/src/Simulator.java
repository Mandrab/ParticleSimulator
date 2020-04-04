import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import main.Body;

public class Simulator {

	private Body[] bodies;		// bodies in the field
	private CyclicBarrier barrier;
	final Body[] conflictArray= new Body[20];
	private int startIdx;
	private int stopIdx;
	private int bodiesSize;
	
	
	public Simulator( ) {
		
	}

	public void start( int nSteps, CyclicBarrier barrier ) {
		
		this.barrier = barrier;

		new Thread( new Executor( nSteps ) ).start( );
	}
	
	public void setBodies( Body[] bodies, int startIdx, int quantity ) {
		this.bodies = bodies;
		this.bodiesSize = bodies.length;		
		this.startIdx = startIdx;
		this.stopIdx = startIdx + quantity;
	}
	
	private class Executor implements Runnable {

		private final int steps;

		public Executor( int nSteps ) {
			steps = nSteps;
		}

		@Override
		public void run( ) {
			/* simulation loop */
			for ( int step = 0; step < steps; step++ ) {			// loop for number of iteration

	        	/* NON CRITICAL SECTION */

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
	        	
	        	Body firstBall, secondBall;

			    for ( int i = startIdx; i < stopIdx; i++ ) {
			    	firstBall = bodies[ i ];
			    	int conflictArrayIdx = 0;

					for ( int j = i + 1; j < bodiesSize; j++ ) {
				        secondBall = bodies[ j ];

				        if ( firstBall.collideWith( secondBall ) ) {
				        	conflictArray[ conflictArrayIdx ] = secondBall;
						}
					}
					/* START OF CRITICAL SECTION */

					if ( conflictArrayIdx != 0 ) {
						firstBall.locked( );
					        for ( int k = 0; k < conflictArrayIdx; k++ ) {
					        	secondBall = conflictArray[ conflictArrayIdx ];
					        	secondBall.locked( );
						    	secondBall.unlocked( );
							}
					        firstBall.unlocked( );
			        }
		        }

			    try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
			    /* END OF CRITICAL SECTION */
			    
			    
	        /*for ( int step = 0; step < steps; step++ ) {

	        	

	        	try {
					barrier.await( );
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}

	        	

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
		     }*/
			    
			    
	        }
		}
	}
}
