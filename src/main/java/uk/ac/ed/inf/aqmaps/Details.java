package uk.ac.ed.inf.aqmaps;

public class Details {
	String country;

	Sqaure square;
	public class Sqaure {
		Southwest southwest;
		public class Southwest {
			double lng;
			double lat;

			@Override
			public String toString() {
				return "Southwest [lng=" + lng + ", lat=" + lat + "]";
			}
		}

		NorthWest northwest;
		public class NorthWest {
			double lng;
			double lat;

			@Override
			public String toString() {
				return "NorthWest [lng=" + lng + ", lat=" + lat + "]";
			}
		}

		@Override
		public String toString() {
			return "Sqaure [southwest=" + southwest + ", northwest=" + northwest
					+ "]";
		}
	}

	String nearestPlace;

	Coordinates coordinates;
	public class Coordinates {
		double lng;
		double lat;

		@Override
		public String toString() {
			return "Coordinates [lng=" + lng + ", lat=" + lat + "]";
		}
	}

	String words;
	String language;
	String map;

	@Override
	public String toString() {
		return "Details [country=" + country + ", square=" + square
				+ ", nearestPlace=" + nearestPlace + ", coordinates="
				+ coordinates + ", words=" + words + ", language=" + language
				+ ", map=" + map + "]";
	}
}
