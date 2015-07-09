package conversion7.engine;

import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.classes.UnitClassConstants;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.quest.start_world.StartWorldQuest;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.view.AreaViewer;
import org.slf4j.Logger;

public class ClientCore extends AbstractClientCore {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static boolean initWorldFromCore;
    public static boolean initWorldFromWorldQuest;
    public static ClientCore core;

    public Battle battle;

    public boolean isBattleActiveStage() {
        return activeStage.getClass().equals(Battle.class);
    }

    public boolean isAreaViewerActiveStage() {
        return activeStage.getClass().equals(AreaViewer.class);
    }

    @Override
    public void create() {
        super.create();

        core = this;
        Assets.loadAll();

        UnitClassConstants.init();
        InventoryItemStaticParams.init();
        registerTweenEngine();
        registerEntitySystems();
        registerShaders();
        registerPostProcessor(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX);

        graphic = new ClientGraphic(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX);
        Gdxg.clientUi.registerInputProcessors(null);

        LOG.info("Created.");
        initialized = true;

        if (initWorldFromCore) {
            LOG.info("initWorldFromCore");
            if (initWorldFromWorldQuest) {
                new StartWorldQuest().start();
            } else {
                World.init();
                WorldServices.showAreaViewer();
            }
        }
    }

    public void returnToWorld() {
        core.activateStage(World.getAreaViewer());
        Gdxg.clientUi.showTeamUi();
    }


}
