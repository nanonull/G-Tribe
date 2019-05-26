package conversion7.game.stages.world.unit.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;

public abstract class AbstractUnitEffect {

    private static final Color A_GREEN = UiUtils.alpha(0.5f, Color.GREEN);
    private static final Color A_ORANGE = UiUtils.alpha(0.5f, Color.ORANGE);
    private static final Color A_RED = UiUtils.alpha(0.7f, Color.SCARLET);
    public String name;
    protected UnitParameters effectParameters;
    public Type type;
    protected int tickCounter;
    protected boolean completed;
    private AbstractSquad owner;
    private boolean enabled = true;
    private Integer tickLogicEvery;
    private int tickSteps;

    public AbstractUnitEffect(String name, Type type) {
        this(name, type, new UnitParameters());

    }

    public AbstractUnitEffect(String name, Type type, UnitParameters parameters) {
        this.name = name;
        this.type = type;
        this.effectParameters = parameters;
        resetTickCounter();
    }

    public UnitParameters getEffectParameters() {
        return effectParameters;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(int tickCounter) {
        this.tickCounter = tickCounter;
    }

    public AbstractSquad getOwner() {
        return owner;
    }

    public void setOwner(AbstractSquad owner) {
        this.owner = owner;
    }

    public String getHint() {
        if (effectParameters != null && effectParameters.getParametersStorage().size > 0) {
            return toString() + "\n \n" + buildEffectParametersHint();
        } else {
            return toString();
        }
    }

    public Actor getIconPanel() {
        Color backColor;
        switch (type) {
            case POSITIVE:
                backColor = A_GREEN;
                break;
            case NEGATIVE:
                backColor = A_RED;
                break;
            case POSITIVE_NEGATIVE:
                backColor = A_ORANGE;
                break;
            default:
                throw new GdxRuntimeException("type?");
        }
        HBox effectPanel = new HBox();
        effectPanel.setBackground(new TextureRegionColoredDrawable((backColor), Assets.pixel));


        Image icon = getIcon();
        if (icon == null) {
            TextButton textButton = new TextButton(getShortIconName(), Assets.uiSkin);
            effectPanel.add(textButton).pad(1)
                    .height(ClientUi.DEFAULT_BUTTON_HEIGHT);

        } else {
            effectPanel.add(icon).size(ClientUi.DEFAULT_BUTTON_HEIGHT).fill().expand();
            icon.setScaling(Scaling.fit);
        }
        return effectPanel;
    }

    public String getShortIconName() {
        return getClass().getSimpleName().substring(0, 3);
    }

    public Image getIcon() {
        return null;
    }

    public void setTickLogicEvery(int tickLogicEvery) {
        this.tickLogicEvery = tickLogicEvery;
    }

    public void complete() {
        completed = true;
    }

    public void resetTickCounter() {
        setTickCounter(0);
    }

    protected String buildEffectParametersHint() {
        StringBuilder builder = new StringBuilder();
        for (ObjectMap.Entry<UnitParameterType, Integer> entry : effectParameters.getParametersStorage()) {
            builder.append(entry.key.name()).append(" ");
            if (entry.key.isParamModifier()) {
                builder.append(UiUtils.getNumberWithSign(entry.value)).append("%\n");
            } else {
                builder.append(UiUtils.getNumberWithSign(entry.value)).append("\n");
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return name;
    }

    public void tick() {
        tickCounter++;
        tickSteps++;
        if (tickLogicEvery != null && tickLogicEvery <= tickSteps) {
            tickSteps = 0;
            tickLogic();
        }
    }

    protected void tickLogic() {

    }

    public void remove() {
        getOwner().getEffectManager().removeEffect(this);
    }

    public void onAdded() {
    }

    public void onRemoved() {

    }

    public enum Type {
        POSITIVE, NEGATIVE, POSITIVE_NEGATIVE
    }

}
