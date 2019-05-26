package conversion7.engine.customscene;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.geometry.BoundingBox2;

public class DecalActor extends SceneNode3d {

    protected DecalBatch decalBatch;
    // texture layer
    public Decal decal;
    public boolean faceToCamera = false;
    // TODO text layer
    private String text;
    private BitmapFont font;
    private SpriteBatch spriteBatch;

    /**
     * Use when you know DecalBatch you will use
     */
    public DecalActor(String name, Decal decal, DecalBatch decalBatch) {
        super(name);
        this.decal = decal;
        this.decalBatch = decalBatch;
    }

    public DecalActor(Decal decal, DecalBatch decalBatch) {
        this(null, decal, decalBatch);
    }

    public DecalActor(String name, Decal decal) {
        this(name, decal, null);
    }

    /**
     * Use when DecalGroup knows about DecalBatch you will use
     */
    public DecalActor(Decal decal) {
        this(null, decal, null);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    /**
     * decalBatch#flush must be called from outside<BR>
     * otherwise use DecalGroup#draw
     */
    @Override
    public boolean draw() {
        if (super.draw()) {
            if (faceToCamera) {
                decal.lookAt(stage.getCamera().position, Vector3.Y);
                decal.rotateX(180);
            }
            this.decalBatch.add(this.decal);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWrappedObjects() {
        decal.setPosition(globalPosition.x, globalPosition.y, globalPosition.z);

        decal.setRotationX(0); // nail... it will reset x,y,z rotation
        decal.rotateX(globalRotation.y);
        decal.rotateY(globalRotation.x);
        decal.rotateZ(globalRotation.z);
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        if (boundBoxActor == null) {
            return;
        } else {
            if (Intersector.intersectRayBounds(pickRay, boundBoxActor.boundingBox.wrappedBox, whereIntersected)) {
                intersectedActors.addNode(this, whereIntersected);
            }
        }
    }

    public void createBoundingBox() {
        boundBoxActor = new BoxActor(this, new BoundingBox2(this));
        boundBoxActor.stage = this.stage;
    }

    /** TODO Write some text over decal */
    public void addTextLayer(String text, BitmapFont font, SpriteBatch spriteBatch) {
        this.text = text;
        this.font = font;
        this.spriteBatch = spriteBatch;
    }
}
