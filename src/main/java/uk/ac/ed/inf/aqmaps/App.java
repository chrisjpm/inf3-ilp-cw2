package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

import com.mapbox.geojson.Point;

/**
 * 
 * @author Chris Perceval-Maxwell (s1839592)
 *
 */

public class App {
	// Constants
	private static final String IP = "localhost";

	static void writeFiles(String[] flightpath, String readings, String yyyy,
			String mm, String dd) {
		try {
			FileWriter myWriter = new FileWriter("ilp-results/flightpath-" + mm
					+ "-" + dd + "-" + yyyy + ".txt");
			for (int i = 0; i < flightpath.length-1; i++) {
				myWriter.write(flightpath[i]+"\n");
			}			
			myWriter.close();
			System.out.println("Flightpath text file successfully created!");
		} catch (IOException e) {
			System.out.println("Fatal error: Readings GeoJson wasn't created.");
			e.printStackTrace();
		}

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

	// Main
	public static void main(String[] args) {
		var dd = args[0];
		var mm = args[1];
		var yyyy = args[2];
		var lat = Double.parseDouble(args[3]);
		var lng = Double.parseDouble(args[4]);
		var port = args[6];

		// Set up a connection to our choice of server
		var httpConn = new HttpConnection(IP, port);

		// Create parser
		var parser = new JsonParser(httpConn);

		// Create a new flight map and drone
		System.out.println(
				"--- CONNECTING TO SERVER ---------------------------------------------------------");
		var map = new Map(parser, yyyy, mm, dd);
		System.out.println(
				"--- CONNECTION COMPLETE ----------------------------------------------------------\n");
		var startPoint = Point.fromLngLat(lng, lat);
		var drone = new Drone(map, startPoint);

		// Start flight path (1 move costs 1 battery power)
		var moves = 0;
		System.out.println(
				"--- FLIGHT BEGINING --------------------------------------------------------------");
		for (int i = 0; i < Drone.BATTERY_POWER; i++) {
			drone.nextMove();
			moves = i + 1;
			if (drone.flightComplete)
				break;
		}
		System.out.println(
				"--- FLIGHT COMPLETE --------------------------------------------------------------");
		System.out.println("[Flight ended in " + moves + " moves]\n");

		System.out.println(
				"--- WRITING FILES ----------------------------------------------------------------");
		var flightpath = map.getFlightPath(drone.getFlightPath(), drone.getBearings(), drone.getSensorsReadWords());
		var readings = map.getReadings(drone.getFlightPath());
		writeFiles(flightpath, readings, yyyy, mm, dd);
		System.out.println(
				"--- WRITING COMPLETE -------------------------------------------------------------");
	}
}