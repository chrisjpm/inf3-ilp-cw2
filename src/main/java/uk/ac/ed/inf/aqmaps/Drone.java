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
	//private static final double READ_SENSOR_ANGLE = 0.0002;
	private Map map;
	private DronePosition dronePos, startPos;
	private List<Point> visitedPoss = new ArrayList<Point>();
	//private List<Feature> visitedSensors = new ArrayList<Feature>();

	// Constructor
	public Drone(Map map, DronePosition startPos) {
		this.map = map;
		this.dronePos = startPos;
		this.startPos = startPos;
	}

	// Methods
	public void nextMove() {
		this.visitedPoss.add(this.dronePos.getPoint()); // Previous position
		for (int i = 0; i < Map.SENSORS; i++) {
			// Add current position to list of visited positions
			var currPos = new DronePosition(
					this.map.getSensorsCoords()[i][1],
					this.map.getSensorsCoords()[i][0]);
			this.visitedPoss.add(currPos.getPoint());
			
			// Update marker for sensor
			var sensor = this.map.getSensorsFts().get(i);
			sensor.addStringProperty("rgb-string", this.map.getMarkerColours().get(i));
			sensor.addStringProperty("fill", this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-color", this.map.getMarkerColours().get(i));
			sensor.addStringProperty("marker-symbol", this.map.getMarkerSymbols().get(i));
		}
		this.visitedPoss.add(this.startPos.getPoint());
	}

	public FeatureCollection drawPath() {
		var flightMultiLine = MultiLineString.fromLngLats(List.of(this.visitedPoss));
		var flightGeo = (Geometry) flightMultiLine;
		var flightFt = Feature.fromGeometry(flightGeo);
		
		var flightList = new ArrayList<Feature>();
		flightList.add(flightFt);
		flightList.addAll(this.map.getSensorsFts());
		flightList.add(this.map.getConfFt());
		flightList.addAll(this.map.getBuildingsFts());
		var flightFtColl = FeatureCollection.fromFeatures(flightList);

		return flightFtColl;
	}

}
