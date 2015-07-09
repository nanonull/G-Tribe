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
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.geometry.terrain.TerrainChunk;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.Area;
import conversion7.scene3dOld.Actor3d;
import org.testng.Assert;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

/**
 * Static methods for building model primitives
 */
public class Modeler {

    private static float MAX_FOREST_RADIUS = 0.5f;
    private static float TREE_SCALE = 0.06f;
    public static final float SELECTION_MODEL_WIDTH = 0.65f;
    public static final float TOWN_MODEL_WIDTH = 0.8f;

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
        Model model = Gdxg.modelBuilder.createBox(SELECTION_MODEL_WIDTH, 1f, SELECTION_MODEL_WIDTH,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

    public static ModelInstance buildBlueBox() {
        Model model = Gdxg.modelBuilder.createBox(TOWN_MODEL_WIDTH, 1f, TOWN_MODEL_WIDTH,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        return new ModelInstance(model);
    }

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
            treeActor.setRotation(Utils.RANDOM.nextInt(360), 0, 0);
        }

        return forestGroup;
    }

    @Deprecated
    public static ModelActor buildCellNotVisibleCube() {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DEPTH_FUNC);
        blendingAttribute.opacity = 0.55f;
        Model model = Gdxg.modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY), blendingAttribute),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelActor modelActor = new ModelActor("CellNotVisibleModel", new ModelInstance(model), Gdxg.modelBatch);
        modelActor.setY(SELECTION_MODEL_WIDTH / 2f);
        return modelActor;
    }

    public static ModelActor buildCellSelector(Color color) {
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.5f;
        Model model = Gdxg.modelBuilder.createBox(SELECTION_MODEL_WIDTH, 0.4f, SELECTION_MODEL_WIDTH,
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

    public static ModelActor createTerrain(Area area) {
        TerrainChunk terrainChunk = new TerrainChunk(area);
        return Modeler.createModelActorFromMesh(terrainChunk.getMesh(), new Material(new TerrainAttribute()),
                terrainChunk.getVertexUnitsAmount(), Gdxg.terrainBatch);
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