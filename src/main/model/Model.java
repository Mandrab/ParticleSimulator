package main.model;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;

import main.builders.BodiesDistributorBuilder;
import main.builders.BodiesDistributorBuilder.Trait;
import main.GlobalLogger;
import main.Position;
import main.builders.SimulatorsPoolBuilder;

/**
 * This class implements Model.
 * This class contains the initialize method to allocate Bodies and create simulatorPool.
 * The execute method is the main method of the class because it create a cyclicBarrier to execute manage method of Resource's class.
 * Contains a State class to set and get an information about virtualTime, iteration and ballsPosition.
 * It also contains the start stop and step methods with the respective calls to the Resource's class.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Model {

	private static final GlobalLogger logger = GlobalLogger.get( );

	private State actualState;							// model's element state ( iteration, virtual time, bodies positions )

	private Resource syncResource;						// resource for synchronized action

	private Simulator[] simulatorPool;					// pool of simulators
	private Body[] bodies;								// bodies in the simulation
	private Boundary bounds;							// boundary of the simulation

	public Model( ) {
		bounds = new Boundary( -1.0, -1.0, 1.0, 1.0 );	// initializing boundary
		actualState = new State( 0, 0, new ArrayList<Position>( ) );

		syncResource = new Resource( );
	}

	/**
     * Initialize the model (needed to run)
     * 
     * @param nBodies
     * 		number of bodies to simulate
     * @param nSimulators
     * 		number of simulator to use
     */
    public void initialize( int nBodies, int nSimulators ) {

    	bodies = allocateBodies( nBodies );				// generate n bodies

    	simulatorPool = SimulatorsPoolBuilder.getQuantity( bounds, nSimulators ); // create simulators (thread) pool

    	for ( int idx = 0; idx < nSimulators; idx++ )	// set workspace (bodies and indexes subdivision function)
    		simulatorPool[ idx ].setWorkspace( bodies, BodiesDistributorBuilder.get( idx, nSimulators, Trait.BALANCED_CALCS ) );

    	List<Position> ballsPositions = new ArrayList<>( );
    	for ( Body body : bodies ) ballsPositions.add( body.getPos( ).clone( ) );

    	actualState = new State( 0, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
    }

    /**
     * Effectively start the simulation
     * 
     * @param nSteps
     * 		steps to simulate/iterate
     */
    public void execute( int nSteps ) throws InterruptedException {

    	logger.log( Level.INFO, "\nExecuting " + simulatorPool.length + " simulators\n" );

    	CyclicBarrier firstBarrier = new CyclicBarrier( simulatorPool.length, ( ) -> {
			try {
				syncResource.manage( );					// manage the "flow" of the program (start, stop, step)
			} catch (InterruptedException e) { e.printStackTrace( ); }

    		List<Position> ballsPositions = new ArrayList<>( );
        	for ( Body body : bodies ) ballsPositions.add( body.getPos( ).clone( ) );

        	// update model state
        	actualState = new State( nSteps, simulatorPool[ 0 ].getVirtualTime( ), ballsPositions );
        } );
    	CyclicBarrier secondBarrier = new CyclicBarrier( simulatorPool.length );

    	// start simulators
        for ( Simulator simulator : simulatorPool ) simulator.start( nSteps, firstBarrier, secondBarrier );

        // wait for simulators to end
        for ( Simulator simulator : simulatorPool ) simulator.join( );
    }

    /**
     * Start the simulation
     */
    public void start( ) {
    	syncResource.start( );
    }

    /**
     * Stop the simulation
     */
    public void stop( ) {
    	syncResource.stop( );
    }

    /**
     * Make a step in the simulation
     */
    public void step( ) {
    	syncResource.step( );
    }

    /**
     * Check if the simulation is terminated
     * 
     * @return
     * 		true if termination is ended, false otherwise
     */
    public boolean isTerminated( ) {
    	return Arrays.stream( simulatorPool ).allMatch( s -> s.isTerminated( ) );
    }

    /**
     * Get last saved state of the model
     * 
     * @return
     * 		last saved state of the model
     */
    public State getState( ) {
    	return actualState;
    }

    /**
     * Generate the bodies to simulate
     * 
     * @param nBodies
     * 		number of bodies to generate
     * 
     * @return
     * 		the array containing the generated bodies
     */
    private Body[] allocateBodies( int nBodies ) {

    	final Body[] bodies = new Body[ nBodies ];

    	final Random rand = new Random( System.currentTimeMillis( ) );

        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( ) * ( bounds.getX1( ) - bounds.getX0( ) );
            double y = bounds.getX0( ) + rand.nextDouble( ) * ( bounds.getX1( ) - bounds.getX0( ) );
            double dx = - 1 + rand.nextDouble( ) * 2;
            double speed = rand.nextDouble( ) * 0.05;

            Body b = new Body( new Position( x, y ), new Speed( dx * speed, Math.sqrt( 1 - dx * dx ) * speed ), 0.01 );
            bodies[ i ] = b;
        }

        return bodies;
    }

    /**
     * A class intended to store the state of the model.
     * It contains the actual iteration, the virtual time and the bodies position
     * 
     * @author Baldini Paolo, Battistini Ylenia
     */
    public class State {

    	private int iterations;
    	private double virtualTime;
    	private List<Position> bodiesPositions;

    	public State( int iterations, double virtualTime, List<Position> bodiesPositions ) {
			this.iterations = iterations;
			this.virtualTime = virtualTime;
			this.bodiesPositions = bodiesPositions;
		}

    	/**
    	 * Return the iteration that was running in the last update
    	 * 
    	 * @return
    	 * 		last known iteration
    	 */
		public int getIterations( ) { return iterations; }

		/**
    	 * Return the approximate virtual time in the simulation
    	 * 
    	 * @return
    	 * 		the virtual time
    	 */
		public double getVirtualTime( ) { return virtualTime; }

		/**
    	 * Return last known positions of the bodies
    	 * 
    	 * @return
    	 * 		last known bodies' positions
    	 */
		public List<Position> getBodiesPositions( ) { return bodiesPositions; }
    }
}
