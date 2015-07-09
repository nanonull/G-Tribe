package conversion7.tests_standalone.applications;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import conversion7.engine.ClientApplication;
import conversion7.game.Assets;
import org.lwjgl.opengl.GL11;

public class MaskTest1 implements ApplicationListener {

    SpriteBatch batch;
    OrthographicCamera cam;
    Texture grass;
    ShapeRenderer shapes;

    public static void main(String[] args) {
        ClientApplication.start(new MaskTest1());
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        cam = new OrthographicCamera();
        grass = new Texture(Assets.IMAGES_ANIMALS_FOLDER + "Wolf.png");
        shapes = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width, height);
        batch.setProjectionMatrix(cam.combined);
    }

    void drawMaskShape() {

    }

    @Override
    public void render() {
        //1. clear screen
        Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);

        //2. clear our depth buffer with 1.0
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        //3. set the function to LESS
        Gdx.gl.glDepthFunc(GL11.GL_LESS);

        //4. enable depth writing
        Gdx.gl.glEnable(GL11.GL_DEPTH_TEST);

        //5. Enable depth writing, disable RGBA color writing 
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);

        ///////////// Draw mask shape(s)

        //6. render your primitive shapes
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        shapes.setColor(1f, 0f, 0f, 0.5f);
        shapes.circle(50, 50, 50);
        shapes.setColor(0f, 1f, 0f, 0.5f);
        shapes.rect(50, 50, 100, 100);

        shapes.end();

        ///////////// Draw sprite(s) to be masked
        batch.begin();

        //8. Enable RGBA color writing
        //   (SpriteBatch.begin() will disable depth mask)
        Gdx.gl.glColorMask(true, true, true, true);

        //9. Make sure testing is enabled.
        Gdx.gl.glEnable(GL11.GL_DEPTH_TEST);

        //10. Now depth discards pixels outside our masked shapes
        Gdx.gl.glDepthFunc(GL11.GL_EQUAL);

        //push to the batch
        batch.draw(grass, 0, 0);

        //end/flush your batch
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        grass.dispose();
        shapes.dispose();
    }
}