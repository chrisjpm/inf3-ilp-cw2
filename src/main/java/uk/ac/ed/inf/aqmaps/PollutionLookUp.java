package uk.ac.ed.inf.aqmaps;

/**
 * Class to assign colours and markers to air quality
 * 
 * @author Chris
 *
 */

public class PollutionLookUp {
	// Private variables
	private String markerColour, markerSymbol;

	/**
	 * PollutionLookUp constructor
	 */
	public PollutionLookUp() {
		this.markerColour = null;
		this.markerSymbol = null;
	}

	// Getters
	public String getMarkerColour() {
		return markerColour;
	}

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	// Methods
	/**
	 * Assign colour and symbol to marker
	 * 
	 * @param batteries - The battery values for the sensor
	 * @param reading   - The air quality reading for the sensor
	 */
	public void lookUp(double battery, String reading) {
		for (var i = 0; i < Map.SENSORS; i++) {
			// If there was no reading taken, assign the colour black and a
			// cross
			if (reading == "null" || reading == "NaN") {
				this.markerColour = "#000000";
				this.markerSymbol = "cross";
				continue;
			}

			// If the battery was below 10%, assign black and a cross
			if (battery < 10) {
				this.markerColour = "#000000";
				this.markerSymbol = "cross";
				continue;
			}

			// Take the valid reading and assign its colour and symbol
			if (Double.parseDouble(reading) >= 0
					&& Double.parseDouble(reading) < 32) {
				this.markerColour = "#00ff00";
				this.markerSymbol = "lighthouse";
			} else if (Double.parseDouble(reading) >= 32
					&& Double.parseDouble(reading) < 64) {
				this.markerColour = "#40ff00";
				this.markerSymbol = "lighthouse";
			} else if (Double.parseDouble(reading) >= 64
					&& Double.parseDouble(reading) < 96) {
				this.markerColour = "#80ff00";
				this.markerSymbol = "lighthouse";
			} else if (Double.parseDouble(reading) >= 96
					&& Double.parseDouble(reading) < 128) {
				this.markerColour = "#c0ff00";
				this.markerSymbol = "lighthouse";
			} else if (Double.parseDouble(reading) >= 128
					&& Double.parseDouble(reading) < 160) {
				this.markerColour = "#ffc000";
				this.markerSymbol = "danger";
			} else if (Double.parseDouble(reading) >= 160
					&& Double.parseDouble(reading) < 192) {
				this.markerColour = "#ff8000";
				this.markerSymbol = "danger";
			} else if (Double.parseDouble(reading) >= 192
					&& Double.parseDouble(reading) < 224) {
				this.markerColour = ("#ff4000");
				this.markerSymbol = ("danger");
			} else if (Double.parseDouble(reading) >= 224
					&& Double.parseDouble(reading) < 256) {
				this.markerColour = ("#ff0000");
				this.markerSymbol = ("danger");
			}
		}
	}
}
