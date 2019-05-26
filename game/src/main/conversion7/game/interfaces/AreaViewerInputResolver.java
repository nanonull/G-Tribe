package conversion7.game.interfaces;

import conversion7.game.stages.world.landscape.Cell;

public interface AreaViewerInputResolver {

    boolean couldAcceptInput(Cell input);

    void beforeInputHandle();

    void handleAcceptedInput(Cell input);

    void afterInputHandled();

    void cancel();

    boolean hasAcceptableDistanceTo(Cell mouseOverCell);
}
