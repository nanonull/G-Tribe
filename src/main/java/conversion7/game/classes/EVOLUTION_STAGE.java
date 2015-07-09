package conversion7.game.classes;

/**
 * Time age (the highest level of evolution) The Oldest > Australopithecus > ...
 */
public enum EVOLUTION_STAGE {

    THE_OLDEST("THE_OLDEST"),
    AUSTRALOPITECUS("AUSTRALOPITECUS"),
    EARLY_HOMO("EARLY_HOMO"),
    HOMO_ERECTUS("HOMO_ERECTUS"),
    NEANTROPINES("NEANTROPINES");

    private final String value;

    EVOLUTION_STAGE(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
