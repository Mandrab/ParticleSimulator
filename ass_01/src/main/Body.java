package main;

import java.util.concurrent.locks.Lock;

/*
 * This class represents a body, moving in the field.
 * 
 */
public class Body {
    
    private Position pos;
    private Velocity vel;
    private double radius;
    private Lock myLock;
    
    public Body(Position pos, Velocity vel, double radius, Lock lock){
        this.pos = pos;
        this.vel = vel;
        this.radius = radius;
        this.myLock=lock;
    }
    
    public double getRadius() {
    	return radius;
    }
    
    public Position getPos(){
        return pos;
    }

    public Velocity getVel(){
        return vel;
    }
    
    /**
     * Update the position, according to current velocity
     * 
     * @param dt time elapsed 
     */
    public void updatePos(double dt){    	
    	double newPosX = pos.getX() + vel.getX()*dt;
    	double newPosY = pos.getY() + vel.getY()*dt;
    	pos.change(newPosX, newPosY);
    }

    /**
     * Change the velocity
     * 
     * @param vx
     * @param vy
     */
    public void changeVel(double vx, double vy){
    	vel.change(vx, vy);
    }
  	
    /**
     * Computes the distance from the specified body
     * 
     * @param b
     * @return
     */
    public double getDistance(Body b) {
    	double dx = pos.getX() - b.getPos().getX();
    	double dy = pos.getY() - b.getPos().getY();
    	return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Check if there is collision with the specified body
     * @param b
     * @return
     */
    public boolean collideWith(Body b) {
    	double dx = Math.abs( pos.getX( ) - b.getPos( ).getX( ) );
    	
    	if ( dx > 0.02 ) return false;

    	double dy = Math.abs( pos.getY( ) - b.getPos( ).getY( ) );
    	
    	if ( dy > 0.02 ) return false;
    	
    	return dx*dx + dy*dy < 0.02*0.02;
    }
    
    /**
     * Check if there collisions with the boundaty and update the
     * position and velocity accordingly
     * 
     * @param bounds
     */
    public void checkAndSolveBoundaryCollision(Boundary bounds){
    	double x = pos.getX();
    	double y = pos.getY();   
    	double bx0 = bounds.getX0();
    	double bx1 = bounds.getX1();
    	double by0 = bounds.getY0();
    	double by1 = bounds.getY1();
    	double velx = vel.getX();
    	double vely = vel.getY();
    	
    	
        if (x > bx1){
            pos.change(bx1, y);
            vel.change(-velx, vely);
        } else if (x < bx0){
            pos.change(bx0, y);
            vel.change(-velx, vely);
        } else if (y > by1){
            pos.change(x, by1);
            vel.change(velx, -vely);
        } else if (y < by0){
            pos.change(x, by0);
            vel.change(velx, -vely);
        }
    }
    
    public static void solveCollision( Body b1, Body b2 ) {

    	Position x1 = b1.getPos();
    	Position x2 = b2.getPos();
    	Velocity v1 = b1.getVel();
    	Velocity v2 = b2.getVel();

    	double x12dx = x1.getX() - x2.getX();
    	double x12dy = x1.getY() - x2.getY();
    	double v12dx = v1.getX() - v2.getX();
    	double v12dy = v1.getY() - v2.getY();    	
    	double fact12 = (x12dx*v12dx + x12dy*v12dy) / (x12dx*x12dx + x12dy*x12dy);    	
    	double v1x = v1.getX() - x12dx*fact12;
    	double v1y = v1.getY() - x12dy*fact12;

    	double x21dx = x2.getX() - x1.getX();
    	double x21dy = x2.getY() - x1.getY();
    	double v21dx = v2.getX() - v1.getX();
    	double v21dy = v2.getY() - v1.getY();    	
    	double fact21 = (x21dx*v21dx + x21dy*v21dy) / (x21dx*x21dx + x21dy*x21dy);    	
    	double v2x = v2.getX() - x21dx*fact21;
    	double v2y = v2.getY() - x21dy*fact21;

    	b1.changeVel(v1x, v1y);
    	b2.changeVel(v2x, v2y);
    }
    
    public void locked( ) {
    	myLock.lock( );
    }
    
    public void unlocked( ) {
    	myLock.unlock( );
    }
}
