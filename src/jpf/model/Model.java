package jpf.model;

import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

import jpf.builders.BodiesDistributorBuilder;
import jpf.builders.BodiesDistributorBuilder.Trait;
import jpf.model.simulators.BasicSimulator;
import jpf.model.simulators.Simulator;
import jpf.model.simulators.StartStopSimulator;

/**
 * Model of MVC pattern
 * 
 * @author baldini paolo, battistini ylenia
 */
public class Model {

	private State actualState;			// model's element state. In this test, it contains almost nothing

	private Resource syncResource;		// resource for synchronized action

	private Simulator[] simulatorPool;	// pool of simulators
	private Body[] bodies;				// bodies in the simulation

	public Model( ) {
		syncResource = new Resource( this );
    	actualState = new State( 0 );
	}
	
	/**
	 * Initialize the model (i.e., prepare the simulation for the GUI TEST)
	 * In this type of test i don't need bodies
	 * 
	 * @param nSimulators
	 * 		number of simulator to run
	 */
	public void initialize( int nSimulators ) {

		simulatorPool = new StartStopSimulator[ nSimulators ];

		// use a custom (almost empty) simulator for GUI TEST
		for ( int i = 0; i < nSimulators; i++ )
			simulatorPool[ i ] = new StartStopSimulator( );
	}

	/**
	 * Initialize the model (i.e., prepare the simulation for the BASIC TEST)
	 * 
	 * @param nBodies
	 * 		number of bodies to run
	 * @param nSimulators
	 * 		number of simulator to run
	 */
    public void initialize( int nBodies, int nSimulators ) {

    	bodies = allocateBodies( nBodies );						// generate nBodies balls

		simulatorPool = new BasicSimulator[ nSimulators ];

		// use a computationally lightweight simulator for BASIC TEST
		for ( int i = 0; i < nSimulators; i++ )
			simulatorPool[ i ] = new BasicSimulator( );

    	for ( int idx = 0; idx < nSimulators; idx++ ) {
    		Function<Integer, int[]> distributor = BodiesDistributorBuilder.get( idx, nSimulators, Trait.NEAREST_BALANCED_CALCS );
    		( ( BasicSimulator ) simulatorPool[ idx ] ).setWorkspace( bodies, distributor );
		}
    }

    /**
     * Run the simulation for a number of steps
     * 
     * @param nSteps
     * 		the steps to execute/run
     * @throws InterruptedException
     * 		see Thread.join( )
     */
    public void execute( int nSteps ) throws InterruptedException {

    	CyclicBarrier firstBarrier = new CyclicBarrier( simulatorPool.length, ( ) -> {
    		// manage the flow of the system
    		try {
				syncResource.manage( );
			} catch ( InterruptedException e ) { e.printStackTrace(); }

        	// update the model state
        	actualState = new State( nSteps );
        } );
    	CyclicBarrier secondBarrier = new CyclicBarrier( simulatorPool.length );

        for ( Simulator simulator : simulatorPool ) simulator.start( nSteps, firstBarrier, secondBarrier );

        for ( Simulator simulator : simulatorPool ) simulator.join( );
    }

    /**
	 * Make the system run
	 */
    public void start( ) {
    	syncResource.start( );
    }

    /**
	 * Stop the system
	 */
    public void stop( ) {
    	syncResource.stop( );
    }
    
    /**
     * Get the simulators/threads. Used for test purpose only
     * 
     * @return
     * 		the simulators-pool
     */
    public Simulator[] getSimulators( ) {
    	return simulatorPool;
    }

    /**
     * Return model state
     * 
     * @return
     * 		the last updated model state
     */
    public State getState( ) {
    	return actualState;
    }

    /**
     * Create and add bodies to the array
     * 
     * @param nBodies
     * 		the required bodies number
     * 
     * @return
     * 		the filled bodies array
     */
    private Body[] allocateBodies( int nBodies ) {

    	final Body[] bodies = new Body[ nBodies ];

        for ( int i = 0; i < nBodies; i++ )
            bodies[ i ] = new Body( );
        
        return bodies;
    }

    /**
     * A class representing the state of the model. In this example it's unused (unless for tests)
     * and empty 
     * 
     * @author baldini paolo, battistini ylenia
     */
    public class State {

    	public State( int iterations ) { }
    }
}
