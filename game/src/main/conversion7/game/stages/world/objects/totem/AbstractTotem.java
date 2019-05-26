package conversion7.game.stages.world.objects.totem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.AreaObjectDetailsButton;
import conversion7.game.stages.world.objects.AreaObjectTickable;
import conversion7.game.stages.world.team.Team;

public abstract class AbstractTotem extends AreaObject implements AreaObjectDetailsButton, AreaObjectTickable {

    public static final int TOTEM_RADIUS = World.BASE_VIEW_RADIUS;
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(-0.3f, -0.1f, 0));
    public static final int STEPS_ALIVE = 10;
    public final Array<Cell> affectedCells;
    protected int tickValue;
    @Override
    public boolean givesExpOnHurt() {
        return true;
    }
    public AbstractTotem(Cell cell, Team team) {
        super(cell, team);
        cell.setTotem(this);
        team.addTotem(this);
        affectedCells = cell.getCellsAroundToRadiusInclusively(getRadius());
        affectedCells.add(cell);
        for (Cell cellAr : getAffectedCells()) {
            cellAr.addEffectiveTotem(this);
        }
    }

    @Override
    public String getDetailsButtonLabel() {
        return "Totem";
    }

    @Override
    public ClickListener getDetailsClickListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.clientUi.getCellDetailsRootPanel().load((AbstractTotem) thiz);
            }
        };
    }

    public int getRadius() {
        return TOTEM_RADIUS;
    }

    protected abstract Color getTotemColor();

    public Array<Cell> getAffectedCells() {
        return affectedCells;
    }
//
//    @Override
//    public String getHint() {
//        return BOOST_HINT + "\n" + getRadiusHint();
//    }

    public String getTickHint() {
        return tickValue + "/" + STEPS_ALIVE;
    }

    public static boolean canBeCreatedOn(Cell cell) {
        return !cell.hasTotem() &&
                cell.canBeSeized() &&
                cell.getObjectsAroundFromToRadiusInclusively(0, AbstractTotem.TOTEM_RADIUS, AbstractTotem.class).size == 0;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("totem", Modeler.buildTotemBox(getTotemColor()), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(SHIFT_POS);
        return sceneBody;
    }

    @Override
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        super.validateView();
    }

    @Override
    public void tick() {
        tickValue++;
        if (tickValue > STEPS_ALIVE) {
            removeFromWorld();
        }
    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
        getLastCell().setTotem(null);
        for (Cell affectedCell : affectedCells) {
            boolean removed = affectedCell.getEffectiveTotems().removeValue(this, true);
        }
    }
}
