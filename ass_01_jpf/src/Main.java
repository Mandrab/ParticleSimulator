/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class Main {

    public static void main(String[] args) throws InterruptedException{

    	Model model = new Model( );
    	model.initialize( 1000 );
        model.execute( 1000 );
		
    }
}
