package main.model;

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
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
