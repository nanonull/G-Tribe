package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.UnitParameters;

public class BurningForest extends AreaObject implements AreaObjectTickable {

    public static final int TOTEM_RADIUS = World.BASE_VIEW_RADIUS;
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(0.3f, -0.3f, 0));
    public static final int HURT_HP = (int) (UnitParameters.START_HEALTH * 0.33f);
    public static final int DURATION = 2;
    public static final int STEP_FIRE_ADJ_FORESTS_AT = DURATION - 1;
    public static final float FIRE_EFFECT_FOOD_MLT = 0.15f;
    public static final String DESC = BurningForest.class.getSimpleName() + " deals " + HURT_HP + " dmg in radius 1 and puts adjacent forests on fire";
    PointLight pointLight;
    Array<Cell> cellsHasLight = new Array<>();
    private int step;

    public BurningForest(Cell cell, Team team) {
        super(cell, team);
        init();
        cell.getCellsAround(0, 1, cellsHasLight);
        for (Cell cellOnLight : cellsHasLight) {
            cellOnLight.addLightSource(this);
        }
        validateView();
        cell.addFloatLabel("Burning Forest", Color.ORANGE);
    }

    public int getRadius() {
        return TOTEM_RADIUS;
    }

    @Override
    public String getShortHint() {
        return super.getShortHint() + " " + step + "/" + DURATION;
    }

    @Override
    public boolean givesExpOnHurt() {
        return false;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("BurningForest",
                Modeler.buildHalfCampBox(Color.ORANGE), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);

        pointLight = new PointLight();
        pointLight.setColor(Color.RED);
        pointLight.setIntensity(2);
        pointLight.setPosition(sceneBody.globalPosition);
        pointLight.position.y += 2;
        Gdxg.core.getGraphic().environment.add(pointLight);

        return sceneBody;
    }


    @Override
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        super.validateView();
    }

    @Override
    public void removeFromWorld() {
        for (Cell cellOnLight : cellsHasLight) {
            cellOnLight.removeLightSource(this);
        }
        getLastCell().refreshViewer();
        Gdxg.core.getGraphic().environment.remove(pointLight);
        super.removeFromWorld();
    }

    @Override
    public void tick() {
        if (step == DURATION) {
            new BurntForest(getLastCell(), team);
            removeFromWorld();
        } else if (step == STEP_FIRE_ADJ_FORESTS_AT) {
            boolean fired = false;
            for (Cell adjCell : getLastCell().getCellsAround()) {
                if (adjCell.canBeFired()) {
                    AreaObject.create(adjCell, this.createdBy, BurningForest.class);
                    fired = true;
                }
            }
            if (fired) {
                Gdxg.getAreaViewer().requestCellsRefresh();
            }
        }

        Array<Cell> cellsAround = cell.getCellsAround(0, 1, new Array<>());
        for (Cell adjCell : cellsAround) {
            int dmg = BurningForest.HURT_HP;
            adjCell.addFloatLabel("Burning dmg " + dmg, Color.ORANGE);
            for (AreaObject object : new Array.ArrayIterable<>(adjCell.getObjectsOnCell())) {
                if (object.hasPower()) {
                    object.hurtBy(dmg, createdBy);
                }
            }
        }


        step++;
    }

}
