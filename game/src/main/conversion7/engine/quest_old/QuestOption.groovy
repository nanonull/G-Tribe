package conversion7.engine.quest_old

import groovy.transform.ToString

@ToString
class QuestOption {

    String text
    Closure actionClosure

    QuestOption(String text, Closure actionClosure) {
        this.actionClosure = actionClosure
        this.text = text
    }

}
