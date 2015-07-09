package conversion7.engine.quest

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
