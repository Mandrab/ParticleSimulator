package main.model;

/**
 * Boundary of the field where bodies move. 
 *
 * @author Baldini Paolo, Battistini Ylenia
 */
public class Boundary {

	private double x0;
	private double y0;
	private double x1;
	private double y1;

	public Boundary( double x0, double y0, double x1, double y1 ) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	/**
	 * Get west border
	 * 
	 * @return
	 * 		left x value
	 */
	public double getX0( ) {
		return x0;
	}

	/**
	 * Get east border
	 * 
	 * @return
	 * 		right x value
	 */
	public double getX1( ) {
		return x1;
	}

	/**
	 * Get south border
	 * 
	 * @return
	 * 		south x value
	 */
	public double getY0( ) {
		return y0;
	}

	/**
	 * Get north border
	 * 
	 * @return
	 * 		north x value
	 */
	public double getY1( ) {
		return y1;
	}
}
