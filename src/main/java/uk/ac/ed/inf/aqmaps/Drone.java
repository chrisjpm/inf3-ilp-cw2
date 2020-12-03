package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
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
	private final Location endLoc;
	private Location droneLoc;
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
		this.getRoute();
	}

	// Getters
	public List<Point> getVisitedPoints() {
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
		Sensor targetSensor = null;

		// If the drone has visited all sensors, make its way back to the start
		// Else, find the next closest sensor
		if (this.targetSensorCounter == 33) {
			if (this.endInRange(this.droneLoc)) {
				this.flightComplete = true;
				return;
			}
			targetLoc = this.endLoc;
		} else {
			targetIdx = this.route[this.targetSensorCounter];
			targetSensor = this.map.getSensors().get(targetIdx);
			targetLoc = targetSensor.getSensorLoc();
		}
		
		// Move drone and update its current Location
		var destination = this.droneLoc.moveDrone(this.map, targetLoc);
		this.droneLoc.setLat(destination.latitude());
		this.droneLoc.setLng(destination.longitude());

		// Attempt to collect readings if there are still ones unvisited
		if (targetSensor != null && targetSensor.sensorInRange(this.droneLoc)) {
			targetSensor.collectReadings(this.map, targetIdx);
			this.sensorsReadWords.add(targetSensor.getWhat3Words());
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
	private int[] getRoute() {
		// Make two local copies of the sensors so we don't modify the original
		var sensorsCopy = this.map.getSensors();
		var sensorsPoints = new ArrayList<Point>();
		var sensorsPointsCopy = new ArrayList<Point>();
		
		for (int i = 0; i < sensorsCopy.size(); i++) {
			sensorsPoints.add(sensorsCopy.get(i).getSensorLoc().getPoint());
			sensorsPointsCopy
					.add(sensorsCopy.get(i).getSensorLoc().getPoint());
		}

		// Find closest sensor and remove it from the list of ones unvisited and
		// add it to the route
		var nextSensor = TurfClassification
				.nearestPoint(this.droneLoc.getPoint(), sensorsPointsCopy);
		this.route[0] = sensorsPoints.indexOf(nextSensor);
		sensorsPointsCopy.remove(nextSensor);

		// Find the rest of the route
		for (int i = 1; i < Map.SENSORS; i++) {
			nextSensor = TurfClassification.nearestPoint(nextSensor,
					sensorsPointsCopy);
			this.route[i] = sensorsPoints.indexOf(nextSensor);
			sensorsPointsCopy.remove(nextSensor);
		}

		System.out.println("[Route of sensors to visit: "
				+ Arrays.toString(this.route) + "]\n");

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
	private boolean endInRange(Location droneLoc) {
		var dist = Math.sqrt(
				Math.pow((this.endLoc.getLng() - droneLoc.getLng()), 2) + Math
						.pow((this.endLoc.getLat() - droneLoc.getLat()), 2));
		var inRange = dist <= MOVE_DIST;

		return inRange;
	}

}
