package conversion7.game.interfaces;

public interface Progressive extends Completable {

    public abstract void act(float delta);

    /** Called when flow started */
    public abstract void start();

    /** Called when half of flow completed */
    public abstract void completeHalf();

}
