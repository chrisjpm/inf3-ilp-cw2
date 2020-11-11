package uk.ac.ed.inf.aqmaps;

public class FlightPath {
	private HttpConnection conn;
	private double[][] sensors;

	// Constructor
	public FlightPath(HttpConnection conn) {
		this.conn = conn;
	}
	
	// Getters
	public double[][] getSensors() {
		return this.sensors;
	}

	public void setUp() {
		// Confinement area
		// TODO: draw square

		// Parse all required files
		var parser = new ParseJsonFiles(this.conn);
		// Buildings
		parser.readBuildings();
		parser.getBuildings();
		// Sensors
		// TODO: loop all days
		parser.readMaps("2020", "01", "01");
		var sensorsWords = parser.getSensorWords();
		var sensorsCoords = new double[sensorsWords.size()][2];
		for (int i = 0; i < sensorsWords.size(); i++) {
			var line = sensorsWords.get(i).split("\\.");
			parser.readWords(line[0], line[1], line[2]);
			sensorsCoords[i][0] = parser.getWordsLng();
			sensorsCoords[i][1] = parser.getWordsLat();
		}
		this.sensors = sensorsCoords;
	}
}
