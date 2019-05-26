package conversion7.game.stages.world.objects.buildings;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.game.stages.world.landscape.Cell;

public class CampNet {
    private static final float NET_BONUS_PER_CAMP = 0.05f;
    private Array<Camp> camps = new Array<>();

    public Array<Camp> getCamps() {
        return camps;
    }

    public ObjectSet<Cell> getCells() {
        ObjectSet<Cell> cells = new ObjectSet<>();
        for (Camp camp : camps) {
            cells.addAll(camp.getCampCells());
        }
        return cells;
    }

    public String getCampsUiHint() {
        return getBuiltCamps() + "/" + camps.size;
    }

    public int getBuiltCamps() {
        int builtCamps = 0;
        for (Camp camp : camps) {
            if (camp.isConstructionCompleted()) {
                builtCamps++;
            }
        }
        return builtCamps;
    }

    public float getBonus() {
        float bonusFromNet = 0;
        for (int i = 1; i < getBuiltCamps(); i++) {
            bonusFromNet += NET_BONUS_PER_CAMP;
        }
        return bonusFromNet;
    }

    public void addCamp(Camp camp) {
        camps.add(camp);
        camp.setNet(this);
    }

    public void addNet(CampNet net) {
        for (Camp camp : net.camps) {
            addCamp(camp);
        }
    }

    public void recalculateCamps() {
        Array<Camp> campsCopy = new Array<>(this.camps);
        for (Camp camp : camps) {
            camp.setNet(null);
        }
        camps.clear();
        for (Camp camp : campsCopy) {
            camp.addToExistingCampNet();
        }
    }
}
