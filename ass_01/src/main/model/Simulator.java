package main.model;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
/**
 * This class is the class that deals with resolving the position update, 
 * solveCollison and checkandsolveBoundaryCollision.
 * It contains the start method, the main method for creating the competition
 * that it performs for the number of steps taken as input.
 * Inside it creates a cyclic barrier and, for each body, it checks collisions; 
 * if there are, it takes the lock on both bodies and resolves them otherwise it goes on 
 * and awaits on barrier waiting for all the Simulators. 
 * The last one will break the barrier by allowing others to move forward. 
 * Then boundary collisions are checked and resolved.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class Simulator {

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
