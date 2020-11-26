package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Coords {
	private double lat;
	private double lng;

	// Constructor
	public Coords(double lat, double lng) {
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
	
	// Setters
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}

	public Point getPoint() {
		return Point.fromLngLat(this.lng, this.lat);
	}

	// Methods
	public boolean validDroneMove(Map map, Point nextPos) {
		// Proposed move
		var linePath = new Line2D.Double(this.lat, this.lng, nextPos.latitude(),
				nextPos.longitude());
		
		// List of buildings
		String[] buildings = { "Appleton Tower", "David Hume Tower",
				"Main Library", "Informatics Forum" };

		// Crossing confinement area border
		var crossConfinements = false;
		var confPoints = map.getConfPoints();

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
		var noFlyZones = map.getNoFlyZones();
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
							">> Illegal move! Attempted to fly through building '"
									+ buildings[i] + "'.");
					crossNoFlyZone = true;
					break;
				}
			}
			if (crossNoFlyZone) {
				break;
			}
		}
		
		if (!crossConfinements && !crossNoFlyZone) {
			System.out.println(">> Valid move!");
		}

		return !crossConfinements && !crossNoFlyZone;
	}
}
