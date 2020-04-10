package jpf.builders;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import gov.nasa.jpf.vm.Verify;

public class BodiesDistributorBuilder {

	static Set<Integer> JPF_CHECK_ASSIGNED_INDEXES = new HashSet<>( );

	public enum Trait {
		NEAREST_BALANCED_CALCS;
	}

	public static Function<Integer, int[]> get( int index, int groupsCount, Trait trait ) {
		return new NearestBalancedCalcsDistributor( index, groupsCount );
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

				if ( remainingItems > 0 ) {
					indexes = new int[ forNum + remainingItems ];
				}
			}

			return getFilledArray( indexes, bodiesCount, forNum, remainingItems );
		}

		public abstract int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems );
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

			Verify.beginAtomic( );
					for ( int i = 0; i < indexes.length; i++ ) {
						assert JPF_CHECK_ASSIGNED_INDEXES.add( indexes[ i ] ) : "bodies subdivision error: an index was previously added to another simulator";
					}
			Verify.endAtomic( );
	    	return indexes;
		}
	}
}
