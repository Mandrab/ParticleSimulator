package main.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.Position;

/**
 * This class represents a body, moving in the field.
 * Contains method to:
 * Update the position, according to current velocity.
 * Change the velocity.
 * Check if there is collision with the specified body.
 * Check if there collisions with the boundary and update the
 * position and velocity accordingly.
 *
 * Also contains method to lock and unlock.
 * 
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Body {

    private Position position;	// body position
    private Speed speed;		// body speed
    private double radius;		// body radius
    private Lock myLock;		// lock to call on this (synchronization purpose)

    public Body( Position position, Speed speed, double radius ) {
        this.position = position;
        this.speed = speed;
        this.radius = radius;
        this.myLock = new ReentrantLock( );
    }

    /**
     * Get the body radius
     * 
     * @return
     * 		body radius
     */
    public double getRadius( ) {
    	return radius;
    }

    /**
     * Get the body position
     * 
     * @return
     * 		body position
     */
    public Position getPos( ) {
        return position;
    }

    /**
     * Get the body speed
     * 
     * @return
     * 		body speed
     */
    public Speed getSpeed( ) {
        return speed;
    }

    /**
     * Update the position of the body
     * 
     * @param dt
     * 		"elapsed" time since last update
     */
    public void updatePos( double dt ) {    	
    	double newPosX = position.getX( ) + speed.getX( ) * dt;
    	double newPosY = position.getY( ) + speed.getY( ) * dt;
    	position.change( newPosX, newPosY );
    }

    /**
     * Change the speed of the body
     * 
     * @param vx
     * 		new x speed
     * @param vy
     * 		new y speed
     */
    public void changeSpeed( double vx, double vy ){
    	speed.change( vx, vy );
    }

    /**
     * Check collision with a body
     * 
     * @param b
     * 		the body with which check the collision
     * 
     * @return
     * 		true if collide, false otherwise
     */
    public boolean collideWith( Body b ) {
    	double dx = Math.abs( position.getX( ) - b.getPos( ).getX( ) );
    	double centersDistance = radius + b.getRadius( );

    	// first control x axis proximity
    	if ( dx > centersDistance ) return false;

    	double dy = Math.abs( position.getY( ) - b.getPos( ).getY( ) );

    	// after control y axis proximity
    	if ( dy > centersDistance ) return false;

    	// check with euclidean distance
    	return dx * dx + dy * dy < centersDistance * centersDistance;
    }

    /**
     * Check and eventually resolve collision with a boundary
     * 
     * @param bounds
     * 		the bounds to check
     */
    public void checkAndSolveBoundaryCollision( Boundary bounds ){
    	double x = position.getX( );
    	double y = position.getY( );   
    	double bx0 = bounds.getX0( );
    	double bx1 = bounds.getX1( );
    	double by0 = bounds.getY0( );
    	double by1 = bounds.getY1( );
    	double velx = speed.getX( );
    	double vely = speed.getY( );
    	
    	
        if ( x > bx1 ) {
            position.change( bx1, y );
            speed.change( -velx, vely );
        } else if ( x < bx0 ) {
            position.change( bx0, y);
            speed.change( -velx, vely );
        } else if ( y > by1 ) {
            position.change( x, by1);
            speed.change( velx, -vely );
        } else if ( y < by0 ) {
            position.change( x, by0);
            speed.change( velx, -vely );
        }
    }

    /**
     * Resolve collision between two bodies
     * 
     * @param b1
     * 		first body
     * @param b2
     * 		second body
     */
    public static void solveCollision( Body b1, Body b2 ) {

    	Position x1 = b1.getPos( );
    	Position x2 = b2.getPos( );
    	Speed v1 = b1.getSpeed( );
    	Speed v2 = b2.getSpeed( );

    	double xDistance = x1.getX( ) - x2.getX( );
    	double xDistanceSquare = xDistance * xDistance;

    	double yDistance = x1.getY( ) - x2.getY( );
    	double yDistanceSquare = yDistance * yDistance;

    	double xVelocity = v1.getX( ) - v2.getX( );
    	double yVelocity = v1.getY( ) - v2.getY( );    	
    	double fact12 = ( xDistance * xVelocity + yDistance * yVelocity ) / ( xDistanceSquare + yDistanceSquare );

    	double v1x = v1.getX( ) - xDistance*fact12;
    	double v1y = v1.getY( ) - yDistance*fact12;

    	double v2x = v2.getX( ) + xDistance * fact12;
    	double v2y = v2.getY( ) + yDistance * fact12;

    	b1.changeSpeed( v1x, v1y );
    	b2.changeSpeed( v2x, v2y );
    }

    /**
     * Gain lock on this item
     */
    public void locked( ) {
    	myLock.lock( );
    }

    /**
     * Release lock on this item
     */
    public void unlocked( ) {
    	myLock.unlock( );
    }
}
