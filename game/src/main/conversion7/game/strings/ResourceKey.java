package conversion7.game.strings;

import conversion7.game.Assets;

// Deprecated - because local resources should be local
@Deprecated
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

    DWSQ_SPLIT_MERGE("DWSQ_SPLIT_MERGE"),
    DWSQ_JOIN("DWSQ_JOIN"),
    DWSQ_SHARE_FOOD("DWSQ_SHARE_FOOD"),

    DWSQ_SQUAD_JOINED("DWSQ_SQUAD_JOINED"),
    DWSQ_SQUAD_JOIN_FAILED("DWSQ_SQUAD_JOIN_FAILED"),
    DWSQ_DESCRIPTION("DWSQ_DESCRIPTION"),
    DWSQ_ALLY_SQUAD("DWSQ_ALLY_SQUAD"),
    DWSQ_ANOTHER_TEAM_SQUAD("DWSQ_ANOTHER_TEAM_SQUAD"),
    DWSQ_ANIMAL_HERD("DWSQ_ANIMAL_HERD"),
    DWSQ_SQUAD_PARAMS_ROW("DWSQ_SQUAD_PARAMS_ROW"),
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

}
