package conversion7.game.stages.world.gods;

import conversion7.engine.Gdxg;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.effects.items.ShamanUnitEffect;

import java.util.HashMap;

public class GodsGlobalStats {

    public HashMap<Class<? extends AbstractGod>, AbstractGod> gods = new HashMap<>();
    private World world;

    public GodsGlobalStats(World world) {
        this.world = world;
        try {
            for (Class<? extends AbstractGod> godClass : PackageReflectedConstants.GOD_CLASSES) {
                AbstractGod god = godClass.newInstance();
                gods.put(godClass, god);
                god.expPoints = godClass.getSimpleName().length();
            }
        } catch (Exception e) {
            Gdxg.core.addError(e);
        }
    }

    private int getTotalExp() {
        return gods.values().stream().mapToInt(i -> i.expPoints).sum();
    }

    public GodsGlobalStats validatePercents() {
        int totalExp = getTotalExp();
        for (AbstractGod god : gods.values()) {
            float div = god.expPoints / (float) totalExp;
            god.expPercent = (int) (div * 100);
        }
        return this;
    }

    public void tick() {
        validatePercents();

        // update shamans' mana
        for (Team team : world.teams) {
            for (AbstractSquad squad : team.getSquads()) {
                if (squad.myGod != null) {
                    squad.myGod.expPoints += ShamanUnitEffect.GOD_EXP_PER_STEP;
                    team.updateGodExp(ShamanUnitEffect.GOD_EXP_PER_STEP);
                    squad.updateMana(squad.myGod.expPercent);
                }
            }
        }
    }

    public int getMyPeople(AbstractGod god) {
        int myPeople = 0;
        for (Team team : world.teams) {
            for (AbstractSquad squad : team.getSquads()) {
                if (squad.isAlive() && squad.myGod == god) {
                    myPeople++;
                }
            }
        }
        return myPeople;
    }

}
