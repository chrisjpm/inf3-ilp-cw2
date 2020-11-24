package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;

public class JsonParser {
	private HttpConnection conn;
	private FeatureCollection buildings;
	private List<String> sensorsWords = new ArrayList<String>();
	private List<Double> sensorsBattery = new ArrayList<Double>();
	private List<String> sensorsReading = new ArrayList<String>();
	private double wordsLng;
	private double wordsLat;

	// Constructor
	public JsonParser(HttpConnection conn) {
		this.conn = conn;
	}

	// Getters
	public FeatureCollection getBuildings() {
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
	// Parse buildings, i.e. the no-fly-zones
	public void readBuildings() {
		conn.connToUrl(conn.getServer() + "/buildings/no-fly-zones.geojson");

		this.buildings = FeatureCollection.fromJson(conn.getJson());
	}

	// Parse maps, i.e. the air-quality-data
	public void readMaps(String yyyy, String mm, String dd) {
		conn.connToUrl(conn.getServer() + "/maps/" + yyyy + "/" + mm + "/" + dd
				+ "/air-quality-data.json");
		
		Type listType = new TypeToken<ArrayList<AirQualityData>>() {}.getType();
		ArrayList<AirQualityData> aqData = new Gson().fromJson(conn.getJson(),
				listType);

		for (int i = 0; i < aqData.size(); i++) {
			this.sensorsWords.add(aqData.get(i).location);			
			this.sensorsBattery.add(aqData.get(i).battery);
			this.sensorsReading.add(aqData.get(i).reading);
		}
	}

	// Parse words, i.e. an area on the map
	public void readWords(String w1, String w2, String w3) {
		conn.connToUrl(conn.getServer() + "/words/" + w1 + "/" + w2 + "/" + w3
				+ "/details.json");
		
		var sensorCoords = new Gson().fromJson(conn.getJson(), Details.class);

		// Set coords of the sensors
		this.wordsLng = sensorCoords.coordinates.lng;
		this.wordsLat = sensorCoords.coordinates.lat;
	}
}