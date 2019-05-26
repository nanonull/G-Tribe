package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.ui.world.main_panel.unit.UnitParametersBasePanelType1;

public class UnitIconWithInfoPanel extends WidgetGroup {
    public static final int ICON_SIZE = 46;
    public static final float SPACE_BASE = 2;
    public static final float DOUBLE_SPACE = SPACE_BASE * 2;

    public static final Label.LabelStyle classNameLblStyle = new Label.LabelStyle(Assets.labelStyle12_i_whiteAndLittleGreen);
    public static Label.LabelStyle maturityStyle = Assets.labelStyle14white;
    public static final Color BASE_BORDER_COLOR = Assets.BORDER_COLOR_2;
    public static final Color LIGHT_PANEL_COLOR = UiUtils.alpha(0.4f, Color.WHITE);
    public static final Color MATURITY_LVL_LABEL_PANEL_COLOR = LIGHT_PANEL_COLOR;
    public static ProgressBar.ProgressBarStyle HP_BAR_STYLE;
    public static final float MAGIC_LABEL_TOP_PAD = -4f;
    public static final float HP_BAR_HEIGHT = 10;
    private static final float BORDER_SIZE = UnitInWorldHintPanel.BORDER_SIZE + 1;
    public static final float BORDER_SIZE_X2 = BORDER_SIZE * 2;
    private static final Label.LabelStyle ARMOR_STYLE;
    private Unit unit;
    Table icon;
    Panel maturityLvlLabelPanel = new HBox();
    VBox infoLayer;
    Label maturityLvlLabel = new Label("", maturityStyle);
    ActionPointsIndicatorsPanel actionPointsIndicatorsPanel;
    Label classNameLbl;

    static {
        classNameLblStyle.background = new TextureRegionColoredDrawable(
                BASE_BORDER_COLOR, Assets.pixel);
    }

    static {
        ARMOR_STYLE = new Label.LabelStyle(Assets.font12_italic, Color.YELLOW);
        ARMOR_STYLE.background = new TextureRegionColoredDrawable(
                Assets.RED, Assets.pixel);
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Color.RED, Assets.pixel);


        TextureRegionDrawable knobDrawable2 = new TextureRegionColoredDrawable(Assets.RED, Assets.pixel);
        TextureRegionDrawable backDrawable2 = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel);
        HP_BAR_STYLE = new ProgressBar.ProgressBarStyle(knobDrawable2, backDrawable2);
        HP_BAR_STYLE.knob.setMinHeight(HP_BAR_HEIGHT);
        HP_BAR_STYLE.knobBefore = HP_BAR_STYLE.knob;
        HP_BAR_STYLE.background.setMinHeight(HP_BAR_HEIGHT);

    }

    public UnitIconWithInfoPanel(Unit unit) {
        this.unit = unit;

        icon = UiUtils.addBorderAroundActor(unit.squad.getClassIconImage(), BASE_BORDER_COLOR,
                BORDER_SIZE).getTable();
        addActor(icon);

        infoLayer = new VBox();
        addActor(infoLayer);
        infoLayer.pad(0);

//        infoLayer.add(maturityLvlLabelPanel).expandX().left().height(9)
//                .space(1);
//        maturityLvlLabelPanel.setBackground(new TextureRegionColoredDrawable(MATURITY_LVL_LABEL_PANEL_COLOR, Assets.pixel));
//        maturityLvlLabelPanel.pad(0).padRight(1);

//        maturityLvlLabelPanel.add(maturityLvlLabel).left().expandX()
//                .padTop(MAGIC_LABEL_TOP_PAD);
//        maturityLvlLabel.setAlignment(Align.left);
//        maturityLvlLabel.setColor(Color.BLACK);

        classNameLbl = infoLayer.addLabel(unit.getGameClassShortName(), classNameLblStyle)
                .pad(1).padRight(0).right().top()
                .getActor();
        classNameLbl.setColor(Color.WHITE);

        infoLayer.add().grow();

        actionPointsIndicatorsPanel = new ActionPointsIndicatorsPanel();
        infoLayer.add(actionPointsIndicatorsPanel)
                .padBottom(2).center().bottom();

        invalidateHierarchy();
    }

    public void setBorderColor(Color borderColor) {
        TextureRegionColoredDrawable background = (TextureRegionColoredDrawable) icon.getBackground();
        if (borderColor == null) {
            background.setColor(BASE_BORDER_COLOR);
        } else {
            background.setColor(borderColor);
        }
    }

    @Override
    public void layout() {
        float parentWidth = getWidth();
        float parentHeight = getHeight();

        icon.setWidth(parentWidth);
        icon.setHeight(parentHeight);

        infoLayer.setPosition(BORDER_SIZE, BORDER_SIZE);
        infoLayer.setWidth(parentWidth - BORDER_SIZE_X2);
        infoLayer.setHeight(parentHeight - BORDER_SIZE_X2);
    }

    public void updateAgeLevel() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= unit.squad.getAge().getLevel() && i <= UnitAge.OLD.getLevel(); i++) {
            builder.append("*");
        }
        maturityLvlLabel.setText(builder.toString());
        updateMaturityLevelColor();
        maturityLvlLabelPanel.invalidateHierarchy();
    }

    public void updateMaturityLevelColor() {
        if (unit.squad.willDieOfAge) {
            ((TextureRegionColoredDrawable) maturityLvlLabelPanel.getBackground()).setColor(Assets.RED);
        }
    }

    public void updateAP() {
        actionPointsIndicatorsPanel.updateAP(unit.squad.getMoveAp(), unit.squad.getAttackAp());
    }
}
