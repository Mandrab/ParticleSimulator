package main;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class Main {

    public static void main(String[] args) {

    	//final int mBodies= 100;
    	final int mBodies=1000;
    	//final int mBodie=5000;
    	//final int nSteps=500;
    	final int nSteps=1000;
    	//final int nSteps=5000;
    	
    	Model model = new Model( );
    	model.initialize( mBodies );

        System.out.println( "Elapsed time " + model.execute( nSteps ) );
    }
}