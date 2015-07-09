package conversion7.scene3dOld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.SnapshotArray;

public class Stage3d extends InputAdapter implements Disposable {
    private float width, height;
    private final ModelBatch modelBatch;
    private Environment environment;

    private PerspectiveCamera camera;

    private final Group3d root;
    private Actor3d scrollFocus;
    private Actor3d keyboardFocus;

    public Touchable touchable = Touchable.disabled;
    private int selecting = -1;

    private boolean canHit = false;


    /**
     * Creates a stage with a {@link #setViewport(float, float, boolean) viewport} equal to the device screen resolution. The stage
     * will use its own {@link SpriteBatch}.
     */
    public Stage3d() {
        this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    /**
     * Creates a stage with the specified {@link #setViewport(float, float, boolean) viewport} that doesn't keep the aspect ratio.
     * The stage will use its own {@link SpriteBatch}, which will be disposed when the stage is disposed.
     */
    public Stage3d(float width, float height) {
        this(width, height, false);
    }

    public Stage3d(float width, float height, boolean keepAspectRatio) {
        this.width = width;
        this.height = height;

        root = new Group3d();
        root.setStage3d(this);

        modelBatch = new ModelBatch();

        camera = new Camera3d();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.9f, 0.9f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0f, 0f, -1f, -0.8f, -0.2f));

