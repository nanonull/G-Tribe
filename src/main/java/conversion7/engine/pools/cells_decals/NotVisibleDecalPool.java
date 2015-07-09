package conversion7.engine.pools.cells_decals;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Pool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.DecalActor;
import conversion7.game.Assets;

public class NotVisibleDecalPool extends Pool<DecalActor> {

    @Override
    protected DecalActor newObject() {
        DecalActor decalActor = new DecalActor("not-visible",
                Decal.newDecal(1, 1, Assets.pixelWhite, true),
                Gdxg.decalBatchTransparentLayer);
        decalActor.setRotation(0, -90, 0);
        decalActor.decal.setColor(0, 0, 0, 0.6f);
        decalActor.frustrumRadius = 1;
        decalActor.translate(0, 1, 0);

        decalActor.linkToPool(this);
        return decalActor;
    }

}
