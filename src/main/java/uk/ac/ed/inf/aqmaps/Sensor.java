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
	private double battery;
	private String w3w, reading, symbol, colour;

	/**
	 * Sensor constructor
	 * 
	 * @param sensorLoc - Location of the target sensor
	 * @param w3w       - the what3words location of the sensor
	 * @param symbol    - marker symbol for GeoJSON
	 * @param colour    - marker colour for GeoJSON
	 */
	public Sensor(Location sensorLoc, String w3w, double battery,
			String reading, String symbol, String colour) {
		this.sensorLoc = sensorLoc;
		this.w3w = w3w;
		this.battery = battery;
		this.reading = reading;
		
		var pollution = new PollutionLookUp();
		pollution.lookUp(this.battery, this.reading);
		this.symbol = pollution.getMarkerSymbol();
		this.colour = pollution.getMarkerColour();
	}

	// Getters
	public Location getSensorLoc() {
		return this.sensorLoc;
	}

	public String getWhat3Words() {
		return this.w3w;
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
		var sensorFt = map.getSensorsFts().get(targetIdx);
		sensorFt.addStringProperty("rgb-string", this.colour);
		sensorFt.addStringProperty("marker-color", this.colour);
		sensorFt.addStringProperty("marker-symbol", this.symbol);

		System.out.println("Sensor " + targetIdx + "'s readings collected!");
	}

}
