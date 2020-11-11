package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class FlightPath {
	private HttpConnection conn;
	private FeatureCollection sensorsFtColl;

	// Constructor
	public FlightPath(HttpConnection conn) {
		this.conn = conn;
	}

	// Getters	
	public FeatureCollection getSensorsFtColl() {
		return this.sensorsFtColl;
	}

	// Methods
	// Set up markers for sensors for a given date
	public void setUp(String yyyy, String mm, String dd) {
		var allFts = new ArrayList<Feature>();
		// Confinement area points to feature collection
		var confinementPts = new ArrayList<>(Arrays.asList(
				Point.fromLngLat(-3.192473, 55.946233),
				Point.fromLngLat(-3.184319, 55.946233),
				Point.fromLngLat(-3.184319, 55.942617),
				Point.fromLngLat(-3.192473, 55.942617),
				Point.fromLngLat(-3.192473, 55.946233)));
		var confPoly = Polygon.fromLngLats(List.of(confinementPts));
		var confGeo = (Geometry) confPoly;
		var confFt = Feature.fromGeometry(confGeo);
		confFt.addNumberProperty("fill-opacity", 0);		
		allFts.add(confFt);	

		// Create parser
		var parser = new ParseJsonFiles(this.conn);
		
		// Parse buildings
		parser.readBuildings();
		var buildings = parser.getBuildings();
		allFts.addAll(buildings.features());
		
		// Parse sensors
		parser.readMaps(yyyy, mm, dd);
		var sensorsWords = parser.getSensorWords();
		var sensorsCoords = new double[sensorsWords.size()][2];
		
		// Split the 3 words up and find their coordinates
		for (int i = 0; i < sensorsWords.size(); i++) {
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			sensorsCoords[i][0] = parser.getWordsLng();
			sensorsCoords[i][1] = parser.getWordsLat();
		}
		
		for (var i = 0; i < sensorsCoords.length; i++) {
			var sensor = Point.fromLngLat(sensorsCoords[i][0],
					sensorsCoords[i][1]);
			var sensorGeo = (Geometry) sensor;
			var sensorFt = Feature.fromGeometry(sensorGeo);
			// TODO: find colour and symobol dynamically
			sensorFt.addStringProperty("rgb-string", "#aaaaaa");
			sensorFt.addStringProperty("fill", "#aaaaaa");
			sensorFt.addStringProperty("marker-symbol", "");
			allFts.add(sensorFt);
		}
		
		// Sensors feature collection
		this.sensorsFtColl = FeatureCollection.fromFeatures(allFts);
	}
}
