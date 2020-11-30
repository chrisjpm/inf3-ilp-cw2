package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

import com.mapbox.geojson.Point;

/**
 * App for initiating a drone flight
 * 
 * @author Chris Perceval-Maxwell (s1839592)
 *
 */

public class App {
	// Constant IP
	private static final String IP = "localhost";

	// Methods
	/**
	 * Write files
	 * 
	 * @param flightpath - An array of strings of all the moves
	 * @param readings   - A GeoJSON of the sensors read and flight path
	 * @param yyyy       - The year of the flight
	 * @param mm         - The month of the flight
	 * @param dd         - The day of the flight
	 * @throws IOException If file cannot be written
	 */
	static void writeFiles(String[] flightpath, String readings, String yyyy,
			String mm, String dd) {
		// Write the flight path file
		try {
			FileWriter myWriter = new FileWriter("ilp-results/flightpath-" + mm
					+ "-" + dd + "-" + yyyy + ".txt");
			for (int i = 0; i < flightpath.length - 1; i++) {
				myWriter.write(flightpath[i] + "\n");
			}
			myWriter.close();
			System.out.println("Flightpath text file successfully created!");
		} catch (IOException e) {
			System.out.println("Fatal error: Readings GeoJson wasn't created.");
			e.printStackTrace();
		}

		// Write the readings file
		try {
			FileWriter myWriter = new FileWriter("ilp-results/readings-" + mm
					+ "-" + dd + "-" + yyyy + ".geojson");
			myWriter.write(readings);
			myWriter.close();
			System.out.println("Readings GeoJson successfully created!");
		} catch (IOException e) {
			System.out.println("Fatal error: Readings GeoJson wasn't created.");
			e.printStackTrace();
		}
	}

	/**
	 * Main
	 * 
	 * @param args
	 *             <ul>
	 *             <li>[0] - Flight day</li>
	 *             <li>[1] - Flight month</li>
	 *             <li>[2] - Flight year</li>
	 *             <li>[3] - Starting latitude of the drone</li>
	 *             <li>[4] - Starting longitude of the drone</li>
	 *             <li>[5] - Seed for flights <b>[UNUSED]</b></li>
	 *             <li>[6] - Port for the server</li>
	 *             </ul>
	 */
	public static void main(String[] args) {
		// Take in arguments for the details of the flight
		var dd = args[0];
		var mm = args[1];
		var yyyy = args[2];
		var lat = Double.parseDouble(args[3]);
		var lng = Double.parseDouble(args[4]);
		var port = args[6];

		// Set up a connection to our choice of server and parser with it
		var httpConn = new HttpConnection(IP, port);
		var parser = new JsonParser(httpConn);

		// Create a new flight map and drone by parsing the JSON files and text
		// files on the server
		System.out.println("> CONNECTING TO SERVER...");
		var map = new Map(parser, yyyy, mm, dd);
		System.out.println("> CONNECTION COMPLETE!\n");
		var startPoint = Point.fromLngLat(lng, lat);
		var drone = new Drone(map, startPoint);

		// Start flight path (1 move costs 1 battery power)
		var moves = 0;
		System.out.println("FLIGHT BEGINING...");
		for (int i = 0; i < Drone.BATTERY_POWER; i++) {
			// Move drone 1 time
			drone.nextMove();
			moves = i + 1;

			// If we reach within 0.0003 of our starting position before 150
			// moves then end the flight
			if (drone.flightComplete)
				break;
		}
		System.out.println("> FLIGHT COMPLETE!\n");
		System.out.println("[Flight ended in " + moves + " moves]\n");

		// Write our files containing the:
		// 1. Flight Path - the series of moves taken by the drone
		// 2. Readings - a visual representation of the data collected by the
		// drone for GeoJSON
		System.out.println("> WRITING FILES...");
		var flightpath = map.getFlightPath(drone.getFlightPath(),
				drone.getBearings(), drone.getSensorsReadWords());
		var readings = map.getReadings(drone.getFlightPath());
		writeFiles(flightpath, readings, yyyy, mm, dd);
		System.out.println("> WRITING COMPLETE!");
	}
}