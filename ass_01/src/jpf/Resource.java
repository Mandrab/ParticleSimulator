package jpf;

public class Resource {
	
	private boolean run ;
	private boolean steps;

	public synchronized void manage( ) throws InterruptedException {
		while ( ! run )
			wait( );

		if ( steps ) run = false;
	}

	public synchronized void start( ) {
       run = true;
       steps = false;
       notify( );
    }

    public synchronized void stop( ) {
    	run = false;
    	steps = false;
     }
    
    public synchronized void step( ) {
    	run = true;
        steps = true;
        notify( );
    }
}
