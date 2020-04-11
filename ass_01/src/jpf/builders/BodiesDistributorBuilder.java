package jpf.builders;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import gov.nasa.jpf.vm.Verify;

/**
 * A builder for distribution of bodies into simulators "algorithm"
 * 
 * @author baldini paolo, battistini ylenia
 */
public class BodiesDistributorBuilder {

	// used to check if the same index is assigned to more than one simulator
	static Set<Integer> JPF_CHECK_ASSIGNED_INDEXES = new HashSet<>( );

	// here i test only a type of distribution
	public enum Trait {
		NEAREST_BALANCED_CALCS;
	}

	/**
	 * Get the simulator with the specific trait (here, only one available)
	 * @param index
	 * 		of the simulator
	 * @param groupsCount
	 * 		number of simulators
	 * @param trait
	 * 		desired algorithm for the distributor
	 * @return
	 * 		the distributor
	 */
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

		/**
		 * Get bodies subset
		 * 
		 * @param bodiesCount
		 * 		number of bodies
		 */
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

		/**
		 * Fill the array with the correct index based on the used algorithm
		 * @param indexes
		 * 		indexes return array to fill
		 * @param bodiesCount
		 * 		number of bodies
		 * @param forNum
		 * 		how many bodies for simulator
		 * @param remainingItems
		 * 		number of remaining items (added to the last simulator)
		 * 
		 * @return
		 * 		the filled array
		 */
		public abstract int[] getFilledArray( int[] indexes, int bodiesCount, int forNum, int remainingItems );
	}
	
	/**
	 * Return a divider based on the first balanced divisor algorithm explained in the report
	 * 
	 * @author baldini paolo, battistini ylenia
	 */
	public static class NearestBalancedCalcsDistributor extends Distributor {
		
		public NearestBalancedCalcsDistributor( int workerIndex, int groupsCount ) {
			super( workerIndex, groupsCount );
		}

		/**
		 * {@inheritDoc}
		 */
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

			// assert that the same index is NOT assigned to more than one simulator
			Verify.beginAtomic( );
			for ( int i = 0; i < indexes.length; i++ )
				assert JPF_CHECK_ASSIGNED_INDEXES.add( indexes[ i ] )
						: "bodies subdivision error: an index was previously added to another simulator";
			Verify.endAtomic( );
	    	return indexes;
		}
	}
}
