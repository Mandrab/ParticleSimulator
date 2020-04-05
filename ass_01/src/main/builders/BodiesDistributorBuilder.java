package main.builders;

import java.util.function.Function;
import java.util.logging.Level;

import main.GlobalLogger;

public class BodiesDistributorBuilder {

	private static final GlobalLogger logger = GlobalLogger.get( );

	public enum Trait {
		INDEX_RANGE,
		BALANCED_CALCS,
		NEAREST_BALANCED_CALCS;
	}

	public static Function<Integer, int[]> get( int index, int groupsCount, Trait trait ) {
		
		if ( trait == Trait.INDEX_RANGE ) return new IndexRangeDistributor( index, groupsCount );
		if ( trait == Trait.BALANCED_CALCS ) return new BalancedCalcsDistributor( index, groupsCount );
		if ( trait == Trait.NEAREST_BALANCED_CALCS ) return new NearestBalancedCalcsDistributor( index, groupsCount );

		return null;
	}
	
	public static class IndexRangeDistributor implements Function<Integer, int[]> {

		private int groupsCount;
		private int workerIndex;
		
		public IndexRangeDistributor( int workerIndex, int groupsCount ) {
			this.groupsCount = groupsCount;
			this.workerIndex = workerIndex;
		}

		@Override
		public int[] apply( Integer bodiesCount ) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class BalancedCalcsDistributor implements Function<Integer, int[]> {

		private int groupsCount;
		private int workerIndex;
		
		public BalancedCalcsDistributor( int workerIndex, int groupsCount ) {
			this.groupsCount = groupsCount;
			this.workerIndex = workerIndex;
		}

		@Override
		public int[] apply( Integer bodiesCount ) {

			final int forNum = bodiesCount / groupsCount;
			final int remainingItems = bodiesCount % groupsCount;

			int[ ] indexes = new int[ forNum ];

			if ( workerIndex == groupsCount -1 ) {
				logger.log( Level.INFO, "Bodies for simulator:\t\t" + forNum );

				if ( remainingItems > 0 ) {
					logger.log( Level.INFO, "Bodies for last simulator: \t" + ( forNum + remainingItems ) );
					indexes = new int[ forNum + remainingItems ];
				}
			}

	    	for ( int i = 0; i < forNum / 2; i++ ) {
    			int idx = workerIndex + groupsCount * i;
    			indexes[ i ] = idx;
    			indexes[ forNum - i -1 ] = bodiesCount - idx -1;
	    	}

			if ( workerIndex == groupsCount -1 ) {
				for ( int i = 0, j = forNum; i < remainingItems; i++ )
					indexes[ j++ ] = i + bodiesCount / 2;
			}

			return indexes;
		}
	}
	
	public static class NearestBalancedCalcsDistributor implements Function<Integer, int[]> {

		private int groupsCount;
		private int workerIndex;
		
		public NearestBalancedCalcsDistributor( int workerIndex, int groupsCount ) {
			this.groupsCount = groupsCount;
			this.workerIndex = workerIndex;
		}

		@Override
		public int[] apply( Integer bodiesCount ) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
