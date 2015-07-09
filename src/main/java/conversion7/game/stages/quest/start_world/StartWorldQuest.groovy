package conversion7.game.stages.quest.start_world

import conversion7.engine.AbstractClientCore
import conversion7.engine.ClientApplication
import conversion7.engine.ClientCore
import conversion7.engine.Gdxg
import conversion7.engine.quest.AbstractQuest
import conversion7.engine.quest.QuestOption
import conversion7.engine.utils.Utils
import conversion7.game.PictureAsset
import conversion7.game.run.RunAndScheduleLibrary
import conversion7.game.stages.test.TestScene
import conversion7.game.stages.world.World
import conversion7.game.strings.ResourceKey
import org.testng.annotations.Test

class StartWorldQuest extends AbstractQuest {

    @Test
    public void testApp() {
        ClientApplication.start(new StartWorldQuestApp());
        AbstractClientCore.waitCoreCreated();
        Utils.infinitySleepThread();
    }

    class StartWorldQuestApp extends ClientCore {
        @Override
        public void create() {
            super.create();
            // test body:
            new StartWorldQuest().start();

            // render it:
            TestScene testScene = new TestScene(Gdxg.graphic.getCamera());
            core.activateStage(testScene);
        }
    }

    final QuestOption IM_CREATOR = new QuestOption(ResourceKey.NEW_GAME.getValue(),
            { ->
                worldNotReady = true
                addChoiceOption(CREATE_WORLD);
                addChoiceOption(BACK);
                setPicture(PictureAsset.ROCK_DRAW);
            });

    final QuestOption CREATE_WORLD = new QuestOption(ResourceKey.CREATE_WORLD.getValue(),
            { ->
                World.init();
                worldNotReady = false

                // focus in quest
                RunAndScheduleLibrary.scheduleCameraFocusOnPlayerSquad(50);

                Gdxg.clientUi.disableWorldInteraction();
                setPictureViewActive(false)
                ClientCore.core.activateStage(World.areaViewer);

                addDescriptionRow(ResourceKey.QUEST1_DESCRIPTION1)
                addChoiceOption(CONTINUE1)

            });

    final QuestOption BACK = new QuestOption(ResourceKey.RETURN_BACK.getValue(),
            { ->
                addDescriptionRow(ResourceKey.SELECT_START_VARIANT)
                addChoiceOption(IM_CREATOR);
            });

    final QuestOption CONTINUE1 = new QuestOption(ResourceKey.CONTINUE.getValue(),
            { ->
                addDescriptionRow(ResourceKey.QUEST1_DESCRIPTION2)
                addChoiceOption(CONTINUE2)
            });


    final QuestOption CONTINUE2 = new QuestOption(ResourceKey.CONTINUE.getValue(),
            { ->
                completeAndEnableInteraction()

                // focus in world
                RunAndScheduleLibrary.scheduleCameraFocusOnPlayerSquad(50);
                RunAndScheduleLibrary.scheduleWelcomeHint();
                Gdxg.clientUi.getTribesSeparationBar().show();

            });

    // flags
    boolean worldNotReady

    @Override
    protected void initQuestState() {
        addDescriptionRow(ResourceKey.SELECT_START_VARIANT)
        setPicture(PictureAsset.STAND_MAN)
        addChoiceOption(IM_CREATOR);
    }

    @Override
    protected void runPostClosureGlobalPhase() {
        if (worldNotReady) {
            addDescriptionRow(ResourceKey.WORLD_NOT_CREATED)
        }
    }

}
