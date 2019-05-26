package shared;

import com.artemis.WorldConfigurationBuilder;
import conversion7.engine.ClientCore;
import conversion7.engine.artemis.engine.GdxgDefaultArtemisBuilder;
import conversion7.engine.artemis.engine.time.BeforeGameEngineTickSystem;
import conversion7.engine.artemis.engine.time.CoreDeltaCollectorSystem;

public class GdxgDefaultTestingArtemisBuilder extends GdxgDefaultArtemisBuilder {
    public GdxgDefaultTestingArtemisBuilder(ClientCore core) {
        super(core);
    }

    @Override
    protected WorldConfigurationBuilder buildArtemisOdbConfigBuilder() {
        WorldConfigurationBuilder builder = super.buildArtemisOdbConfigBuilder();

        builder.with(WorldConfigurationBuilder.Priority.HIGH, new BeforeGameEngineTickSystem());
        builder.with(WorldConfigurationBuilder.Priority.HIGH, new CoreDeltaCollectorSystem());

        return builder;
    }
}