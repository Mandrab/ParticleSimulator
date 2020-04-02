package main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/*
 * possibilità di eseguire il calcolo del solveCollision fuori sezione critica
 * 	-> check variazione nelle prestazioni
 */
public class Simulator {

	private static final double INCREMENT_TIME = 0.1;
	
	private Thread thread;

	private Boundary bounds;

	private int bodiesSize;
	private Body[] bodies;		// bodies in the field
	private int startIdx;
	private int stopIdx;
	
	public Simulator( Boundary bounds ) {
		this.bounds = bounds;
	}

	public void start( int nSteps, CyclicBarrier barrier ) {

		thread = new Thread( ( ) -> {
			Body[] x = new Body[20];

	        for ( int step = 0; step < nSteps; step++ ) {			// loop for number of iteration
	        	
	        	// compute bodies new pos
	        	for ( int i = startIdx; i < stopIdx; i++ )
	        		bodies[ i ].updatePos( INCREMENT_TIME );

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

	        	// check collisions
	        	Body firstBall, secondBall;

			    /*for ( int i = startIdx; i < stopIdx; i++ ) {
			    	firstBall = bodies[ i ];
			    	int xi = 0;
					for ( int j = i + 1; j < bodiesSize; j++ ) {
				        secondBall = bodies[ j ];
				        
				        if ( firstBall.collideWith( secondBall ) ) {
				        	synchronized ( firstBall ) {
								synchronized ( secondBall ) {
						    		Body.solveCollision( firstBall, secondBall );
					        	}
							}
						}
					}
		        }*/

			    for ( int i = startIdx; i < stopIdx; i++ ) {
			    	firstBall = bodies[ i ];
			    	int xi = 0;
					for ( int j = i + 1; j < bodiesSize; j++ ) {
				        secondBall = bodies[ j ];

				        if ( firstBall.collideWith( secondBall ) ) {
				        	x[ xi ] = secondBall;
						}
					}

					if ( xi != 0 ) {
			        	synchronized ( firstBall ) {
					        for ( int k = 0; k < xi; k++ ) {
					        	secondBall = x[ xi ];
								synchronized ( secondBall ) {
						    		Body.solveCollision( firstBall, secondBall );
					        	}
							}
				        }
			        }
		        }

			    // check boundaries
			    for ( int i = startIdx; i < stopIdx; i++ )
	        		bodies[ i ].checkAndSolveBoundaryCollision( bounds );
			    
			    try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}			    
	        }
		} );

		thread.start( );
	}

	public void setBodies( Body[] bodies, int startIdx, int quantity ) {
		this.bodiesSize = bodies.length;
		this.bodies = bodies;
		this.startIdx = startIdx;
		this.stopIdx = startIdx + quantity;
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
