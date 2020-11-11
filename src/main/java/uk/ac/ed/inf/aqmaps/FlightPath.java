package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class FlightPath {
	private HttpConnection conn;
	private double[][] sensors;
	private FeatureCollection sensorsFtColl;

	// Constructor
	public FlightPath(HttpConnection conn) {
		this.conn = conn;
	}

	// Getters
	public double[][] getSensorsCoords() {
		return this.sensors;
	}
	
	public FeatureCollection getSensorsFtColl() {
		return this.sensorsFtColl;
	}

	public void setUp() {
		// Confinement area
		// TODO: draw square?

		// Create parser
		var parser = new ParseJsonFiles(this.conn);
		
		// Parse buildings
		parser.readBuildings();
		parser.getBuildings();
		
		// Parse sensors
		// TODO: loop all days?
		parser.readMaps("2021", "02", "01");
		var sensorsWords = parser.getSensorWords();
		var sensorsCoords = new double[sensorsWords.size()][2];
		for (int i = 0; i < sensorsWords.size(); i++) {
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			sensorsCoords[i][0] = parser.getWordsLng();
			sensorsCoords[i][1] = parser.getWordsLat();
		}
		
		this.sensors = sensorsCoords;

		var features = new Feature[sensorsCoords.length];
		for (var i = 0; i < sensorsCoords.length; i++) {
			var sensor = Point.fromLngLat(sensorsCoords[i][0],
					sensorsCoords[i][1]);

			var sensorGeo = (Geometry) sensor;
			var sensorFt = Feature.fromGeometry(sensorGeo);

			sensorFt.addStringProperty("rgb-string", "#aaaaaa");
			sensorFt.addStringProperty("fill", "#aaaaaa");
			sensorFt.addStringProperty("marker-symbol", "");
			features[i] = sensorFt;
		}
		
		this.sensorsFtColl = FeatureCollection.fromFeatures(features);
	}
}
