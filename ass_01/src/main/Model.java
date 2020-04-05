package main;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import main.builders.BodiesDistributorBuilder;
import main.builders.BodiesDistributorBuilder.Trait;
import main.builders.SimulatorsPoolBuilder;

public class Model {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private boolean running;							// model state
	private State actualState;							// model's element state ( iteration, virtual time, bodies positions )

	private AtomicBoolean run;							// intention

	private int simulatorCount;
	private Simulator[] simulatorPool;
	private Body[] bodies;								// bodies in the field
	private Boundary bounds;							// boundary of the field
	private Lock lock = new ReentrantLock( );

	public Model( ) {
		bounds = new Boundary( -1.0, -1.0, 1.0, 1.0 );	// initializing boundary
		actualState = new State( 0, 0, new ArrayList<Position>( ) );

		running = false;
		run = new AtomicBoolean( false );		
	}

    public void initialize( int nBodies ) {

    	bodies = allocateBalls( nBodies );						// generate nBodies balls

    	simulatorPool = SimulatorsPoolBuilder.getOptimizedNum( bounds, nBodies, true ); // create right number of simulators (thread)
    	simulatorCount = simulatorPool.length;

    	for ( int idx = 0; idx < simulatorCount; idx++ ) {
    		simulatorPool[ idx ].setWorkspace( bodies, BodiesDistributorBuilder.get( idx, simulatorCount, Trait.INDEX_RANGE ) );
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
        } );

        for ( Simulator simulator : simulatorPool ) simulator.start( nSteps, barrier, barrier );

        for ( Simulator simulator : simulatorPool ) simulator.join( );

        running = false;
    }
    
    public State getState( ) {
    	return actualState;
    }
    
    public boolean isRunning( ) {
    	return running;
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
    
    public void stop( ) {
    	synchronized ( run ) {
    		run.set( false );
    	}
    }

    private Body[] allocateBalls( int nBodies ) {

    	final Body[] bodies = new Body[ nBodies ];

    	final Random rand = new Random( System.currentTimeMillis( ) );

        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double y = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double dx = - 1 + rand.nextDouble( ) * 2;
            double speed = rand.nextDouble( ) * 0.05;

            Body b = new Body( new Position( x, y ), new Velocity( dx * speed, Math.sqrt( 1 - dx*dx ) * speed ), 0.01, lock );
            bodies[ i ] = b;
        }
        
        return bodies;
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