        setViewport(width, height, keepAspectRatio);
    }

    public Stage3d(float width, float height, PerspectiveCamera camera) {
        this.width = width;
        this.height = height;
        root = new Group3d();
        root.setStage3d(this);
        modelBatch = new ModelBatch();
        this.camera = camera;
    }

    public Stage3d(float width, float height, PerspectiveCamera camera, Environment environment) {
        this.width = width;
        this.height = height;
        root = new Group3d();
        root.setStage3d(this);
        modelBatch = new ModelBatch();
        this.camera = camera;
        this.environment = environment;
    }


    public void setViewport(float width, float height) {
        setViewport(width, height, false, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Sets up the stage size using a viewport that fills the entire screen.
     *
     * @see #setViewport(float, float, boolean, float, float, float, float)
     */
    public void setViewport(float width, float height, boolean keepAspectRatio) {
        setViewport(width, height, keepAspectRatio, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Sets up the stage size and viewport. The viewport is the glViewport position and size, which is the portion of the screen
     * used by the stage. The stage size determines the units used within the stage, depending on keepAspectRatio:
     * <p/>
     * If keepAspectRatio is false, the stage is stretched to fill the viewport, which may distort the aspect ratio.
     * <p/>
     * If keepAspectRatio is true, the stage is first scaled to fit the viewport in the longest dimension. Next the shorter
     * dimension is lengthened to fill the viewport, which keeps the aspect ratio from changing. The {@link #getGutterWidth()} and
     * {@link #getGutterHeight()} provide access to the amount that was lengthened.
     *
     * @param viewportX      The top left corner of the viewport in glViewport coordinates (the origin is bottom left).
     * @param viewportY      The top left corner of the viewport in glViewport coordinates (the origin is bottom left).
     * @param viewportWidth  The width of the viewport in pixels.
     * @param viewportHeight The height of the viewport in pixels.
     */
    public void setViewport(float stageWidth, float stageHeight, boolean keepAspectRatio, float viewportX, float viewportY,
                            float viewportWidth, float viewportHeight) {
        if (keepAspectRatio) {
            if (viewportHeight / viewportWidth < stageHeight / stageWidth) {
                float toViewportSpace = viewportHeight / stageHeight;
                float toStageSpace = stageHeight / viewportHeight;
                float deviceWidth = stageWidth * toViewportSpace;
                float lengthen = (viewportWidth - deviceWidth) * toStageSpace;
                this.width = stageWidth + lengthen;
                this.height = stageHeight;
            } else {
                float toViewportSpace = viewportWidth / stageWidth;
                float toStageSpace = stageWidth / viewportWidth;
                float deviceHeight = stageHeight * toViewportSpace;
                float lengthen = (viewportHeight - deviceHeight) * toStageSpace;
                this.height = stageHeight + lengthen;
                this.width = stageWidth;
            }
        } else {
            this.width = stageWidth;
            this.height = stageHeight;
        }
        camera.viewportWidth = this.width;
        camera.viewportHeight = this.height;
    }

    public void draw() {
        camera.update();
        if (!root.isVisible()) return;
        modelBatch.begin(camera);
        root.draw(modelBatch, environment);
        modelBatch.end();
    }

    /** Calls {@link #act(float)} with {@link Graphics#getDeltaTime()}. */
    public void act() {
        act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    /**
     * Calls the {@link Actor#act(float)} method on each actor in the stage. Typically called each frame. This method also fires
     * enter and exit events.
     *
     * @param delta Time in seconds since the last frame.
     */
    public void act(float delta) {
        root.act(delta);
    }

    /**
     * Adds an actor to the root of the stage.
     *
     * @see Group#addActor(Actor)
     * @see Actor#remove()
     */
    public void addActor3d(Actor3d actor) {
        root.addActor3d(actor);
    }

    /**
     * Adds an action to the root of the stage.
     *
     * @see Group#addAction3d(Action)
     */
    public void addAction3d(Action3d action) {
        root.addAction3d(action);
    }

    /**
     * Returns the root's child actors.
     *
     * @see Group#getChildren()
     */
    public Array<Actor3d> getActors3d() {
        return root.getChildren();
    }

    /**
     * Adds a listener to the root.
     *
     * @see Actor#addListener(EventListener)
     */
    public boolean addListener(Event3dListener listener) {
        return root.addListener(listener);
    }

    /**
     * Removes a listener from the root.
     *
     * @see Actor#removeListener(EventListener)
     */
    public boolean removeListener(Event3dListener listener) {
        return root.removeListener(listener);
    }

    /** Removes the root's children, actions, and listeners. */
    public void clear() {
        unfocusAll();
        root.dispose();
        root.clear();
    }

    /** Removes the touch, keyboard, and scroll focused actors. */
    public void unfocusAll() {
        scrollFocus = null;
        keyboardFocus = null;
        //cancelTouchFocus();
    }

    /** Removes the touch, keyboard, and scroll focus for the specified actor and any descendants. */
    public void unfocus(Actor3d actor) {
        if (scrollFocus != null && scrollFocus.isDescendantOf(actor)) scrollFocus = null;
        if (keyboardFocus != null && keyboardFocus.isDescendantOf(actor)) keyboardFocus = null;
    }

    /**
     * Sets the actor that will receive key events.
     *
     * @param actor May be null.
     */
    public void setKeyboardFocus(Actor3d actor) {
        if (keyboardFocus == actor) return;
    }

    /**
     * Gets the actor that will receive key events.
     *
     * @return May be null.
     */
    public Actor3d getKeyboardFocus() {
        return keyboardFocus;
    }

    /**
     * Sets the actor that will receive scroll events.
     *
     * @param actor May be null.
     */
    public void setScrollFocus(Actor3d actor) {
        if (scrollFocus == actor) return;
    }

    /**
     * Gets the actor that will receive scroll events.
     *
     * @return May be null.
     */
    public Actor3d getScrollFocus() {
        return scrollFocus;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    /**
     * Sets the stage's camera. The camera must be configured properly or {@link #setViewport(float, float, boolean)} can be called
     * after the camera is set. {@link Stage#draw()} will call {@link Camera#update()} and use the {@link Camera#combined} matrix
     * for the SpriteBatch {@link SpriteBatch#setProjectionMatrix(com.badlogic.gdx.math.Matrix4) projection matrix}.
     */
    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    /** Returns the root group which holds all actors in the stage. */
    public Group3d getRoot() {
        return root;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void enableHit() {
        canHit = true;
    }

    public void disableHit() {
        canHit = false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (canHit) {
            Actor3d actor3d = getObject(screenX, screenY);
            selecting = actor3d != null ? 1 : -1;
            if (actor3d != null && actor3d.getName() != null)
                Gdx.app.log("", "" + actor3d.getName());
        }
        return selecting > 0;
        //return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            //setSelected(getObject(screenX, screenY));
            selecting = -1;
            return true;
        }
        return false;
        //if(touchable == Touchable.enabled)
        //	hit(screenX, screenY);
        //return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return selecting >= 0;
    }

    Vector3 position = new Vector3();
    int result = -1;
    float distance = -1;

    public Actor3d getObject(int screenX, int screenY) {
        Actor3d temp = null;
        SnapshotArray<Actor3d> children = root.getChildren();
        Actor3d[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            temp = hit3d(screenX, screenY, actors[i]);
            if (actors[i] instanceof Group3d)
                temp = hit3d(screenX, screenY, (Group3d) actors[i]);
        }
        children.end();
        return temp;
    }

    public Actor3d hit3d(int screenX, int screenY, Actor3d actor3d) {
        Ray ray = camera.getPickRay(screenX, screenY);
        float distance = -1;
        final float dist2 = actor3d.intersects(ray);
        if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
            distance = dist2;
            return actor3d;
        }
        return null;
    }

    public Actor3d hit3d(int screenX, int screenY, Group3d group3d) {
        Actor3d temp = null;
        SnapshotArray<Actor3d> children = group3d.getChildren();
        Actor3d[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            temp = hit3d(screenX, screenY, actors[i]);
            if (actors[i] instanceof Group3d)
                temp = hit3d(screenX, screenY, (Group3d) actors[i]);
        }
        children.end();
        return temp;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        clear();
    }
}
