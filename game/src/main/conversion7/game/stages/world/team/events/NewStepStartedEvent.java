package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.team.GatheringStatistic;
import conversion7.game.stages.world.team.Team;

public class NewStepStartedEvent extends AbstractEventNotification {

    private String hint;

    public NewStepStartedEvent(Team team) {
        super(team, new ButtonWithActor(new Image(Assets.homeIcon)));
        hint = super.getHint() + "\n ";

        GatheringStatistic statistic = team.getGatheringStatistic();
        if (statistic.evolutionPoints > 0 || statistic.evolutionExp > 0) {
            if (statistic.evolutionPoints > 0) {
                hint += " \nNew evolution points +" + statistic.evolutionPoints;
            }
            if (statistic.evolutionExp > 0) {
                hint += " \nNew evolution experience +" + statistic.evolutionExp;
            }
        }

        hint += "\n \nResources at your team disposal: \n" +
                " Food: " + statistic.food + "\n" +
                " Water: " + statistic.water;

        StringBuilder itemsBuilder = new StringBuilder();
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, Integer> entry : statistic.items) {
            if (entry.value > 0) {
                itemsBuilder.append(" ").append(entry.key.getSimpleName())
                        .append(" +").append(entry.value).append("\n");
            }
        }

        if (itemsBuilder.length() > 0) {
            hint += "\n \nNew items:\n" + itemsBuilder.toString();
        }
    }

    @Override
    public String getHint() {
        return hint;
    }
}
