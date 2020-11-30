package uk.ac.ed.inf.aqmaps;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * Rigorous Test :-)
	 */
	@Test
	public void testReadings() {
		var yyyy = "2020";
		var lat = "55.9444";
		var lng = "-3.1878";
		var seed = "5678";
		var port = "80";
		var counter = 0;

		for (int j = 1; j < 13; j++) {
			for (int k = 1; k < 13; k++) {
				var mm = "";
				var dd = "";

				if (j == k) {
					if (j < 10) {
						mm = "0" + Integer.toString(j);
					} else {
						mm = Integer.toString(j);
					}

					if (k < 10) {
						dd = "0" + Integer.toString(k);
					} else {
						dd = Integer.toString(k);
					}

					String[] input = { dd, mm, yyyy, lat, lng, seed, port };
					App.main(input);

					counter++;
				}

			}
		}

		System.out
				.println("\n*** TEST 1 *************************************");
		System.out.println("Number of dates expected to test: 12");
		System.out.println("Actual number of dates tested: " + counter);
		System.out.println("************************************************");
		assertTrue(counter == 12);
	}
}
