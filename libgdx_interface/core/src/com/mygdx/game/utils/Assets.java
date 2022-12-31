package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Assets {

    public static Texture stationImg;
    public static ParticleEffect genericPE;

    public static void load() {
        stationImg = new Texture("station.png");

        genericPE = new ParticleEffect();
        genericPE.load(Gdx.files.internal("generic.pe"), Gdx.files.internal(""));
        // genericPE.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,0);
    }

    public static void dispose() {
        stationImg.dispose();
        genericPE.dispose();
    }
}