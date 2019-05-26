package conversion7.engine.artemis;

import com.artemis.BaseSystem;
import conversion7.engine.ClientCore;

public class BattleSystem extends BaseSystem {
    private static final float INTERVAL = 1 / 10f;
    public boolean activateNextSquad;
    ClientCore core;
    AnimationSystem animationSystem;
    private float deltaAcc;

    @Override
    protected void processSystem() {
        deltaAcc += world.getDelta();
        if (deltaAcc < INTERVAL || !activateNextSquad || AnimationSystem.isLocking()) {
            return;
        }
        deltaAcc = 0;
        activateNextSquad = false;

        if (core.world.isBattleActive()) {
            core.world.getActiveBattle().activateNextSquad();
        }
    }

    public void nextSquad() {
        activateNextSquad = true;
    }
}
