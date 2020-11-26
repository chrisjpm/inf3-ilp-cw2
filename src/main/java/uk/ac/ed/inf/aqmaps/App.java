package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

/**
 * 
 * @author Chris Perceval-Maxwell (s1839592)
 *
 */

public class App {
	// Constants
	private static final String IP = "localhost";

	// Main
	public static void main(String[] args) {
		// Assign args to vars
		var dd = args[0];
		var mm = args[1];
		var yyyy = args[2];
		var lat = Double.parseDouble(args[3]);
		var lng = Double.parseDouble(args[4]);
		// var seed = Integer.parseInt(args[5]);
		var port = args[6];

		// Set up a connection to our choice of server
		var httpConn = new HttpConnection(IP, port);

		// Create parser
		var parser = new JsonParser(httpConn);

		// Create a new flight map and drone
		var map = new Map(parser, yyyy, mm, dd);
		var startPos = new Coords(map, lat, lng);
		var drone = new Drone(map, startPos);
		var drone2 = new Drone(map, startPos);
		Coords target = new Coords(map, 55.9435, -3.1877); // testing valid moves with start postion to first move
		startPos.validDroneMove(target.getPoint());

		// Start flight path (1 move costs 1 battery power)
		for (int i = 0; i < Drone.BATTERY_POWER-115; i++) {
			drone.nextMove();
		}
		System.out.println(">>>> " + (LineString.fromLngLats(drone.line)).toJson());
		var targetPos = new Coords(map, map.getSensorsCoords()[6][1],
				map.getSensorsCoords()[6][0]);
		System.out.println(">>>> " + targetPos.getPoint().toJson());

		drone2.moveToSensors();
		System.out.println("###########\nTesting going to all sensors and collecting readings:");
		System.out.println(map.drawPath(drone.getFlightPath()).toJson());
	}
}
