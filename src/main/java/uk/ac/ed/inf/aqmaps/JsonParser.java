package uk.ac.ed.inf.aqmaps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

/**
 * Class to parse the JSON files that contain the map details
 * 
 * @author Chris
 *
 */

public class JsonParser {
	// Private variables
	private HttpConnection conn;
	private List<Feature> buildings;
	private List<String> sensorsWords;
	private List<Double> sensorsBattery;
	private List<String> sensorsReading;
	private double wordsLng;
	private double wordsLat;

	/**
	 * JsonParser constructor
	 * 
	 * @param conn - The server to connect to
	 */
	public JsonParser(HttpConnection conn) {
		this.conn = conn;
		this.sensorsWords = new ArrayList<String>();
		this.sensorsBattery = new ArrayList<Double>();
		this.sensorsReading = new ArrayList<String>();
	}

	// Getters
	public List<Feature> getBuildings() {
		return this.buildings;
	}

	public List<String> getSensorWords() {
		return this.sensorsWords;
	}

	public List<Double> getSensorBatteries() {
		return this.sensorsBattery;
	}

	public List<String> getSensorReadings() {
		return this.sensorsReading;
	}

	public double getWordsLng() {
		return this.wordsLng;
	}

	public double getWordsLat() {
		return this.wordsLat;
	}

	// Methods
	/**
	 * Parse buildings, i.e. the no-fly-zones
	 */
	public void readBuildings() {
		this.conn.connToUrl(conn.getServer() + "/buildings/no-fly-zones.geojson");
		System.out.println("No-Fly Zones fetched!");

		this.buildings = FeatureCollection.fromJson(conn.getJson()).features();
	}

	/**
	 * Parse maps, i.e. the air-quality-data
	 * 
	 * @param yyyy - year of flight
	 * @param mm   - month of flight
	 * @param dd   - day of flight
	 */
	public void readMaps(String yyyy, String mm, String dd) {
		// Get air quality data details of given flight date
		this.conn.connToUrl(conn.getServer() + "/maps/" + yyyy + "/" + mm + "/" + dd
				+ "/air-quality-data.json");
		System.out.println("Air Quality Data fetched!");

		// Assign to AirQuality Class
		Type listType = new TypeToken<ArrayList<AirQualityData>>() {
		}.getType();
		ArrayList<AirQualityData> aqData = new Gson().fromJson(conn.getJson(),
				listType);

		for (int i = 0; i < aqData.size(); i++) {
			this.sensorsWords.add(aqData.get(i).location);
			this.sensorsBattery.add(aqData.get(i).battery);
			this.sensorsReading.add(aqData.get(i).reading);
		}
	}

	/**
	 * Parse words, i.e. an area on the map, from what3words location
	 * 
	 * @param w1 - Word 1
	 * @param w2 - Word 2
	 * @param w3 - Word 3
	 */
	public void readWords(String w1, String w2, String w3) {
		// Get location from given words
		this.conn.connToUrl(conn.getServer() + "/words/" + w1 + "/" + w2 + "/" + w3
				+ "/details.json");

		// Assign to Details class
		var sensorCoords = new Gson().fromJson(conn.getJson(), LocationDetails.class);

		// Set coords of the sensors
		this.wordsLng = sensorCoords.coordinates.lng;
		this.wordsLat = sensorCoords.coordinates.lat;
	}
}
