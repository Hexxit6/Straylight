package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    double longitude = 0;
    double latitude = 0;
    String newStationLat;
    String newStationLng;
    boolean mapClicked = false;
    boolean addedStation = false;

    @Override
    public void create() {
        Assets.load();
        stations = new Stations();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        stage = new Stage();

        sprite = new Sprite(Assets.plusImg);
        sprite.setPosition(WIDTH - 100, 70);
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

        touchPosition = new Vector3();
        Gdx.input.setInputProcessor(new GestureDetector(this));

        Gdx.input.setInputProcessor(new InputProcessor() {
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
                // convert screen coordinates to world coordinates
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));

                Rectangle plusIcon = sprite.getBoundingRectangle();

                // check if the texture was clicked
                if (plusIcon.contains(worldCoords.x, worldCoords.y)) {
                    Gdx.input.setInputProcessor(stage);
                    stage.addActor(createUI());
                }

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
        });

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

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawStations(Gdx.graphics.getDeltaTime());
        sprite.draw(batch);
        if(mapClicked) {
            font.setColor(Color.RED);
            font.draw(batch, "User clicked at - long: " + longitude + ", lat: " + latitude , WIDTH / 3f - 30, 50);
        }
        if(addedStation) {
            batch.setColor(Color.RED);
            stations.list.add(new Station(Double.parseDouble(newStationLat),Double.parseDouble(newStationLng)));
            batch.setColor(Color.WHITE);
        }
        batch.end();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawAreas();
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
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
