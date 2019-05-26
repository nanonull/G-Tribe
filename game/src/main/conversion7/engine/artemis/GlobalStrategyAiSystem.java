package conversion7.engine.artemis;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.Gdxg;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.utils.Utils;
import conversion7.game.ai.global.CompositeAiEvaluator;
import conversion7.game.ai.global.GlobalStrategyAiEvaluator;
import conversion7.game.stages.world.ai_deprecated.AnimalAiTeamControllerOld;
import conversion7.game.stages.world.objects.composite.CompositeAreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.utils.collections.Comparators;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.List;

/** Each Ai action (driven by Ai task) is atomic to show action animation if exist */
public class GlobalStrategyAiSystem extends BaseSystem {
    public static final int COMPLETE_TASK_DEADLINE_MS = AnimationSystem.ANIM_DURATION_MS;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int MAX_ATTEMPTS_PER_UNIT = 1;
    private static final float INTERVAL = 1 / 60f;
    private static Array<AbstractSquad> squadsInPlayerSight = new Array<AbstractSquad>();
    GlobalStrategyAiEvaluator globalStrategyAiEvaluator = GlobalStrategyAiEvaluator.instance;
    // perf
    private int findSortedTasks;
    private int newTeams;
    private int newUnits;
    private int findSortedTasksMS;
    private int runActiveSquadAiMS;
    private int startNewSquadMS;
    private int compositesActedOnTeam;
    private Array<AbstractSquad> squads = new Array<>();
    private Array<CompositeAreaObject> composites = new Array<>();
    private Team activeTeam;
    private AbstractSquad activeSquad;
    private AiTask<AbstractSquad> activeSquadTask;
    private AiTask<CompositeAreaObject> activeCompTask;
    private int runActiveSquadAiAttempts;
    private int runActiveCompAiAttempts;
    private int unitsActedOnTeam;
    private State state;
    private CompositeAreaObject activeComposite;
    private float deltaAcc;

    public static void startFor(Team team) {
        GlobalStrategyAiSystem system = Gdxg.core.artemis.getSystem(GlobalStrategyAiSystem.class);
        system.newTeams++;
//        System.out.println("startFor");
//        System.out.println(system.newTeams);
//        System.out.println(system.newUnits);
//        System.out.println(system.findSortedTasks);
//        System.out.println(system.findSortedTasksMS);
//        System.out.println(system.runActiveSquadAiMS);
//        System.out.println(system.startNewSquadMS);
        Assert.assertNull(system.activeTeam);
        system.activeTeam = team;
        system.state = State.SQUAD_AI;

        // reset
        system.squads.clear();
        system.unitsActedOnTeam = 0;
        system.composites.clear();
        system.compositesActedOnTeam = 0;

        // schedule units
        system.squads.addAll(team.getSquads());
        system.composites.addAll(team.getCompositeObjects());

        if (team.isAnimals()) {
            system.squads.sort(AnimalAiTeamControllerOld.AI_ACT_ORDER_COMPARATOR);

            squadsInPlayerSight.clear();
            for (AbstractSquad animalUnit : system.squads) {
                if (animalUnit.isAlive() && animalUnit.cell != null) {
                    for (AbstractSquad visibleBySquad : animalUnit.cell.visibleBySquads) {
                        visibleBySquad.team.isHumanPlayer();
                        squadsInPlayerSight.add(animalUnit);
                        break;
                    }
                }
            }

            for (AbstractSquad squad : squadsInPlayerSight) {
                system.squads.removeValue(squad, true);
                system.squads.insert(0, squad);
            }

        } else {
            system.squads.sort(Comparators.SQUAD_POWER_COMPARATOR);
        }

    }

    // TODO: 06.05.2019 review animals - more attacks per step
    public static void decideBattle(Team team) {
        if (team.potentialBattleTargets.size > 0) {
            AbstractSquad humanSquadAsTarget = getHumanPlayerAsTarget(team);
            if (humanSquadAsTarget == null) {
                team.world.autoBattle(team, team.potentialBattleTargets.first());
            } else {
                team.world.postponeBattleWithPlayer(team, humanSquadAsTarget);
            }
        }
    }

    private static AbstractSquad getHumanPlayerAsTarget(Team team) {
        for (AbstractSquad potentialBattleTarget : team.potentialBattleTargets) {
            if (potentialBattleTarget.team.isHumanPlayer()) {
                return potentialBattleTarget;
            }
        }

        return null;
    }

