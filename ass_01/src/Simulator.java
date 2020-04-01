import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Simulator {

	private static final double INCREMENT_TIME = 0.1;
	
	private List<Body> bodies;		// bodies in the field
	private CyclicBarrier barrier;
	
	public Simulator( ) {
		bodies = new ArrayList<Body>( );
	}

	public void start( int nSteps, CyclicBarrier barrier ) {

		new Thread( ( ) -> {

			/* init virtual time */
	        double virtualTime = 0;
	        
	        /* simulation loop */
	        while ( nSteps-- > 0 ){

	        	/* compute bodies new pos */
	        	for ( Body b: bodies ) {
	        		b.updatePos( INCREMENT_TIME );
			    }

	        	barrier.await( );

	        	/* check collisions */
			    for (int i = 0; i < bodies.size() - 1; i++) {
			    	Body b1 = bodies.get(i);
			        for (int j = i + 1; j < bodies.size(); j++) {
			        	Body b2 = bodies.get(j);
			            if (b1.collideWith(b2)) {
			            	Body.solveCollision(b1, b2);
			            }
			        }
		        }
			    
			    /* check boundaries */
			    
			    for (Body b: bodies) {
			    	b.checkAndSolveBoundaryCollision(bounds);
			    }
			    
			    /* update virtual time */
			    virtualTime += INCREMENT_TIME;

			    /* display current stage */
			    
	        	viewer.display(bodies, virtualTime, ++iter);
	        
	        }
		} ).start( );
	}
	
	public void setBodies( List<Body> bodies ) {
		
	}
}
