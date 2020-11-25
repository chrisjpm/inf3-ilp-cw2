package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.Point;

public class Drone {
	public static final int BATTERY_POWER = 150;
	public static final double MOVE_DIST = 0.0003;
	// private static final double READ_SENSOR_ANGLE = 0.0002;
	private Map map;
	private DronePosition dronePos, startPos;
	private List<Point> visitedPoss = new ArrayList<Point>();
	// private List<Feature> visitedSensors = new ArrayList<Feature>();

	// Constructor
	public Drone(Map map, DronePosition startPos) {
		this.map = map;
		this.dronePos = startPos;
		this.startPos = startPos;
	}

	// Methods
	// Move drone to next position
	public void nextMove() {
		this.visitedPoss.add(this.dronePos.getPoint()); // current pos

		for (int i = 0; i < Map.SENSORS; i++) {
			// Add current position to list of visited positions
			var nextPos = new DronePosition(this.map, this.map.getSensorsCoords()[i][1],
					this.map.getSensorsCoords()[i][0]);
			this.visitedPoss.add(nextPos.getPoint()); // since this would not actually be a loop, this would be dealt with at the end 

			// Update marker for sensor
			var sensor = this.map.getSensorsFts().get(i);
			sensor.addStringProperty("rgb-string",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("fill",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-color",
					this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-symbol",
					this.map.getMarkerSymbols().get(i));
		}

		// Keep track of drone's path
		this.visitedPoss.add(this.startPos.getPoint()); // this is start pos jut to get back there for sake of testing
	}

	// Draw the flight path
	public FeatureCollection drawPath() {
		var flightMultiLine = MultiLineString
				.fromLngLats(List.of(this.visitedPoss));
		var flightGeo = (Geometry) flightMultiLine;
		var flightFt = Feature.fromGeometry(flightGeo);

		var flightList = new ArrayList<Feature>();
		flightList.add(flightFt);
		flightList.addAll(this.map.getSensorsFts());
		//flightList.add(this.map.getConfFt());
		//flightList.addAll(this.map.getBuildings());
		var flightFtColl = FeatureCollection.fromFeatures(flightList);

		return flightFtColl;
	}

}
