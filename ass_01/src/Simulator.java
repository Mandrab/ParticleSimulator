import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/*
 * possibilità di eseguire il calcolo del solveCollision fuori sezione critica
 * 	-> check variazione nelle prestazioni
 */
public class Simulator {

	private static final double INCREMENT_TIME = 0.1;
	
	private List<Body> bodies;		// bodies in the field
	
	public Simulator( ) {
		bodies = new ArrayList<Body>( );
	}

	public void start( int nSteps, CyclicBarrier barrier, Boundary bounds ) {

		new Thread( ( ) -> {
	        
	        /* simulation loop */
	        for ( int step = 0; step < nSteps; step++ ) {

	        	/* compute bodies new pos */
	        	for ( Body b: bodies )
	        		b.updatePos( INCREMENT_TIME );

	        	try {
					barrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace();
				}

	        	/* check collisions */
			    for ( int i = 0; i < bodies.size( ) - 1; i++ ) {

			    	Body b1 = bodies.get(i);
			    	synchronized ( b1 ) {
						for (int j = i + 1; j < bodies.size(); j++) {

				        	Body b2 = bodies.get(j);
				        	synchronized ( b2 ) {
								if (b1.collideWith(b2))
									Body.solveCollision(b1, b2);
							}
				            
				        }
					}
			        
		        }
			    
			    /* check boundaries */
			    for (Body b: bodies)
			    	b.checkAndSolveBoundaryCollision( bounds );        
	        }
		} ).start( );
	}
	
	public void setBodies( List<Body> bodies ) {
		this.bodies = bodies;
	}
}
