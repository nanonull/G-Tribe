package conversion7.game.stages.world.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.utils.Normalizer;
import conversion7.game.Assets;
import conversion7.game.stages.world.climate.WinterEvent;
import conversion7.game.stages.world.landscape.Cell;

public class Winter extends AreaObject implements AreaObjectDetailsDescriptor {
    private final WinterEvent winterEvent;

    public Winter(Cell cell, WinterEvent winterEvent) {
        super(cell, null);
        init();
        this.winterEvent = winterEvent;
    }
    @Override
    public boolean givesExpOnHurt() {
        return false;
    }
    public int getTemperatureAdd() {
        return winterEvent.getTemperatureOn(this);
    }

    public int getWinterValuePercent() {
        return (int) Math.round(Normalizer.normalize(getWinterValue(), 1, 0, 100, 0));
    }

    public float getWinterValue() {
        return winterEvent.getWinterValue(this);
    }

    @Override
    public String getDetailsButtonLabel() {
        return "Winter";
    }

    @Override
    public Actor getDetailsDescriptionActor() {
        VBox root = new VBox();
        int winterValuePercent = getWinterValuePercent();
        root.addLabel("Winter effect = " + winterValuePercent + "%", Assets.labelStyle14blackWithBackground);
        root.addLabel("Temperature is decreased: " + getTemperatureAdd(), Assets.labelStyle14orange);
        root.addLabel("Gathering is decreased:  -" + winterValuePercent + "%", Assets.labelStyle14orange);
        root.addLabel("View radius is decreased: -50%", Assets.labelStyle14orange);
        return root;
    }

}
