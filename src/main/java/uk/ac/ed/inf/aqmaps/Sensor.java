package uk.ac.ed.inf.aqmaps;

import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Sensor {
	private static final double READ_SENSOR_RANGE = 0.0002;	
	private Coords sensorPos;

	public Sensor(Coords pos) {
		this.sensorPos = pos;
	}

	public boolean sensorInRange(Coords dronePos) {
		var dronePoint = dronePos.getPoint();
		var sensorPoint = this.sensorPos.getPoint();
		var inRange = TurfMeasurement.distance(dronePoint, sensorPoint,
				TurfConstants.UNIT_DEGREES) <= READ_SENSOR_RANGE;

		return inRange;
	}
	
	public void collectReadings() {
		
	}

}
