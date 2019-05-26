package conversion7.game.stages.world.objects.unit;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.stages.world.unit.effects.items.ConcealmentEffect;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.unit_classes.ufo.Archon;
import org.slf4j.Logger;
import org.testng.Assert;

public class WorldSquad extends AbstractSquad {

    private static final Logger LOG = Utils.getLoggerForClass();
    private final WorldSquad thiz;

    WorldSquad(Cell cell, Team team) {
        super(cell, team);
        thiz = this;
    }

    public static WorldSquad create(Unit unit, Team team, Cell cell) {
        Assert.assertNotNull(team);
        Assert.assertNotNull(unit);
        Assert.assertNotNull(cell);
        WorldSquad squad;
        if (unit instanceof BaseAnimalClass) {
            squad = new AnimalHerd(cell, team);
        } else {
            squad = new WorldSquad(cell, team);
        }
        squad.unit = unit;
        squad.init();
        return squad;
    }

    public static WorldSquad create(Class<? extends Unit> aClass, Team team, Cell cell) {
        return create(createUnit(aClass), team, cell);
    }

    public static Unit createUnit(Class<? extends Unit> unitClass) {
        Unit unit = UnitFertilizer2.createStandardUnit(unitClass
                , WorldServices.nextUnitGender == null ? MathUtils.RANDOM.nextBoolean() : WorldServices.nextUnitGender);
        WorldServices.nextUnitGender = null;
        return unit;
    }

    public static void killUnit(AreaObject unit) {
        LOG.info("killUnit: {} ", unit);
        AbstractSquad squad = (AbstractSquad) unit;
        Team team = squad.getTeam();
        squad.getActionsController().disableChildRecursive();

        team.defeatUnit(squad);
        if (!team.checkAlive()) {
            team.world.defeat(team);
        }
    }

    @Override
    public void init() {
        unit.squad = this;

        super.init();

        if (unit.isAnimal()) {
            team.world.createdAnimalHerds++;
            name = unit.getGameClassName();
        } else {
            name = team.getNextUnitName(getGender());
        }

        if (Gdxg.core.world.isRaceFoundersStep()) {
            unit.squad.initRaceFounderBloodline();
        }

        setUnit(unit);
        team.addSquad2(this);
        
        if (isArchon()) {
            getEffectManager().getOrCreate(ConcealmentEffect.class);
            archonHypnoCharges = 1;
        }
    }

    @Override
    public void initActions() {
        super.initActions();
    }

    @Override
    public boolean couldJoinToTeam(AreaObject targetToBeJoined) {
        if (targetToBeJoined instanceof WorldSquad
                && !this.getTeam().equals(targetToBeJoined.getTeam())) {
            int totalTribesSeparationValue = getLastCell().getArea().world.getTotalTribesSeparationValue();
            int tribeSeparationValue = targetToBeJoined.getTeam().getTribeSeparationValue();
            return tribeSeparationValue > totalTribesSeparationValue;
        }
        return false;
    }


}
