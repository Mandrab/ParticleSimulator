import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class Model {

	private List<Simulator> simulatorPool;
	private List<Body> bodies;				// bodies in the field

    public Model( ) {

        final int nProc = Runtime.getRuntime( ).availableProcessors( );
		System.out.println( "N cores: " + nProc );

		simulatorPool = new ArrayList<Simulator>( );
		
		for ( int i = 0; i < nProc + 1; i++ )
			simulatorPool.add( new Simulator( ) );
    }

    public void execute( int nBodies, int nSteps ) {

    	bodies = new ArrayList<Body>( );
    	for ( int i = 0; i < nBodies; i++ )
    		bodies.add( new Body( ) );

        int increment = bodies.size( ) / simulatorPool.size( ) - 1;

        int toIdx, fromIdx = 0;
        
        if ( bodies.size( ) < simulatorPool.size( ) ) {
        	increment = 1;
        	toIdx = 1;
        } else
        	toIdx = increment + bodies.size( ) - simulatorPool.size( ) * increment;

        for( int i = 0; i < simulatorPool.size( ) && i < bodies.size( ); i++ ) {
        	simulatorPool.get( i ).setBodies( bodies.subList( fromIdx, toIdx ) );
        	fromIdx = toIdx;
        	toIdx += increment;
        }

        final CyclicBarrier barrier = new CyclicBarrier( simulatorPool.size( ) );
        simulatorPool.forEach( simulator -> simulator.start( nSteps, barrier ) );
    }
}
