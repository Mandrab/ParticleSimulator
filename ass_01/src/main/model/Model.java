package main.model;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import main.builders.BodiesDistributorBuilder;
import main.builders.BodiesDistributorBuilder.Trait;
import main.GlobalLogger;
import main.Position;
import main.Resource;
import main.builders.SimulatorsPoolBuilder;

public class Model {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private State actualState;							// model's element state ( iteration, virtual time, bodies positions )
	
	private Resource syncResource;						// resource for synchronized action

	private Simulator[] simulatorPool;
	private Body[] bodies;								// bodies in the field
	private Boundary bounds;							// boundary of the field

	public Model( ) {
		bounds = new Boundary( -1.0, -1.0, 1.0, 1.0 );	// initializing boundary
		actualState = new State( 0, 0, new ArrayList<Position>( ) );
		
		syncResource = new Resource( );
	}

    public void initialize( int nBodies, int nSimulators ) {

    	bodies = allocateBalls( nBodies );						// generate nBodies balls

    	simulatorPool = SimulatorsPoolBuilder.getQuantity( bounds, nSimulators ); // create simulators (thread) pool

    	for ( int idx = 0; idx < nSimulators; idx++ ) {
    		simulatorPool[ idx ].setWorkspace( bodies, BodiesDistributorBuilder.get( idx, nSimulators, Trait.BALANCED_CALCS ) );
		}

    	List<Position> ballsPositions = new ArrayList<>( );
    	for ( Body body : bodies ) ballsPositions.add( body.getPos( ).clone( ) );

    	actualState = new State( 0, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
    }

    public void execute( int nSteps ) throws InterruptedException {

    	logger.log( Level.INFO, "\nExecuting " + simulatorPool.length + " simulators\n" );

    	CyclicBarrier firstBarrier = new CyclicBarrier( simulatorPool.length, ( ) -> {
    		try {
				syncResource.manage( );
			} catch ( InterruptedException e ) { e.printStackTrace(); }
    		
    		List<Position> ballsPositions = new ArrayList<>( );
        	for ( Body body : bodies ) ballsPositions.add( body.getPos( ).clone( ) );

        	actualState = new State( nSteps, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
        } );
    	CyclicBarrier secondBarrier = new CyclicBarrier( simulatorPool.length );

        for ( Simulator simulator : simulatorPool ) simulator.start( nSteps, firstBarrier, secondBarrier );

        for ( Simulator simulator : simulatorPool ) simulator.join( );
    }

    public void start( ) {
    	syncResource.start( );
    }

    public void stop( ) {
    	syncResource.stop( );
    }

    public void step( ) {
    	syncResource.step( );
    }

    public boolean isTerminated( ) {
    	return Arrays.stream( simulatorPool ).allMatch( s -> s.isTerminated( ) );
    }

    public State getState( ) {
    	return actualState;
    }

    private Body[] allocateBalls( int nBodies ) {

    	final Body[] bodies = new Body[ nBodies ];

    	final Random rand = new Random( System.currentTimeMillis( ) );

        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double y = bounds.getX0( ) + rand.nextDouble( )*( bounds.getX1( ) - bounds.getX0( ) );
            double dx = - 1 + rand.nextDouble( ) * 2;
            double speed = rand.nextDouble( ) * 0.05;

            Body b = new Body( new Position( x, y ), new Speed( dx * speed, Math.sqrt( 1 - dx*dx ) * speed ), 0.01, new ReentrantLock() );
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
