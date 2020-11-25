package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

public class DronePosition {
	private Map map;
	private double lat;
	private double lng;

	// Constructor
	public DronePosition(Map map, double lat, double lng) {
		this.map = map;
		this.lat = lat;
		this.lng = lng;
	}

	// Getters
	public double getLng() {
		return this.lng;
	}

	public double getLat() {
		return this.lat;
	}

	public Point getPoint() {
		return Point.fromLngLat(this.lng, this.lat);
	}

	// Methods
	public DronePosition nextPos(Movement move) {
		// Calculate the resulting coordinates from moving in the given
		// direction
		// double nextLat = this.lat + Drone.MOVE_DIST * move.cos();
		// double nextLng = this.lng + Drone.MOVE_DIST * move.sin();

		// return new DronePosition(nextLat, nextLng);

		return null;
	}

	public boolean validPosition() {
		// In confinement area
		var inConfinements = TurfJoins.inside(
				Point.fromLngLat(this.lng, this.lat), this.map.getConfPoly());

		// In no-fly zone
		var noFlyZones = this.map.getNoFlyZones();
		var inFlyZone = false;
		for (int i = 0; i < noFlyZones.size(); i++) {
			var checkPoint = TurfJoins.inside(
					Point.fromLngLat(this.lng, this.lat),
					(Polygon) noFlyZones.get(i));
			if (checkPoint) {
				inFlyZone = true;
				break;
			}
		}

		return inConfinements && !inFlyZone;
	}
}
