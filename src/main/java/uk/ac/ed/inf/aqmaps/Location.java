package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

public class Location {
	private double lat;
	private double lng;

	// Constructor
	public Location(double lat, double lng) {
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

	// Setters
	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	// Methods
	public Point moveDrone(Map map, Location prevPos, Location targetPos) {
		var bearingToTarget = Math.round((TurfMeasurement
				.bearing(prevPos.getPoint(), targetPos.getPoint()) / 10)) * 10;
		var move = false;
		Point nextPos = null;

		do {		
			nextPos = destination(prevPos, bearingToTarget);
			if (prevPos.validDroneMove(map, nextPos)) {
				move = true;
			} else {
				bearingToTarget-=10;
			}
		} while (!move);

		return nextPos;
	}
	
	public Point destination(Location prevPos, double bearingToTarget) {
		var nextPosLng = prevPos.getLng() + Drone.MOVE_DIST * Math.sin(Math.toRadians(bearingToTarget));
		var nextPosLat = prevPos.getLat() + Drone.MOVE_DIST * Math.cos(Math.toRadians(bearingToTarget));
		
		return Point.fromLngLat(nextPosLng, nextPosLat);
	}

	public boolean validDroneMove(Map map, Point nextPos) {
		// Proposed move
		var linePath = new Line2D.Double(this.lat, this.lng, nextPos.latitude(),
				nextPos.longitude());

		// List of buildings
		String[] buildings = { "Appleton Tower", "David Hume Tower",
				"Main Library", "Informatics Forum" };

		// Check intersections of proposed move path and borders
		// Crossing confinement area border
		var crossConfinements = false;
		var confPoints = map.getConfPoints();

		for (int i = 0; i < confPoints.size() - 1; i++) {
			int j = (i + 1) % confPoints.size();
			Line2D barrier = new Line2D.Double(
					confPoints.get(i).coordinates().get(1),
					confPoints.get(i).coordinates().get(0),
					confPoints.get(j).coordinates().get(1),
					confPoints.get(j).coordinates().get(0));

			if (linePath.intersectsLine(barrier)) {
				System.out.println(
						"Illegal move! Attempted to fly out of confinemeant area: ("
								+ nextPos.latitude() + ", "
								+ nextPos.longitude() + ")");
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

			for (int j = 0; j < nfzPoints.size() - 1; j++) {
				int k = (j + 1) % nfzPoints.size();
				Line2D barrier = new Line2D.Double(nfzPoints.get(j).latitude(),
						nfzPoints.get(j).longitude(),
						nfzPoints.get(k).latitude(),
						nfzPoints.get(k).longitude());
				if (linePath.intersectsLine(barrier)) {
					System.out.println(
							"Illegal move! Attempted to fly through building '"
									+ buildings[i] + "': (" + nextPos.latitude()
									+ ", " + nextPos.longitude() + ")");
					crossNoFlyZone = true;
					break;
				}
			}
			if (crossNoFlyZone) {
				break;
			}
		}
		
		var validMove = !crossConfinements && !crossNoFlyZone;

		if (validMove) {
			System.out.println("Valid move! (" + nextPos.latitude() + ", "
					+ nextPos.longitude() + ")");
		}

		return validMove;
	}
}
