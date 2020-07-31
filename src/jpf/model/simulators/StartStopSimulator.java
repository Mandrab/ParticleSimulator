package jpf.model.simulators;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
/**
 * This class represents the simulator of the View or start and stop. 
 * In this class there are methods for obtaining some information such as the state 
 * of the thread and the iteration number.
 * The main method is the start method in which everything possible has been eliminated 
 * except synchronization constructs.
 *  
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class StartStopSimulator implements Simulator {

	private Thread thread;
	private int step;
	
	/**
	 * Constructor.
	 * 
	 * Create a new thread.
	 */
	public StartStopSimulator( ) {
		thread = new Thread( );
	}

	/**
	 * This is a start method. 
	 * Everything possible has been eliminated except synchronization constructors.
	 * 
	 * @param nSteps
	 * 		number of iteration to run
	 * @param firstBarrier
	 * 		first synchronization barrier
	 * @param secondBarrier
	 * 		second synchronization barrier
	 */
	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier ) {

		thread = new Thread( ( ) -> {

	        for ( step = 0; step < nSteps; step++ ) {

	        	// UPDATE POSITIONS

	        	try {
	        		firstBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
   	
	        	// SOLVE CONFLICTS

			    try {
			    	secondBarrier.await( );
				} catch ( InterruptedException | BrokenBarrierException e ) {
					e.printStackTrace( );
				}
			    
			    // SOLVE BOUNDARY COLLISIONS
			    
	        }
		} );

		thread.start( );
	}
	
	/**
	 * Get the state of the internal thread. Used for test purpose only
	 * 
	 * @return
	 * 		see Thread.getState( )
	 */
	public Thread.State getState( ) {
		return thread.getState( );
	}
	
	/**
	 * Get the actual iteration. Used for test purpose only
	 * 
	 * @return
	 * 		the actual iteration's step
	 */
	public int getIteration( ) {
		return step;
	}

	/**
	 * Wait the simulation to end
	 * 
	 * @throws InterruptedException
	 * 		see Thread.join( )
	 */
	public void join( ) throws InterruptedException {
		thread.join( );
	}
}
