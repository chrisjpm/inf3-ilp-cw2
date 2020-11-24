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
	private static final double READ_SENSOR_ANGLE = 0.0002;
	private Map map;
	private DronePosition dronePos, endPos;
	private ArrayList<Point> vistedSensors = new ArrayList<Point>();

	// Constructor
	public Drone(Map map, DronePosition startPos) {
		this.map = map;
		this.dronePos = startPos;
		this.endPos = startPos;
		this.vistedSensors.add(null);
	}

	// Methods
	public void nextMove() {
		var currentPos = this.dronePos;
	}
	
	public FeatureCollection drawPath() {
		var sensors = this.map.getSensorsPoints(); // TODO: get reordered sensors
		var flightMultiLine = MultiLineString.fromLngLats(List.of(sensors));
		
		var flightGeo = (Geometry) flightMultiLine;
		var pathFt = Feature.fromGeometry(flightGeo);
		var flight = new ArrayList<Feature>();
		flight.add(pathFt);
		flight.addAll(this.map.getSensorsFts());
		flight.add(this.map.getConfFt());
		flight.addAll(this.map.getBuildingsFts());
		var flightFtColl = FeatureCollection.fromFeatures(flight);
		
		return flightFtColl;
	}

}
