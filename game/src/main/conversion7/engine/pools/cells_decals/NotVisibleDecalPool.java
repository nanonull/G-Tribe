package conversion7.engine.pools.cells_decals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.FlushablePool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.DecalActor;
import conversion7.game.Assets;

public class NotVisibleDecalPool extends FlushablePool<DecalActor> {
    private Color currentColor = Color.RED;

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        for (DecalActor actor : obtained) {
            actor.decal.setColor(color);
        }
    }

    @Override
    protected DecalActor newObject() {
        DecalActor decalActor = new DecalActor("not-visible",
                Decal.newDecal(1, 1, Assets.pixel, true),
                Gdxg.decalBatchTransparentLayer);
        decalActor.setRotation(0, -90, 0);
        decalActor.decal.setColor(currentColor);
        decalActor.frustrumRadius = 1;
        decalActor.translate(0, 1, 0);

        decalActor.linkToPool(this);
        return decalActor;
    }

}
