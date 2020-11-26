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
	public Coords dronePos, endPos;
	
	private Map map;
	private List<Point> visitedPoss;
	private int targetSensorCounter;
	private int[] route;
	

	// Constructor
	public Drone(Map map, Coords startPos, Coords endPos) {
		this.map = map;
		this.dronePos = startPos;
		this.endPos = endPos;
		this.targetSensorCounter = 0;
		this.visitedPoss = new ArrayList<Point>();
		this.route = new int[Map.SENSORS];	
		this.visitedPoss.add(this.dronePos.getPoint());
		this.flightComplete = false;
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
		var targetIdx = 0;
		Coords targetPos = null;

		if(this.targetSensorCounter == 33) {
			
			if(endInRange(this.dronePos)) {
				System.out.println(">>> Flight Complete");
				this.flightComplete = true;
				
				return; 
			}
			targetPos = this.endPos;
		} else {
			targetIdx = this.route[this.targetSensorCounter];
			targetPos = new Coords(this.map.getSensorsCoords()[targetIdx][1],
					this.map.getSensorsCoords()[targetIdx][0]);
		}
		
		var targetSensor = new Sensor(targetPos);
		
		// Move drone
		// TODO: own bearings method [MOVEMENT CLASS??]
		var bearingToTarget = Math.round((TurfMeasurement.bearing(prevPos.getPoint(), targetPos.getPoint())/10))*10; // TODO: convert to E 000 and ACW
		var moved = false;
		
		do {
			// TODO: own destination method
			var nextPos = TurfMeasurement.destination(prevPos.getPoint(), MOVE_DIST, bearingToTarget, TurfConstants.UNIT_DEGREES); // TODO: make own destination method
			
			if(prevPos.validDroneMove(this.map, nextPos)) {
				this.dronePos.setLat(nextPos.latitude());
				this.dronePos.setLng(nextPos.longitude());		
				moved = true;
			} else {
				bearingToTarget = bearingToTarget+10;
				moved = false;
			}
		} while (!moved);		
		
		// Attempt to collect readings
		// TODO: move this from drone to sensor class [MOVEMENT CLASS??]
		if(targetSensor.sensorInRange(this.dronePos) && this.targetSensorCounter <= 32) {
			var sensor = this.map.getSensorsFts().get(targetIdx);
			sensor.addStringProperty("rgb-string",
					this.map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("fill",
					this.map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("marker-color",
					this.map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("marker-symbol",
					this.map.getMarkerSymbols().get(targetIdx));
			
			System.out.println(">>> Sensor read! [" + this.targetSensorCounter + "]");
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

		return this.route;
	}
	
	public boolean endInRange(Coords dronePos) {
		var dronePoint = dronePos.getPoint();
		var endPoint = this.endPos.getPoint();
		var inRange = TurfMeasurement.distance(dronePoint, endPoint,
				TurfConstants.UNIT_DEGREES) <= MOVE_DIST;

		return inRange;
	}
	
}
