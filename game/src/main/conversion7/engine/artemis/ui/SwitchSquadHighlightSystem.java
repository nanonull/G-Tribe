package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class SwitchSquadHighlightSystem extends IteratingSystem {
    public static ComponentMapper<SwitchSquadHighlightComp> components;
    private static final Color CLEAR = Color.valueOf("ccccccff");

    public SwitchSquadHighlightSystem() {
        super(Aspect.all(SwitchSquadHighlightComp.class));
    }

    @Override
    protected void process(int entityId) {
        SwitchSquadHighlightComp comp = components.get(entityId);
        components.remove(entityId);

        AbstractSquad squad = comp.squad;
        if (!squad.isAlive()) {
            return;
        }

        Color color;
        if (comp.toHighlight) {
            color = Color.YELLOW;
        } else {
            color = CLEAR;
        }

        for (SceneNode3d node3d : squad.getSceneBody().getChildren()) {
            if (node3d instanceof ModelActor) {
                ModelActor modelActor = (ModelActor) node3d;
                modelActor.applyMaterialAttribute(ColorAttribute.createDiffuse(color));
            }
        }
    }
}
