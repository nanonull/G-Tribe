package conversion7.game.stages.world;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.artemis.audio.PlayerTribeAudioSystem;
import conversion7.engine.artemis.ui.UnitInWorldHintPanelsSystem;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeBehaviourTag;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.ui.UiLogger;

public class WorldRelations {
    public static final float MIN_CHANCE_AT_SQUADS_REL = 0.75f;
    public static final Integer NEUTRAL_RELATION_MID = 0;
    public static final Integer ENEMIES_RELATION_TOP = -Team.BASE_REL_ITEM * 3;
    public static final Integer ALLY_RELATION_BOTTOM = Math.abs(ENEMIES_RELATION_TOP);
    public static int ALLY_PERC_PER_REL_POINT = 100 / ALLY_RELATION_BOTTOM;
    public static final Integer ANIMALS_RELATION_START = ENEMIES_RELATION_TOP;
    private World world;
    ObjectMap<String, RelationData> relationDatas = new ObjectMap<>();

    public WorldRelations(World world) {

        this.world = world;
    }

    /** get balance >> get rel types >> calc rel types list >> calc balance */
    public RelationData getRelationData(Team team1, Team team2) {
        String compositeId = world.getCompositeId(team1, team2);
        RelationData relationData = relationDatas.get(compositeId);
        if (relationData == null) {
            relationData = new RelationData(compositeId, team1, team2);
            relationDatas.put(compositeId, relationData);
        }
        relationData.refresh();
        return relationData;
    }

    public static class RelationData {

        public String compositeId;
        private final Team team1;
        private final Team team2;
        public ObjectSet<TribeRelationType> types = new ObjectSet<>();
        private Integer relationBalance;

        public RelationData(String compositeId, Team team1, Team team2) {
            this.compositeId = compositeId;
            this.team1 = team1;
            this.team2 = team2;
        }

