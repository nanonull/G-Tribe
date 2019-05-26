package conversion7.engine.pools.cells_decals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.FlushablePool;
import conversion7.engine.customscene.DecalActor;
import conversion7.game.Assets;

public class UnexploredDecalPool extends FlushablePool<DecalActor> {

    private Color currentColor = Color.YELLOW;

    public void setCurrentColor(Color color) {
        currentColor = color;
        for (DecalActor decalActor : obtained) {
            decalActor.decal.setColor(color);
        }
    }

    @Override
    protected DecalActor newObject() {
        Decal decal = Decal.newDecal(1, 1, Assets.pixel, true);
        decal.setColor(currentColor);
        DecalActor decalActor = new DecalActor("unexplored", decal);
        decalActor.setRotation(0, -90, 0);
        decalActor.frustrumRadius = 1;
        decalActor.translate(0, 1, 0);

        decalActor.linkToPool(this);
        return decalActor;
    }

}
