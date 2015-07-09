package conversion7.game.stages.test;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import conversion7.engine.customscene.CustomStage;
import conversion7.game.stages.GameStage;

public class TestScene extends GameStage {

    protected CustomStage customStage;

    public TestScene(PerspectiveCamera perspectiveCamera) {
        customStage = new CustomStage("test", perspectiveCamera);
        registerInputProcessors();
    }

    @Override
    public void registerInputProcessors() {
        inputProcessors.add(customStage);
    }

    @Override
    public void act(float delta) {
        customStage.act(delta);
    }

    @Override
    public void draw() {
        customStage.draw();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    public CustomStage getCustomStage() {
        return customStage;
    }

    public void setCustomStage(CustomStage customStage) {
        this.customStage = customStage;
    }
}
