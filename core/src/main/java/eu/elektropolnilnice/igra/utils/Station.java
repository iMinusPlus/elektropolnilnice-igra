package eu.elektropolnilnice.igra.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Station {
    public double lattitude;
    public double longitude;

    public Station(double lattitude, double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public static Array<Station> getStations() throws IOException {
        URL url = new URL("http://elektropolnilnice.eu:3000/address");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Request setup
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) { // HTTP OK
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            // Read the response
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON Array using org.json
            JSONArray stations = new JSONArray(response.toString());

            Array<Station> stationsArray = new Array<Station>();
//            Station[] stationsArray = new Station[stations.length()];

            // Loop through the data
            for (int i = 0; i < stations.length(); i++) {
                stationsArray.add(new Station(stations.getJSONObject(i).getDouble("latitude"), stations.getJSONObject(i).getDouble("longitude")));
//                stationsArray[i] = new Station(stations.getJSONObject(i).getDouble("latitude"), stations.getJSONObject(i).getDouble("longitude"));
                System.out.println("Station: " + stations.getJSONObject(i).getDouble("latitude"));
            }

            connection.disconnect();
            return stationsArray;
        } else {
            System.out.println("Failed to fetch data. HTTP Response Code: " + responseCode);
        }

        connection.disconnect();
        return null;
    }
}
