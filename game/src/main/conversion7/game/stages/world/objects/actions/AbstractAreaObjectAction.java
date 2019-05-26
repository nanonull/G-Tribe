package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.AbstractStageObjectAction;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;

public abstract class AbstractAreaObjectAction extends AbstractStageObjectAction implements Comparable<AbstractAreaObjectAction> {

    public static final ObjectMap<Class<? extends AbstractAreaObjectAction>, ActionEvaluation> ACTION_EVAL_BY_CLASS = new ObjectMap<>();
    private String description;
    private Group group;
    protected String name;
    public ActionEvaluation actionEvaluation;
    public boolean active;

    public AbstractAreaObjectAction(Group group) {
        this.group = group;
        this.actionEvaluation = calcActionEvaluation();
        name = this.getClass().getSimpleName();
    }

    /** the lowest value will be the leftmost in ActionBar */
    @Deprecated
    public int getActionPositionPriority() {
        return 0;
    }

    @Override
    public AreaObject getObject() {
        return (AreaObject) super.getObject();
    }

    public String getHint() {
        return toString();
    }

    public TextureRegion getIconTexture() {
        return null;
    }

    public Image getIcon() {
        TextureRegion iconTexture = getIconTexture();
        if (iconTexture == null) {
            return null;
        }
        return new Image(iconTexture);
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public String getDescription() {
        if (description == null) {
            description = buildDescription() + "\n \n"
                    + actionEvaluation.getApCostHint()
                   /* + "\n" + actionEvaluation.getLevelReqHint()*/;
        }
        return description;
    }

    public abstract boolean isTwoStepCompletion();

    @Override
    public void end() {

    }

    private ActionEvaluation calcActionEvaluation() {
        return ACTION_EVAL_BY_CLASS.get(getClass());
    }

    protected abstract String buildDescription();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public int compareTo(AbstractAreaObjectAction o) {
        return name.compareTo(o.name);
    }

    public enum Group {
        COMMON, ATTACK, DEFENCE, TRIBE
    }
}
