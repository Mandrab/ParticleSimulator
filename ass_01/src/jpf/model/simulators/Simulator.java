package jpf.model.simulators;

import java.util.concurrent.CyclicBarrier;

public interface Simulator {

	public void start( int nSteps, CyclicBarrier firstBarrier, CyclicBarrier secondBarrier );
	
	public void join( ) throws InterruptedException;
}
