package main;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class Main {

    public static void main(String[] args) {

    	Model model = new Model( );
    	model.initialize( 1000 );

        System.out.println( "Elapsed time " + model.execute( 1000 ) );
    }
}