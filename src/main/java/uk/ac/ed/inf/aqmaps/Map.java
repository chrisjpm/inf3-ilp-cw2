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
	private List<String> markerColours, markerSymbols;
	private double[][] sensorsCoords = new double[SENSORS][2];
	private List<Feature> sensorsFts = new ArrayList<Feature>();

	// Constructor
	public Map(JsonParser parser, String yyyy, String mm, String dd) {
		setUpMap(parser, yyyy, mm, dd);
	}

	// Getters
	public Feature getConfFt() {
		return confFt;
	}
	
	public List<Feature> getBuildingsFts() {
		return buildingsList;
	}
	
	public double[][] getSensorsCoords() {
		return this.sensorsCoords;
	}
	
	public List<String> getMarkerColours() {
		return this.markerColours;
	}
	
	public List<String> getMarkerSymbols() {
		return this.markerSymbols;
	}

	public List<Feature> getSensorsFts() {
		return this.sensorsFts;
	}

	// Methods
	// Set up markers for sensors for a given date
	private void setUpMap(JsonParser parser, String yyyy, String mm,
			String dd) {

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
		this.sensorsCoords = new double[33][2];
		var sensorsBattery = parser.getSensorBatteries();
		var sensorsReading = parser.getSensorReadings();

		// 1. Split the 3 words up and find their coordinates
		// 2. Convert sensors to features and add to a list
		var sensorsPoints = new ArrayList<Point>();
		for (int i = 0; i < SENSORS; i++) {
			// 1
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			this.sensorsCoords[i][0] = parser.getWordsLng();
			this.sensorsCoords[i][1] = parser.getWordsLat();
			
			// 2
			var sensor = Point.fromLngLat(this.sensorsCoords[i][0],
					this.sensorsCoords[i][1]);
			sensorsPoints.add(sensor);
			var sensorGeo = (Geometry) sensor;
			var sensorFt = Feature.fromGeometry(sensorGeo);
			// Set up marker as not visited by default
			sensorFt.addStringProperty("rgb-string", "#aaaaaa");
			sensorFt.addStringProperty("fill", "#aaaaaa");
			sensorFt.addStringProperty("marker-symbol", "");
			this.sensorsFts.add(sensorFt);
		}
		
		// Get pollution data from all sensors
		var pollution = new PollutionLookUp();
		System.out.println(sensorsBattery);
		System.out.println(sensorsReading);
		pollution.lookUp(sensorsBattery, sensorsReading);
		this.markerColours = pollution.getMarkerColours();
		this.markerSymbols = pollution.getMarkerSymbols();
		
	}
}
