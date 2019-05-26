package conversion7.game.stages.world.objects.actions.items;

import conversion7.engine.artemis.ui.UnitHeroIndicatorSystem;
import conversion7.game.dialogs.SelectHeroDialog;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.HeroUnitEffect;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;

public class SelectHeroClassAction extends AbstractSquadAction {

    public SelectHeroClassAction() {
        super(Group.COMMON);
    }

    @Override
    public String getShortName() {
        return "HERO";
    }

    @Deprecated
    public static void makeHero(AbstractSquad squad) {
        makeHero(squad, HeroClass.getRandom());
    }

    public static void makeHero(AbstractSquad squad, HeroClass heroClass) {
        squad.getEffectManager().addEffect(new HeroUnitEffect());
        squad.team.addHero(squad);
        UnitHeroIndicatorSystem.components.create(squad.entityId).squad = squad;
        squad.setHeroClass(heroClass);
        squad.refreshUiPanelInWorld();
    }

    @Override
    public String buildDescription() {
        AbstractSquad squad = getSquad();
        return getName() + "\n \nPromote Unit to Hero\n" +
                "New hero classes can be learnt in Tribe Skills" +
                "\n \nHeroes counter for team: " + squad.team.getHeroProgressAsText()
                + "\nVisit animal spawns to have more hero units in team";
    }

    @Override
    public void begin() {
        new SelectHeroDialog(getSquad()).start();
    }

}
