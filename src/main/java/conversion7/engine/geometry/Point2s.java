package conversion7.engine.geometry;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class Point2s extends Point {


    public Point2s() {
        super();
    }

    public Point2s(int x, int y) {
        super(x, y);
    }

    public Point2s(Point2s position) {
        this(position.x, position.y);
    }

    public Point2s(float x, float y) {
        this((int) x, (int) y);
    }

    @Override
    public String toString() {
        return this.x + "," + this.y;
    }

    public String getValue() {
        return toString();
    }

    public Point2s abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public Point2s minus(Point2s position) {
        return minus(position.x, position.y);
    }

    public Point2s minus(int px, int py) {
        x -= px;
        y -= py;
        return this;
    }

    public Point2s plus(Point position) {
        return plus(position.x, position.y);
    }

    public Point2s plus(int px, int py) {
        x += px;
        y += py;
        return this;
    }

    public float len() {
        return Vector2.len(x, y);
    }

    public float len2() {
        return Vector2.len2(x, y);
    }

    public Point2s trim(int trimTo) {
        if (x > 0) {
            int absX = Math.abs(x);
            if (absX > trimTo) {
                x = x / absX * trimTo;
            }
        }

        if (y > 0) {
            int absY = Math.abs(y);
            if (absY > trimTo) {
                y = y / absY * trimTo;
            }
        }
        return this;
    }

    public Point2s trimAndFill(int fillTo) {
        if (x == 0) {
            x = fillTo;
        } else {
            x = x / Math.abs(x) * fillTo;
        }

        if (y == 0) {
            y = fillTo;
        } else {
            y = y / Math.abs(y) * fillTo;
        }
        return this;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Point2s multiply(int by) {
        x *= by;
        y *= by;
        return this;
    }

    public Point2s multiply(float by) {
        x = Math.round(x * by);
        y = Math.round(y * by);
        return this;
    }

    public Point2s divide(int by) {
        x /= by;
        y /= by;
        return this;
    }

    public Point2s divide(float by) {
        x = Math.round(x / by);
        y = Math.round(y / by);
        return this;
    }
}
