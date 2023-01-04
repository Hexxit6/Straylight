package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    public static Texture stationImg;
    public static Texture plusImg;
    public static Skin skin;
    public static ParticleEffect genericPE;

    public static void load() {
        stationImg = new Texture("station.png");
        plusImg = new Texture("add.png");
        skin = new Skin(Gdx.files.internal("neon_skin/neon-ui.json"));

        genericPE = new ParticleEffect();
        genericPE.load(Gdx.files.internal("generic.pe"), Gdx.files.internal(""));
        // genericPE.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,0);
    }

    public static void dispose() {
        stationImg.dispose();
        plusImg.dispose();
        genericPE.dispose();
    }
}