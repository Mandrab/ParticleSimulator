package main.model;

/**
 * Class for speed. 
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

    /**
     * Change speed to specified values
     * 
     * @param x
     * 		x axis value
     * @param y
     * 		y axis value
     */
    public void change( double x, double y ) {
    	this.x = x;
    	this.y = y;
    }

    /**
     * Get x speed
     * 
     * @return
     * 		speed on x axis
     */
    public double getX( ) {
    	return x;
    }

    /**
     * Get y speed
     * 
     * @return
     * 		speed on y axis
     */
    public double getY( ) {
    	return y;
    }

    /**
     * Get module of the speed
     * 
     * @return
     * 		speed module
     */
    public double getModule( ) {
    	return x * x + y * y;
    }
}
