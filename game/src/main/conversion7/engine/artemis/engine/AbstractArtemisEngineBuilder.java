package conversion7.engine.artemis.engine;

import com.artemis.AspectSubscriptionManager;
import com.artemis.SystemInvocationStrategy;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.injection.CachedInjector;
import com.artemis.injection.FieldHandler;
import com.artemis.injection.FieldResolver;
import com.artemis.injection.InjectionCache;
import com.artemis.injection.Injector;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public abstract class AbstractArtemisEngineBuilder {
    private static final Logger LOG = Utils.getLoggerForClass();

    private World world;
    private TagManager tagManager;
    private UuidEntityManager uuidEntityManager;
    private AspectSubscriptionManager subscriptionManager;

    public AbstractArtemisEngineBuilder() {

    }

    public World getWorld() {
        return world;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public UuidEntityManager getUuidEntityManager() {
        return uuidEntityManager;
    }

    public AspectSubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    public AbstractArtemisEngineBuilder build() {
        if (world != null) {
            throw new RuntimeException("Already built!");
        }

        LOG.info("Register Artemis-odb engine");
        WorldConfigurationBuilder configBuilder = buildArtemisOdbConfigBuilder();
        SystemInvocationStrategy strategy = buildCustomArtemisStrategy();
        if (strategy != null) {
            LOG.info("Custom Artemis-odb strategy");
            configBuilder.register(strategy);
        }

        WorldConfiguration worldConfiguration = configBuilder.build();
        Injector injector = buildCustomArtemisInjector();
        if (injector != null) {
            LOG.info("Custom Artemis-odb injector");
            worldConfiguration.setInjector(injector);
        }

        world = new com.artemis.World(worldConfiguration);
        tagManager = world.getSystem(TagManager.class);
        uuidEntityManager = world.getSystem(UuidEntityManager.class);
        subscriptionManager = world.getAspectSubscriptionManager();
        return this;
    }

    protected abstract WorldConfigurationBuilder buildArtemisOdbConfigBuilder();

    protected SystemInvocationStrategy buildCustomArtemisStrategy() {
        return null;
    }

    private Injector buildCustomArtemisInjector() {
        FieldResolver fieldResolver = buildFieldResolver();
        if (fieldResolver == null) {
            return null;
        } else {
            FieldHandler fieldHandler = new FieldHandler(new InjectionCache());
            fieldHandler.addFieldResolver(fieldResolver);
            return new CachedInjector().setFieldHandler(fieldHandler);
        }
    }

    protected FieldResolver buildFieldResolver() {
        return null;
    }
}
