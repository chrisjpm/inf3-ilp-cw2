package uk.ac.ed.inf.aqmaps;

import java.util.Arrays;

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
		var flightPath = new FlightPath(httpConn);
		flightPath.setUp();
		var sensorsCoords = flightPath.getSensorsCoords();
		System.out.println(Arrays.deepToString(sensorsCoords));
		var sensorsFtColl = flightPath.getSensorsFtColl();
		System.out.println(sensorsFtColl.toJson());
		
	}
}
