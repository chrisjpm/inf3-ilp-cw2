package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class DronePosition {
	private double lat;
	private double lng;
	
	// Constructor
	public DronePosition(double lat, double lng) {
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
		// Calculate the resulting coordinates from moving in the given direction
		//double nextLat = this.lat + Drone.MOVE_DIST * move.cos();
		//double nextLng = this.lng + Drone.MOVE_DIST * move.sin();

		//return new DronePosition(nextLat, nextLng);
		
		
		return null;
	}
}
