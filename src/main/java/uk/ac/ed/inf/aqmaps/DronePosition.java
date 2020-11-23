package uk.ac.ed.inf.aqmaps;

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

	// Methods
	public DronePosition nextPos(Movement move) {
		return null;
		// Calculate the resulting coordinates from moving in the given direction
		//double nextLat = this.lat + Drone.MOVE_DIST * move.cos();
		//double nextLng = this.lng + Drone.MOVE_DIST * move.sin();

		//return new DronePosition(nextLat, nextLng);
	}
}
