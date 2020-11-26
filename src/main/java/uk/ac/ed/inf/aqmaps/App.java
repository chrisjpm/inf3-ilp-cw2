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
		startPos.validDroneMove(target);
		
		//testing
		drone.dronePos = startPos.getPoint();
		
		List<Point> line = new ArrayList<>(Arrays.asList(
				startPos.getPoint(), target.getPoint()));
		System.out.println((LineString.fromLngLats(line)).toJson());
		
		var bearingToTarget = Math.round((TurfMeasurement.bearing(drone.dronePos, target.getPoint())/10))*10; // need to dela with east being 0deg
		System.out.println(bearingToTarget);
		
		var nextPos = TurfMeasurement.destination(drone.dronePos, 0.0003, bearingToTarget, TurfConstants.UNIT_DEGREES);
		System.out.println(nextPos.toJson());
		
		List<Point> line2 = new ArrayList<>(Arrays.asList(
				startPos.getPoint(), nextPos));
		System.out.println((LineString.fromLngLats(line2)).toJson());
		
		drone.dronePos = nextPos;
		
		bearingToTarget = Math.round((TurfMeasurement.bearing(drone.dronePos, target.getPoint())/10))*10; // need to dela with east being 0deg
		System.out.println(bearingToTarget);
		
		nextPos = TurfMeasurement.destination(drone.dronePos, 0.0003, bearingToTarget, TurfConstants.UNIT_DEGREES);
		System.out.println(nextPos.toJson());
		
		line2.add(nextPos);
		System.out.println((LineString.fromLngLats(line2)).toJson());
		
		

		// Start flight path (1 move costs 1 battery power)
//		for (int i = 0; i < Drone.BATTERY_POWER; i++) {
//			drone.nextMove();
//		}

		drone2.moveToSensors();
		System.out.println(map.drawPath(drone.getFlightPath()).toJson());
	}
}
