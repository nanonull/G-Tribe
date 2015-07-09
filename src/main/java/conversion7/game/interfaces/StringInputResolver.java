package conversion7.game.interfaces;

public interface StringInputResolver {
    public boolean couldAcceptInput(String text);

    public void handleInput(String text);

    public void cancel();
}
