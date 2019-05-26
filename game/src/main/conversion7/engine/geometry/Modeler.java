package conversion7.engine.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.Actor3d;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;
import org.testng.Assert;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

public class Modeler {

    private static float MAX_FOREST_RADIUS = 0.5f;
    private static float TREE_SCALE = 0.06f;
    public static final float CELL_SELECTION_MODEL_WIDTH = 0.8f;
    public static final float CAMP_MODEL_WIDTH = 0.7f;
    public static final float LEVEL0_SELECTION_ALPHA = 0.1f;
    public static final float LEVEL1_SELECTION_ALPHA = 0.2f;
    public static final float LEVEL2_SELECTION_ALPHA = 0.4f;
    private static final float HALF_OPACITY = 0.5f;

    public static ModelInstance buildDebugModel() {
        Model model = Gdxg.modelBuilder.createBox(0.5f, 0.5f, 0.5f,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildAreaDebugModel() {
        Model model = Gdxg.modelBuilder.createBox(0.2f, 0.2f, 0.2f,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildRedBox() {
        Model model = Gdxg.modelBuilder.createBox(CELL_SELECTION_MODEL_WIDTH, 1f, CELL_SELECTION_MODEL_WIDTH,
                new Material(ColorAttribute.createDiffuse(Color.SCARLET)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildMountDebrisModel(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.85f;
        Model model = Gdxg.modelBuilder.createBox(0.42f, 1f, 0.42f,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildBox(Color color, float opacity, float size) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = opacity;
        Model model = Gdxg.modelBuilder.createBox(size, size, size,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildExpJewel(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.85f;
        Model model = Gdxg.modelBuilder.createBox(0.2f, 2, 0.2f,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildSmallBox(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.6f;
        Model model = Gdxg.modelBuilder.createBox(0.3f, 0.6f, 0.3f,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildTotemBox(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = HALF_OPACITY;
        Model model = Gdxg.modelBuilder.createBox(0.2f, 2, 0.2f,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildAnimalSpawn(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = HALF_OPACITY;

        Model model = Gdxg.modelBuilder.createCapsule(CAMP_MODEL_WIDTH / 2f, 2, 10,
                new Material(ColorAttribute.createDiffuse(color)
                        , blendingAttribute
                        , new NoiseCubeAttribute(),
                        new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildHalfCampBox(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = HALF_OPACITY;
        Model model = Gdxg.modelBuilder.createBox(CAMP_MODEL_WIDTH, 2, CAMP_MODEL_WIDTH,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildCampBox(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = HALF_OPACITY;
        Model model = Gdxg.modelBuilder.createBox(CAMP_MODEL_WIDTH, 3, CAMP_MODEL_WIDTH,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute, new DepthTestAttribute()),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    @Deprecated
    public static ModelActor buildModelActor_tree() {
        Actor3d actor3d = new Actor3d(Assets.getModel("tree"), 0, 0, 0f);
        ModelActor modelActor = new ModelActor(actor3d, Gdxg.modelBatch);
        modelActor.setEnvironment(Gdxg.graphic.environment);
        modelActor.setScale(TREE_SCALE);
        modelActor.frustrumRadius = 1;
        return modelActor;
    }

    public static ModelGroup buildModelGroup_forest() {
        ModelGroup forestGroup = new ModelGroup("forestGroup", Gdxg.modelBatch);
        forestGroup.frustrumRadius = 1f;

        for (int i = 0; i < 4; i++) {
            Actor3d actor3d = new Actor3d(Assets.getModel("tree"), 0, 0, 0f);
            ModelActor treeActor = new ModelActor(actor3d);
            forestGroup.addModel(treeActor);
            treeActor.setEnvironment(Gdxg.graphic.environment);
            treeActor.setScale(TREE_SCALE);
            treeActor.setRotation(MathUtils.RANDOM.nextInt(360), 0, 0);
        }

        return forestGroup;
    }

    public static ModelActor buildCellSelector(Color color) {
        return buildCellSelector(color, LEVEL2_SELECTION_ALPHA);
    }

    public static ModelActor buildCellSelector(Color color, float height) {
        return buildCellSelector(color, height, CELL_SELECTION_MODEL_WIDTH, LEVEL2_SELECTION_ALPHA);
    }

    public static ModelActor buildCellSelector(Color color, float height, float width, float opacity) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = opacity;
        Model model = Gdxg.modelBuilder.createBox(width, height, width,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelActor modelActor = new ModelActor(new ModelInstance(model), Gdxg.modelBatch);
        return modelActor;
    }

    public static DecalActor getLabelDecal(String text, float width, float height) {
        DecalActor decalActor = new DecalActor(Decal.newDecal(width, height,
                new TextureRegion(Drawer2d.getTextTexture(text, (int) (width * 100), (int) (height * 100),
                        Assets.font28)), true),
                Gdxg.decalBatchTransparentLayer);
        decalActor.frustrumRadius = 1;
        return decalActor;
    }


    private static TriangleGrid createLiquidPlane() {
        Water water = Gdxg.graphic.water;

        int waterCellSize = water.getWaterTileSize();
        int width = water.getPlaneWidthInTiles();
        int height = water.getPlaneHeightInTiles();

        TriangleGrid waterTriangleGrid = new TriangleGrid(width, height, false,
                new VertexAttribute[]{VertexAttribute.Position(),
                        VertexAttribute.ColorPacked()
//                        VertexAttribute.Normal()
                });

        short n1, n2, n3;
        int xPos;
        int yPos;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                xPos = waterCellSize * x;
                yPos = waterCellSize * y;

                  /* Top right Vertex */
                n1 = waterTriangleGrid.addVertex(xPos + waterCellSize, 0, yPos);
                waterTriangleGrid.addColorToVertex(1, 0, 0, 1);
//                waterTriangleGrid.addNormal();
                /* top left Vertex */
                n2 = waterTriangleGrid.addVertex(xPos, 0, yPos);
                waterTriangleGrid.addColorToVertex(0, 1, 0, 1);
//                waterTriangleGrid.addNormal();
                /* bottom Right Vertex */
                n3 = waterTriangleGrid.addVertex(xPos + waterCellSize, 0, yPos + waterCellSize);
                waterTriangleGrid.addColorToVertex(0, 0, 1, 1);
//                waterTriangleGrid.addNormal();

                waterTriangleGrid.addIndices(n1, n2, n3);
                /* Bottom left Vertex */
                n1 = waterTriangleGrid.addVertex(xPos, 0, yPos + waterCellSize);
                waterTriangleGrid.addColorToVertex(0, 0, 0, 0);
//                waterTriangleGrid.addNormal();

                waterTriangleGrid.addIndices(n3, n2, n1);
            }
        }

        return waterTriangleGrid;
    }


    public static ModelActor buildWaterModel() {
        TriangleGrid waterTriangleGrid = Modeler.createLiquidPlane();
        Mesh waterTriangleGridMesh = waterTriangleGrid.getMesh();
        Assert.assertNotNull(waterTriangleGridMesh);

        Node node = new Node();

        MeshPart meshPart = new MeshPart("water-mesh", waterTriangleGridMesh, 0,
                waterTriangleGrid.getVertexUnitsTotalAmount(), GL20.GL_TRIANGLES);
        NodePart nodePart = new NodePart(meshPart, Gdxg.graphic.water.getMaterial());
        node.parts.add(nodePart);

        Model model = new Model();
        model.nodes.add(node);

        ModelActor modelActor = new ModelActor("water-plane", new ModelInstance(model), Gdxg.modelBatch);

        return modelActor;
    }

    /** vertexUnitsTotalAmount looks like a nail */
    public static ModelActor createModelActorFromMesh(Mesh mesh, Material material, int vertexUnitsTotalAmount) {
        return createModelActorFromMesh(mesh, material, vertexUnitsTotalAmount, Gdxg.modelBatch);
    }

    public static ModelActor createModelActorFromMesh(Mesh mesh, Material material, int vertexUnitsTotalAmount, ModelBatch modelBatch) {
        Node node = new Node();

        MeshPart meshPart = new MeshPart("ModelActorFromMesh-meshPart", mesh, 0, vertexUnitsTotalAmount, GL20.GL_TRIANGLES);
        NodePart nodePart = new NodePart(meshPart, material);
        node.parts.add(nodePart);

        Model model = new Model();
        model.nodes.add(node);

        return new ModelActor("ModelActorFromMesh", new ModelInstance(model), modelBatch);
    }

}