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
	
	public static abstract class Distributor implements Function<Integer, int[]> {

		protected int groupsCount;
		protected int workerIndex;

		public Distributor( int workerIndex, int groupsCount ) {
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

			return getFilledArray( indexes, bodiesCount, forNum, remainingItems );
		}

		public abstract int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems );
	}
	
	public static class IndexRangeDistributor extends Distributor {
		
		public IndexRangeDistributor( int workerIndex, int groupsCount ) {
			super( workerIndex, groupsCount );
		}

		@Override
		public int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems ) {

			int startIndex = workerIndex * ( int ) ( bodiesCount / groupsCount );

			for( int i = 0; i < forNum; i++ ) {
	        	indexes[ i ] = startIndex++;
	        }

			if ( workerIndex == groupsCount -1 )
				for ( int i = forNum; i < forNum + remainingItems; i++ )
					indexes[ i ] = startIndex++;

			return indexes;
		}
	}
	
	public static class BalancedCalcsDistributor extends Distributor {
		
		public BalancedCalcsDistributor( int workerIndex, int groupsCount ) {
			super( workerIndex, groupsCount );
		}

		@Override
		public int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems ) {

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
	
	public static class NearestBalancedCalcsDistributor extends Distributor {
		
		public NearestBalancedCalcsDistributor( int workerIndex, int groupsCount ) {
			super( workerIndex, groupsCount );
		}

		@Override
		public int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems ) {

			int bodyIndex = workerIndex;
			for ( int i = 0; i < forNum; i++ ) {
				indexes[ i ] = bodyIndex;
				bodyIndex += groupsCount;
			}

			if ( workerIndex == groupsCount -1 )
				for ( int i = 0, j = bodyIndex - groupsCount; i < remainingItems; i++ )
					indexes[ forNum + i ] = ++j;

	    	return indexes;
		}
	}
}
