import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.Body;
import main.Position;
import main.Velocity;

public class Model {

	private Simulator[] simulatorPool;
	private Body[] bodies;				// bodies in the field
	private int simulatorCount;
	private Lock lock = new ReentrantLock();
	
    public Model( ) {
    }
    
    public void initialize( int nBodies) {

    	bodies = new Body[nBodies];
    	addBalls( nBodies );

    	createSimulators( nBodies );					// create right number of simulators (thread)
    	
    	final int forNum = nBodies < simulatorCount ? 1
    			: nBodies / simulatorCount;

    	int simulatorIdx = 0;

        for( int fromIdx = 0; simulatorIdx < simulatorCount; simulatorIdx++ ) {
        	simulatorPool[ simulatorIdx ].setBodies( bodies, fromIdx, forNum );
        	fromIdx += forNum;
        }

        if ( nBodies % simulatorCount != 0 ) {
        	final int bodiesForLast = forNum + nBodies % simulatorCount;
        	simulatorPool[ --simulatorIdx ].setBodies( bodies, simulatorIdx * forNum, bodiesForLast );
        }
    }
    
    public void execute( int nSteps ) throws InterruptedException{
    	
    	final CyclicBarrier barrier = new CyclicBarrier( simulatorCount, ( ) -> {
        		List<Position> ballsPositions = new ArrayList<>( );
            	for ( Body body : bodies ) {
    				ballsPositions.add( new Position( body.getPos( ).getX( ), body.getPos( ).getY( ) ) );
    			}
        } );

        for ( Simulator simulator : simulatorPool ) {
			simulator.start( nSteps, barrier );
		}
        
        /*for ( Simulator simulator : simulatorPool ) {
        	simulator.join( );
		}*/
    }
    
    private void addBalls( int nBodies ) {

        for ( int i = 0; i < nBodies; i++ ) {
           
            Body b = new Body( new Position( 0, 0 ), new Velocity( 00, Math.sqrt( 1 - 0 ) * 0 ), 0.01, lock );
            bodies[ i ] = b;
        }
    }
    
    private void createSimulators(int nBodies) {
    	
    	final int nProc = Runtime.getRuntime( ).availableProcessors( );

		simulatorCount = Math.min( nProc + 1, nBodies );
		simulatorPool = new Simulator[ simulatorCount ];
		for ( int i = 0; i < simulatorCount; i++ )
			simulatorPool[ i ] = new Simulator( );
    }
}
