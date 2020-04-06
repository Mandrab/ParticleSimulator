package main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;
import java.util.logging.Level;

import gov.nasa.jpf.vm.Verify;

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

	private Body[] bodies;		// bodies in the field
	private Function<Integer, int[]> indexesSupplier;

	private Body[] conflictArray;

	private double virtualTime;

	public Simulator( Boundary bounds ) {
		this.bounds = bounds;
		this.conflictArray = new Body[ CONFLICT_ARRAY_SIZE ];
	}

	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier ) {

		thread = new Thread( ( ) -> {

			Verify.beginAtomic( );

			final int bodiesCount = bodies.length;

			final int[] indexes = indexesSupplier.apply( bodiesCount );
			final int indexesCount = indexes.length;

			int conflictArrayIdx = 0;

        	Body firstBall, secondBall;

        	Verify.endAtomic( );

	        for ( int step = 0; step < nSteps; step++ ) {			// loop for number of iteration

	        	Verify.beginAtomic( );

	        	// compute new bodies positions
	        	for ( int i = 0; i < indexesCount; i++ )
		        	bodies[ indexes[ i ] ].updatePos( INCREMENT_TIME );

	        	Verify.endAtomic( );

	        	try {
	        		firstBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
	        	
	        	Verify.beginAtomic( );
	        	
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
								
								Verify.endAtomic( );
				        			firstBall.locked( );
				        		Verify.beginAtomic( );
				        		
				        		for ( int k = 0; k < conflictArrayIdx; k++ ) {
						        	secondBall = conflictArray[ k ];
						        	
						        	Verify.endAtomic( );
						        		secondBall.locked( );
						        	Verify.beginAtomic( );

						    		Body.solveCollision( firstBall, secondBall );
						    		
						    		Verify.endAtomic( );
						    			secondBall.unlocked( );
						    		Verify.beginAtomic( );
								}
				        		
				        		Verify.endAtomic( );
				        			firstBall.unlocked( );
				        		Verify.beginAtomic( );

				        		conflictArray[ 0 ] = secondBall;
				        		conflictArrayIdx = 1;
				        	}
						}
					}

					if ( conflictArrayIdx > 0 ) {
						Verify.endAtomic( );
							firstBall.locked( );
						Verify.beginAtomic( );

				        for ( int k = 0; k < conflictArrayIdx; k++ ) {
				        	secondBall = conflictArray[ k ];
				        	Verify.endAtomic( );
				        		secondBall.locked( );
				        	Verify.beginAtomic( );

				    		Body.solveCollision( firstBall, secondBall );

				    		Verify.endAtomic( );
				    			secondBall.unlocked( );
				    		Verify.beginAtomic( );
						}

				        Verify.endAtomic( );
				        	firstBall.unlocked( );
				        Verify.beginAtomic( );
				        conflictArrayIdx = 0;
			        }
		        }
	        	
	        	Verify.endAtomic( );

			    try {
			    	secondBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
			    
			    Verify.beginAtomic( );

			    // check boundaries
			    for ( int i = 0; i < indexesCount; i++ )
	        		bodies[ indexes[ i ] ].checkAndSolveBoundaryCollision( bounds );

			    virtualTime += INCREMENT_TIME;
			    
			    Verify.endAtomic( );
	        }
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

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
