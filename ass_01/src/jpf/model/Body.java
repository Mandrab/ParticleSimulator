package jpf.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gov.nasa.jpf.vm.Verify;

/**
 * Normally contains body's data (position, speed) and methods to use them,
 * Now it only check some assertions relative to critical sections
 * 
 * @author baldini paolo, battistini ylenia
 */
public class Body {

	// atomic integer used to check that only a thread/simulator is using solve-collision method on this body
	AtomicInteger CALLS_IN_SOLVE_COLLISION;

    private Lock myLock;
    
    public Body( ) {
        myLock = new ReentrantLock( );

        CALLS_IN_SOLVE_COLLISION = new AtomicInteger( );
    }

    /**
     * Solve collision between two bodies. Now only check that the critical section is "safe"
     * 
     * @param b1
     * 		the first body to check
     * @param b2
     * 		the second body to check
     */
    public static void solveCollision( Body b1, Body b2 ) {

    	// i expect that only me, a thread/simulator, is using this method on the two bodies
    	Verify.beginAtomic( );
    	assert b1.CALLS_IN_SOLVE_COLLISION.incrementAndGet( ) == 1 : "more than a 'solveCollision' call on a ball!";
    	assert b2.CALLS_IN_SOLVE_COLLISION.incrementAndGet( ) == 1 : "more than a 'solveCollision' call on a ball!";
    	Verify.endAtomic( );
    	
    	// ok interleaving
    	
    	// really, i only need to decrease the counter (i'm exiting), but why not add another check that i'm the only 
    	// thread calling this function on this two bodies? The control is conceptually equal that the one before
    	Verify.beginAtomic( );
    	assert b1.CALLS_IN_SOLVE_COLLISION.decrementAndGet( ) == 0 : "more than a 'solveCollision call' on a ball!";
    	assert b2.CALLS_IN_SOLVE_COLLISION.decrementAndGet( ) == 0 : "more than a 'solveCollision call' on a ball!";
    	Verify.endAtomic( );
    }
    
    /**
     * Gain a lock on this body
     */
    public void lock( ) {
    	myLock.lock( );
    }
    
    /**
     * Release the lock on this body
     */
    public void unlock( ) {
    	myLock.unlock( );
    }
}
