package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;

public class ParseJsonFiles {
	private HttpConnection conn;
	private FeatureCollection buildings;

	// Constructor
	public ParseJsonFiles(HttpConnection conn) {
		this.conn = conn;
	}

	// Getters
	public FeatureCollection getBuildings() {
		return this.buildings;
	}

//	public  getMaps() {
//		return ;
//	}
//	
//	public  getWords() {
//		return ;
//	}

	// Methods
	// Parse buildings, i.e. the no-fly-zones
	public void readBuildings() {
		conn.connToUrl(conn.getServer() + "/buildings/no-fly-zones.geojson");
		this.buildings = FeatureCollection.fromJson(conn.getJson());
	}

	// Parse maps, i.e. the air-quality-data
	public void readMaps(String yyyy, String mm, String dd) {
		conn.connToUrl(conn.getServer() + "/maps/" + yyyy + "/" + mm + "/" + dd + "/air-quality-data.json");
		Type listType = new TypeToken<ArrayList<AirQualityData>>() {
		}.getType();
		ArrayList<AirQualityData> details = new Gson().fromJson(conn.getJson(), listType);
		System.out.println(details.toString());

	}

	// Parse words, i.e. an area on the map
	public void readWords(String w1, String w2, String w3) {
		conn.connToUrl(conn.getServer() + "/words/" + w1 + "/" + w2 + "/" + w3 + "/details.json");
		var details = new Gson().fromJson(conn.getJson(), Details.class);
		System.out.println(details.toString());
	}
}
