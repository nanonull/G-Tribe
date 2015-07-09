package conversion7.engine.geometry.water;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class WaterAttribute extends Attribute {
    private final static String ALIAS = "WaterAttribute";
    public final static long ID = register(ALIAS);

    public WaterAttribute() {
        super(ID);
    }

    @Override
    public Attribute copy() {
        return new WaterAttribute();
    }

    @Override
    protected boolean equals(Attribute other) {
        return this.type == other.type;
    }

    @Override
    public int compareTo(Attribute o) {
        return this.equals(o) ? 0 : 1;
    }
}
