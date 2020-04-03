package main;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Model {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private boolean running;
	private State actualState;

	private AtomicBoolean run;

	private int simulatorCount;
	private Simulator[] simulatorPool;
	private Body[] bodies;							// bodies in the field
	private Boundary bounds;						// boundary of the field
	private Lock lock = new ReentrantLock( );

	public Model( ) {
		bounds = new Boundary( -1.0, -1.0, 1.0, 1.0 );	// initializing boundary
		actualState = new State( 0, 0, new ArrayList<Position>( ) );

		running = false;
		run = new AtomicBoolean( false );		
	}

    public void initialize( int nBodies ) {

    	bodies = new Body[ nBodies ];
    	addBalls( nBodies );						// generate nBodies balls

    	createSimulators( nBodies );					// create right number of simulators (thread)
    	
    	final int forNum = nBodies < simulatorCount ? 1
    			: nBodies / simulatorCount;
    	logger.log( Level.INFO, "Bodies for simulator:\t\t" + forNum );

    	int simulatorIdx = 0;

        for( int fromIdx = 0; simulatorIdx < simulatorCount; simulatorIdx++ ) {
        	simulatorPool[ simulatorIdx ].setBodies( bodies, fromIdx, forNum );
        	fromIdx += forNum;
        }

        if ( nBodies % simulatorCount != 0 ) {
        	final int bodiesForLast = forNum + nBodies % simulatorCount;
        	simulatorPool[ --simulatorIdx ].setBodies( bodies, simulatorIdx * forNum, bodiesForLast );
        	logger.log( Level.INFO, "Bodies for last simulator: \t" + bodiesForLast );
        }

        run.set( true );
    }

    public void execute( int nSteps ) throws InterruptedException {

    	logger.log( Level.INFO, "\nExecuting " + simulatorCount + " simulators\n" );

    	running = true;

    	synchronized( run ) {
    		while ( ! run.get( ) )
				try {
					run.wait( );
				} catch (InterruptedException e) { e.printStackTrace( ); }
    	}
    	
    	CyclicBarrier barrier = new CyclicBarrier( simulatorCount, ( ) -> {
    		synchronized( run ) {
        		while ( ! run.get( ) )
    				try {
    					run.wait( );
    				} catch (InterruptedException e) { e.printStackTrace( ); }
        	}
    		List<Position> ballsPositions = new ArrayList<>( );
        	for ( Body body : bodies ) {
				ballsPositions.add( new Position( body.getPos( ).getX( ), body.getPos( ).getY( ) ) );
			}
        	actualState = new State( nSteps, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
			//}
        } );

        for ( Simulator simulator : simulatorPool ) simulator.start( nSteps, barrier );

        for ( Simulator simulator : simulatorPool ) simulator.join( );

        running = false;
    }

    public void start( ) {
    	synchronized ( run ) {
    		run.set( true );
        	run.notify( );
		}
    }

    public void pause( ) {
    	synchronized ( run ) {
    		run.set( false );
    	}
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
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + nProc );

		simulatorCount = Math.min( nProc + 1, nBodies );
		simulatorPool = new Simulator[ simulatorCount ];
		for ( int i = 0; i < simulatorCount; i++ )
			simulatorPool[ i ] = new Simulator( bounds );
		logger.log( Level.INFO, "Number of simulators:\t\t" + simulatorCount );
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
