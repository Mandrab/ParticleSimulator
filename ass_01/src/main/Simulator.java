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
	
	private int myBodiesNum;
	private int otherBodiesNum;
	private int bodiesSize;
	private List<Body> bodies;		// bodies in the field
	private int startIdx;
	private int stopIdx;
	
	public Simulator( Boundary bounds ) {
		this.bodies = new ArrayList<Body>( );
		this.bounds = bounds;
	}

	public void start( int nSteps, CyclicBarrier barrier ) {

		thread = new Thread( ( ) -> {

	        for ( int step = 0; step < nSteps; step++ ) {			// loop for number of iteration
	        	
	        	/* compute bodies new pos */
	        	for ( int i = startIdx; i < stopIdx; i++ )
	        		bodies.get( i ).updatePos( INCREMENT_TIME );

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

	        	// check collisions
	        	Body firstBall, secondBall;

			    for ( int i = startIdx; i < stopIdx; i++ ) {
			    	firstBall = bodies.get( i );
			    	
					for (int j = i + 1; j < bodiesSize; j++ ) {
			        	secondBall = bodies.get( j );

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
			    for ( int i = startIdx; i < stopIdx; i++ )
	        		bodies.get( i ).checkAndSolveBoundaryCollision( bounds );
	        }
		} );

		thread.start( );
	}

	public void setBodies( List<Body> bodies, int startIdx, int quantity ) {
		this.bodiesSize = bodies.size( );
		this.bodies = bodies;
		this.startIdx = startIdx;
		this.stopIdx = startIdx + quantity;
		this.myBodiesNum = quantity;
		
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
