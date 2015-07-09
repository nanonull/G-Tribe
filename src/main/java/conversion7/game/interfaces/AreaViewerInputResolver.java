package conversion7.game.interfaces;

import conversion7.game.stages.world.landscape.Cell;

public interface AreaViewerInputResolver {

    boolean couldAcceptInput(Cell input);

    void handleInput(Cell input);

    void cancel();

    void onInputHandled();
}
