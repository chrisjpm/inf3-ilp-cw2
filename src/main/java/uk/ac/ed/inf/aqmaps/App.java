package uk.ac.ed.inf.aqmaps;

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
		System.out.println("--- CONNECTING TO SERVER ---------------------------------------------------------");
		var map = new Map(parser, yyyy, mm, dd);
		System.out.println("--- CONNECTION COMPLETE ----------------------------------------------------------\n");
		var startPos = new Location(lat, lng);
		var endPos = new Location(lat, lng);
		var drone = new Drone(map, startPos, endPos);

		// Start flight path (1 move costs 1 battery power)
		var moves = 0;
		System.out.println("--- FLIGHT BEGINING --------------------------------------------------------------");
		for (int i = 0; i < Drone.BATTERY_POWER; i++) {
			drone.nextMove();
			moves = i+1;
			if(drone.flightComplete) break;
		}
		System.out.println("--- FLIGHT COMPLETE --------------------------------------------------------------");
		System.out.println("[Flight ended in " + moves + " moves]");
		System.out.println("\nFlight Path: " + map.drawFlight(drone.getFlightPath()).toJson());	
	}
}
