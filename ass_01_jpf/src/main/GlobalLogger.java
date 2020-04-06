package main;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

public class GlobalLogger {

	private static GlobalLogger logger;

	private OutputStream stream;

	private boolean printLevel;

	public static synchronized GlobalLogger get( ) {
		if ( logger == null ) logger = new GlobalLogger( System.out );
		return logger;
	}

	private GlobalLogger( OutputStream stream ) {

		this.stream = stream;
		this.printLevel = false;
	}
	
	public void log( Level level, String msg ) {

		String out = msg + "\n";
		if ( printLevel ) out = level.getName( ) + msg;

		try {
			stream.write( out.getBytes( ) );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
	}

	public void setOutputStream( OutputStream stream ) {
		this.stream = stream;
	}

	public void enablePrintLevel( boolean printLevel ) {
		this.printLevel = printLevel;
	}
}
