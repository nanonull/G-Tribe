package conversion7.engine.dialog;

public class QuestOption {
    private String text;
    private Runnable actionClosure;
    private String description;


    public QuestOption(String text, Runnable actionClosure) {
        this.actionClosure = actionClosure;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Runnable getActionClosure() {
        return actionClosure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
