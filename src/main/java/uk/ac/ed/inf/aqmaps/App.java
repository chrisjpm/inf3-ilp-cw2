package uk.ac.ed.inf.aqmaps;

/**
 * 
 * @author Chris Perceval-Maxwell (s1839592)
 *
 */

public class App {
	// Constants
	private static final String IP = "localhost";
	private static final String PORT = "80";

	public static void main(String[] args) {
		// Set up a connection to our choice of server
		var httpConn = new HttpConnection(IP, PORT);

		// Parse all required files
		var parser = new ParseJsonFiles(httpConn);
		//parser.readBuildings();
		//parser.getBuildings();
		parser.readMaps("2020", "01", "01");
		parser.readWords("acid", "chair", "butter");
	}
}
