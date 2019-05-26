package conversion7.engine.customscene;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.geometry.BoundingBox2;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class ModelActor extends SceneNode3d {

    private static final Logger LOG = Utils.getLoggerForClass();

    protected ModelBatch modelBatch;
    public ModelInstance modelInstance;
    private Environment environment;
    private float collectedDelta;
    Matrix4 rotationMatrix;

    public ModelActor(String name, ModelInstance modelInstance, ModelBatch modelBatch) {
        super(name);
        this.modelInstance = modelInstance;
        this.modelBatch = modelBatch;
    }

    /** Use when you know ModelBatch you will use */
    public ModelActor(ModelInstance modelInstance, ModelBatch modelBatch) {
        this(null, modelInstance, modelBatch);
    }

    /** Use when ModelGroup knows about ModelBatch you will use */
    public ModelActor(ModelInstance modelInstance) {
        this(null, modelInstance, null);
    }

    /**
     * Get modelInstance as Actor3d.<br>
     * Be sure you have constructed this actor from Actor3d wrapper before!<br>
     * Because it could throw cast exception.
     */
    public Actor3d getAsActor3d() {
        return (Actor3d) modelInstance;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /** Old materials will be removed */
    public void setMaterial(Material material) {
        modelInstance.materials.clear();
        modelInstance.materials.add(material);
        modelInstance.model.materials.clear();
        modelInstance.model.materials.add(material);
        for (Node node : modelInstance.nodes) {
            for (NodePart part : node.parts) {
                part.material = material;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (modelInstance instanceof Actor3d) {
            Actor3d actor3d = (Actor3d) modelInstance;
            if (isFrustrumVisible()) {
                stage._actAnimations++;
                if (collectedDelta > 0) {
                    actor3d.doAnimate(delta + collectedDelta);
                    collectedDelta = 0;
                } else {
                    actor3d.doAnimate(delta);
                }
            } else {
                stage._skipAnimations++;
                collectedDelta += delta;
            }
        }
    }

    @Override
    public void dispose() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("dispose {}", toString());
        }
        try {
            if (modelInstance instanceof Actor3d) {
                ((Actor3d) modelInstance).dispose();
            } else {
                modelInstance.model.dispose();
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("buffer not allocated with newUnsafeByteBuffer or already disposed")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Already disposed: {}", e.getMessage());
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * modelBatch#flush must be called from outside<BR>
     * otherwise use ModelGroup#draw
     */
    @Override
    public boolean draw() {
        if (super.draw()) {
            this.modelBatch.render(this.modelInstance, environment);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWrappedObjects() {
        rotationMatrix = modelInstance.transform.setFromEulerAngles(
                globalRotation.x, globalRotation.y, globalRotation.z).cpy();
        modelInstance.transform.setToTranslationAndScaling(
                globalPosition.x, globalPosition.y, globalPosition.z,
                globalScale.x, globalScale.y, globalScale.z);
        modelInstance.transform.mul(rotationMatrix);
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

    /** It doesn't affected by scale */
    public void createAutoBoundingBox() {
        boundBoxActor = new BoxActor(this, new BoundingBox2(modelInstance.calculateBoundingBox(new BoundingBox())));
        boundBoxActor.stage = this.stage;
    }

    public void applyMaterialAttribute(Attribute attribute) {
        for (Material material : modelInstance.materials) {
            material.set(attribute);
        }
    }
}
