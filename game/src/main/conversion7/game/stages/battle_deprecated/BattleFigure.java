package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Pools;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.Actor3d;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.BattleConstants;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.Progressive;
import conversion7.game.stages.StageObject;
import conversion7.game.stages.battle_deprecated.calculation.Cell;
import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;
import conversion7.game.stages.battle_deprecated.contollers.DieController;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.items.InjuryEffect;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import org.slf4j.Logger;

import java.awt.*;

import static conversion7.game.stages.battle_deprecated.Battle.ANIM_SPEED;
import static conversion7.game.stages.battle_deprecated.Battle.ANIM_TRANSITION;
import static conversion7.game.stages.battle_deprecated.BattleFigure.AnimationMode.DIE;
import static conversion7.game.stages.battle_deprecated.BattleFigure.AnimationMode.IDLE;

@Deprecated
public class BattleFigure extends StageObject implements Progressive {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final float ACTOR_Z = 1.05f;
    public static final float ACTOR_BOX_HEIGHT = 0.5f * ACTOR_Z;
    public static final float ACTOR_SCALE = 0.11f;
    private static Color resetColor = new Color(Color.valueOf("ccccccff"));
    private static Color selectedColor = Color.GREEN;
    private static Color highlightedColor = Color.WHITE;
    private TeamSide teamSide;
    public Battle battle;
    public SceneGroup3d figureVisualGroup;
    public ModelActor modelActor;
    private Unit worldUnit;
    /** Represents unit params at certain battle step */
    public FigureStepParams params;
    public int paramsNumber = 0;
    BattleFigure figureLink;
    public int howManyKilled = 0;
    public int experience;
    /** 0% - 10% */
    private float resistanceToDeathChance;
    private boolean dead;
    public Point2s savedMirrorPosition;
    /** It was introduced to handle tricky work of CheckBox in ManagerWindow... */
    public boolean activated = false;

    public BattleFigure(Battle b, Unit worldUnit, TeamSide teamSide) {

        super(worldUnit.id);
        this.teamSide = teamSide;
        figureLink = this;
        battle = b;
        this.worldUnit = worldUnit;

        params = new FigureStepParams(this);
        int initialHealth = 1;
//        int initialHealth = worldUnit.getBaseParams().get(UnitParameterType.HEALTH);
//        resistanceToDeathChance = initialHealth / (float) worldUnit.getMaxHealth() / 3f;
//        resistanceToDeathChance *= resistanceToDeathChance;
        params.life = initialHealth;

        LOG.info("Figure created with params: " + params);
    }

    @Override
    public String getHint() {
        return "to be implemented";
    }

    @Override
    public String getName() {
        return "Figure";
    }

    public TeamSide getTeamSide() {
        return teamSide;
    }

    public Unit getWorldUnit() {
        return worldUnit;
    }

