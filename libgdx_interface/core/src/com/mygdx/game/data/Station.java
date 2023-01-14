package com.mygdx.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.utils.Assets;
import com.mygdx.game.utils.Geolocation;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Station extends Geolocation {

    private URL url;
    public float AQI = 0;
    public float PM2_5 = 0;
    public float PM10 = 0;
    public float NO2 = 0;
    public float birch = 0;
    public float alder = 0;
    public float grasses = 0;
    public float ragweed = 0;
    public float mugwort = 0;
    public float olivetree = 0;

    public ParticleEffect particle;
    private final float FACTOR = 5f;

    public Station(String url) throws MalformedURLException {
        super(0, 0);
        this.url = new URL(url);
        particle = new ParticleEffect();
        particle.load(Gdx.files.internal("generic.pe"), Gdx.files.internal(""));
        particle.getEmitters().first().scaleSize(0.2f);
        this.refresh();
    }

    public Station(double latitude, double longitude) {
        super(latitude,longitude);
        particle = new ParticleEffect();
        particle.load(Gdx.files.internal("generic.pe"), Gdx.files.internal(""));
    }

    public void drawParticles(SpriteBatch batch, float delta, float x, float y) {
        ParticleEmitter emitter = particle.getEmitters().first();
        emitter.setPosition(x, y);

        particle.update(delta);
        if (particle.isComplete()) particle.reset();
        particle.draw(batch);
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

                this.AQI = pollutants.getFloat("AQI");
                this.PM2_5 = pollutants.getFloat("PM2_5");
                this.PM10 = pollutants.getFloat("PM10");
                this.NO2 = pollutants.getFloat("NO2");
                this.birch = allergens.getFloat("Birch");
                this.alder = allergens.getFloat("Alder");
                this.grasses = allergens.getFloat("Grasses");
                this.ragweed = allergens.getFloat("Ragweed");
                this.mugwort = allergens.getFloat("Mugwort");
                this.olivetree = allergens.getFloat("OliveTree");
            }
            configureEmitter();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(url.toString());
        }
    }

    private void setEmitterColor(ParticleEmitter emitter, float r, float g, float b) {
        float colors[] = emitter.getTint().getColors();
        colors[0] = r / 255;
        colors[1] = g / 255;
        colors[2] = b / 255;
    }

    public void configureEmitter() {
        float[] pollutants = {AQI, PM2_5, PM10, NO2, birch, alder, grasses, ragweed, mugwort, olivetree};
        float max = AQI;
        int index = 0;
        for (int i=0; i<pollutants.length; i++) {
            if (pollutants[i] > max) {
                max = pollutants[i];
                index = i;
            }
        }

        ParticleEmitter emitter = particle.getEmitters().first();
        switch (index) {
            case 0:
                emitter.getEmission().setHigh(AQI * FACTOR);
                setEmitterColor(emitter, 0, 188, 242);
                break;
            case 1:
                emitter.getEmission().setHigh(PM2_5 * FACTOR);
                setEmitterColor(emitter, 255, 241, 0);
                break;
            case 2:
                emitter.getEmission().setHigh(PM10 * FACTOR);
                setEmitterColor(emitter, 255, 140, 0);
                break;
            case 3:
                emitter.getEmission().setHigh(NO2 * FACTOR);
                setEmitterColor(emitter, 232, 17, 35);
                break;
            case 4:
                emitter.getEmission().setHigh(birch * FACTOR);
                setEmitterColor(emitter, 236, 0, 140);
                break;
            case 5:
                emitter.getEmission().setHigh(alder * FACTOR);
                setEmitterColor(emitter, 104, 33, 122);
                break;
            case 6:
                emitter.getEmission().setHigh(grasses * FACTOR);
                setEmitterColor(emitter, 0, 24, 143);
                break;
            case 7:
                emitter.getEmission().setHigh(ragweed * FACTOR);
                setEmitterColor(emitter, 0, 178, 148);
                break;
            case 8:
                emitter.getEmission().setHigh(mugwort * FACTOR);
                setEmitterColor(emitter, 0, 158, 73);
                break;
            case 9:
                emitter.getEmission().setHigh(olivetree * FACTOR);
                setEmitterColor(emitter, 186, 216, 10);
                break;
        }
    }

    public void dipose() {
        particle.dispose();
    }
}
