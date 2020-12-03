package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * Class to create the map for the drone to navigate
 * 
 * @author Chris
 *
 */
public class Map {
	// Variables useful for multiple classes
	public static final int SENSORS = 33;

	// Private constants and variables
	private static final double LNG1 = -3.192473;
	private static final double LNG2 = -3.184319;
	private static final double LAT1 = 55.946233;
	private static final double LAT2 = 55.942617;
	private List<Point> confPoints;
	private List<Geometry> noFlyZones;
	private List<Feature> sensorsFts;
	private List<Sensor> sensors;

	/**
	 * Map constructor
	 * 
	 * @param parser - The object needed to parse we need files on the server
	 * @param yyyy   - The flight year
	 * @param mm     - The flight month
	 * @param dd     - The flight day
	 */
	public Map(JsonParser parser, String yyyy, String mm, String dd) {
		this.confPoints = new ArrayList<Point>();
		this.noFlyZones = new ArrayList<Geometry>();
		this.sensorsFts = new ArrayList<Feature>();
		this.sensors = new ArrayList<Sensor>();

		// Create map on object creation
		setUpMap(parser, yyyy, mm, dd);
	}

	// Getters
	public List<Point> getConfPoints() {
		return confPoints;
	}

	public List<Geometry> getNoFlyZones() {
		return this.noFlyZones;
	}

	public List<Feature> getSensorsFts() {
		return this.sensorsFts;
	}
	
	public List<Sensor> getSensors() {
		return this.sensors;
	}

	// Methods
	/**
	 * Set up the map to the given date
	 * 
	 * @param parser - The object needed to parse we need files on the server
	 * @param yyyy   - The flight year
	 * @param mm     - The flight month
	 * @param dd     - The flight day
	 */
	private void setUpMap(JsonParser parser, String yyyy, String mm,
			String dd) {

		// Confinement area points to feature collection
		this.confPoints = new ArrayList<>(Arrays.asList(
				Point.fromLngLat(LNG1, LAT1), Point.fromLngLat(LNG2, LAT1),
				Point.fromLngLat(LNG2, LAT2), Point.fromLngLat(LNG1, LAT2),
				Point.fromLngLat(LNG1, LAT1)));

		// Parse buildings
		parser.readBuildings();
		var buildingsList = parser.getBuildings();
		for (int i = 0; i < buildingsList.size(); i++) {
			this.noFlyZones.add(buildingsList.get(i).geometry());
		}

		// Parse sensors
		parser.readMaps(yyyy, mm, dd);
		System.out.println("What3Words Data fetched!");	
		
		for (int i = 0; i < SENSORS; i++) {
			var battery = parser.getSensorBatteries().get(i);
			var reading = parser.getSensorReadings().get(i);
			var w3w = parser.getSensorWords().get(i);
			
			// Split the 3 words up and find their coordinates and points		
			var words = w3w.split("\\.");
			parser.readWords(words[0], words[1], words[2]);

			var sensorLoc = new Location(parser.getWordsLat(),
					parser.getWordsLng());
			
			// Make new sensor
			var sensor = new Sensor(sensorLoc, w3w, battery, reading);
			this.sensors.add(sensor);

			// Convert sensors to features and add to a list
			var sensorGeo = (Geometry) sensor.getSensorLoc().getPoint();
			var sensorFt = Feature.fromGeometry(sensorGeo);

			// Set up marker as not visited by default
			this.sensorsFts.add(sensorFt);	
		}
	}

	/**
	 * Create the flight path
	 * 
	 * @param dronePoints - The points visited in sequential order
	 * @param bearings    - The bearings chosen in sequential order
	 * @param words       - The 3 word location of the sensors visited
	 * @return An array of strings of all the moves
	 */
	public String[] getFlightPath(List<Point> dronePoints,
			List<Integer> bearings, List<String> words) {
		String[] lines = new String[dronePoints.size()];

		// Add each move to a new entry in the array
		// Loop the size -1 since the last point is the destination, not a move
		for (int i = 0; i < dronePoints.size() - 1; i++) {
			lines[i] = (i + 1) + "," + dronePoints.get(i).longitude() + ","
					+ dronePoints.get(i).latitude() + "," + bearings.get(i)
					+ "," + dronePoints.get(i + 1).longitude() + ","
					+ dronePoints.get(i + 1).latitude() + "," + words.get(i);
		}

		return lines;
	}

	/**
	 * Create the readings GeoJson
	 * 
	 * @param dronePoints - The points visited in sequential order
	 * @return The readings GeoJson
	 */
	public String getReadings(List<Point> dronePoints) {
		// Create line string between all visited points and convert to Feature
		var flightLineString = LineString.fromLngLats(dronePoints);
		var flightGeo = (Geometry) flightLineString;
		var flightFt = Feature.fromGeometry(flightGeo);

		// Add the flight path and the sensors to a Feature Collection
		var flightList = new ArrayList<Feature>();
		flightList.add(flightFt);
		flightList.addAll(getSensorsFts());
		var flightFtColl = FeatureCollection.fromFeatures(flightList);

		// Convert to JSON format
		return flightFtColl.toJson();
	}
}
