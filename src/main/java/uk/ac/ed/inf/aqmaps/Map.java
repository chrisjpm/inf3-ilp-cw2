package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Map {
	public static final int SENSORS = 33;
	private static final double LNG1 = -3.192473;
	private static final double LNG2 = -3.184319;
	private static final double LAT1 = 55.946233;
	private static final double LAT2 = 55.942617;
	private List<Point> confPoints;
	private List<Geometry> noFlyZones;
	private double[][] sensorsCoords;
	private List<Feature> sensorsFts;
	private List<String> markerColours, markerSymbols;

	// Constructor
	public Map(JsonParser parser, String yyyy, String mm, String dd) {
		this.confPoints = new ArrayList<Point>();
		this.noFlyZones = new ArrayList<Geometry>();
		this.sensorsCoords = new double[SENSORS][2];
		this.sensorsFts = new ArrayList<Feature>();

		setUpMap(parser, yyyy, mm, dd);
	}

	// Getters
	public List<Point> getConfPoints() {
		return confPoints;
	}

	public List<Geometry> getNoFlyZones() {
		return this.noFlyZones;
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
	private void setUpMap(JsonParser parser, String yyyy, String mm,
			String dd) {

		// Confinement area points to feature collection
		this.confPoints = new ArrayList<>(Arrays.asList(
				Point.fromLngLat(LNG1, LAT1), Point.fromLngLat(LNG2, LAT1),
				Point.fromLngLat(LNG2, LAT2), Point.fromLngLat(LNG1, LAT2),
				Point.fromLngLat(LNG1, LAT1)));
		var x = LineString.fromLngLats(confPoints);
		System.out.println(x.toJson());
		System.out.println("hfdgfcujxch");

		// Parse buildings
		parser.readBuildings();
		var buildingsList = parser.getBuildings();
		for (int i = 0; i < buildingsList.size(); i++) {
			this.noFlyZones.add(buildingsList.get(i).geometry());
		}

		// Parse sensors
		parser.readMaps(yyyy, mm, dd);
		var sensorsBattery = parser.getSensorBatteries();
		var sensorsReading = parser.getSensorReadings();

		var sensorsWords = parser.getSensorWords();
		this.sensorsCoords = new double[33][2];
		var sensorsPoints = new ArrayList<Point>();

		for (int i = 0; i < SENSORS; i++) {
			// Split the 3 words up and find their coordinates
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			this.sensorsCoords[i][0] = parser.getWordsLng();
			this.sensorsCoords[i][1] = parser.getWordsLat();

			// Convert sensors to features and add to a list
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
		pollution.lookUp(sensorsBattery, sensorsReading);
		this.markerColours = pollution.getMarkerColours();
		this.markerSymbols = pollution.getMarkerSymbols();

	}

	// Draw the flight path
	public FeatureCollection drawPath(List<Point> dronePos) {
		var flightLineString = LineString.fromLngLats(dronePos);
		var flightGeo = (Geometry) flightLineString;
		var flightFt = Feature.fromGeometry(flightGeo);

		var flightList = new ArrayList<Feature>();
		flightList.add(flightFt);
		flightList.addAll(getSensorsFts());
		var flightFtColl = FeatureCollection.fromFeatures(flightList);

		return flightFtColl;
	}
}
