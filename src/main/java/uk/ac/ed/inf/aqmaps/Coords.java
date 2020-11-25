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
		// Proposed move
		var linePath = new Line2D.Double(this.lat, this.lng, nextPos.getLat(),
				nextPos.getLng());
		var line = new ArrayList<>(
				Arrays.asList(getPoint(), nextPos.getPoint()));
		
		// List of buildings
		String[] buildings = { "Appleton Tower", "David Hume Tower",
				"Main Library", "Informatics Forum" };

		// Crossing confinement area border
		var crossConfinements = false;
		var confPoints = this.map.getConfPoints();

		for (int i = 0; i < confPoints.size() - 1; i++) { // Fist & last point are the same
			int j = (i + 1) % confPoints.size();
			Line2D barrier = new Line2D.Double(
					confPoints.get(i).coordinates().get(1),
					confPoints.get(i).coordinates().get(0),
					confPoints.get(j).coordinates().get(1),
					confPoints.get(j).coordinates().get(0));

			if (linePath.intersectsLine(barrier)) {
				System.out.println(
						">> Illegal move! Attempted to fly out of confinemeant area.");
				crossConfinements = true;
				break;
			}
			if (crossConfinements) {
				break;
			}
		}

		// Crossing a no-fly-zone border
		var crossNoFlyZone = false;
		var noFlyZones = this.map.getNoFlyZones();
		for (int i = 0; i < noFlyZones.size(); i++) {
			var nfzPoly = (Polygon) noFlyZones.get(i);
			var nfzPoints = nfzPoly.coordinates().get(0);

			for (int j = 0; j < nfzPoints.size() - 1; j++) { // Fist & last point are the same
				int k = (j + 1) % nfzPoints.size();
				Line2D barrier = new Line2D.Double(nfzPoints.get(j).latitude(),
						nfzPoints.get(j).longitude(),
						nfzPoints.get(k).latitude(),
						nfzPoints.get(k).longitude());
				if (linePath.intersectsLine(barrier)) {
					System.out.println(
							">> Illegal move! Attmpeted to fly through building '"
									+ buildings[i] + "'.");
					crossNoFlyZone = true;
					break;
				}
			}
			if (crossNoFlyZone) {
				break;
			}
		}

		return !crossConfinements && !crossNoFlyZone;
	}
}