    public BattleSide getBattleSide() {
        return teamSide.getBattleSide();
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public void init() {

    }

    @Override
    public String toString() {
        return "FIGURE [" +
                "id = " + getId() + "; " +
                "side = " + getBattleSide().toString() + "; " +
                "]";
    }

    public void initVisual() {
        figureVisualGroup = Pools.obtain(SceneGroup3d.class);
        figureVisualGroup.setDimensions(1, 1, ACTOR_Z);
        figureVisualGroup.createBoundingBox();

        Actor3d knight = new Actor3d(Assets.getModel("knight"), 0, 0, 0f);
        modelActor = new ModelActor(knight, Gdxg.modelBatch);
        figureVisualGroup.addNode(modelActor);
        modelActor.setEnvironment(Gdxg.graphic.environment);
        modelActor.getAsActor3d().animating = true;
        modelActor.setScale(ACTOR_SCALE);
        modelActor.getAsActor3d().getAnimation().animate(IDLE.toString(), -1, ANIM_SPEED, null, ANIM_TRANSITION);
        modelActor.frustrumRadius = 1;

        modelActor.createBoundingBox(0.7f * ACTOR_Z, 0.7f * ACTOR_Z, 0.75f * ACTOR_Z);
        modelActor.boundBoxActor.translate(MathUtils.toEngineCoords(0, 0, -ACTOR_Z / 2));
    }

    public void activate() {
        battle.armyPlaceArea.placeFigureOnSavedOrNextAvailableCell(this);
        battle.mainGroup.addNode(figureVisualGroup);
        updateBodyPosition();
        activated = true;
    }

    public void updateBodyPosition() {
        updateBody(params.cell.x, params.cell.y);
    }

    public void updateBody(int x, int y) {
        figureVisualGroup.setPosition(MathUtils.toEngineCoords(
                x + 0.5f - battle.getHalfOfTotalWidth(),
                y + 0.5f - battle.getHalfOfTotalHeight(), ACTOR_Z));
    }

    //
    // ROUND PROGRESS PART
    //

    public void deactivate() {
        battle.mainGroup.removeNode(figureVisualGroup);
        params.step.removeFigure(this);
        activated = false;
    }

    public void select() {
        modelActor.applyMaterialAttribute(ColorAttribute.createDiffuse(selectedColor));
        if (Gdxg.clientUi.getBattleWindowManageArmyForRound().isShown()) {
            Gdxg.clientUi.getBattleWindowManageArmyForRound().selectFigure(this);
        }
    }

    public void resetSelections() {
        modelActor.applyMaterialAttribute(ColorAttribute.createDiffuse(resetColor));
        if (Gdxg.clientUi.getBattleWindowManageArmyForRound().isShown()) {
            Gdxg.clientUi.getBattleWindowManageArmyForRound().resetSelectFigure();
        }
    }

    public void highlight() {
        modelActor.applyMaterialAttribute(ColorAttribute.createDiffuse(highlightedColor));
        if (Gdxg.clientUi.getBattleWindowManageArmyForRound().isShown()) {
            Gdxg.clientUi.getBattleWindowManageArmyForRound().highlightFigure(this);
        }
    }

    public void resetHighlight() {
        modelActor.applyMaterialAttribute(ColorAttribute.createDiffuse(resetColor));
        if (Gdxg.clientUi.getBattleWindowManageArmyForRound().isShown()) {
            Gdxg.clientUi.getBattleWindowManageArmyForRound().resetHighlightFigure();
        }
    }

    public void applyBattleAffectOnWorldUnit() {
        int worldUnitLife;
        if (isDead()) {
            if (!GdxgConstants.isAlwaysDontResurrectUnitsInBattle() &&
                    (GdxgConstants.isResurrectUnitInBattleIfResistFailed() || MathUtils.RANDOM.nextFloat() < resistanceToDeathChance)) {
                // back to life
                worldUnitLife = Unit.HEALTH_AFTER_BACK_TO_LIFE;
                setDead(false);
                worldUnit.squad.getEffectManager().addEffect(new InjuryEffect());
            } else {
                // dies
                LOG.info(" unit was died in battle: " + worldUnit);
                if (worldUnit instanceof BaseAnimalClass) {
                    battle.killedAnimals++;
                }
//                worldUnit.squad.diesInBattle();
                return;
            }
        } else {
            // alive
            worldUnitLife = params.life;
        }

        worldUnit.squad.getMainParams().put(UnitParameterType.HEALTH, worldUnitLife);
        worldUnit.squad.updateExperience(experience);
        battle.aliveFiguresAfterBattle.add(this);
    }

    @Override
    public void act(float delta) {
        if (params.action != null) {
            params.action.act(delta);
        }
    }

    @Override
    public void start() {
        if (params.action != null) {
            params.action.start();
        }
    }

    @Override
    public void completeHalf() {
        if (isDead()) {
            if (params.action != null) {
                params.action.cancel();
            }
            params.action = new DieController(params);
            params.battleFigure.modelActor.getAsActor3d().getAnimation()
                    .animate(DIE.toString(), 1, ANIM_SPEED, new AnimationController.AnimationListener() {
                        @Override
                        public void onEnd(AnimationController.AnimationDesc animation) {
                            params.battleFigure.modelActor.getAsActor3d().animating = false;
                        }

                        @Override
                        public void onLoop(AnimationController.AnimationDesc animation) {

                        }
                    }, ANIM_TRANSITION);
        } else if (params.action != null) {
            params.action.completeHalf();
        }
    }

    @Override
    public void complete() {
        if (params.action != null) {
            params.action.complete();
        }
    }

    public void draw() {
        // TODO remove battle.stage2d and use faceToCamera decals
        // or
        // TODO remove battle.stage2d and draw bars using Gdxg.spriteBatch
        // or
        // TODO battle.stage2d.<drawMyHealthBar>()
    }

    public void rotateOnTarget(Cell target) {
        Point newDirection = new Point2s(target.x, target.y)
                .minus(params.cell.x, params.cell.y);
        int newYaw = BattleConstants.getYawByDirection(newDirection);
        figureVisualGroup.setRotation(newYaw, 0, 0);
    }

    public enum AnimationMode {
        ATTACK("Attack"),
        DAMAGED("Damaged"),
        DIE("Die"),
        IDLE("Idle"),
        SNEAK("Sneak"),
        WALK("Walk");

        private final String value;

        AnimationMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
