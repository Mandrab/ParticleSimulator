package jpf.model.simulators;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

import gov.nasa.jpf.vm.Verify;
import jpf.model.Body;

/**
 * Basic simulator used in BASIC TEST to check synchronization properties
 * 
 * @author baldini paolo, battistini ylenia
 */
public class BasicSimulator implements Simulator {

	private static final int CONFLICT_ARRAY_SIZE = 20;

	private Thread thread;

	private Body[] bodies;								// list of bodies
	private Function<Integer, int[]> indexesSupplier;	// supplier for my bodies list

	private Body[] conflictArray;						// arrays for store conflicts
	
	private int step;									// actual step (used for test purpose only)

	public BasicSimulator( ) {
		this.conflictArray = new Body[ CONFLICT_ARRAY_SIZE ];
		this.thread = new Thread( );
	}

	/**
	 * Start the execution
	 * 
	 * @param nSteps
	 * 		number of iteration to run
	 * @param firstBarrier
	 * 		first synchronization barrier
	 * @param secondBarrier
	 * 		second synchronization barrier
	 */
	@Override
	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier ) {

		thread = new Thread( ( ) -> {

			Verify.beginAtomic( );
			
			final int bodiesCount = bodies.length;

			final int[] indexes = indexesSupplier.apply( bodiesCount );

			int conflictArrayIdx = 0;

        	Body firstBall, secondBall;

	        for ( step = 0; step < nSteps; step++ ) {			// loop for number of iteration

	        	/*
	        	 * due to the control made in 'BodiesDistributorBuilder', only a  thread can call updatePos on a body.
	        	 * This, combined with the certainty that two thread cannot be respectively in updatePos and 
	        	 * solveCollision section (barriers guaranteed) and that checkAndSolveBoundaryCollision is executed
	        	 * serially by the same thread, guarantee that the updatePos operation cannot have race conditions
	        	 */
	        	// UPDATE POSITIONS

	        	Verify.endAtomic( );

	        	try {
	        		firstBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
   	
	        	Verify.beginAtomic( );

	        	for ( Integer i : indexes ) {
			    	firstBall = bodies[ i ];

					for ( int j = i + 1; j < bodiesCount; j++ ) {
				        secondBall = bodies[ j ];

				        /*
				         * collideWith does not use speed but only position. Furthermore, in this section of
				         * the barriers, there isn't any chance that the position could be changed. Then, check
				         * the collision without take the lock is a safe operation
				         */
				        if ( true /*firstBall.collideWith( secondBall )*/ ) {
				        	conflictArray[ conflictArrayIdx++ ] = secondBall;

				        	/*
				        	 * The code in the try-catch block is the same (except for the empty of the array) as
				        	 * the code below. The number of bodies in the JPF simulation doesn't need the try-catch 
				        	 * block (<< ARRAY_SIZE) and it has been removed due to performance reasons
				        	 */
				        	// TRY CATCH BLOCK
						}
					}

					// empties conflict array
					if ( conflictArrayIdx > 0 ) {
						Verify.endAtomic( );
						firstBall.lock( );
						Verify.beginAtomic( );

				        for ( int k = 0; k < conflictArrayIdx; k++ ) {
				        	secondBall = conflictArray[ k ];
				        	
				        	Verify.endAtomic( );
				        	secondBall.lock( );

				    		Body.solveCollision( firstBall, secondBall );
				    		Verify.beginAtomic( );

				    		secondBall.unlock( );
						}
				        conflictArrayIdx = 0;
				        firstBall.unlock( );
			        }
		        }
	        	Verify.endAtomic( );

			    try {
			    	secondBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}

			    Verify.beginAtomic( );

			    /*
	        	 * due to the control made in 'BodiesDistributorBuilder', only a  thread can call updatePos on a body.
	        	 * This, combined with the certainty that two thread cannot be respectively in checkAndSolveBoundary-
	        	 * Collision and solveCollision section (barriers guaranteed) and that updatePos is executed serially
	        	 * by the same thread, guarantee that the checkAndSolveBoundaryCollision operation cannot have race 
	        	 * conditions
	        	 */
			    // SOLVE BOUNDARY COLLISION
	        }
	        Verify.endAtomic( );
		} );

		thread.start( );
	}

	/**
	 * Sets workspace of the simulator (i.e., bodies and index supplier function)
	 * 
	 * @param bodies
	 * 		list of bodies
	 * @param indexesSupplier
	 * 		index distribution function
	 */
	public void setWorkspace( Body[] bodies, Function<Integer, int[]> indexesSupplier ) {
		this.bodies = bodies;
		this.indexesSupplier = indexesSupplier;
	}
	
	/**
	 * Get the state of the internal thread. Used for test purpose only
	 * 
	 * @return
	 * 		see Thread.getState( )
	 */
	@Override
	public Thread.State getState( ) {
		return thread.getState( );
	}
	
	/**
	 * Get the actual iteration. Used for test purpose only
	 * 
	 * @return
	 * 		the actual iteration's step
	 */
	@Override
	public int getIteration( ) {
		return step;
	}

	/**
	 * Wait the simulation to end
	 * 
	 * @throws InterruptedException
	 * 		see Thread.join( )
	 */
	@Override
	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
