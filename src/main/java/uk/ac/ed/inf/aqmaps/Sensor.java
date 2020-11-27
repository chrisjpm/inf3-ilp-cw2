package uk.ac.ed.inf.aqmaps;

import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Sensor {
	private static final double READ_SENSOR_RANGE = 0.0002;	
	private Location sensorPos;

	public Sensor(Location pos) {
		this.sensorPos = pos;
	}

	public boolean sensorInRange(Location dronePos) {
		var dronePoint = dronePos.getPoint();
		var sensorPoint = this.sensorPos.getPoint();
		var dist = Math.sqrt(Math.pow((sensorPoint.longitude() -
				dronePoint.longitude()), 2) +Math.pow((sensorPoint.latitude() -
				dronePoint.latitude()), 2));
		var inRange = dist <= READ_SENSOR_RANGE;

		return inRange;
	}
	
	public void collectReadings(Map map, Location dronePos, int targetIdx) {
			var sensor = map.getSensorsFts().get(targetIdx);
			sensor.addStringProperty("rgb-string",
					map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("fill",
					map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("marker-color",
					map.getMarkerColours().get(targetIdx));
			sensor.addStringProperty("marker-symbol",
					map.getMarkerSymbols().get(targetIdx));
			
			System.out.println("Sensor " + targetIdx + "'s readings collected!");
	}

}
