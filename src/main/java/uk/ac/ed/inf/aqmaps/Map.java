package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.Point;

public class Map {
	public static final int SENSORS = 33;
	
	private static final double LNG1 = -3.192473;
	private static final double LNG2 = -3.184319;
	private static final double LAT1 = 55.946233;
	private static final double LAT2 = 55.942617;
	
	private Feature confFt;
	private List<Feature> buildingsList;
	private List<Point> sensorsPoints = new ArrayList<Point>();
	private List<Feature> sensorsFts = new ArrayList<Feature>();

	// Constructor
	public Map(HttpConnection conn, String yyyy, String mm, String dd) {
		setUpMap(conn, yyyy, mm, dd);
	}

	// Getters
	public Feature getConfFt() {
		return confFt;
	}
	
	public List<Feature> getBuildingsFts() {
		return buildingsList;
	}
	
	public List<Point> getSensorsPoints() {
		return this.sensorsPoints;
	}

	public List<Feature> getSensorsFts() {
		return this.sensorsFts;
	}

	// Methods
	// Set up markers for sensors for a given date
	private void setUpMap(HttpConnection conn, String yyyy, String mm,
			String dd) {
		
		// Create parser
		var parser = new ParseJsonFiles(conn);

		// Confinement area points to feature collection
		var confinementPts = new ArrayList<>(Arrays.asList(
				Point.fromLngLat(LNG1, LAT1), Point.fromLngLat(LNG2, LAT1),
				Point.fromLngLat(LNG2, LAT2), Point.fromLngLat(LNG1, LAT2),
				Point.fromLngLat(LNG1, LAT1)));	
		var confMultiLine = MultiLineString
				.fromLngLats(List.of(confinementPts));	
		var confGeo = (Geometry) confMultiLine;
		this.confFt = Feature.fromGeometry(confGeo);

		// Parse buildings
		parser.readBuildings();
		this.buildingsList = parser.getBuildings().features();

		// Parse sensors
		parser.readMaps(yyyy, mm, dd);
		var sensorsWords = parser.getSensorWords();
		var sensorsSplit = new double[33][2];

		// Split the 3 words up and find their coordinates
		for (int i = 0; i < 33; i++) {
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			sensorsSplit[i][0] = parser.getWordsLng();
			sensorsSplit[i][1] = parser.getWordsLat();
		}

		// Convert sensors to features and add to a list
		for (var i = 0; i < SENSORS; i++) {
			var sensor = Point.fromLngLat(sensorsSplit[i][0],
					sensorsSplit[i][1]);
			this.sensorsPoints.add(sensor);
			var sensorGeo = (Geometry) sensor;
			var sensorFt = Feature.fromGeometry(sensorGeo);
			// Set up marker as not visited by default
			sensorFt.addStringProperty("rgb-string", "#aaaaaa");
			sensorFt.addStringProperty("fill", "#aaaaaa");
			sensorFt.addStringProperty("marker-symbol", "");
			this.sensorsFts.add(sensorFt);
		}
		
	}
}
