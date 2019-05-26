package conversion7.tests_standalone.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.DecalGroup;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.Scene3dInputListener;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.ui.UiLogger;

// TODO fix: rebase on TestStage
@Deprecated
public class TestsCustomScene3d {

    public static SceneNode3d sceneNodeForMove = new SceneGroup3d("dummyLandscape to avoid null");
    public static SceneNode3d currentTestObject;
    /**
     * switch active test mode in TestsCustomScene3d: 0 - translate, 1 - rotate
     */
    public static int currentTestMode = 1;
    public static DecalGroup decalGroup; // 1
    public static ModelGroup modelGroup; // 2
    public static DecalActor decalActor; // 3
    public static ModelActor modelActor; // 4
    public static SceneGroup3d sceneGroup3d1; // 5
    public static SceneGroup3d rootGroup; // 6
    public static boolean test_ModelAndDecalGroupsEnabled = false;

    public static void test1_customGroup() {
        SceneGroup3d sceneGroup3d1 = new SceneGroup3d();
        sceneGroup3d1.setPosition(2, 4, 6);
        SceneGroup3d sceneGroup3d2 = new SceneGroup3d();
        sceneGroup3d2.setPosition(1, 3, 5);

        sceneGroup3d1.addNode(sceneGroup3d2);
        sceneGroup3d1.removeNode(sceneGroup3d2);
    }

    public static void test1_decalGroup() {
        SceneGroup3d sceneGroup3d1 = new SceneGroup3d();
        sceneGroup3d1.setPosition(0, 0, 0);

//        DecalGroup decalGroup = Gdxg.core.graphic._testDecalGroup;
        DecalGroup decalGroup = null;
        DecalBatch decalBatch = Gdxg.decalBatchCommon;

        decalGroup.addDecal(
                new DecalActor(Decal.newDecal(1, 1, Assets.grass, true),
                        decalBatch));

        DecalActor decalActor = new DecalActor(Decal.newDecal(1, 1, Assets.grass, true), decalBatch);
        decalActor.setPosition(0, 0, 1);
        decalGroup.addDecal(decalActor);

        sceneGroup3d1.addNode(decalGroup);
    }

    /**
     * Run console command 'test_object $index'
     */
    public static void switchTestObject(int index) {
        switch (index) {
            case 1:
                currentTestObject = decalGroup;
                break;

            case 2:
                currentTestObject = modelGroup;
                break;

            case 3:
                currentTestObject = decalActor;
                break;

            case 4:
                currentTestObject = modelActor;
                break;

            case 5:
                currentTestObject = sceneGroup3d1;
                break;

            case 6:
                currentTestObject = rootGroup;
                break;

            default:
                Utils.error("unknown index");
        }
    }

    public static void switchTestMode(int newMode) {
        currentTestMode = newMode;
    }

    public static void mainRun() {
        test_ModelAndDecalGroupsEnabled = true;

        DecalBatch decalBatch = Gdxg.decalBatchCommon;

//        CustomStage customStage = Gdxg.core.graphic._customStageForDebug;
        CustomStage customStage = null;
        customStage.addListener(new Scene3dInputListener() {

            @Override
            public boolean touchDown(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
                UiLogger.addInfoLabel(" click on CustomStage");
                return super.touchDown(event, touchPoint, pointer, button);
            }

        });
        Gdxg.clientUi.inputMultiplexer.addProcessor(customStage);
        rootGroup = customStage.root;

        // DecalGroup
        decalGroup = new DecalGroup(decalBatch);
        customStage.addNode(decalGroup);
        decalGroup.setName("decalGroup");
        decalGroup.setPosition(MathUtils.toEngineCoords(0, 0, 0));
        decalGroup.createBoundingBox(6, 20, 1);

        Decal decal = Decal.newDecal(2, 3, Assets.grass, true);
//        decal.setRotationX(-90);
        decalActor = new DecalActor(decal, decalBatch);
        decalGroup.addDecal(decalActor);
        decalActor.setName("decalActor");
        decalActor.setPosition(MathUtils.toEngineCoords(1, 1.5f, 0));
        decalActor.createBoundingBox();


        // ModelGroup
        modelGroup = new ModelGroup(Gdxg.modelBatch);
        customStage.addNode(modelGroup);
        modelGroup.setPosition(MathUtils.toEngineCoords(0, 0, 0));
        modelGroup.createBoundingBox(20, 6, 2);

        ModelInstance armyModel = Modeler.buildRedBox();
        modelActor = new ModelActor(armyModel);
        modelGroup.addModel(modelActor);
        modelActor.setPosition(MathUtils.
                toEngineCoords(0 + 0.5f, 0 + 0.5f, 0.5f));
        modelActor.createAutoBoundingBox();

//        // test2: SceneGroup3d parent (ENABLE if NEEDED)
//        sceneGroup3d1 = new SceneGroup3d();
//        customStage.addNode(sceneGroup3d1);
//        sceneGroup3d1.name = "sceneGroup3d1";
//        sceneGroup3d1.setBoundingBox(new BoundingBox2(sceneGroup3d1.getWorldPosition(), 5, 10, 15));

        currentTestObject = rootGroup;
    }

    public static void handleTestKeys(float DELTA_CAM_MOVEMENT) {

        switch (currentTestMode) {
            case 0: // translation
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { // -X in game
                    currentTestObject.translate(-DELTA_CAM_MOVEMENT, 0, 0);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { // +X in game
                    currentTestObject.translate(DELTA_CAM_MOVEMENT, 0, 0);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) { // +Y in game
                    currentTestObject.translate(0, 0, -DELTA_CAM_MOVEMENT);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { // -Y in game
                    currentTestObject.translate(0, 0, DELTA_CAM_MOVEMENT);
                }
                break;

            case 1: // rotation
                float ROTATION_DELTA = DELTA_CAM_MOVEMENT * 10;

                if
                        (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    currentTestObject.rotate(-ROTATION_DELTA, 0, 0);
                } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    currentTestObject.rotate(ROTATION_DELTA, 0, 0);
                }

                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    currentTestObject.rotate(0, -ROTATION_DELTA, 0);
                } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    currentTestObject.rotate(0, ROTATION_DELTA, 0);
                }

                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    currentTestObject.rotate(0, 0, -ROTATION_DELTA);
                } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                    currentTestObject.rotate(0, 0, ROTATION_DELTA);
                }
                break;

            default:
                Utils.error("Unsupported test mode");
        }

    }
}
