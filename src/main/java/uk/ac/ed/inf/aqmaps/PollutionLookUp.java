package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to assign colours and markers to air quality
 * 
 * @author Chris
 *
 */

public class PollutionLookUp {
	// Private variables
	private List<String> markerColour;
	private List<String> markerSymbol;

	/**
	 * PollutionLookUp constructor
	 */
	public PollutionLookUp() {
		this.markerColour = new ArrayList<String>();
		this.markerSymbol = new ArrayList<String>();
	}

	// Getters
	public List<String> getMarkerColours() {
		return markerColour;
	}

	public List<String> getMarkerSymbols() {
		return markerSymbol;
	}

	// Methods
	/**
	 * Assign colours and markers to air quality
	 * 
	 * @param batteries - The battery values for all the sensors
	 * @param readings  - The air quality reading for each sensor
	 */
	public void lookUp(List<Double> batteries, List<String> readings) {
		for (var i = 0; i < Map.SENSORS; i++) {
			// If there was no reading taken, assign the colour black and a
			// cross and continue to next list item
			if (readings.get(i) == "null" || readings.get(i) == "NaN") {
				this.markerColour.add("#000000");
				this.markerSymbol.add("cross");
				continue;
			}

			// If the battery was below 10%, assign black and a cross and
			// continue to next list item
			if (batteries.get(i) < 10) {
				this.markerColour.add("#000000");
				this.markerSymbol.add("cross");
				continue;
			}

			// Take the valid reading and assign its colour and symbol 
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
