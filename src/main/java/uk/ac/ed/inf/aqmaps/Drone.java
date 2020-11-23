package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Drone {
	private Map map;
	private DronePosition dronePos, endPos;
	private ArrayList<Point> vistedSensors = new ArrayList<Point>();
	public static final int BATTERY_POWER = 150;
	public static final double MOVE_DIST = 0.0003;
	private static final double READ_SENSOR_ANGLE = 0.0002;

	// Constructor
	public Drone(Map map, DronePosition startPos) {
		this.map = map;
		this.dronePos = startPos;
		this.endPos = startPos;
		this.vistedSensors.add(null);
	}

	// Methods
	public void nextMove() {
		var currentPos = this.dronePos;
		
		if (this.vistedSensors.size() != 33) {
			var nextGoal = nearestSensorToDrone();
			System.out.println(nextGoal);
		} else {
			var nextGoal = this.endPos;
		}
		
		//pick direction
		//change position by angle to get to target and movement dist
	}

	public Point nearestSensorToDrone() {
		// Find dist to all points from current pos
		var sensorsPoints = this.map.getSensorsPoints();
		var dronePoint = Point.fromLngLat(this.dronePos.getLng(),
				this.dronePos.getLat());
		var minDist = TurfMeasurement.distance(dronePoint, sensorsPoints.get(0),
				TurfConstants.UNIT_DEGREES);
		var minIdx = 0;

		for (int i = 0; i < sensorsPoints.size(); i++) {
			if (!this.vistedSensors.contains(sensorsPoints.get(i))) {
				var dist = TurfMeasurement.distance(dronePoint,
						sensorsPoints.get(i), TurfConstants.UNIT_DEGREES);
				// if no fly zone isnt infront then
				if (dist < minDist) {
					minDist = dist;
					minIdx = i; 
				}
			}			
		}
		
		this.vistedSensors.add(sensorsPoints.get(minIdx));	
		return sensorsPoints.get(minIdx);
	}

}
