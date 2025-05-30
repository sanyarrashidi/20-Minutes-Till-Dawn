package com.example.dawn.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.example.dawn.Dawn;
import com.example.dawn.configuration.Configuration;
import com.example.dawn.entity.Player;
import com.example.dawn.service.controls.Control;


@Component
public class Box2DService {
    private static final Vector2 GRAVITY = new Vector2(0f, -9.81f); 
    private static final float STEP = 1f / 30f; 
    private static final float WIDTH = Dawn.WIDTH / 10f; 
    private static final float HEIGHT = Dawn.HEIGHT / 10f; 
    private static final float SIZE = 3f; 
    @Inject private ControlsService controlsService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(WIDTH, HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();

    
    public void create() {
        dispose();
        world = new World(GRAVITY, true);
        createWorldBounds();
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                players.add(new Player(control, getPlayerBody(index), viewport));
            }
        }
    }

    
    private void createWorldBounds() {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;

        final ChainShape shape = new ChainShape();
        shape.createLoop(new float[] { -WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f, -WIDTH / 2f, HEIGHT / 2f, WIDTH / 2f,
                HEIGHT / 2f, WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f });

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        world.createBody(bodyDef).createFixture(fixtureDef);
        shape.dispose();
    }

    private Body getPlayerBody(final int index) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = false;
        final PolygonShape shape = new PolygonShape();
        switch (index) {
            case 0:
                bodyDef.position.set(-WIDTH / 2f + SIZE * 2f, HEIGHT / 4f);
                
                shape.setAsBox(SIZE, SIZE);
                break;
            case 1:
                bodyDef.position.set(0f, HEIGHT / 4f);
                
                shape.set(new float[] { -SIZE, 0f, -SIZE / 2f, SIZE, SIZE / 2f, SIZE, SIZE, 0f, SIZE / 2f, -SIZE,
                        -SIZE / 2f, -SIZE, -SIZE, 0f });
                break;
            default:
                bodyDef.position.set(WIDTH / 2f - SIZE * 2f, HEIGHT / 4f);
                
                shape.set(new float[] { -SIZE, -SIZE, 0f, SIZE, SIZE, -SIZE, -SIZE, -SIZE });
        }
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.3f;
        fixtureDef.density = 0.05f;
        final Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    
    public void update(final float delta) {
        timeSinceUpdate += delta;
        while (timeSinceUpdate > STEP) {
            timeSinceUpdate -= STEP;
            world.step(STEP, 8, 3);
            for (final Player player : players) {
                player.update(STEP);
            }
        }
    }

    
    public void initiateControls(final InputMultiplexer inputMultiplexer) {
        for (final Player player : players) {
            player.getControl().attachInputListener(inputMultiplexer);
        }
    }

    public void resize(final int width, final int height) {
        viewport.update(width, height);
    }

    
    public World getWorld() {
        return world;
    }

    
    public Viewport getViewport() {
        return viewport;
    }

    @Destroy
    public void dispose() {
        players.clear();
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}