    @Override
    protected void processSystem() {
        deltaAcc += world.getDelta();
        if (deltaAcc < INTERVAL) {
            return;
        }

        deltaAcc -= INTERVAL;
        if (activeTeam == null /*|| AnimationSystem.isLocking()*/) {
            return;
        }

        // switch units or end
        boolean exitProcess = false;
        if (state == State.SQUAD_AI) {
            if (activeSquad == null) {
                if (squads.size > 0 && !shouldStopAnimalsDueToActLimit()) {
                    startNewSquad(squads.removeIndex(0));
                } else {
                    startCompositeAi();
                    exitProcess = true;
                }
            }

            if (!exitProcess) {
                AbstractSquad activeSquadWip = this.activeSquad;
                activeSquadWip.batchFloatingStatusLines.start();
                runActiveSquadAi();
                activeSquadWip.batchFloatingStatusLines.flush(Color.CYAN);
            }


        } else if (state == State.COMPOSITE_AI) {
            if (activeComposite == null) {
                if (composites.size == 0) {
                    endActiveTeamAi();
                    return;
                }
                startNewComposite(composites.removeIndex(0));
            }

            runActiveCompositeAi();
        } else {
            Gdxg.core.addError(new GdxRuntimeException("Unknown state"));
        }
    }

    private void startCompositeAi() {
        state = State.COMPOSITE_AI;
    }

    private void runActiveSquadAi() {
        boolean hasActionLeft = activeSquad.isAnimal() ? runActiveSquadAiAttempts < 1
                : runActiveSquadAiAttempts < MAX_ATTEMPTS_PER_UNIT;
        if (activeSquad.isRemovedFromWorld()
                || !activeSquad.isAiEnabled()
                || !hasActionLeft) {
            endActiveSquadAi();
            return;
        }

        // move task >> squad.moveOneStepTo >> parentComp.moveOneStepTo
        if (activeSquadTask == null) {
            List<AiTask> aiTasks = null;
            try {
                aiTasks = globalStrategyAiEvaluator.findSortedTasks(activeSquad);
                findSortedTasks++;
            } catch (Throwable error) {
                LOG.error("aiEvaluator.findSortedTasks: " + error.getMessage(), error);
            }

            if (aiTasks == null || aiTasks.size() == 0) {
                endActiveSquadAi();
            } else {
                activeSquadTask = aiTasks.remove(0);
                runActiveSquadAiAttempts++;
//                AnimationSystem.lockAnimation();
                globalStrategyAiEvaluator.runTask(activeSquadTask);

            }
        } else {
            if (activeSquadTask.deadline < System.currentTimeMillis()) {
                activeSquadTask.complete();
            }
            if (activeSquadTask.completed) {
                endActiveSquadTask();
            }
        }

    }

    private void runActiveCompositeAi() {
        if (!activeComposite.isActive()
                || !activeComposite.isAiEnabled()
                || runActiveCompAiAttempts >= activeComposite.getMaxAiAttemptsPerTurn()) {
            endActiveCompositeAi();
            return;
        }

        if (activeCompTask == null) {
            List<AiTask> aiTasks = null;
            CompositeAiEvaluator compositeAiEvaluator = activeComposite.getAiEvaluator();
            try {
                aiTasks = compositeAiEvaluator.findSortedTasks(activeComposite);
            } catch (Throwable error) {
                LOG.error("compositeAiEvaluator.findSortedTasks: " + error.getMessage(), error);
            }

            if (aiTasks == null || aiTasks.size() == 0) {
                endActiveCompositeAi();
            } else {
                activeCompTask = aiTasks.remove(0);
                runActiveCompAiAttempts++;
//                AnimationSystem.lockAnimation();
                compositeAiEvaluator.runTask(activeCompTask);

            }
        } else {
            // has task
            if (activeCompTask.deadline < System.currentTimeMillis()) {
                activeCompTask.complete();
            }
            if (activeCompTask.completed) {
                endActiveCompTask();
            }
        }
    }

    private boolean shouldStopAnimalsDueToActLimit() {
        return activeTeam.isAnimals()
                && unitsActedOnTeam > activeTeam.world.getDesiredActiveAnimalsPerStep();
    }

    private void endActiveTeamAi() {
        decideBattle(activeTeam);
        activeTeam.aiInProgress = false;
        activeTeam = null;
    }

    private void startNewComposite(CompositeAreaObject compositeAreaObject) {
        runActiveCompAiAttempts = 0;
        activeComposite = compositeAreaObject;
        activeComposite.hadAiActAt(Gdxg.core.world.step);
        Gdxg.clientUi.getTeamsTurnsPanel().newCompStarted();
    }

    private void startNewSquad(AbstractSquad squad) {
        runActiveSquadAiAttempts = 0;
        newUnits++;
        activeSquad = squad;
        activeSquad.hadAiActAt(Gdxg.core.world.step);
        Gdxg.clientUi.getTeamsTurnsPanel().newSquadStarted();
    }

    private void endActiveSquadAi() {
        globalStrategyAiEvaluator.validateExpiration(activeSquad.getAiTasks());
        activeSquad = null;
        unitsActedOnTeam++;
    }

    private void endActiveCompositeAi() {
//        aiEvaluator.validateExpiration(activeComposite.getAiTasks());
        activeComposite = null;
        compositesActedOnTeam++;
    }

    private void endActiveCompTask() {
        activeCompTask = null;
    }

    private void endActiveSquadTask() {
        activeSquadTask = null;
    }

    enum State {
        SQUAD_AI,
        COMPOSITE_AI,
    }
}
