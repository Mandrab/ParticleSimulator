package main.builders;

import java.util.logging.Level;

import main.GlobalLogger;
import main.model.Boundary;
import main.model.Simulator;
/**
 * This class calculates the number of physical cores of the pc it is running on. 
 * To be more complete, check if the PC supports hyperthreading mode. 
 * Create a simulator for each core + 1 of the machine.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 *
 */
public class SimulatorsPoolBuilder {

	private static final GlobalLogger logger = GlobalLogger.get( );

	public enum CoresQT {
		OPTIMIZED_NO_HYPERTREADING,
		OPTIMIZED_HYPERTREADING;
	}
	
	public static Simulator[] getOne( Boundary bounds ) {
		return new Simulator[] { new Simulator( bounds ) };
	}
	
	public static Simulator[] getQuantity( Boundary bounds, int simulatorCount ) {

		if ( simulatorCount == 0 )
			throw new IllegalArgumentException( "quantity of core cannot be 0" );

		final Simulator[] simulatorPool = new Simulator[ simulatorCount ];

		for ( int i = 0; i < simulatorCount; i++ )
			simulatorPool[ i ] = new Simulator( bounds );

		logger.log( Level.INFO, "Number of simulators:\t\t" + simulatorCount );
		
		return simulatorPool;
	}
	
	public static Simulator[] getSimulatorsForCores( Boundary bounds, boolean hyperThreadingActive ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = hyperThreadingActive ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	return getQuantity( bounds, realProcs );
	}

	public static Simulator[] getOptimizedNum( Boundary bounds, int bodiesCount, boolean considerHyperThreading ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = considerHyperThreading ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	int simulatorsCount = Math.min( realProcs + 1, bodiesCount );

    	return getQuantity( bounds, simulatorsCount );
	}
	
	public static int getOptimizedNum( int bodiesCount, boolean considerHyperThreading ) {

		final int nProcs = Runtime.getRuntime( ).availableProcessors( );
		final int realProcs = considerHyperThreading ? nProcs / 2 : nProcs;
    	logger.log( Level.INFO, "Number of cores:\t\t\t" + realProcs );

    	return Math.min( realProcs + 1, bodiesCount );
	}
}
