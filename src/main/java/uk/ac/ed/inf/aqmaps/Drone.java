package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Drone {
	public static final int BATTERY_POWER = 150;
	public static final double MOVE_DIST = 0.0003;
	private Map map;
	public Coords dronePos;
	private List<Point> visitedPoss;
	public List<Point> line = new ArrayList<>();

	// Constructor
	public Drone(Map map, Coords startPos) {
		this.map = map;
		this.dronePos = startPos;
		this.visitedPoss = new ArrayList<Point>();
		
		this.visitedPoss.add(this.dronePos.getPoint());
	}
	
	// Getters
	public List<Point> getFlightPath() {
		return this.visitedPoss;
	}

	// Methods
	// Move drone to next position
	public void nextMove() {
		var prevPos = this.dronePos;
		var targetPos = new Coords(this.map, this.map.getSensorsCoords()[6][1],
				this.map.getSensorsCoords()[6][0]); // closest sensor & not visited
		
		// TODO: Move drone		
		var bearingToTarget = Math.round((TurfMeasurement.bearing(prevPos.getPoint(), targetPos.getPoint())/10))*10; // TODO: convert to E 000 and ACW
		System.out.println(bearingToTarget);
		
		var nextPos = TurfMeasurement.destination(prevPos.getPoint(), 0.0003, bearingToTarget, TurfConstants.UNIT_DEGREES); // TODO: make own destination method
		System.out.println(nextPos.toJson());

		this.line.add(prevPos.getPoint());
		
		prevPos.validDroneMove(nextPos);
		this.dronePos.setLat(nextPos.latitude());
		this.dronePos.setLng(nextPos.longitude());
		
		// TODO: Attempt to collect readings
		// ...
		
		//this.visitedPoss.add(this.dronePos.getPoint());
	}
	
	// Testing path and updating markers
	public void moveToSensors() {
		var startPos = this.dronePos.getPoint();
		this.visitedPoss.add(this.dronePos.getPoint()); // current pos

		for (int i = 0; i < Map.SENSORS; i++) {
			// Add current position to list of visited positions
			var nextPos = new Coords(this.map, this.map.getSensorsCoords()[i][1],
					this.map.getSensorsCoords()[i][0]);
			this.visitedPoss.add(nextPos.getPoint()); // since this would not actually be a loop, this would be dealt with at the end 

			// Update marker for sensor
			var sensor = this.map.getSensorsFts().get(i);
			sensor.addStringProperty("rgb-string",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("fill",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-color",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-symbol",
					this.map.getMarkerSymbols().get(i));
		}

		// Keep track of drone's path
		this.visitedPoss.add(startPos); // this is start pos jut to get back there for sake of testing
	}
}
