package eu.elektropolnilnice.igra.utils;

import com.badlogic.gdx.utils.Array;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Station {
    public double lattitude;
    public double longitude;
    public String title;
    public String postcode;
    public String country;
    public String town;
    public String _id;
    public String usageCost;
    public int usageType;
    public String dateCreated;
    public String dateAddedToOurApp;

    public Station(double lattitude, double longitude, String title, String postcode, String country, String town, String _id) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.title = title;
        this.postcode = postcode;
        this.country = country;
        this.town = town;
        this._id = _id;
    }

    public static Array<Station> getStations() throws IOException {
        Array<Station> stationsArray = fetchAddressData();

        if (stationsArray != null) {
            fetchElektropolnilnicaData(stationsArray);
        } else {
            System.out.println("No stations found in /address.");
        }

        return stationsArray;
    }

    private static Array<Station> fetchAddressData() throws IOException {
        URL url = new URL("http://elektropolnilnice.eu:3000/address");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray stationsJSON = new JSONArray(response.toString());
            Array<Station> stationsArray = new Array<>();

            for (int i = 0; i < stationsJSON.length(); i++) {
                JSONObject stationJSON = stationsJSON.getJSONObject(i);

                stationsArray.add(new Station(
                    stationJSON.getDouble("latitude"),
                    stationJSON.getDouble("longitude"),
                    stationJSON.getString("title"),
                    stationJSON.getString("postcode"),
                    stationJSON.getString("country"),
                    stationJSON.getString("town"),
                    stationJSON.getString("_id")
                ));
            }

            connection.disconnect();
            return stationsArray;
        } else {
            System.out.println("Failed to fetch data from /address. HTTP Response Code: " + responseCode);
        }

        connection.disconnect();
        return null;
    }

    private static void fetchElektropolnilnicaData(Array<Station> stationsArray) throws IOException {
        URL url = new URL("http://elektropolnilnice.eu:3000/elektropolnilnice");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray elektropolnilniceJSON = new JSONArray(response.toString());

            for (int i = 0; i < elektropolnilniceJSON.length(); i++) {
                JSONObject elektropolnilnica = elektropolnilniceJSON.getJSONObject(i);
                String addressId = elektropolnilnica.getString("address");

                for (Station station : stationsArray) {
                    if (station._id.equals(addressId)) {
                        // Shranjevanje dodatnih podatkov v objekt Station
                        station.usageCost = elektropolnilnica.optString("usageCost", "Unknown");
                        station.usageType = elektropolnilnica.optInt("usageType", -1);
                        station.dateCreated = DateFormatter.formatDate(elektropolnilnica.optString("dateCreated", "Unknown"));
                        station.dateAddedToOurApp = DateFormatter.formatDate(elektropolnilnica.optString("dateAddedToOurApp", "Unknown"));

                        // Izpis podatkov v konzolo
                        System.out.println("Station Updated:");
                        System.out.println("  Title: " + station.title);
                        System.out.println("  Latitude: " + station.lattitude);
                        System.out.println("  Longitude: " + station.longitude);
                        System.out.println("  Usage Cost: " + station.usageCost);
                        System.out.println("  Usage Type: " + station.usageType);
                        System.out.println("  Date Created: " + station.dateCreated);
                        System.out.println("  Date Added to Our App: " + station.dateAddedToOurApp);
                        break;
                    }
                }
            }

            connection.disconnect();
        } else {
            System.out.println("Failed to fetch data from /elektropolnilnice. HTTP Response Code: " + responseCode);
        }

        connection.disconnect();
    }
}
