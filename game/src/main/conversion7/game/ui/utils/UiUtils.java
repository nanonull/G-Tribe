package conversion7.game.ui.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;

import static java.lang.String.valueOf;

public class UiUtils {

    public static String getNumberCode(int original) {
        int abs = Math.abs(original);
        String resCode;
        if (abs >= 1000000000) {
            resCode = ">G";
        } else if (abs >= 1000000) {
            resCode = abs / 1000000 + "M";

        } else if (abs >= 1000) {
            StringBuilder sb = new StringBuilder();
            if (abs < 10000) {
                // 9876 to 9.87K
                sb.append((valueOf(abs)).substring(0, 1))
                        .append(".")
                        .append((valueOf(abs)).substring(1, 3))
                        .append("K");
            } else if (abs < 100000) {
                // 98765 to 98.7K
                sb.append((valueOf(abs)).substring(0, 2))
                        .append(".")
                        .append(("" + abs).substring(2, 3))
                        .append("K");
            } else {
                // 987654 to 987K
                sb.append((valueOf(abs)).substring(0, 3))
                        .append("K");
            }
            resCode = sb.toString();

        } else {
            resCode = valueOf(abs);
        }

        if (original < 0) {
            resCode = "-" + resCode;
        }
        return resCode;
    }

    /** Adds + for positive numbers */
    public static String getNumberWithSign(int i) {
        return i > 0 ? "+" + i : valueOf(i);
    }

    public static String fancyCamelCase(String camelText) {
        return camelText;
    }

    public static void keepWithinStage(Actor actor, boolean positioningTopFromCursor) {
        Stage stage = actor.getStage();
        if (actor.getParent() == stage.getRoot()) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (!positioningTopFromCursor) {
                actor.setY(actor.getY() - actor.getHeight());
            }
            if (actor.getX() < 0) actor.setX(0);
            if (actor.getY() < 0) actor.setY(0);
            if (actor.getRight() > parentWidth) actor.setX(actor.getX() - actor.getWidth());
            if (actor.getTop() > parentHeight) actor.setY(actor.getY() - actor.getHeight());
        }
    }

    /**
     * Can be used to create borders around solid actors.<br>
     * To get parent panel use Cell#getTable().<br>
     * To add background to label better use: labelStyle.background = new TextureRegionColoredDrawable...
     */
    public static Cell<Actor> addBorderAroundActor(Actor actor, Color color, float borderSize) {
        Panel panel = new Panel();
        Cell<Actor> actorCellInsidePanel = panel.add(actor)
                .expand().fill().center().pad(borderSize);
        if (color != null) {
            panel.setBackground(new TextureRegionColoredDrawable(color, Assets.pixel));
        }
        return actorCellInsidePanel;
    }

    public static float getFontHeight(BitmapFont font) {
        return font.getData().capHeight + 1;
    }

    public static Color alpha(float a, Color color, boolean createCopy) {
        if (createCopy) {
            return new Color(color.r, color.g, color.b, a);
        } else {
            color.a = a;
            return color;
        }
    }

    public static Color alpha(float a, Color color) {
        return new Color(color.r, color.g, color.b, a);
    }
}
