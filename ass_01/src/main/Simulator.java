package main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;

/*
 * possibilita'� di eseguire il calcolo del solveCollision fuori sezione critica
 * 	-> check variazione nelle prestazioni
 */
public class Simulator {
	
	private static final GlobalLogger logger = GlobalLogger.get( );

	private static final double INCREMENT_TIME = 0.1;
	private static final int CONFLICT_ARRAY_SIZE = 20;

	private Thread thread;

	private Boundary bounds;

	private int bodiesSize;
	private Body[] bodies;		// bodies in the field
	private int[] indexes;
	
	private Body[] conflictArray;

	private double virtualTime;

	public Simulator( Boundary bounds ) {
		this.bounds = bounds;
		this.conflictArray = new Body[ CONFLICT_ARRAY_SIZE ];
	}

	public void start( int nSteps, CyclicBarrier barrier ) {
		
		thread = new Thread( ( ) -> {
			
			int conflictArrayIdx = 0;

	        for ( int step = 0; step < nSteps; step++ ) {			// loop for number of iteration

	        	// compute bodies new pos
	        	for ( int i = 0; i < indexes.length; i++ )
		        	bodies[ indexes[ i ] ].updatePos( INCREMENT_TIME );

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

	        	// check collisions
	        	Body firstBall, secondBall;

	        	for ( Integer i : indexes ) {
			    	firstBall = bodies[ i ];

					for ( int j = i + 1; j < bodiesSize; j++ ) {
				        secondBall = bodies[ j ];

				        if ( firstBall.collideWith( secondBall ) ) {
				        	try {
				        		conflictArray[ conflictArrayIdx++ ] = secondBall;
				        	} catch ( ArrayIndexOutOfBoundsException e ) {
				        		logger.log( Level.WARNING, e.toString( ) );

								conflictArrayIdx--;
				        		firstBall.locked( );
				        		for ( int k = 0; k < conflictArrayIdx; k++ ) {
						        	secondBall = conflictArray[ k ];
						        	secondBall.locked( );

						    		Body.solveCollision( firstBall, secondBall );

						    		secondBall.unlocked( );
								}
				        		firstBall.unlocked( );
				        		conflictArrayIdx = 0;
				        	}
						}
					}

					if ( conflictArrayIdx > 0 ) {
						firstBall.locked( );

				        for ( int k = 0; k < conflictArrayIdx; k++ ) {
				        	secondBall = conflictArray[ k ];
				        	secondBall.locked( );

				    		Body.solveCollision( firstBall, secondBall );

				    		secondBall.unlocked( );
						}

				        firstBall.unlocked( );
				        conflictArrayIdx = 0;
			        }
		        }

			    try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

			    // check boundaries
			    for ( int i = 0; i < indexes.length; i++ )
	        		bodies[ indexes[ i ] ].checkAndSolveBoundaryCollision( bounds );

			    virtualTime += INCREMENT_TIME;
	        }
		} );

		thread.start( );
	}

	public void setBodies( Body[] bodies, int[] indexes ) {
		this.bodiesSize = bodies.length;
		this.bodies = bodies;
		this.indexes = indexes;
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}

	public double getVirtualTime( ) {
		return virtualTime;
	}
}
