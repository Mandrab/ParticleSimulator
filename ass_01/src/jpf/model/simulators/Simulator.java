package jpf.model.simulators;

import java.util.concurrent.CyclicBarrier;

/**
 * Interface for simulators. Used to have a unique array for the two type of simulations. Tests purpose only.
 * 
 * @author baldini paolo, battistini ylenia
 */
public interface Simulator {

	/**
	 * Start the execution
	 * 
	 * @param nSteps
	 * 		number of iteration to run
	 * @param firstBarrier
	 * 		first synchronization barrier
	 * @param secondBarrier
	 * 		second synchronization barrier
	 */
	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier );
	
	/**
	 * Get the state of the internal thread. Used for test purpose only
	 * 
	 * @return
	 * 		see Thread.getState( )
	 */
	public Thread.State getState( );
	
	/**
	 * Get the actual iteration. Used for test purpose only
	 * 
	 * @return
	 * 		the actual iteration's step
	 */
	public int getIteration( );
	
	/**
	 * Wait the simulation to end
	 * 
	 * @throws InterruptedException
	 * 		see Thread.join( )
	 */
	public void join( ) throws InterruptedException;
}
