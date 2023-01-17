package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    public static Texture stationImg;
    public static Texture plusImg;
    public static Skin skin;

    public static void load() {
        stationImg = new Texture("station.png");
        plusImg = new Texture("plus.png");
        //skin = new Skin(Gdx.files.internal("neon_skin/neon-ui.json"));
        skin = new Skin(Gdx.files.internal("clean-crispy-skin/clean-crispy-ui.json"));
    }

    public static void dispose() {
        stationImg.dispose();
        plusImg.dispose();
    }
}