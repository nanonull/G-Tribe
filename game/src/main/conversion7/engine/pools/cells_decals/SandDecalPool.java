package conversion7.engine.pools.cells_decals;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Pool;
import conversion7.engine.customscene.DecalActor;
import conversion7.game.Assets;

public class SandDecalPool extends Pool<DecalActor> {

    @Override
    protected DecalActor newObject() {
        DecalActor decalActor = new DecalActor("sand", Decal.newDecal(1, 1, Assets.sand, true));
        decalActor.setRotation(0, -90, 0);
        decalActor.frustrumRadius = 1;

        decalActor.linkToPool(this);
        return decalActor;
    }

}
