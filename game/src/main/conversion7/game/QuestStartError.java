package conversion7.game;

public class QuestStartError extends RuntimeException {


    public QuestStartError(String message) {
        super(message);
    }

    public QuestStartError(Throwable t) {
        super(t);
    }

    public QuestStartError(String message, Throwable t) {
        super(message, t);
    }

}