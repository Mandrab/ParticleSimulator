package jpf.model;

import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

import jpf.builders.BodiesDistributorBuilder;
import jpf.builders.BodiesDistributorBuilder.Trait;
import jpf.model.simulators.BasicSimulator;
import jpf.model.simulators.Simulator;
import jpf.model.simulators.StartStopSimulator;
import jpf.Resource;

public class Model {

	private State actualState;							// model's element state ( iteration, virtual time, bodies positions )

	private Resource syncResource;						// resource for synchronized action

	private Simulator[] simulatorPool;
	private Body[] bodies;								// bodies in the field

	public Model( ) {
		syncResource = new Resource( this );
    	actualState = new State( 0 );
	}
	
	public void initialize( int nSimulators ) {

		simulatorPool = new StartStopSimulator[ nSimulators ];

		for ( int i = 0; i < nSimulators; i++ )
			simulatorPool[ i ] = new StartStopSimulator( );
	}

    public void initialize( int nBodies, int nSimulators ) {

    	bodies = allocateBalls( nBodies );						// generate nBodies balls

		simulatorPool = new BasicSimulator[ nSimulators ];

		for ( int i = 0; i < nSimulators; i++ )
			simulatorPool[ i ] = new BasicSimulator( );

    	for ( int idx = 0; idx < nSimulators; idx++ ) {
    		Function<Integer, int[]> distributor = BodiesDistributorBuilder.get( idx, nSimulators, Trait.NEAREST_BALANCED_CALCS );
    		( ( BasicSimulator ) simulatorPool[ idx ] ).setWorkspace( bodies, distributor );
		}
    }

    public void execute( int nSteps ) throws InterruptedException {

    	CyclicBarrier firstBarrier = new CyclicBarrier( simulatorPool.length, ( ) -> {
    		try {
				syncResource.manage( );
			} catch ( InterruptedException e ) { e.printStackTrace(); }

        	//assignment are atomic operation TODO check
        	actualState = new State( nSteps );
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
    
    public Simulator[] getSimulators( ) {
    	return simulatorPool;
    }

    public State getState( ) {
    	return actualState;
    }

    private Body[] allocateBalls( int nBodies ) {

    	final Body[] bodies = new Body[ nBodies ];

        for ( int i = 0; i < nBodies; i++ )
            bodies[ i ] = new Body( );
        
        return bodies;
    }

    public class State {

    	public State( int iterations ) { }
    }
}
