package main;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Model {

	private List<Simulator> simulatorPool;
	private List<Body> bodies;				// bodies in the field
	private Boundary bounds;				// boundary of the field
	
    public void initialize( int nBodies ) {

    	final int nProc = Runtime.getRuntime( ).availableProcessors( );
		System.out.println( "N cores: " + nProc );
		
		bounds = new Boundary(-1.0,-1.0,1.0,1.0);	// initializing boundary and bodies
    	
    	simulatorPool = IntStream.rangeClosed( 0, nProc ).mapToObj( i -> new Simulator( bounds ) )
        		.collect( Collectors.toList( ) );

    	final Random rand = new Random( System.currentTimeMillis( ) );

    	bodies = new ArrayList<Body>( );
        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*(bounds.getX1() - bounds.getX0());
            double y = bounds.getX0( ) + rand.nextDouble( )*(bounds.getX1() - bounds.getX0());
            double dx = -1 + rand.nextDouble() * 2;
            double speed = rand.nextDouble()*0.05;

            Body b = new Body(new Position(x, y), new Velocity(dx*speed,Math.sqrt(1 - dx*dx)*speed), 0.01);
            bodies.add(b);
        }
		
		final int forNum = bodies.size( ) < simulatorPool.size( ) ? 1
        		: bodies.size( ) / simulatorPool.size( );

        for( int i = 0, fromIdx = 0; i < simulatorPool.size( ); i++ ) {
        	simulatorPool.get( i ).setBodies( bodies, fromIdx, forNum );
        	fromIdx = forNum;
        }

        if ( bodies.size( ) % simulatorPool.size( ) != 0 )
        	simulatorPool.get( 0 ).setBodies( bodies, 0, forNum + bodies.size( ) % simulatorPool.size( ) );
    }

    public long execute( int nSteps ) {

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
}
