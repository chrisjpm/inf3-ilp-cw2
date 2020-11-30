package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfClassification;

/**
 * Class to control the drone
 * 
 * @author Chris
 *
 */

public class Drone {
	// Constants and variables useful for multiple classes
	public static final int BATTERY_POWER = 150;
	public static final double MOVE_DIST = 0.0003;
	public boolean flightComplete;

	// Private variables
	private Location droneLoc;
	private final Location endLoc;
	private Map map;
	private List<Point> visitedPoints;
	private int targetSensorCounter;
	private int[] route;
	private List<Integer> bearings;
	private List<String> sensorsReadWords;

	/**
	 * Drone constructor
	 * 
	 * @param map      - The map created by (sensors, confinement area, no fly
	 *                 zones)
	 * @param startLoc - Point where drone starts its flight
	 */
	public Drone(Map map, Point startLoc) {
		this.map = map;
		this.endLoc = new Location(startLoc.latitude(), startLoc.longitude());
		this.droneLoc = new Location(startLoc.latitude(), startLoc.longitude());
		this.targetSensorCounter = 0;
		this.visitedPoints = new ArrayList<Point>();
		this.route = new int[Map.SENSORS];
		this.bearings = new ArrayList<Integer>();
		this.sensorsReadWords = new ArrayList<String>();

		// Initialise drone with a flight route
		this.visitedPoints.add(this.droneLoc.getPoint());
		this.flightComplete = false;
		getRoute();
	}

	// Getters
	public List<Point> getFlightPath() {
		return this.visitedPoints;
	}

	public List<Integer> getBearings() {
		return this.bearings;
	}

	public List<String> getSensorsReadWords() {
		return this.sensorsReadWords;
	}

	// Methods
	/**
	 * Move drone to next position
	 */
	public void nextMove() {
		// Choose a target for the drone
		var targetIdx = 0;
		Location targetLoc = null;

		// If the drone has visited all sensors, make its way back to the start
		// Else, find the next closest sensor
		if (this.targetSensorCounter == 33) {
			if (endInRange(this.droneLoc)) {
				this.flightComplete = true;
				return;
			}
			targetLoc = this.endLoc;
		} else {
			targetIdx = this.route[this.targetSensorCounter];
			targetLoc = this.map.getSensorsLocs().get(targetIdx);
		}
		var targetSensor = new Sensor(targetLoc);

		// Move drone and update its current Location
		var destination = this.droneLoc.moveDrone(this.map, this.droneLoc,
				targetLoc);
		this.droneLoc.setLat(destination.latitude());
		this.droneLoc.setLng(destination.longitude());

		// Attempt to collect readings if there are still ones unvisited
		if (targetSensor.sensorInRange(this.droneLoc)
				&& this.targetSensorCounter <= 32) {
			targetSensor.collectReadings(this.map, targetIdx);
			this.sensorsReadWords
					.add(this.map.getSensorsWords().get(targetIdx));
			this.targetSensorCounter++;
		} else {
			this.sensorsReadWords.add(null);
		}

		this.visitedPoints.add(this.droneLoc.getPoint());
		this.bearings.add(this.droneLoc.getBearing());
	}

	/**
	 * Finds a route of sensors using a Greedy Best-First approach
	 * 
	 * @return The route of sensors the drone should visit
	 */
	public int[] getRoute() {
		// Make two local copies of the sensors so we don't modify the original
		var sensors = new ArrayList<Point>(); // Original indexing of sensors
		var sensorsCopy = new ArrayList<Point>(); // Unvisited sensors
		sensors.addAll(this.map.getSensorsPoints());
		sensorsCopy.addAll(this.map.getSensorsPoints());

		// Find closest sensor and remove it from the list of ones unvisited and
		// add it to the route
		var nextSensor = TurfClassification
				.nearestPoint(this.droneLoc.getPoint(), sensorsCopy);
		this.route[0] = sensors.indexOf(nextSensor);
		sensorsCopy.remove(sensorsCopy.indexOf(nextSensor));

		// Find the rest of the route
		for (int i = 1; i < Map.SENSORS; i++) {
			nextSensor = TurfClassification.nearestPoint(nextSensor,
					sensorsCopy);
			this.route[i] = sensors.indexOf(nextSensor);
			sensorsCopy.remove(sensorsCopy.indexOf(nextSensor));
		}

		System.out.println("[Route of sensors to visit: " + route + "]\n");

		return this.route;
	}

	/**
	 * Determine whether the end location is close enough to terminate the
	 * flight
	 * 
	 * @param droneLoc - Current Location of the drone
	 * @return the truth value of whether it's in range of the end point (i.e.
	 *         where it started).
	 */
	public boolean endInRange(Location droneLoc) {
		var dist = Math.sqrt(
				Math.pow((this.endLoc.getLng() - droneLoc.getLng()), 2) + Math
						.pow((this.endLoc.getLat() - droneLoc.getLat()), 2));
		var inRange = dist <= MOVE_DIST;

		return inRange;
	}

}
