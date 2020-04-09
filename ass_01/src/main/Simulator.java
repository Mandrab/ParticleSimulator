package main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;
import java.util.logging.Level;

import main.Body;
import main.Boundary;
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

	private Boundary bounds;

	private Body[] bodies;		// bodies in the field
	private Function<Integer, int[]> indexesSupplier;

	private Body[] conflictArray;

	private double virtualTime;
	private int mystep;
	private int step;

	public Simulator( Boundary bounds ) {
		this.bounds = bounds;
		this.conflictArray = new Body[ CONFLICT_ARRAY_SIZE ];
		this.mystep = 0;
		this.step = 0;
	}

	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier, int iteration ) {
		if( iteration != 1 ) {
			this.mystep = nSteps;
		}
		
		System.out.println("sono in start");
		
		thread = new Thread( ( ) -> {

			final int bodiesCount = bodies.length;

			final int[] indexes = indexesSupplier.apply( bodiesCount );
			final int indexesCount = indexes.length;

			int conflictArrayIdx = 0;

        	Body firstBall, secondBall;
        	
        	while( step < nSteps ) {
        		System.out.println("Step " + step + " di " + nSteps);
		      for ( ; step < this.mystep ; step++ ) {			// loop for number of iteration
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
		        	
		        	//i thread si fermano gia' in barriera
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
	        } 
		});

		thread.start( );
	}

	public void setWorkspace( Body[] bodies, Function<Integer, int[]> indexesSupplier ) {
		this.bodies = bodies;
		this.indexesSupplier = indexesSupplier;
	}
	
	public double getVirtualTime( ) {
		return virtualTime;
	}
	
	public void setStep( int iteration ) {
		if( iteration == 1 ) {
			this.mystep = step + 1;
		} 
	}

	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
