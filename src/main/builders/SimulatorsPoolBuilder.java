package main.builders;

import java.util.logging.Level;

import main.GlobalLogger;
import main.model.Boundary;
import main.model.Simulator;

/**
 * This class aims to help to create threads for the pool.
 * Specifically, it can create theoretic "optimal" number of
 * thread.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class SimulatorsPoolBuilder {

	private static final GlobalLogger logger = GlobalLogger.get( );

	/**
	 * Provides a pool of one simulator
	 * 
	 * @param bounds
	 * 		bound to pass to the simulator constructor
	 * 
	 * @return
	 * 		the simulators pool (one simulator)
	 */
	public static Simulator[] getOne( Boundary bounds ) {
		return new Simulator[] { new Simulator( bounds ) };
	}

	/**
	 * Provides a pool of simulator which size is specified by the user
	 * 
	 * @param bounds
	 * 		bound to pass to the simulator constructor
	 * @param simulatorCount
	 * 		number of simulator to create
	 * 
	 * @return
	 * 		the simulators pool
	 */
	public static Simulator[] getQuantity( Boundary bounds, int simulatorCount ) {

		if ( simulatorCount == 0 )
			throw new IllegalArgumentException( "quantity of core cannot be 0" );

		final Simulator[] simulatorPool = new Simulator[ simulatorCount ];

		for ( int i = 0; i < simulatorCount; i++ )
			simulatorPool[ i ] = new Simulator( bounds );

		logger.log( Level.INFO, "Number of simulators:\t\t" + simulatorCount );

		return simulatorPool;
	}

	/**
	 * Provides a pool of simulator which size if the same as the number
	 * of the core of the system.
	 * 
	 * @param bounds
	 * 		bound to pass to the simulator constructor
	 * @param hyperThreadingActive
	 * 		tell the algorithm to consider (or not) the hyper-threading
	 * 
	 * @return
	 * 		the simulators pool
	 */
	public static Simulator[] getSimulatorsForCores( Boundary bounds, boolean hyperThreadingActive ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = hyperThreadingActive ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	return getQuantity( bounds, realProcs );
	}

	/**
	 * Provides a pool of simulator of optimized size (considering bodies quantity).
	 * If the quantity of bodies is less than the core of the system, then return
	 * a pool which size is equal to the bodies quantity.
	 * 
	 * @param bounds
	 * 		bound to pass to the simulator constructor
	 * @param bodiesCount
	 * 		the number of bodies to run on the system
	 * @param hyperThreadingActive
	 * 		tell the algorithm to consider (or not) the hyper-threading
	 * 
	 * @return
	 * 		the simulators pool
	 */
	public static Simulator[] getOptimizedNum( Boundary bounds, int bodiesCount, boolean considerHyperThreading ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = considerHyperThreading ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	int simulatorsCount = Math.min( realProcs + 1, bodiesCount );

    	return getQuantity( bounds, simulatorsCount );
	}

	/**
	 * Provides a pool of simulator of optimized size based on system specifics
	 * 
	 * @param bounds
	 * 		bound to pass to the simulator constructor
	 * @param considerHyperThreading
	 * 		tell the algorithm to consider (or not) the hyper-threading
	 * 
	 * @return
	 * 		the simulators pool
	 */
	public static int getOptimizedNum( int bodiesCount, boolean considerHyperThreading ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = considerHyperThreading ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	return Math.min( realProcs + 1, bodiesCount );
	}
}
