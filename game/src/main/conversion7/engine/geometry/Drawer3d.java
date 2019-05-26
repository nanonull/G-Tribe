package conversion7.engine.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;

/**
 * Static methods for drawing primitives
 */
public class Drawer3d {

    /**
     * Engine coords. Starts from corner.
     */
    public static void line(float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r, float g, float b, float a) {
        Gdxg.glPrimitiveRenderer.color(r, g, b, a);
        Gdxg.glPrimitiveRenderer.vertex(x1, y1, z1);
        Gdxg.glPrimitiveRenderer.color(r, g, b, a);
        Gdxg.glPrimitiveRenderer.vertex(x2, y2, z2);
    }

    public static void line(Vector3 start, Vector3 end,
                            float r, float g, float b, float a) {
        line(start.x, start.y, start.z,
                end.x, end.y, end.z,
                r, g, b, a);

    }

    public static void line(Vector3 start, Vector3 end, Color color) {
        line(start, end,
                color.r, color.g, color.b, color.a);
    }

    public static void box(BoundingBox2 boundingBox) {
        Vector3 corner0 = boundingBox.getCorner000();
        Vector3 corner1 = boundingBox.getCorner100();
        Vector3 corner2 = boundingBox.getCorner110();
        Vector3 corner3 = boundingBox.getCorner010();
        Vector3 corner4 = boundingBox.getCorner001();
        Vector3 corner5 = boundingBox.getCorner101();
        Vector3 corner6 = boundingBox.getCorner111();
        Vector3 corner7 = boundingBox.getCorner011();

        Drawer3d.line(corner0, corner4, Color.GREEN);
        Drawer3d.line(corner1, corner5, Color.GREEN);
        Drawer3d.line(corner2, corner6, Color.GREEN);
        Drawer3d.line(corner3, corner7, Color.GREEN);

        Drawer3d.line(corner0, corner3, Color.GREEN);
        Drawer3d.line(corner3, corner2, Color.GREEN);
        Drawer3d.line(corner2, corner1, Color.GREEN);
        Drawer3d.line(corner1, corner0, Color.GREEN);

        Drawer3d.line(corner4, corner7, Color.GREEN);
        Drawer3d.line(corner7, corner6, Color.GREEN);
        Drawer3d.line(corner6, corner5, Color.GREEN);
        Drawer3d.line(corner5, corner4, Color.GREEN);
    }

    public static void grid(int width, int height) {
        grid(0, 0, width, height);
    }

    /**
     * 2d grid
     */
    public static void grid(int startX, int startY, int width, int height) {
        for (int x = startX; x <= width + startX; x++) {
            // draw vertical
            Drawer3d.line(x, 0, -startY,
                    x, 0, -height + -startY,
                    0, 1, 0, 0);
        }

        for (int y = startY; y <= height + startY; y++) {
            // draw horizontal
            Drawer3d.line(startX, 0, -y,
                    width + startX, 0, -y,
                    0, 1, 0, 0);
        }
    }
}
