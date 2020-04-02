package main;

import java.util.ArrayList;
import java.util.List;
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
	private List<Body> bodies;		// bodies in the field
	private int startIdx;
	private int quantity;
	
	public Simulator( Boundary bounds ) {
		this.bodies = new ArrayList<Body>( );
		this.bounds = bounds;
	}

	public void start( int nSteps, CyclicBarrier barrier ) {

		thread = new Thread( ( ) -> {

	        /* simulation loop */
	        for ( int step = 0; step < nSteps; step++ ) {
	        	
	        	/* compute bodies new pos */
	        	for ( int i = startIdx; i < quantity; i++ )
	        		bodies.get( i ).updatePos( INCREMENT_TIME );

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

	        	// check collisions
	        	Body subjectBall, firstBall, secondBall;
	        	int realIdx;

			    for ( int i = startIdx; i < quantity; i++ ) {
			    	subjectBall = bodies.get( i );

					for (int j = i + 1; j < bodiesSize; j++ ) {
						realIdx = j % bodiesSize;

			        	if ( i < realIdx ) {
			        		firstBall = subjectBall;
				        	secondBall = bodies.get( realIdx );
			        	} else {
			        		firstBall = bodies.get( realIdx );
			        		secondBall = subjectBall;
			        	}
			        	synchronized ( firstBall ) {
				        	synchronized ( secondBall ) {
								if ( firstBall.collideWith( secondBall ) ) {
									Body.solveCollision( firstBall, secondBall );
								}
							}
			        	}
					}
		        }

			    // check boundaries
			    for ( int i = startIdx; i < quantity; i++ )
	        		bodies.get( i ).checkAndSolveBoundaryCollision( bounds );
	        }
		} );

		thread.start( );
	}

	public void setBodies( List<Body> bodies, int startIdx, int quantity ) {
		this.bodiesSize = bodies.size( );
		this.bodies = bodies;
		this.startIdx = startIdx % bodiesSize;
		this.quantity = quantity;
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
