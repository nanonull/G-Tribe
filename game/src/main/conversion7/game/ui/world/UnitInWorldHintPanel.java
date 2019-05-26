package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;
import conversion7.game.ui.ClientUi;

public class UnitInWorldHintPanel extends VBox {

    public static final float FIX_LABELS_PADDING = -2;
    public static final float SPACE_BASE = 2;
    public static final float BORDER_SIZE = 2;
    @Deprecated
    public static Label.LabelStyle labelStyle;
    public static final int FRONT_Z_INDEX = ClientUi.Z_INDEX_CELL_INDICATOR - 10;
    public static final int BACK_Z_INDEX = FRONT_Z_INDEX - 10;
    protected static final Color PANEL_COLOR = Assets.PANEL_COLOR_DARK_A;
    public static final Label.LabelStyle hpLblStyle;
    public static final Label.LabelStyle defLblStyle;
    private static final float BORDER_SIZE_X2 = BORDER_SIZE * 2;
    private Label powerLabel;
    private AbstractSquad squad;
    VBox detailsPanelRoot = new VBox() {
        @Override
        public void layout() {
            super.layout();
        }
    };
    private UnitIconWithInfoPanel unitIconWithInfoPanel;
    @Deprecated
    Panel levelLabelPanel = new HBox();
    Panel powerLabelPanel = new HBox();
    @Deprecated
    Label levelLabel = new Label("", UnitInWorldHintPanel.labelStyle);
    private Color teamColor;
    private UnitInWorldIndicatorIconsPanel unitIndicatorIconsPanel = new UnitInWorldIndicatorIconsPanel();
    Image heroIcon;
    Label defLbl;
    ProgressBar hpBar = new ProgressBar(0, 100, 1, false, UnitIconWithInfoPanel.HP_BAR_STYLE);
    Cell<ProgressBar> hpBarCell;
    private HealthWidgetBar healthWidgetBar;

    static {
        hpLblStyle = new Label.LabelStyle(Assets.labelStyle12_i_whiteAndLittleGreen);
        hpLblStyle.fontColor = Color.BLACK;
//        hpLblStyle.background = new TextureRegionColoredDrawable(
//                PANEL_COLOR, Assets.pixel);

        defLblStyle = new Label.LabelStyle(Assets.labelStyle12_i_whiteAndLittleGreen);
        defLblStyle.fontColor = Color.BLACK;
        defLblStyle.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixel);

        labelStyle = new Label.LabelStyle(Assets.labelStyle12_i_whiteAndLittleGreen);
        labelStyle.fontColor = Color.ORANGE;
    }

    public UnitInWorldHintPanel(AbstractSquad squad) {
        this.squad = squad;
        setVisible(false);

        setBackground(new TextureRegionColoredDrawable(Color.GRAY, Assets.pixel));
        pad(SPACE_BASE);
        padLeft(SPACE_BASE * 2);
        padRight(SPACE_BASE * 2);

        detailsPanelRoot.pad(0);

        unitIconWithInfoPanel = new UnitIconWithInfoPanel(squad.unit);
        detailsPanelRoot.add(unitIconWithInfoPanel)
                .size(UnitIconWithInfoPanel.ICON_SIZE, UnitIconWithInfoPanel.ICON_SIZE)
                .grow().center();
        detailsPanelRoot.add().height(1);

        healthWidgetBar = new HealthWidgetBar();
        detailsPanelRoot.add(healthWidgetBar).grow();

        // hack for hp bar pad
        detailsPanelRoot.addLabel(" ", HealthWidgetBar.POWER_LABEL_STYLE);

        detailsPanelRoot.add().height(1);

        detailsPanelRoot.add(unitIndicatorIconsPanel).grow()
                .pad(SPACE_BASE);

        showShortDetails();
    }

    public Label getPowerLabel() {
        return powerLabel;
    }

    public Color getTeamColor() {
        return teamColor == null ? Color.ORANGE : teamColor;
    }

    public UnitInWorldIndicatorIconsPanel getUnitIndicatorIconsPanel() {
        return unitIndicatorIconsPanel;
    }

    public UnitIconWithInfoPanel getUnitIconWithInfoPanel() {
        return unitIconWithInfoPanel;
    }

    public HealthWidgetBar getHealthWidgetBar() {
        return healthWidgetBar;
    }

    public void setPanelColor(Color teamColor) {
        this.teamColor = teamColor;
        ((TextureRegionColoredDrawable) getBackground()).setColor(teamColor);
    }

    public void setHeroIndicator(HeroClass heroClass) {
        unitIndicatorIconsPanel.setHeroIndicator(heroClass);
        if (heroClass == null) {
            if (heroIcon != null) {
                removeActor(heroIcon);
            }
        } else {
            if (heroIcon == null) {
                heroIcon = new Image(Assets.getTextureReg(squad.getHeroClass().getIconName()));
                add(heroIcon).size(24).center();
            }
        }
    }

    public void showShortDetails() {
        clearChildren();
        add(detailsPanelRoot).expand().fill();
        setZIndex(BACK_Z_INDEX);
        invalidate();
        pack();
    }

    public void updateAgeAndLvl() {
        updateLevel();
        unitIconWithInfoPanel.updateAgeLevel();
    }

    public void updateLevel() {
        levelLabel.setText(String.valueOf(squad.getExpLevelUi()));
        levelLabelPanel.invalidateHierarchy();
    }

    @Override
    public void layout() {
        super.layout();
        pack();
    }


}
