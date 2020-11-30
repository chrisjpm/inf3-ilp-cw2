package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfClassification;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Drone {
	public static final int BATTERY_POWER = 150;
	public static final double MOVE_DIST = 0.0003;

	public boolean flightComplete;

	private Location droneLoc;
	private final Location endLoc;
	private Map map;
	private List<Point> visitedPoints;
	private int targetSensorCounter;
	private int[] route;
	private List<Long> bearings;
	private List<String> sensorsReadWords;

	// Constructor
	public Drone(Map map, Point startLoc) {
		this.map = map;
		this.endLoc = new Location(startLoc.latitude(), startLoc.longitude());
		this.droneLoc = new Location(startLoc.latitude(), startLoc.longitude());
		this.targetSensorCounter = 0;
		this.visitedPoints = new ArrayList<Point>();
		this.route = new int[Map.SENSORS];	
		this.bearings = new ArrayList<Long>();
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
	
	public List<Long> getBearings() {
		return this.bearings;
	}
	
	public List<String> getSensorsReadWords() {
		return this.sensorsReadWords;
	}


	// Methods
	// Move drone to next position
	public void nextMove() {
		// Choose a target for the drone
		var targetIdx = 0;
		Location targetLoc = null;

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

		// Move drone
		var destination = this.droneLoc.moveDrone(this.map, this.droneLoc,
				targetLoc);
		this.droneLoc.setLat(destination.latitude());
		this.droneLoc.setLng(destination.longitude());

		// Attempt to collect readings
		if (targetSensor.sensorInRange(this.droneLoc)
				&& this.targetSensorCounter <= 32) {
			targetSensor.collectReadings(this.map, targetIdx);
			this.sensorsReadWords.add(this.map.getSensorsWords().get(targetIdx));
			this.targetSensorCounter++;
		} else {
			this.sensorsReadWords.add(null);
		}
	
		this.visitedPoints.add(this.droneLoc.getPoint());
		this.bearings.add(droneLoc.getBearing());
		System.out.println(droneLoc.getBearing());
	}

	public int[] getRoute() {
		var route = new ArrayList<Point>();
		route.addAll(this.map.getSensorsPoints());

		var routeCopy = new ArrayList<Point>();
		routeCopy.addAll(this.map.getSensorsPoints());

		var nextSensor = TurfClassification
				.nearestPoint(this.droneLoc.getPoint(), routeCopy);
		this.route[0] = routeCopy.indexOf(nextSensor);
		routeCopy.remove(routeCopy.indexOf(nextSensor));

		for (int i = 1; i < Map.SENSORS; i++) {
			nextSensor = TurfClassification.nearestPoint(nextSensor, routeCopy);
			this.route[i] = route.indexOf(nextSensor);
			routeCopy.remove(routeCopy.indexOf(nextSensor));
		}

		System.out.println("[Route of sensors to visit: " + route + "]");

		return this.route;
	}

	public boolean endInRange(Location droneLoc) {
		var dronePoint = droneLoc.getPoint();
		var endPoint = this.endLoc.getPoint();
		var inRange = TurfMeasurement.distance(dronePoint, endPoint,
				TurfConstants.UNIT_DEGREES) < MOVE_DIST;

		return inRange;
	}

}