        public String getRelationTypesHint() {
            ObjectSet<TribeRelationType> tribeRelationTypes = types;
            if (tribeRelationTypes == null) {
                return "no comments yet";
            }
            if (tribeRelationTypes.size == 0) {
                return "no relations yet";
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (TribeRelationType relationType : tribeRelationTypes) {
                stringBuilder.append(relationType.toString()).append(" ")
                        .append(relationType.getRelationValue()).append("\n");
            }
            return stringBuilder.toString();
        }

        public int getBalance() {
            int values = 0;
            for (TribeRelationType relationType : types) {
                values += relationType.getRelationValue();
            }
            return values;
        }

        public void refresh() {
            refreshRelationTagsAndTypes();
            refreshBalance();
        }

        /** Assume types are init */
        public void refreshBalance() {
            setRelation(getBalance(), team1, team2);
        }

        private void alliesToNeutral(Team team1, Team team2) {
            if (team1.isHumanPlayer()) {
                UnitInWorldHintPanelsSystem.refresh(team1);
                UiLogger.addImportantGameInfoLabel(team2.getName() + " is not ally, but neutral from now!");
                PlayerTribeAudioSystem.metTribe = true;
            } else if (team2.isHumanPlayer()) {
                UnitInWorldHintPanelsSystem.refresh(team2);
                UiLogger.addImportantGameInfoLabel(team1.getName() + " is not ally, but neutral from now!");
                PlayerTribeAudioSystem.metTribe = true;
            }
        }

        private void enemyToNeutral(Team team1, Team team2) {
            UnitInWorldHintPanelsSystem.refresh(team1);
            UnitInWorldHintPanelsSystem.refresh(team2);
            if (team1.isHumanPlayer()) {
                UiLogger.addImportantGameInfoLabel(team2.getName() + " is not enemy, but neutral from now!");
                PlayerTribeAudioSystem.metTribe = true;
            } else if (team2.isHumanPlayer()) {
                UiLogger.addImportantGameInfoLabel(team1.getName() + " is not enemy, but neutral from now!");
                PlayerTribeAudioSystem.metTribe = true;
            }
        }

        private void newEnemies(Team team1, Team team2) {
            testEnemy(team1, team2);
            testEnemy(team2, team1);
        }
        private void testEnemy(Team team1, Team team2) {
            UnitInWorldHintPanelsSystem.refresh(team1);
            if (team1.isHumanPlayer()) {
                if (team1.metTeams.contains(team2)) {
                    UiLogger.addImportantGameInfoLabel(team2.getName() + " is new enemy of your tribe!");
                }
                PlayerTribeAudioSystem.newWar = true;
            }
        }

        private void newAllies(Team team1, Team team2) {
            UnitInWorldHintPanelsSystem.refresh(team1);
            UnitInWorldHintPanelsSystem.refresh(team2);
            if (team1.isHumanPlayer() && team1.metTeams.contains(team2)) {
                UiLogger.addImportantGameInfoLabel(team2.getName() + " is new ally of your tribe!");
                PlayerTribeAudioSystem.metTribe = true;
            } else if (team2.isHumanPlayer()) {
                UiLogger.addImportantGameInfoLabel(team1.getName() + " is new ally of your tribe!");
                PlayerTribeAudioSystem.metTribe = true;
            }
        }

        public void setRelation(int relationBalanceNew, Team team1, Team team2) {
            Integer relationBalanceOld = relationBalance;
            if (relationBalance == null) {
                if (relationBalanceNew <= ENEMIES_RELATION_TOP) {
                    newEnemies(team1, team2);
                } else if (relationBalanceNew >= ALLY_RELATION_BOTTOM) {
                    newAllies(team1, team2);
                }

            } else {
                if (relationBalanceNew != relationBalanceOld) {
                    if (relationBalanceNew <= ENEMIES_RELATION_TOP) {
                        if (relationBalanceOld > ENEMIES_RELATION_TOP) {
                            newEnemies(team1, team2);
                        }
                    } else if (relationBalanceNew >= ALLY_RELATION_BOTTOM) {
                        if (relationBalanceOld < ALLY_RELATION_BOTTOM) {
                            newAllies(team1, team2);
                        }
                    } else {
                        if (relationBalanceOld <= ENEMIES_RELATION_TOP) {
                            enemyToNeutral(team1, team2);
                        } else if (relationBalanceOld >= ALLY_RELATION_BOTTOM) {
                            alliesToNeutral(team1, team2);
                        }
                    }
                }
            }
            relationBalance = relationBalanceNew;
        }

        public void refreshRelationTagsAndTypes() {
            if (team1.isAnimals() || team2.isAnimals()) {
                types.add(TribeRelationType.WILD);
            } else {
                types.remove(TribeRelationType.WILD);
            }

            if (team1.tags.contains(TribeBehaviourTag.AGGRESSIVE)
                    || team2.tags.contains(TribeBehaviourTag.AGGRESSIVE)) {
                types.add(TribeRelationType.AGGRESSIVE);
            } else {
                types.remove(TribeRelationType.AGGRESSIVE);
            }

            boolean genderRatio1 = team1.getGenderRatio();
            boolean genderRatio2 = team2.getGenderRatio();
            if (team1.tags.contains(TribeBehaviourTag.RESPECT_MALE)
                    || team2.tags.contains(TribeBehaviourTag.RESPECT_MALE)) {
                if (genderRatio1 || genderRatio2) {
                    types.add(TribeRelationType.RESPECT_MALE);
                } else {
                    types.remove(TribeRelationType.RESPECT_MALE);
                }
            }
            if (team1.tags.contains(TribeBehaviourTag.RESPECT_FEMALE)
                    || team2.tags.contains(TribeBehaviourTag.RESPECT_FEMALE)) {
                if (!genderRatio1 || !genderRatio2) {
                    types.add(TribeRelationType.RESPECT_FEMALE);
                } else {
                    types.remove(TribeRelationType.RESPECT_FEMALE);
                }
            }

            if (team1.tags.contains(TribeBehaviourTag.HATE_MALE)
                    || team2.tags.contains(TribeBehaviourTag.HATE_MALE)) {
                if (genderRatio1 || genderRatio2) {
                    types.add(TribeRelationType.HATE_MALE);
                } else {
                    types.remove(TribeRelationType.HATE_MALE);
                }
            }

            if (team1.tags.contains(TribeBehaviourTag.HATE_FEMALE)
                    || team2.tags.contains(TribeBehaviourTag.HATE_FEMALE)) {
                if (!genderRatio1 || !genderRatio2) {
                    types.add(TribeRelationType.HATE_FEMALE);
                } else {
                    types.remove(TribeRelationType.HATE_FEMALE);
                }
            }
        }

        public void add(TribeRelationType type) {
            types.add(type);
            refreshBalance();
        }
    }
}
