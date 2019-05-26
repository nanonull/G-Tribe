package conversion7.game.stages.world.team;

import conversion7.game.stages.world.WorldRelations;

public enum TribeRelationType {
    HELP(Team.BASE_REL_ITEM),
    QUEST(Team.BASE_REL_ITEM),
    GOAL(Team.BASE_REL_ITEM),
    GIFT(Team.BASE_REL_ITEM),
    JUST_NICE(Team.BASE_REL_ITEM),
    ALLY(WorldRelations.ALLY_RELATION_BOTTOM),
    HATE_MALE(-Team.BASE_REL_ITEM),
    RESPECT_MALE(Team.BASE_REL_ITEM),
    HATE_FEMALE(-Team.BASE_REL_ITEM),
    RESPECT_FEMALE(Team.BASE_REL_ITEM),
    AGGRESSIVE(WorldRelations.ENEMIES_RELATION_TOP + Team.BASE_REL_ITEM),
    ATTACK((int) (WorldRelations.ENEMIES_RELATION_TOP * 1.5f)),
    UNIT_CAPTURED(WorldRelations.ENEMIES_RELATION_TOP),
    MAIN_ENEMY(ATTACK.relationValue),
    CAMP_CAPTURED(ATTACK.relationValue),
    WILD(-99 * Team.BASE_REL_ITEM),;

    private int relationValue = 1;


    TribeRelationType(int relationValue) {
        this.relationValue = relationValue;
    }

    public int getRelationValue() {
        return relationValue;
    }
}
