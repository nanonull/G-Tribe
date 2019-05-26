package conversion7.engine.geometry;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class NoiseCubeAttribute extends Attribute {
    public final static long ID = register("NoiseCubeAttribute");

    public NoiseCubeAttribute() {
        super(ID);
    }

    @Override
    public Attribute copy() {
        return new NoiseCubeAttribute();
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
