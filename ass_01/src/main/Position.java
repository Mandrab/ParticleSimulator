package main;

/**
 * Class that implements all methods to be able to use body positions
 * 
 * @author Baldini Paolo, Battistini Ylenia 
 */
public class Position { 

    private double x, y;

    public Position( double x,double y ) {
        this.x = x;
        this.y = y;
    }

    /**
     * Change position to specified values
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
     * Get x coordinate
     * 
     * @return
     * 		position on x axis
     */
    public double getX( ) {
    	return x;
    }

    /**
     * Get y coordinate
     * 
     * @return
     * 		position on y axis
     */
    public double getY( ) {
    	return y;
    }

    /**
     * Clone this position
     * 
     * @return
     * 		a new position that's a clone of this
     */
    public Position clone( ) {
    	return new Position( x, y );
    }
}
