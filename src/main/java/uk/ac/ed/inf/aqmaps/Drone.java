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
	
	private Location dronePos, endPos;	
	private Map map;
	private List<Point> visitedPoss;
	private int targetSensorCounter;
	private int[] route;
	

	// Constructor
	public Drone(Map map, Location startPos, Location endPos) {
		this.map = map;
		this.dronePos = startPos;
		this.endPos = endPos;
		this.targetSensorCounter = 0;
		this.visitedPoss = new ArrayList<Point>();
		this.route = new int[Map.SENSORS];	
		this.visitedPoss.add(this.dronePos.getPoint());
		this.flightComplete = false;
		
		// Initialise drone with a flight route
		getRoute();
	}
	
	// Getters
	public List<Point> getFlightPath() {
		return this.visitedPoss;
	}

	// Methods
	// Move drone to next position
	public void nextMove() {
		var prevPos = this.dronePos;
		
		// Choose a target for the drone
		var targetIdx = 0;
		Location targetPos = null;

		if(this.targetSensorCounter == 33) {		
			if(endInRange(this.dronePos)) {
				this.flightComplete = true;				
				return; 
			}
			targetPos = this.endPos;
		} else {
			targetIdx = this.route[this.targetSensorCounter];
			targetPos = this.map.getSensorsLocs().get(targetIdx);
		}
		
		var targetSensor = new Sensor(targetPos);
		
		// Move drone
		var destination = this.dronePos.moveDrone(this.map, prevPos, targetPos);
		this.dronePos.setLat(destination.latitude());
		this.dronePos.setLng(destination.longitude());	
		
		// Attempt to collect readings 
		if(targetSensor.sensorInRange(this.dronePos) && this.targetSensorCounter <= 32) {
			targetSensor.collectReadings(this.map, targetPos, targetIdx);
			this.targetSensorCounter++;
		}
		
		this.visitedPoss.add(this.dronePos.getPoint());
	}
	
	public int[] getRoute() {
		var route = new ArrayList<Point>();
		route.addAll(this.map.getSensorsPoints());
		
		var routeCopy = new ArrayList<Point>();
		routeCopy.addAll(this.map.getSensorsPoints());
		
		var nextSensor = TurfClassification.nearestPoint(
				this.dronePos.getPoint(), routeCopy);
		this.route[0] = routeCopy.indexOf(nextSensor);
		routeCopy.remove(routeCopy.indexOf(nextSensor));

		for (int i = 1; i < Map.SENSORS; i++) {
			nextSensor = TurfClassification.nearestPoint(
					nextSensor, routeCopy);
			this.route[i] = route.indexOf(nextSensor);
			routeCopy.remove(routeCopy.indexOf(nextSensor));
		}
		
		System.out.println("[Route of sensors to visit: " + route +"]");

		return this.route;
	}
	
	public boolean endInRange(Location dronePos) {
		var dronePoint = dronePos.getPoint();
		var endPoint = this.endPos.getPoint();
		var inRange = TurfMeasurement.distance(dronePoint, endPoint,
				TurfConstants.UNIT_DEGREES) < MOVE_DIST;

		return inRange;
	}
	
}
