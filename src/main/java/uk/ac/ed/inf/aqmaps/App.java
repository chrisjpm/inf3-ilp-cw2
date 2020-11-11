package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Chris Perceval-Maxwell (s1839592)
 *
 */

public class App {
	// Constants
	private static final String IP = "localhost";
	private static final String PORT = "80";

	// Methods
	// Write the flight path to a text file
	static void writeFlightPath(String flightpath, String yyy, String mm,
			String dd) {
		try (FileWriter myWriter = new FileWriter(
				"flightpath-" + dd + "-" + mm + "-" + yyy + ".txt")) {
			myWriter.write(flightpath);
			myWriter.close();
			System.out.println("Flight path text file successfully created!");
		} catch (IOException e) {
			System.out.println(
					"Fatal error: Flight path text file wasn't created!");
			e.printStackTrace();
		}
	}
	
	// Write the readings to a GeoJson file
	static void writeReadings(String readings, String yyy, String mm,
			String dd) {
		try (FileWriter myWriter = new FileWriter(
				"readings-" + dd + "-" + mm + "-" + yyy + ".geojson")) {
			myWriter.write(readings);
			myWriter.close();
			System.out.println("Flight path GeoJson successfully created!");
		} catch (IOException e) {
			System.out.println(
					"Fatal error: Flight path GeoJson wasn't created!");
			e.printStackTrace();
		}
	}

	// Main
	public static void main(String[] args) {
		// Set up a connection to our choice of server
		var httpConn = new HttpConnection(IP, PORT);

		// Start new flight path
		var flightPath = new FlightPath(httpConn);

		// Set up sensors for given date
		flightPath.setUp("2020", "01", "01");
		var sensorsFtColl = flightPath.getSensorsFtColl();
		System.out.println(sensorsFtColl.toJson());

	}
}
