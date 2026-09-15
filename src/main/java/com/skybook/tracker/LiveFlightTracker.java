package com.skybook.tracker;

import com.skybook.model.*;
import com.skybook.data.*;
import com.skybook.ui.*;
import com.skybook.ui.components.*;
import com.skybook.util.*;
import com.skybook.main.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;

public class LiveFlightTracker {

    // Bounding box for Kerala, India (approximate)
    private static final double LAT_MIN = 8.0;
    private static final double LAT_MAX = 13.0;
    private static final double LON_MIN = 74.0;
    private static final double LON_MAX = 78.0;

    // Flight data model
    static class Flight {
        String callsign;
        String origin;
        double latitude;
        double longitude;
        double altitude;
        double speedKmh;

        public Flight(String callsign, String origin, double lat, double lon, double alt, double speed) {
            this.callsign = callsign.replace("\"", "").trim();
            this.origin = origin.replace("\"", "").trim();
            this.latitude = lat;
            this.longitude = lon;
            this.altitude = alt;
            this.speedKmh = speed;
        }
    }

    public static void main(String[] args) {
        boolean loop = args.length > 0 && args[0].equalsIgnoreCase("--loop");

        System.out.println("Starting Java Live Flight Tracker...");
        
        while (true) {
            try {
                System.out.println("\nFetching live data from OpenSky Network...");
                String jsonResponse = fetchApiData();

                if (jsonResponse != null && !jsonResponse.isEmpty()) {
                    List<Flight> flights = parseFlights(jsonResponse);
                    System.out.println("Found " + flights.size() + " active flights in the bounding box.");
                    
                    if (!flights.isEmpty()) {
                        generateHtmlMap(flights);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error during tracking: " + e.getMessage());
            }

            if (loop) {
                System.out.println("Waiting 60 seconds... (Press Ctrl+C to stop)");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    System.out.println("Tracker stopped.");
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Makes an HTTP GET request to the OpenSky API and returns the JSON string.
     */
    private static String fetchApiData() throws Exception {
        String urlString = String.format("https://opensky-network.org/api/states/all?lamin=%s&lomin=%s&lamax=%s&lomax=%s", 
                LAT_MIN, LON_MIN, LAT_MAX, LON_MAX);
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int status = conn.getResponseCode();
        if (status != 200) {
            System.out.println("API Error. HTTP Status: " + status);
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        
        return response.toString();
    }

    /**
     * Parses the JSON manually to avoid forcing beginners to download external .jar files like Gson.
     */
    private static List<Flight> parseFlights(String json) {
        List<Flight> flightList = new ArrayList<>();
        
        // Find the "states" array in the JSON response
        int startIndex = json.indexOf("\"states\":[");
        if (startIndex == -1) return flightList; // No flights found

        // Extract the inner array content
        int endOfStates = json.lastIndexOf("]");
        if (endOfStates < startIndex + 10) return flightList;
        
        String statesStr = json.substring(startIndex + 10, endOfStates);
        
        // Split each flight array (they are formatted like ["val1","val2",...],["val1","val2",...])
        String[] rawFlights = statesStr.split("\\],\\[");
        
        for (String raw : rawFlights) {
            raw = raw.replace("[", "").replace("]", "");
            // Split by comma (OpenSky data doesn't contain commas inside their strings)
            String[] cols = raw.split(",");
            
            if (cols.length > 9) {
                try {
                    String callsign = cols[1];
                    String origin = cols[2];
                    
                    // Skip if lat/long/alt is null
                    if (cols[5].equals("null") || cols[6].equals("null") || cols[7].equals("null")) {
                        continue;
                    }
                    
                    double lon = Double.parseDouble(cols[5]);
                    double lat = Double.parseDouble(cols[6]);
                    double alt = Double.parseDouble(cols[7]);
                    
                    double speedMs = cols[9].equals("null") ? 0.0 : Double.parseDouble(cols[9]);
                    double speedKmh = speedMs * 3.6; // Convert m/s to km/h
                    
                    flightList.add(new Flight(callsign, origin, lat, lon, alt, speedKmh));
                } catch (Exception e) {
                    // Ignore parsing errors for individual flights
                }
            }
        }
        return flightList;
    }

    /**
     * Generates an HTML map using Leaflet.js (same library Python Folium uses)
     * and opens it in the default web browser.
     */
    private static void generateHtmlMap(List<Flight> flights) {
        try {
            File mapFile = new File("flight_map.html");
            BufferedWriter writer = new BufferedWriter(new FileWriter(mapFile));
            
            double centerLat = (LAT_MIN + LAT_MAX) / 2.0;
            double centerLon = (LON_MIN + LON_MAX) / 2.0;
            
            writer.write("<!DOCTYPE html>\n<html>\n<head>\n<title>Live Flight Map</title>\n");
            writer.write("<meta charset='utf-8' />\n");
            writer.write("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
            writer.write("<link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>\n");
            writer.write("<script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<div id='map' style='width: 100%; height: 100vh;'></div>\n");
            writer.write("<script>\n");
            
            // Initialize map
            writer.write(String.format("var map = L.map('map').setView([%f, %f], 7);\n", centerLat, centerLon));
            writer.write("L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);\n");
            
            // Draw Bounding Box
            writer.write(String.format(
                "var bounds = [[%f, %f], [%f, %f]];\n", LAT_MIN, LON_MIN, LAT_MAX, LON_MAX));
            writer.write("L.rectangle(bounds, {color: '#ff7800', weight: 1, fillOpacity: 0.1}).addTo(map);\n");
            
            // Define plane icon
            writer.write("var planeIcon = L.icon({\n");
            writer.write("  iconUrl: 'https://cdn-icons-png.flaticon.com/512/61/61212.png',\n");
            writer.write("  iconSize: [24, 24]\n");
            writer.write("});\n");

            // Add markers
            for (Flight f : flights) {
                String popup = String.format("<b>Callsign:</b> %s<br><b>Origin:</b> %s<br><b>Altitude:</b> %.1f m<br><b>Speed:</b> %.1f km/h", 
                        f.callsign, f.origin, f.altitude, f.speedKmh);
                writer.write(String.format("L.marker([%f, %f], {icon: planeIcon}).bindPopup(\"%s\").addTo(map);\n", 
                        f.latitude, f.longitude, popup));
            }
            
            writer.write("</script>\n</body>\n</html>\n");
            writer.close();
            
            System.out.println("Map generated successfully: " + mapFile.getAbsolutePath());
            
            // Automatically open the HTML file in the default browser (if supported)
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(mapFile.toURI());
            }
            
        } catch (Exception e) {
            System.out.println("Failed to generate map: " + e.getMessage());
        }
    }
}
