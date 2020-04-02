package main;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Model {

	private boolean running;
	private State actualState;

	private int simulatorCount;
	private Simulator[] simulatorPool;
	private Body[] bodies;							// bodies in the field
	private Boundary bounds;						// boundary of the field
	private Lock lock = new ReentrantLock();

	public Model( ) {
		bounds = new Boundary(-1.0,-1.0,1.0,1.0);	// initializing boundary
		running = false;
		actualState = new State( 0, 0, new ArrayList<Position>( ) );
	}

    public void initialize( int nBodies ) {

    	bodies = new Body[ nBodies ];
    	addBalls( nBodies );						// generate nBodies balls

    	createSimulators( nBodies );					// create right number of simulators (thread)
    	
    	final int forNum = nBodies < simulatorCount ? 1
    			: nBodies / simulatorCount;
    	System.out.println( "Bodies for simulator:\t" + forNum );

    	int simulatorIdx = 0;

        for( int fromIdx = 0; simulatorIdx < simulatorCount; simulatorIdx++ ) {
        	simulatorPool[ simulatorIdx ].setBodies( bodies, fromIdx, forNum );
        	fromIdx += forNum;
        }

        if ( nBodies % simulatorCount != 0 ) {
        	final int bodiesForLast = forNum + nBodies % simulatorCount;
        	simulatorPool[ --simulatorIdx ].setBodies( bodies, simulatorIdx * forNum, bodiesForLast );
        	System.out.println( "Bodies for last simulator: " + bodiesForLast );
        }
    }

    public long execute( int nSteps ) throws InterruptedException {

    	System.out.println( "\nExecuting " + simulatorCount + " simulators\n" );

    	running = true;

        final CyclicBarrier barrier = new CyclicBarrier( simulatorCount, ( ) -> {
        	//synchronized( actualState ) {
        		List<Position> ballsPositions = new ArrayList<>( );
            	for ( Body body : bodies ) {
    				ballsPositions.add( new Position( body.getPos( ).getX( ), body.getPos( ).getY( ) ) );
    			}
            	actualState = new State( nSteps, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
			//}
        } );

        long startTime = System.currentTimeMillis( );

        for ( Simulator simulator : simulatorPool ) {
			simulator.start( nSteps, barrier );
		}
        
        for ( Simulator simulator : simulatorPool ) {
        	simulator.join( );
		}

        running = false;

        return System.currentTimeMillis( ) - startTime;
    }
    
    public boolean isRunning( ) {
    	return running;
    }
    
    public State getState( ) {
    	return actualState;
    }

    private void addBalls( int nBodies ) {

    	final Random rand = new Random( System.currentTimeMillis( ) );

        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double y = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double dx = - 1 + rand.nextDouble( ) * 2;
            double speed = rand.nextDouble( ) * 0.05;

            Body b = new Body( new Position( x, y ), new Velocity( dx * speed, Math.sqrt( 1 - dx*dx ) * speed ), 0.01, lock );
            bodies[ i ] = b;
        }
    }

    private void createSimulators( int nBodies ) {
    	
    	final int nProc = Runtime.getRuntime( ).availableProcessors( );
		System.out.println( "Number of cores:\t" + nProc );

		simulatorCount = Math.min( nProc + 1, nBodies );
		simulatorPool = new Simulator[ simulatorCount ];
		for ( int i = 0; i < simulatorCount; i++ )
			simulatorPool[ i ] = new Simulator( bounds );
		System.out.println( "Number of simulators:\t" + simulatorCount );
    }
    
    public class State {

    	private int iterations;
    	private double virtualTime;
    	private List<Position> ballsPositions;
    	
    	public State( int iterations, double virtualTime, List<Position> ballsPositions ) {
			this.iterations = iterations;
			this.virtualTime = virtualTime;
			this.ballsPositions = ballsPositions;
		}

		public int getIterations( ) { return iterations; }

		public double getVirtualTime( ) { return virtualTime; }

		public List<Position> getBallsPositions( ) { return ballsPositions; }
    }
}
