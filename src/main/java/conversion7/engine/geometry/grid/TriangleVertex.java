package conversion7.engine.geometry.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.geometry.terrain.SoilData;

public class TriangleVertex {
    public Vector3 position;
    public Color color;
    public Vector3 normal;
    public Vector2 textureCordinates;
    public SoilData soil;

    public TriangleVertex() {
        this.position = new Vector3();
        this.color = new Color(255, 255, 255, 255);
        this.normal = new Vector3();
        this.textureCordinates = new Vector2();
    }
}
