package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;
import com.mapbox.turf.TurfMeta;
import com.mapbox.turf.models.LineIntersectsResult;

public class Coords {
	private Map map;
	private double lat;
	private double lng;

	// Constructor
	public Coords(Map map, double lat, double lng) {
		this.map = map;
		this.lat = lat;
		this.lng = lng;
	}

	// Getters
	public double getLat() {
		return this.lat;
	}
	
	public double getLng() {
		return this.lng;
	}

	public Point getPoint() {
		return Point.fromLngLat(this.lng, this.lat);
	}

	// Methods
	public Coords nextDronePos(Movement move) {
		// Calculate the resulting coordinates from moving in the given
		// direction
		// double nextLat = this.lat + Drone.MOVE_DIST * move.cos();
		// double nextLng = this.lng + Drone.MOVE_DIST * move.sin();

		// return new DronePosition(nextLat, nextLng);

		return null;
	}

	public boolean validDroneMove(Coords nextPos) {
		// In confinement area
//		var inConfinements = TurfJoins.inside(getPoint(),
//				this.map.getConfPoly());

		// In no-fly zone
//		var noFlyZones = this.map.getNoFlyZones();
//		var inFlyZone = false;
//		for (int i = 0; i < noFlyZones.size(); i++) {
//			var checkPoint = TurfJoins.inside(getPoint(),
//					(Polygon) noFlyZones.get(i));
//			if (checkPoint) {
//				inFlyZone = true;
//				break;
//			}
//		}

		// Crossing confinement area border
		var crossConfinements = false;
		var linePath = new Line2D.Double(this.lat, this.lng, nextPos.getLat(), nextPos.getLng());
		System.out.println(this.lat + " " + this.lng);
		System.out.println(nextPos.getLat() + " " + nextPos.getLng());
		var confCorners = this.map.getConfPoints();
		System.out.println(confCorners.get(2).coordinates().get(1) + " " + confCorners.get(2).coordinates().get(0));
		System.out.println(confCorners.get(3).coordinates().get(1) + " " + confCorners.get(3).coordinates().get(0));
		System.out.println(confCorners.size());
		for (int i = 0; i < confCorners.size(); i++) {
			int j = (i + 1) % confCorners.size();
			Line2D edge = new Line2D.Double(
					confCorners.get(i).coordinates().get(1),
					confCorners.get(i).coordinates().get(0),
					confCorners.get(j).coordinates().get(1),
					confCorners.get(j).coordinates().get(0));
			if (linePath.intersectsLine(edge)) {
				System.out.println("got here at " + i);
				crossConfinements = true;
				break;
			}
		}

		// Crossing a no-fly-zone border
		// TODO

		// return inConfinements && !inFlyZone;
		return !crossConfinements;
	}
}
