package conversion7.engine.artemis.engine;

import com.artemis.WorldConfigurationBuilder;
import com.artemis.injection.FieldResolver;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import conversion7.engine.ClientCore;
import conversion7.engine.artemis.*;
import conversion7.engine.artemis.audio.CellAudioSystem;
import conversion7.engine.artemis.audio.PlayerTribeAudioSystem;
import conversion7.engine.artemis.audio.TrackAudioSystem;
import conversion7.engine.artemis.engine.time.IntervalExecutorSystem;
import conversion7.engine.artemis.engine.time.PollingSystem;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.artemis.scene.RefreshAreaViewCellsSystem;
import conversion7.engine.artemis.scene.TranslateSystem;
import conversion7.engine.artemis.ui.*;
import conversion7.engine.artemis.ui.float_lbl.FloatingPostponedLabelsSystem;
import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.world.main_panel.WorldMainWindowSystem;
import net.namekdev.entity_tracker.EntityTracker;


public class GdxgDefaultArtemisBuilder extends AbstractArtemisEngineBuilder {

    private ClientCore core;

    public GdxgDefaultArtemisBuilder(ClientCore core) {
        this.core = core;
    }

    @Override
    protected WorldConfigurationBuilder buildArtemisOdbConfigBuilder() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder().with(
                // MANAGERS:
                new TagManager()
                , new UuidEntityManager()
                , new NameManager()

                // SYSTEMS:
                , new GlobalStrategySystem()
                , new GlobalStrategyAiSystem()
                , new BattleAiSystem()
                , new BattleSystem()
                , new AnimationSystem()

                , new SwitchSquadHighlightSystem()
                , new UnitInWorldHintPanelsSystem()
                , new UnitUnderControlIndicatorSystem()
                , new UnitHeroIndicatorSystem()
                , new UnitUltIndicatorSystem()
                , new FloatingStatusOnCellSystem()
                , new FloatingPostponedLabelsSystem()
                , new TranslateSystem()
                , new ShowTeamUiSystem()
                , new UnitSelectionUiSystem()
                , new AreaViewerCellSelectionSystem()
                , new RefreshAreaViewCellsSystem()
                , new InWorldPanelsOverlaySystem()
                , new WorldMainWindowSystem()
                , new GameEventUiNotificationSystem()
                , new CellAudioSystem()
                , new TrackAudioSystem()
                , new PlayerTribeAudioSystem()

                , new SchedulingSystem(1 / 40f)
                , new IntervalExecutorSystem(1 / 40f)
                , new PollingSystem()
                , new DisableEntitySystem()
                , new EnableEntitySystem()
                , new DestroyEntitySystem()
        );
        if (GdxgConstants.DEVELOPER_MODE) {
            builder.with(new EntityTracker(core.registerArtemisOdbEntityTracker()));
        }
        return builder;
    }

    @Override
    protected FieldResolver buildFieldResolver() {
        return new CustomFieldsResolver(core);
    }

}
