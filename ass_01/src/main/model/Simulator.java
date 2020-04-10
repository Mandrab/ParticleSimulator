package main.model;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;

import main.GlobalLogger;

/*
 * possibilita' di eseguire il calcolo del solveCollision fuori sezione critica
 * 	-> check variazione nelle prestazioni
 */
public class Simulator {
	
	private static final GlobalLogger logger = GlobalLogger.get( );

	private static final double INCREMENT_TIME = 0.1;
	private static final int CONFLICT_ARRAY_SIZE = 20;

	private Thread thread;
	private AtomicBoolean terminated;

	private Boundary bounds;

	private Body[] bodies;		// bodies in the field
	private Function<Integer, int[]> indexesSupplier;

	private Body[] conflictArray;

	private double virtualTime;

	public Simulator( Boundary bounds ) {
		this.bounds = bounds;
		this.conflictArray = new Body[ CONFLICT_ARRAY_SIZE ];
		this.terminated = new AtomicBoolean( );
	}

	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier ) {

		terminated.set( false );
		
		thread = new Thread( ( ) -> {

			final int bodiesCount = bodies.length;

			final int[] indexes = indexesSupplier.apply( bodiesCount );
			final int indexesCount = indexes.length;

			int conflictArrayIdx = 0;

        	Body firstBall, secondBall;

	        for ( int step = 0; step < nSteps; step++ ) {			// loop for number of iteration

	        	// compute new bodies positions
	        	for ( int i = 0; i < indexesCount; i++ )
		        	bodies[ indexes[ i ] ].updatePos( INCREMENT_TIME );

	        	try {
	        		firstBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

				// check collisions
	        	for ( Integer i : indexes ) {
			    	firstBall = bodies[ i ];

					for ( int j = i + 1; j < bodiesCount; j++ ) {
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

				        		conflictArray[ 0 ] = secondBall;
				        		conflictArrayIdx = 1;
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
			    	secondBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

			    // check boundaries
			    for ( int i = 0; i < indexesCount; i++ )
	        		bodies[ indexes[ i ] ].checkAndSolveBoundaryCollision( bounds );

			    virtualTime += INCREMENT_TIME;
	        }

	        terminated.set( true );
		} );

		thread.start( );
	}

	public void setWorkspace( Body[] bodies, Function<Integer, int[]> indexesSupplier ) {
		this.bodies = bodies;
		this.indexesSupplier = indexesSupplier;
	}

	public double getVirtualTime( ) {
		return virtualTime;
	}

	public boolean isTerminated( ) {
		return terminated.get( );
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
