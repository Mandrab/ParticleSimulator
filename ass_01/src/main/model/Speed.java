package main.model;

/**
 * Class for Velocity. 
 * Contains 2-dimensional vector
 * objects are completely state-less
 *
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Speed  {

    public double x, y;

    public Speed( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public void change( double x, double y ) {
    	this.x = x;
    	this.y = y;
    }
    
    public double getX( ) {
    	return x;
    }

    public double getY( ) {
    	return y;
    }
    
    public double getModule( ) {
    	return x * x + y * y;
    }
    
}
