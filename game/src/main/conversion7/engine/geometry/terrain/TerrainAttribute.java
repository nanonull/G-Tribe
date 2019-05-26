package conversion7.engine.geometry.terrain;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class TerrainAttribute extends Attribute {
    private final static String ALIAS = "TerrainAttribute";
    public final static long ID = register(ALIAS);
    public static final String VERTEX_ALIAS = "a_terrain_soil";

    public TerrainAttribute() {
        super(ID);
    }

    @Override
    public Attribute copy() {
        return new TerrainAttribute();
    }

    @Override
    protected boolean equals(Attribute other) {
        return this.type == other.type;
    }

    public static VertexAttribute getVertexAttribute() {
        return new VertexAttribute(VertexAttributes.Usage.Generic, 4, VERTEX_ALIAS);
    }

    @Override
    public int compareTo(Attribute o) {
        return this.equals(o) ? 0 : 1;
    }
}
