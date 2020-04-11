package main;
/**
 * Class that implements all methods to be able to use body positions
 * 
 * @author Baldini Paolo, Battistini Ylenia 
 *
 */
public class Position { 

    private double x, y;

    public Position(double x,double y){
        this.x = x;
        this.y = y;
    }

    public void change(double x, double y){
    	this.x = x;
    	this.y = y;
    }
    
    public double getX() {
    	return x;
    }

    public double getY() {
    	return y;
    }
    
    public Position clone( ) {
    	return new Position( x, y );
    }
}
