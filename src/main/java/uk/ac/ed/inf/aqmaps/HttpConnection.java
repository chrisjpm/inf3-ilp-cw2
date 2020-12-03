package uk.ac.ed.inf.aqmaps;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * Set up the connection to the given server
 * 
 * @author Chris
 *
 */

public class HttpConnection {
	// Constant HttpClient so we only call it once
	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	// Private variables
	private String ip;
	private String port;
	private String json;

	/**
	 * HttpConnection constructor
	 * 
	 * @param ip   - Server IP: {@code https://localhost/}
	 * @param port - User given port
	 */
	public HttpConnection(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

	// Getters
	public String getServer() {
		return "http://" + this.ip + ":" + this.port;
	}

	public String getJson() {
		return this.json;
	}

	// Methods
	/**
	 * Connect to the server and access the desired file
	 * 
	 * @param urlString - The URL to connect to
	 * @throws IOException If unable to connect to server, then exits system
	 */
	public void connToUrl(String urlString) {
		// HttpClient assumes that it is a GET request by default.
		var request = HttpRequest.newBuilder().uri(URI.create(urlString))
				.build();

		// Return the GeoJson content if response code is 200, i.e. a valid
		// urlString. Else, return nothing.
		// Try and connect to the URI, catch if it cannot.
		try {
			var response = CLIENT.send(request, BodyHandlers.ofString());

			// Get page content if connected OKAY
			if (response.statusCode() == 200) {
				this.json = response.body();
			} else {
				System.out.println("Fatal error: Response code: "
						+ response.statusCode() + " for '" + urlString + "'");
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
