package main;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class Controller {
	
	private final long REFRESH_RATE = 30;
	
	private View view;
	private Model model;

    public static void main(String[] args) throws InterruptedException {

    	new Controller( );
    }
    
    public Controller( ) throws InterruptedException {

    	//final int mBodies= 100;
    	//final int mBodies=1000;
    	final int mBodies = 5000;

    	//final int nSteps=500;
    	//final int nSteps=1000;
    	final int nSteps = 5000;

    	view = new View( 620, 620 );

    	model = new Model( );
    	model.initialize( mBodies );
    	new Updater( ).start( );

        System.out.println( "Elapsed time " + model.execute( nSteps ) );
	}

    private class Updater extends Thread {

    	@Override
    	public void run( ) {
    		while( model.isRunning( ) ) {
    			view.updateView( model.getState( ) );

    			try {
					Thread.sleep( REFRESH_RATE );
				} catch (InterruptedException e) { }
    		}
    	}
    }
}