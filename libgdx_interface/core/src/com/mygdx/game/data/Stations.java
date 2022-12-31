package com.mygdx.game.data;

import com.badlogic.gdx.utils.Array;

public class Stations {

    public Array<Station> list = new Array<Station>();

    private final String[] urls = {
            "http://straylight.ignorit.al:3000/data/search/Tezno",
            "http://straylight.ignorit.al:3000/data/search/Center",
            "http://straylight.ignorit.al:3000/data/search/Maribor",
            "http://straylight.ignorit.al:3000/data/search/Magdalena",
            "http://straylight.ignorit.al:3000/data/search/MB%20Titova",
            "http://straylight.ignorit.al:3000/data/search/MB%20Vrbanski",
            "http://straylight.ignorit.al:3000/data/search/Koro%C5%A1ka%20vrata"
    };

    public Stations() {
        for (String url : urls) {
            try {
                this.list.add(new Station(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        for (Station station : list) {
            station.refresh();
        }
    }
}
