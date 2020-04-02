package main;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class Model {

	private List<Simulator> simulatorPool;
	private List<Body> bodies;						// bodies in the field
	private Boundary bounds;						// boundary of the field
	
	public Model( ) {
		simulatorPool = new ArrayList<>( );			// create simulator pools as arraylist (faster access than linked et similia)
		
    	bodies = new ArrayList<Body>( );			// create bodies as arraylist (faster access than linked et similia)

		bounds = new Boundary(-1.0,-1.0,1.0,1.0);	// initializing boundary
	}
	
    public void initialize( int nBodies ) {
    	
    	buildBalls( nBodies );						// generate nBodies balls
    	
    	buildSimulators( nBodies );					// create right number of simulators (thread)
    	
    	final int forNum = nBodies < simulatorPool.size( ) ? 1
    			: nBodies / simulatorPool.size( );
    	System.out.println( "Bodies for simulator:\t" + forNum );

    	int simulatorIdx = 0;
        for( int fromIdx = 0; simulatorIdx < simulatorPool.size( ); simulatorIdx++ ) {
        	simulatorPool.get( simulatorIdx ).setBodies( bodies, fromIdx, forNum );
        	fromIdx += forNum;
        }

        if ( nBodies % simulatorPool.size( ) != 0 ) {
        	final int bodiesForLast = forNum + nBodies % simulatorPool.size( );
        	simulatorPool.get( --simulatorIdx ).setBodies( bodies, simulatorIdx * forNum, bodiesForLast );
        	System.out.println( "Bodies for last simulator: " + bodiesForLast );
        }
    }

    public long execute( int nSteps ) {
    	
    	System.out.println( "\nExecuting " + simulatorPool.size( ) + " simulators\n" );

        final CyclicBarrier barrier = new CyclicBarrier( simulatorPool.size( ) );
        
        long startTime = System.currentTimeMillis( );

        simulatorPool.forEach( simulator -> simulator.start( nSteps, barrier ) );

        simulatorPool.forEach( simulator -> {
			try {
				simulator.join( );
			} catch ( InterruptedException e ) {
				e.printStackTrace( );
			}
		} );

        return System.currentTimeMillis( ) - startTime;
    }

    private void buildBalls( int nBodies ) {

    	final Random rand = new Random( System.currentTimeMillis( ) );

        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double y = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double dx = - 1 + rand.nextDouble( ) * 2;
            double speed = rand.nextDouble( ) * 0.05;

            Body b = new Body( new Position( x, y ), new Velocity( dx * speed, Math.sqrt( 1 - dx*dx ) * speed ), 0.01 );
            bodies.add( b );
        }
    }

    private void buildSimulators( int nBodies ) {
    	
    	final int nProc = Runtime.getRuntime( ).availableProcessors( );
		System.out.println( "Number of cores:\t" + nProc );
    	
		for ( int i = 0; i < nProc +1 && i < nBodies; i++ )
			simulatorPool.add( new Simulator( bounds ) );
		System.out.println( "Number of simulators:\t" + simulatorPool.size( ) );
    }
}
