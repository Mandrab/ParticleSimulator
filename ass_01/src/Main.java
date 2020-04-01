/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class Main {

    public static void main(String[] args) {

    	long startTime = System.currentTimeMillis( );
    	Model model = new Model( );
    	model.execute( 100, 500 );
    	long stopTime = System.currentTimeMillis( );
        
        System.out.println( "Process speedup " + ( stopTime - startTime ) );
    }
    
    
}

