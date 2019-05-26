package conversion7.game.stages.world

import conversion7.engine.utils.PropertiesLoader
import conversion7.game.stages.world.area.Area
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.AnimalSpawn
import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ['metaClass'])
public class WorldSettings {

    public static
    final int MAX_HUMAN_SQUAD_CHANCE = PropertiesLoader.getIntProperty("WorldDirector.MAX_HUMAN_SQUAD_CHANCE");
    @Deprecated public static
    final int ANIMAL_HERD_CHANCE_MAX = PropertiesLoader.getIntProperty("WorldDirector.MAX_ANIMAL_HERD_CHANCE");

    @Deprecated
    public static final int ANIMAL_HERDS_AMOUNT_LIMIT =
            PropertiesLoader.getIntProperty("WorldDirector.MAX_ANIMAL_HERDS_AMOUNT_LIMIT");
    public static final int POWER_BALANCE_MLT = 2

    public final int widthInAreas;
    public final int heightInAreas;
    public boolean dummyLandscape;
    public boolean dummyFauna;
    public final boolean fogOfWar;
    public final boolean createAnimalTeam;
    public final boolean createPlayerTeam;
    public final int aiTeamsAmount;
    /** no limit: -1 */
    public final int animalHerdsAmount;
    public final int worldCreationTimeout;

    // -1 .. +1
    public int placeBalance
    public int resBalance
    public int powerBalance
    public int animalsBalance
    private int playerTeams

    public WorldSettings(
            int widthInAreas
            , int heightInAreas
            , boolean dummyLandscape
            , boolean fogOfWar
            , int playerTeams
            , int aiTeamsAmount
            , int animalHerdsAmount
            , worldCreationTimeout = 10
    ) {
        this.playerTeams = playerTeams
        this.worldCreationTimeout = worldCreationTimeout
        this.widthInAreas = widthInAreas;
        this.heightInAreas = heightInAreas;
        this.dummyLandscape = dummyLandscape;
        this.fogOfWar = fogOfWar;
        this.createPlayerTeam = playerTeams > 0;
        this.aiTeamsAmount = aiTeamsAmount;
        this.animalHerdsAmount = animalHerdsAmount;

        this.createAnimalTeam = animalHerdsAmount > 0 || animalHerdsAmount == -1;
        this.dummyFauna = !createAnimalTeam && !createPlayerTeam && aiTeamsAmount == 0;

        if ((createAnimalTeam || createPlayerTeam) && dummyFauna) {
            throw new RuntimeException("Incompatible modes! " + this);
        }

    }

    int getAnimalsBalanceSpawnEvery() {
        def i = animalsBalance + AnimalSpawn.BASE_SPAWN_EVERY
        if (i < 1) {
            i = 1
        }
        return i;
    }

    int totalBalance() {
        resBalance + animalsBalance + powerBalance + placeBalance
    }

    boolean isTemperatureOkForPlayerSpawn(Cell startCell) {
        int goodTempMid = Area.BASE_MIN_TEMPERATURE_FOR_SPAWN +
                placeBalance * Area.TEMPERATURE_ZONE_STEP
        def minT = goodTempMid - Area.TEMPERATURE_ZONE_STEP
        def maxT = goodTempMid + Area.TEMPERATURE_ZONE_STEP
        return startCell.temperature >= minT && startCell.temperature <= maxT;
    }
}
