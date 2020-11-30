package uk.ac.ed.inf.aqmaps;

/**
 * Class to control sensors
 * 
 * @author Chris
 *
 */

public class Sensor {
	// Private constants and variables
	private static final double READ_SENSOR_RANGE = 0.0002;
	private Location sensorLoc;

	/**
	 * Sensor constructor
	 * 
	 * @param targetLoc - Location of the target sensor
	 */
	public Sensor(Location sensorLoc) {
		this.sensorLoc = sensorLoc;
	}

	/**
	 * Determine if drone can take reading off sensor
	 * 
	 * @param droneLoc - Location of the drone
	 * @return Truth value of the drone's Location is within range of the target
	 *         Sensor
	 */
	public boolean sensorInRange(Location droneLoc) {
		var dronePoint = droneLoc.getPoint();
		var sensorPoint = this.sensorLoc.getPoint();
		var dist = Math.sqrt(Math
				.pow((sensorPoint.longitude() - dronePoint.longitude()), 2)
				+ Math.pow((sensorPoint.latitude() - dronePoint.latitude()),
						2));
		var inRange = dist <= READ_SENSOR_RANGE;

		return inRange;
	}

	/**
	 * Collect the drones readings if within range
	 * 
	 * @param map       - The map the drone is navigating
	 * @param targetIdx - Index value of the target sensor in the original list
	 */
	public void collectReadings(Map map, int targetIdx) {
		// Take sensor's data and look up what colour and marker to assign
		var sensor = map.getSensorsFts().get(targetIdx);
		sensor.addStringProperty("rgb-string",
				map.getMarkerColours().get(targetIdx));
		sensor.addStringProperty("fill", map.getMarkerColours().get(targetIdx));
		sensor.addStringProperty("marker-color",
				map.getMarkerColours().get(targetIdx));
		sensor.addStringProperty("marker-symbol",
				map.getMarkerSymbols().get(targetIdx));

		System.out.println("Sensor " + targetIdx + "'s readings collected!");
	}

}
