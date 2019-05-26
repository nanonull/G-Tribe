package conversion7.game.stages.world.landscape;

import conversion7.game.stages.world.area.Area;

import static conversion7.game.stages.world.landscape.LandscapeBiomsMapLevel1.TypeMapping.*;

public class LandscapeBiomsMapLevel1 {

    private static final BiomMapData[][] map = {
            {RND, RND, DIR, RMO, RND, RND, RND, RND, RND, RND},
            {RMO, RMO, RMO, RMO, RMO, RMO, RND, RND, _QA, RND},
            {RND, MON, _SS, RND, WAT, RMO, DIR, RND, RMO, RND},
            {RMO, MON, STO, WAT, MON, _BC, RMO, RMO, RND, RMO},
            {RMO, DIR, RMO, RND, RMO, DIR, RMO, RND, RMO, RND},
            {MON, _PL, DIR, RMO, RMO, RMO, RND, RMO, RND, RMO},
            {RND, MON, RND, RMO, MON, MON, RND, RND, RMO, RND},
            {RND, RND, RND, RND, RND, RND, RND, RND, RND, RND},
            {RND, RND, RND, RND, RND, RND, RND, RND, RND, RND},
            {RND, RND, RND, RND, RND, RND, RND, RND, RND, RND},
    };

    public static int MAP_WIDTH;
    public static int MAP_HEIGHT;

    static {
        MAP_WIDTH = map[0].length;
        MAP_HEIGHT = map.length;
    }

    public static BiomMapData getBiom(int areaBiomX, int areaBiomY) {
        if (areaBiomY < MAP_HEIGHT && areaBiomX < MAP_WIDTH) {
            return map[MAP_HEIGHT - areaBiomY - 1][areaBiomX];
        }
        return RND;
    }

    public static void setStoryObjectsByBiom(Area area) {
        for (Biom biom : area.bioms) {
            if (biom.getBiomMapData().playerStart) {
                area.world.playerStartBiom = biom;
            } else if (biom.getBiomMapData().baalsCamp) {
                area.world.baalsCampStart = biom;
            } else if (biom.getBiomMapData().questAnimal) {
                area.world.questAnimal = biom;
            } else if (biom.getBiomMapData().spaceShip) {
                area.world.spaceShipStart = biom;
            }
        }

    }

    public static class BiomMapData {
        private static int ids;
        private final int id;

        public boolean randomBiom;
        public boolean randomMoveable;
        public boolean sand;
        public boolean stone;
        public boolean dirt;
        public boolean water;
        public boolean mountains;

        public boolean playerStart;
        public boolean spaceShip;
        public boolean baalsCamp;
        public boolean questAnimal;

        public BiomMapData() {
            id = ids++;
        }

        @Override
        public String toString() {
            return super.toString() + "ID " + id;
        }
    }

    public static class TypeMapping {
        public static BiomMapData DIR = new BiomMapData();
        public static BiomMapData SAN = new BiomMapData();
        public static BiomMapData STO = new BiomMapData();
        public static BiomMapData WAT = new BiomMapData();
        public static BiomMapData MON = new BiomMapData();
        public static BiomMapData RND = new BiomMapData();
        public static BiomMapData RMO = new BiomMapData();
        public static BiomMapData _PL = new BiomMapData();
        public static BiomMapData _SS = new BiomMapData();
        public static BiomMapData _BC = new BiomMapData();
        public static BiomMapData _QA = new BiomMapData();

        static {
            DIR.dirt = true;
            SAN.sand = true;
            STO.stone = true;
            WAT.water = true;
            MON.mountains = true;

            RMO.randomMoveable = true;
            RND.randomBiom = true;

            _PL.playerStart = true;
            _PL.dirt = true;

            _SS.spaceShip = true;
            _SS.randomMoveable = true;

            _BC.baalsCamp = true;
            _BC.dirt = true;

            _QA.questAnimal = true;
            _QA.randomMoveable = true;
        }
    }
}
