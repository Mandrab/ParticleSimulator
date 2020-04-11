package main;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Class that implements the GlobalLogger, allows you to print in the console
 * the strings specifying the importance level and the text to be printed.
 * 
 * @author Baldini Paolo, Battistini Ylenia 
 */
public class GlobalLogger {

	private static GlobalLogger logger; // the common application logger (singleton)

	private OutputStream stream;		// the output stream in which write

	private boolean printLevel;			// print the level of importance of the message

	/**
	 * Return the singleton logger
	 * 
	 * @return
	 * 		the common logger
	 */
	public static synchronized GlobalLogger get( ) {
		if ( logger == null ) logger = new GlobalLogger( System.out );
		return logger;
	}

	private GlobalLogger( OutputStream stream ) {

		this.stream = stream;
		this.printLevel = false;
	}

	/**
	 * Print message with specified level of importance
	 * 
	 * @param level
	 * 		importance level
	 * @param msg
	 * 		the message to write
	 */
	public void log( Level level, String msg ) {

		String out = msg + "\n";
		if ( printLevel ) out = level.getName( ) + msg;

		try {
			stream.write( out.getBytes( ) );
		} catch ( IOException e ) {
			e.printStackTrace( );
		}
	}

	/**
	 * Change the output stream on which write
	 * 
	 * @param stream
	 * 		stream on which write
	 */
	public void setOutputStream( OutputStream stream ) {
		this.stream = stream;
	}

	/**
	 * Specify if the importance level should be printed
	 * 
	 * @param printLevel
	 * 		true: print the level
	 * 		false: does not print the level
	 */
	public void enablePrintLevel( boolean printLevel ) {
		this.printLevel = printLevel;
	}
}
