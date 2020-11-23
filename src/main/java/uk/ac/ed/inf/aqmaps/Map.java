package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Map {
	private static final double LNG1 = -3.192473;
	private static final double LNG2 = -3.184319;
	private static final double LAT1 = 55.946233;
	private static final double LAT2 = 55.942617;
	private ArrayList<Point> sensorsPoints = new ArrayList<Point>();
	private Feature confFt;
	private List<Feature> buildingsList;
	private FeatureCollection sensorsFtColl;
	
	// Constructor
	public Map(HttpConnection conn, String yyyy, String mm, String dd) {
		this.sensorsFtColl = findSensors(conn, yyyy, mm, dd);
		System.out.println(this.sensorsFtColl.toJson());
	}
	
	public ArrayList<Point> getSensorsPoints(){
		return this.sensorsPoints;
	}

	// Methods
	// Set up markers for sensors for a given date
	private FeatureCollection findSensors(HttpConnection conn, String yyyy,
			String mm, String dd) {
		var allFts = new ArrayList<Feature>();
		// Confinement area points to feature collection
		var confinementPts = new ArrayList<>(Arrays.asList(
				Point.fromLngLat(LNG1, LAT1), Point.fromLngLat(LNG2, LAT1),
				Point.fromLngLat(LNG2, LAT2), Point.fromLngLat(LNG1, LAT2),
				Point.fromLngLat(LNG1, LAT1)));
		var confPoly = Polygon.fromLngLats(List.of(confinementPts));
		var confGeo = (Geometry) confPoly;
		this.confFt = Feature.fromGeometry(confGeo);
		this.confFt.addNumberProperty("fill-opacity", 0);
		allFts.add(this.confFt);

		// Create parser
		var parser = new ParseJsonFiles(conn);

		// Parse buildings
		parser.readBuildings();
		this.buildingsList = parser.getBuildings().features();
		allFts.addAll(this.buildingsList);

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

		// Place sensors on map
		for (var i = 0; i < 33; i++) {
			var sensor = Point.fromLngLat(sensorsSplit[i][0],
					sensorsSplit[i][1]);
			this.sensorsPoints.add(sensor);
			var sensorGeo = (Geometry) sensor;
			var sensorFt = Feature.fromGeometry(sensorGeo);
			// Set up marker as not visited by default
			sensorFt.addStringProperty("rgb-string", "#aaaaaa");
			sensorFt.addStringProperty("fill", "#aaaaaa");
			sensorFt.addStringProperty("marker-symbol", "");
			allFts.add(sensorFt);
		}

		// Sensors feature collection
		return FeatureCollection.fromFeatures(allFts);
	}
}
