package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpConnection {
	private static final HttpClient CLIENT = HttpClient.newHttpClient();
	private String ip;
	private String port;
	private String json;

	// Constructor
	public HttpConnection(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

	// Getters
	public String getIp() {
		return this.ip;
	}

	public String getPort() {
		return this.port;
	}

	public String getServer() {
		return "http://" + this.ip + ":" + this.port;
	}

	public String getJson() {
		return this.json;
	}

	// Methods
	// Connect to the server and access the desired file
	public void connToUrl(String urlString) {
		// HttpClient assumes that it is a GET request by default.
		var request = HttpRequest.newBuilder()
				.uri(URI.create(urlString))
				.build();

		// Return the GeoJson content if response code is 200, i.e. a valid
		// urlString. Else, return nothing.
		// Try and connect to the URI, catch if it cannot.
		try {
			var response = CLIENT.send(request, BodyHandlers.ofString());
			System.out.println("Valid URI!");
			System.out.println("Attempting to reach: " + urlString);

			if (response.statusCode() == 200) {
				System.out.println("Success! [Response code: "
						+ response.statusCode() + "]");
				this.json = response.body();
			} else {
				System.out.println(
						"Fatal error: Response code: " + response.statusCode());
				System.exit(1); // Exit the application
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Fatal error: Unable to connect to " + this.ip
					+ " at port " + this.port + ".");
			e.printStackTrace();
			System.exit(1); // Exit the application
		}
	}
}
