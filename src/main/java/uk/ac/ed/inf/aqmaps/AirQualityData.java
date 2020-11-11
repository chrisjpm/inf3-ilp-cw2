package uk.ac.ed.inf.aqmaps;

public class AirQualityData {
	String location;
	double battery;
	String reading;
	
	@Override
	public String toString() {
		return "AirQualityData [location=" + location + ", battery=" + battery
				+ ", reading=" + reading + "]";
	}
}
