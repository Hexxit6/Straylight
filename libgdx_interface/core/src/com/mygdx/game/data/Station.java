package com.mygdx.game.data;

import com.badlogic.gdx.utils.Json;
import com.mygdx.game.utils.Geolocation;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Station extends Geolocation {

    private URL url;

    public double AQI = 0;
    public double PM2_5 = 0;
    public double PM10 = 0;
    public double NO2 = 0;
    public double birch = 0;
    public double alder = 0;
    public double grasses = 0;
    public double ragweed = 0;
    public double mugwort = 0;
    public double olivetree = 0;

    public Station(String url) throws MalformedURLException {
        super(0, 0);
        this.url = new URL(url);
        this.refresh();
    }

    public void refresh() {
        try {
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestMethod("GET");
            req.connect();

            int res_code = req.getResponseCode();
            if (res_code != 200) throw new RuntimeException("Response code: " + res_code);
            else {
                String lines;
                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                while ((lines = reader.readLine()) != null) response.append(lines);
                JSONObject json = new JSONObject(response.toString());
                reader.close();

                JSONObject station = json.getJSONObject("station");
                JSONObject data = json.getJSONObject("data");

                JSONObject pollutants = data.getJSONObject("pollutants");
                JSONObject allergens = data.getJSONObject("allergens");

                String[] geolocation = station.getString("geolocation").split(",");
                this.lat = Double.parseDouble(geolocation[0]);
                this.lng = Double.parseDouble(geolocation[1]);

                this.AQI = pollutants.getDouble("AQI");
                this.PM2_5 = pollutants.getDouble("PM2_5");
                this.PM10 = pollutants.getDouble("PM10");
                this.NO2 = pollutants.getDouble("NO2");
                this.birch = allergens.getDouble("Birch");
                this.alder = allergens.getDouble("Alder");
                this.grasses = allergens.getDouble("Grasses");
                this.ragweed = allergens.getDouble("Ragweed");
                this.mugwort = allergens.getDouble("Mugwort");
                this.olivetree = allergens.getDouble("OliveTree");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(url.toString());
        }
    }
}
