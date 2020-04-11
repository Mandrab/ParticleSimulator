package jpf.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gov.nasa.jpf.vm.Verify;

public class Body {

	AtomicInteger CALLS_IN_UPDATE_VEL;
	AtomicInteger CALLS_IN_SOLVE_COLLISION;

    private Lock myLock;
    
    public Body( ) {
        myLock = new ReentrantLock( );

        CALLS_IN_UPDATE_VEL = new AtomicInteger( );
        CALLS_IN_SOLVE_COLLISION = new AtomicInteger( );
    }
    
    public static void solveCollision( Body b1, Body b2 ) {

    	Verify.beginAtomic( );
    	assert b1.CALLS_IN_SOLVE_COLLISION.incrementAndGet( ) == 1 : "more than a 'solveCollision' call on a ball!";
    	assert b2.CALLS_IN_SOLVE_COLLISION.incrementAndGet( ) == 1 : "more than a 'solveCollision' call on a ball!";
    	Verify.endAtomic( );
    	
    	// ok interleaving
    	
    	Verify.beginAtomic( );
    	assert b1.CALLS_IN_SOLVE_COLLISION.decrementAndGet( ) == 0 : "more than a 'solveCollision call' on a ball!";
    	assert b2.CALLS_IN_SOLVE_COLLISION.decrementAndGet( ) == 0 : "more than a 'solveCollision call' on a ball!";
    	Verify.endAtomic( );
    }
    
    public void lock( ) {
    	myLock.lock( );
    }
    
    public void unlock( ) {
    	myLock.unlock( );
    }
}
