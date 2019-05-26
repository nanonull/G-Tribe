package conversion7.game.stages.world.team.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.stages.world.unit.effects.items.ColdEffect;
import conversion7.game.stages.world.unit.effects.items.HungerEffect;
import conversion7.game.stages.world.unit.effects.items.ThirstEffect;
import org.apache.commons.lang3.StringUtils;

public class ReproductionHintEvent extends AbstractEventSquadNotification {

    public ReproductionHintEvent(AbstractSquad squad) {
        super(squad, new ButtonWithActor(new Image(Assets.eyeGreen), true));
    }

    @Override
    public String getHint() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(super.getHint());

        if (squad.willDieOfAge) {
            stringBuilder.append("\nUnit died of age...");
        } else {
            stringBuilder.append("\nUnit died at ")
                    .append(squad.getAgeName()).append(" age.");
        }

        String exceptionalStatusHint = squad.getExceptionalStatusHint();
        if (!StringUtils.isEmpty(exceptionalStatusHint)) {
            stringBuilder.append("\nThis ").append(squad.getGenderUi())
                    .append(" was a great person: ").append(exceptionalStatusHint).append("...");
        }

        return "Unit reproduction.\n \n" +
                "Male tries to fertilize adjacent female unit every turn.\n" +
                "Only Adult(2) and Mature(3) units take part.\n" +
                "\nBase chance for fertilization: " + UnitFertilizer2.BASE_PERC + "%." +
                "\nChance in camp: " + UnitFertilizer2.CAMP_FERTILIZE_PERC + "%" +
                "\n " +
                "\nFertilization can't be done for female with effects: "
                + ColdEffect.class.getSimpleName() + ", "
                + HungerEffect.class.getSimpleName() + ", "
                + ThirstEffect.class.getSimpleName();
    }
}
