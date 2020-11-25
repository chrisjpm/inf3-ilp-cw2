package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

public class PollutionLookUp {
	private List<String> markerColour = new ArrayList<String>();
	private List<String> markerSymbol = new ArrayList<String>();

	// Constructor
	public PollutionLookUp() {

	}

	// Getters
	public List<String> getMarkerColours() {
		return markerColour;
	}

	public List<String> getMarkerSymbols() {
		return markerSymbol;
	}

	// Methods
	public void lookUp(List<Double> batteries, List<String> readings) {
		for (var i = 0; i < Map.SENSORS; i++) {
			if (readings.get(i) == "null" || readings.get(i) == "NaN"){
				this.markerColour.add("#000000");
				this.markerSymbol.add("cross");
				continue;
			}
			
			if (batteries.get(i) < 10) {
				this.markerColour.add("#000000");
				this.markerSymbol.add("cross");
				continue;
			}
			
			if (Double.parseDouble(readings.get(i)) >= 0
					&& Double.parseDouble(readings.get(i)) < 32) {
				this.markerColour.add("#00ff00");
				this.markerSymbol.add("lighthouse");
			} else if (Double.parseDouble(readings.get(i)) >= 32
					&& Double.parseDouble(readings.get(i)) < 64) {
				this.markerColour.add("#40ff00");
				this.markerSymbol.add("lighthouse");
			} else if (Double.parseDouble(readings.get(i)) >= 64
					&& Double.parseDouble(readings.get(i)) < 96) {
				this.markerColour.add("#80ff00");
				this.markerSymbol.add("lighthouse");
			} else if (Double.parseDouble(readings.get(i)) >= 96
					&& Double.parseDouble(readings.get(i)) < 128) {
				this.markerColour.add("#c0ff00");
				this.markerSymbol.add("lighthouse");
			} else if (Double.parseDouble(readings.get(i)) >= 128
					&& Double.parseDouble(readings.get(i)) < 160) {
				this.markerColour.add("#ffc000");
				this.markerSymbol.add("danger");
			} else if (Double.parseDouble(readings.get(i)) >= 160
					&& Double.parseDouble(readings.get(i)) < 192) {
				this.markerColour.add("#ff8000");
				this.markerSymbol.add("danger");
			} else if (Double.parseDouble(readings.get(i)) >= 192
					&& Double.parseDouble(readings.get(i)) < 224) {
				this.markerColour.add("#ff4000");
				this.markerSymbol.add("danger");
			} else if (Double.parseDouble(readings.get(i)) >= 224
					&& Double.parseDouble(readings.get(i)) < 256) {
				this.markerColour.add("#ff0000");
				this.markerSymbol.add("danger");
			}
		}
	}
}
