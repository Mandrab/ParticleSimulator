import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Model {

	private List<Simulator> simulatorPool;
	private List<Body> bodies;				// bodies in the field
	private Boundary bounds;				// boundary of the field
	
    public Model( ) {

    	/* initializing boundary and bodies */
        bounds = new Boundary(-1.0,-1.0,1.0,1.0);

        final int nProc = Runtime.getRuntime( ).availableProcessors( );
		System.out.println( "N cores: " + nProc );

		simulatorPool = IntStream.rangeClosed( 0, 1 ).mapToObj( i -> new Simulator(  ) )
        		.collect( Collectors.toList( ) );
    }

    public void execute( int nBodies, int nSteps ) {

    	final Random rand = new Random( System.currentTimeMillis( ) );

        bodies = new ArrayList<Body>( );
        for ( int i = 0; i < nBodies; i++ ) {
            double x = bounds.getX0( ) + rand.nextDouble( )*(bounds.getX1() - bounds.getX0());
            double y = bounds.getX0( ) + rand.nextDouble( )*(bounds.getX1() - bounds.getX0());
            double dx = -1 + rand.nextDouble()*2;
            double speed = rand.nextDouble()*0.05;

            Body b = new Body(new Position(x, y), new Velocity(dx*speed,Math.sqrt(1 - dx*dx)*speed), 0.01);
            bodies.add(b);
        }

        int increment = bodies.size( ) / simulatorPool.size( );

        int toIdx, fromIdx = 0;
        
        if ( bodies.size( ) < simulatorPool.size( ) ) {
        	increment = 1;
        	toIdx = 1;
        } else
        	toIdx = increment + bodies.size( ) - simulatorPool.size( ) * increment;

        for( int i = 0; i < simulatorPool.size( ); i++ ) {
        	simulatorPool.get( i ).setBodies( bodies.subList( fromIdx, toIdx ) );
        	fromIdx = toIdx;
        	toIdx += increment;
        }

        final CyclicBarrier barrier = new CyclicBarrier( simulatorPool.size( ) );
        simulatorPool.forEach( simulator -> simulator.start( nSteps, barrier, bounds ) );
    }
}
