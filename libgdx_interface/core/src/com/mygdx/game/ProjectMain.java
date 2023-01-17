package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.data.Station;
import com.mygdx.game.data.Stations;
import com.mygdx.game.utils.Assets;
import com.mygdx.game.utils.Geolocation;
import com.mygdx.game.utils.MapRasterTiles;
import com.mygdx.game.utils.PixelPosition;
import com.mygdx.game.utils.ZoomXY;

import java.io.IOException;

public class ProjectMain extends ApplicationAdapter implements GestureDetector.GestureListener, InputProcessor {

    private SpriteBatch batch;
    private Sprite sprite;
    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;
    public BitmapFont font;
    private Rectangle rect;
    private InputMultiplexer inputMultiplexer;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    private final int NUM_TILES = 3;
    private final int ZOOM = 13;
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.559428, 15.645674);
    // private final Geolocation MARKER_GEOLOCATION = new Geolocation(46.559070, 15.638100);
    private final int WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    private final int HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;

    // Stations
    Stations stations;
    private Stage stage;
    TextField latitudeField;
    TextField longitudeField;
    TextField stationName;
    TextButton addBtn;

    TextButton backBtn;

    double longitude = 0;
    double latitude = 0;
    String newStationLat;
    String newStationLng;
    boolean mapClicked = false;
    boolean addedStation = false;

    Station pickedStation;
    String theme = "AQI";

    @Override
    public void create() {
        Assets.load();
        stations = new Stations();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        stage = new Stage();

        sprite = new Sprite(Assets.plusImg);
        sprite.setPosition(WIDTH - 150, 70);
        sprite.setSize(80,80);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        camera.viewportWidth = WIDTH / 2f;
        camera.viewportHeight = HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        font = new BitmapFont();
        font.getData().setScale(2);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        //Gdx.input.setInputProcessor(stage);
        stage.addActor(createDropDown());

        touchPosition = new Vector3();
        inputMultiplexer.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(inputMultiplexer);
       // Gdx.input.setInputProcessor(new GestureDetector(this));


        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(ZOOM, centerTile.x - ((NUM_TILES - 1) / 2), centerTile.y - ((NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(NUM_TILES, NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        stage.act();
        stage.draw();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawAreas();
        drawMessage();
        shapeRenderer.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawStations(Gdx.graphics.getDeltaTime());
        sprite.draw(batch);
        if(mapClicked) {
            font.setColor(Color.GREEN);
            font.draw(batch, "User clicked at - long: " + longitude + ", lat: " + latitude , rect.x + 20, rect.y + 35);
        }
        if(addedStation) {
            batch.setColor(Color.YELLOW);
            PixelPosition marker = MapRasterTiles.getPixelPosition(Double.parseDouble(newStationLat),Double.parseDouble(newStationLng), MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
            batch.draw(Assets.stationImg, marker.x-(Assets.stationImg.getWidth()/2), marker.y-(Assets.stationImg.getHeight()/2));
          //  stations.list.add(new Station(Double.parseDouble(newStationLat),Double.parseDouble(newStationLng)));
            batch.setColor(Color.WHITE);
        }
        batch.end();


    }

    private void drawStations(float delta) {
        for (Station station : stations.list) {
            PixelPosition marker = MapRasterTiles.getPixelPosition(station.lat, station.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
            batch.draw(Assets.stationImg, marker.x-(Assets.stationImg.getWidth()/2), marker.y-(Assets.stationImg.getHeight()/2));
            station.drawParticles(batch, delta, marker.x, marker.y);
        }
    }


    private Actor createUI() {
        final Table table = new Table();

        table.setPosition(WIDTH / 3f - 30, HEIGHT / 3f - 30);
        //table.setFillParent(true);

        latitudeField = new TextField("Latitude: ", Assets.skin);
        longitudeField = new TextField("Longitude: ", Assets.skin);
        stationName = new TextField("Station Name:", Assets.skin);
        addBtn = new TextButton("ADD", Assets.skin);

        table.add(stationName).expandX().fillX().row();
        table.add(latitudeField).expandX().fillX().row();
        table.add(longitudeField).expandX().fillX().row();
        table.add(addBtn).expandX().fillX().row();

        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newStationLat = latitudeField.getText();
                newStationLng = longitudeField.getText();
                addedStation = true;
                table.remove();
            }
        });

        return table;
    }

    private Actor createDropDown() {
        final Table table = new Table();

        table.setPosition(WIDTH/2f, HEIGHT/2f);
        table.setFillParent(true);

        table.add(new TextField("Choose filter", Assets.skin)).expandX().fillX().row();

        // Create a new SelectBox
        final SelectBox<String> selectBox = new SelectBox<String>(Assets.skin);
        // Create a list of items to display in the SelectBox
        Array<String> items = new Array<String>();
        items.addAll("AQI", "PM2_5", "PM10", "NO2", "birch", "alder", "grasses", "ragweed", "mugwort", "olivetree");

        // Set the items list to the SelectBox
        selectBox.setItems(items);

        // Set the selected item in the SelectBox
        selectBox.setSelected("AQI");

        // Add the SelectBox to a stage
        table.addActor(selectBox);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theme = selectBox.getSelected();
            }
        });

        return table;
    }

    private Actor createData(Station station, String theme, PixelPosition marker) {
        String data="0";
        switch (theme) {
            case "AQI":
                data = String.valueOf(station.AQI);
                break;
            case "PM2_5":
                data = String.valueOf(station.PM2_5);
                break;
            case "PM10":
                data = String.valueOf(station.PM10);
                break;
            case "NO2" :
                data = String.valueOf(station.NO2);
                break;
            case "birch" :
                data = String.valueOf(station.birch);
                break;
            case "alder" :
                data = String.valueOf(station.alder);
                break;
            case "grasses" :
                data = String.valueOf(station.grasses);
                break;
            case "ragweed" :
                data = String.valueOf(station.ragweed);
                break;
            case "mugwort" :
                data = String.valueOf(station.mugwort);
                break;
            case "olivetree" :
                data = String.valueOf(station.olivetree);
                break;
        };

        final Table table = new Table();

        //table.setPosition((float)station.lat + WIDTH/2f, (float)station.lng + HEIGHT/2f);

        table.setPosition((float)marker.x, (float)marker.y);
        //table.setFillParent(true);

        TextField textField = new TextField(theme + ": " + data, Assets.skin);
        textField.setAlignment(Align.center);
        font.getData().setScale(2);
        table.add(textField).expandX().fillX().center().row();
        backBtn = new TextButton("BACK", Assets.skin);

        table.add(textField).expandX().fillX().row();
        table.add(backBtn).expandX().fillX().row();

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.remove();
            }
        });
        return table;
    }
    private void drawMessage() {
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        rect = new Rectangle(WIDTH / 3f - 30, 75, 850, 50);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }
    private void drawAreas() {
        shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));

        PixelPosition vrt = MapRasterTiles.getPixelPosition(46.561851, 15.637359, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        float vrtW = 60; float vrtH = 30;
        shapeRenderer.rect(vrt.x, vrt.y, vrtW/2, vrtH/2, vrtW, vrtH, 1.0f, 1.0f, -20f);

        PixelPosition magdalena = MapRasterTiles.getPixelPosition(46.550595, 15.644463, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        float magdalenaW = 20; float magdalenaH = 40;
        shapeRenderer.rect(magdalena.x, magdalena.y, magdalenaW/2, magdalenaH/2, magdalenaW, magdalenaH, 1.0f, 1.0f, -20f);

        PixelPosition rakusev = MapRasterTiles.getPixelPosition(46.561222, 15.650760, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        float rakusevW = 15; float rakusevH = 20;
        shapeRenderer.rect(rakusev.x-(rakusevW/2), rakusev.y-(rakusevH/2), rakusevW/2, rakusevH/2, rakusevW, rakusevH, 1.0f, 1.0f, 0);

        PixelPosition sodobna = MapRasterTiles.getPixelPosition(46.558788, 15.654294, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        float sodobnaW = 10; float sodobnaH = 20;
        shapeRenderer.rect(sodobna.x-(sodobnaW/2), sodobna.y-(sodobnaH/2), sodobnaW/2, sodobnaH/2, sodobnaW, sodobnaH, 1.0f, 1.0f, -10);
    }

    @Override
    public void dispose() {
        for (Station station : stations.list) station.dipose();
        batch.dispose();
        shapeRenderer.dispose();
        Assets.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }
        TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // get the first layer of the map

        if (Gdx.input.justTouched()) {
            Vector3 clickCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 position = camera.unproject(clickCoordinates);
            int x = (int) (position.x / layer.getTileWidth());
            int y = (int)(position.y / layer.getTileHeight());

            inputMultiplexer.addProcessor(stage);

            for (Station station : stations.list) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(station.lat, station.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                if (marker.x <= clickCoordinates.x && clickCoordinates.x <= marker.x + Assets.stationImg.getWidth() &&
                        marker.y <= clickCoordinates.y && clickCoordinates.y <= marker.y + Assets.stationImg.getHeight()) {
                    pickedStation = station;
                   // Gdx.input.setInputProcessor(stage);
                    stage.addActor(createData(station, theme, marker));
                }
            }
            Rectangle plusIcon = sprite.getBoundingRectangle();

            // check if the texture was clicked
            if (plusIcon.contains(position.x, position.y)) {
               // Gdx.input.setInputProcessor(stage);
                stage.addActor(createUI());
            }

            TiledMapTileLayer.Cell cell = layer.getCell(x, y);
            if (cell != null) {
               longitude = MapRasterTiles.tile2long(x + beginTile.x,ZOOM);
               latitude = MapRasterTiles.tile2lat(y + beginTile.y, ZOOM);
               mapClicked = true;
            }
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, HEIGHT - effectiveViewportHeight / 2f);
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
