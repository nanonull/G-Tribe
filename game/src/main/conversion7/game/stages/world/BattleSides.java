package conversion7.game.stages.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class BattleSides {
    public ObjectMap<Team, Array<AbstractSquad>> teamsToEnemies = new ObjectMap<>();

    public BattleSides(Array<AbstractSquad> squadsInFight) {
        for (AbstractSquad squad1 : new Array.ArrayIterable<>(squadsInFight)) {
            Team team1 = squad1.team;
            Array<AbstractSquad> enemies = teamsToEnemies.get(team1);
            if (enemies == null) {
                enemies = new Array<>();
                teamsToEnemies.put(team1, enemies);
            }
            for (AbstractSquad squad2 : squadsInFight) {
                if (squad1 != squad2 && squad1.team.canAttack(squad2.team)
                        && !enemies.contains(squad2, true)) {
                    enemies.add(squad2);
                }
            }
        }

    }

    public boolean checkStillHasEnemies() {
        for (Team team : teamsToEnemies.keys().toArray()) {
            if (teamsToEnemies.get(team).size > 0) {
                return true;
            }
        }

        return false;
    }
}
