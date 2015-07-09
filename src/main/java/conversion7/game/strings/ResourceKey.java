package conversion7.game.strings;

import conversion7.game.Assets;

public enum ResourceKey {
    NEW_GAME("NEW_GAME"),
    CREATE_WORLD("CREATE_WORLD"),
    WORLD_NOT_CREATED("WORLD_NOT_CREATED"),
    SELECT_START_VARIANT("SELECT_START_VARIANT"),
    RETURN_BACK("RETURN_BACK"),
    CONTINUE("CONTINUE"),
    ATTACK("ATTACK"),

    QUEST1_DESCRIPTION1("QUEST1_DESCRIPTION1"),
    QUEST1_DESCRIPTION2("QUEST1_DESCRIPTION2"),

    DWSQ_DESCRIPTION("DWSQ_DESCRIPTION"),
    DWSQ_SPLIT_MERGE("DWSQ_SPLIT_MERGE"),
    DWSQ_JOIN("DWSQ_JOIN"),
    DWSQ_SQUAD_JOINED("DWSQ_SQUAD_JOINED"),
    DWSQ_SQUAD_JOIN_FAILED("DWSQ_SQUAD_JOIN_FAILED"),
    //
    ;

    private String name;

    ResourceKey(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getValue() {
        return Assets.textResources.get(this);
    }

}
