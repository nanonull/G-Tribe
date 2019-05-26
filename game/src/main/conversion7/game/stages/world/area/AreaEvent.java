package conversion7.game.stages.world.area;

import com.badlogic.gdx.utils.Predicate;
import conversion7.engine.geometry.Point2s;

// TODO
public abstract class AreaEvent {


    AreaEventI object;
    public Area area;
    private int step;
    int moveFreq = 1;
    Point2s moveDir = new Point2s();
    int codeFreq = 1;
    //    executed for all cells on area
    Predicate<Area> code;
    int movesAlive = 1;

    public AreaEvent(AreaEventI areaEventI) {
        setObject(areaEventI);
    }

    public AreaEvent setMoveFreq(int moveFreq) {
        this.moveFreq = moveFreq;
        return this;
    }

    public AreaEvent setMoveDir(Point2s moveDir) {
        this.moveDir = moveDir;
        return this;
    }

    public AreaEvent setCodeFreq(int codeFreq) {
        this.codeFreq = codeFreq;
        return this;
    }

    @Deprecated
    public AreaEvent setCode(Predicate<Area> code) {
        this.code = code;
        return this;
    }

    public AreaEvent setMovesAlive(int movesAlive) {
        this.movesAlive = movesAlive;
        return this;
    }

    public AreaEvent setObject(AreaEventI object) {
        this.object = object;
        return this;
    }

    public void act() {
        if (step % codeFreq == 0) {
            runEvent();
        }
        if (step % moveFreq == 0) {
            move();
        }
        step++;
    }

    private void move() {
        Area nextArea = this.area.getArea(moveDir.x, moveDir.y);
        nextArea.addEvent(this);
    }

    private void runEvent() {
        object.notifyAt(area);
    }
}
