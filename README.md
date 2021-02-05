# INF3 ILP CW2 - AqMaps

## Implementation Task
As the main part of this practical exercise, you are to develop an application which, given the initial starting location of the drone and the date, calculates a flight path which visits as many of the sensors listed for that date as possible, and returns as close to its initial starting location once all sensors have been visited. The application produces a report on the drone’s flight which takes the form of a Geo-JSON map and a log file which lists where the drone has been and which sensors it connected to.

Recall that the drone flight path which you generate should be at most 150 moves in length; the drone is also a battery-powered device so the number of moves that it can make is limited, and 150 is the limit here. Your application should load the air-quality-data.json file from the project’s webserver for the specified date and produce the Geo-JSON map and the log file for that date.

Your application may write any messages which it likes to the standard output stream but it should also write two text files in the current working directory. These are named flightpath-DD-MM-YYYY.txt and readings-DD-MM-YYYY.geojson where DD, MM, and YYYY are replaced by the day, month and year of the relevant air-quality-data.json file. Please use hyphens (not underscores) in the file names and use only lowercase letters.

*Confinement area (latitudes 55.942617 to 55.946233 and longitudes −3.184319 and −3.192473) and no-fly zones:*

![Done area](https://i.imgur.com/cZsWu3b.png)


## How To Run
First, start up the web server by running `java -jar WebServerLite.jar` in the WebServer folder.

Then, in the root folder, run `java -jar target/aqmaps-0.0.1-SNAPSHOT.jar 15 06 2021 55.9444 -3.1878 5678 80`, where the arguements are: day, month, year, latitude, longitude, seed and port *(Note: I have not inlcuded the seed in any calculations, but must still be entered)*. To view the path, upload the `readings-DD-MM-YYYY.geojson` file to [GeoJson](https://geojson.io). 

The available dates are all days through years 2020 and 2021.

Valid starting locations are within the confinement area and outside of no-fly zones.

Set up the web server on any port you like, just make sure you use that in the arguements of the app.

---

### Final Grade
*Released 26/02/21*
