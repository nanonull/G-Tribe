package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.ui.hint.PopupHintPanel;

import static conversion7.game.Assets.*;

public class UnitParametersBasePanelType1 extends VBox {
    public static ProgressBar.ProgressBarStyle BAR_STYLE_YELLOW_PURPLE;
    public static ProgressBar.ProgressBarStyle BAR_STYLE_GREEN_RED;
    public static final int MAX_WIDTH = 300;
    public static final float SPACE = 2;
    protected Label teamLabel;
    protected Label strLabel;
    protected Label agiLabel;
    protected Label vitLabel;
    public UnitEffectsPanel unitEffectsPanel;

    public boolean showUnitLevel = false;
    public boolean showHitChance = false;

    private final UnitLevelPanel unitLevelPanel = new UnitLevelPanel();

    private final UnitDamagePanel unitDamagePanel = new UnitDamagePanel();

    private final UnitHitChancePanel unitHitChancePanel = new UnitHitChancePanel();

    public UnitPowerPanel unitPowerPanel = new UnitPowerPanel();
    private final UnitActionPointsFullPanel unitActionPointsFullPanel = new UnitActionPointsFullPanel();
    UnitExperiencePanel unitExperiencePanel;
    UnitAgePanel unitAgePanel;
    public UnitUltProgressBarPanel ultProgressBarPanel = new UnitUltProgressBarPanel();

    private final UnitStatusPanel unitStatusPanel = new UnitStatusPanel();

    protected boolean showExp;
    protected boolean showAge;
    protected boolean showSoul;
    protected boolean showBloodline;
    Label soulLbl;
    Label bloodlineLbl;
    private UnitManaPanel manaPanel = new UnitManaPanel();

    static {
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Assets.SOLAR_YELLOW, Assets.pixel);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Assets.PURPLE, Assets.pixel);
        UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE = new ProgressBar.ProgressBarStyle(backDrawable, knobDrawable);
        UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE.knob.setMinHeight(6);
        UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE.knobBefore = UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE.knob;
        UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE.background.setMinHeight(6);

        TextureRegionDrawable knobDrawable2 = new TextureRegionColoredDrawable(Assets.RED, Assets.pixel);
        TextureRegionDrawable backDrawable2 = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel);
        UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED = new ProgressBar.ProgressBarStyle(knobDrawable2, backDrawable2);
        UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED.knob.setMinHeight(6);
        UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED.knobBefore = UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED.knob;
        UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED.background.setMinHeight(6);
    }

    public UnitParametersBasePanelType1() {
        defaults().space(SPACE);
    }

    protected void init() {
        add(unitStatusPanel);

        teamLabel = new Label("", labelStyle14blackWithBackground);
        addLabel(teamLabel);

        if (showAge) {
            unitAgePanel = new UnitAgePanel();
            add(unitAgePanel);
        }

        if (showUnitLevel) {
            add(unitLevelPanel);
        }

        if (showExp) {
            unitExperiencePanel = new UnitExperiencePanel();
            add(unitExperiencePanel);
        }

        add(unitActionPointsFullPanel);
        add(ultProgressBarPanel);
        add(unitPowerPanel);

        add(unitDamagePanel);
        if (showHitChance) {
            add(unitHitChancePanel);
        }

//        add(manaPanel);

        strLabel = new Label("", Assets.labelStyle14orange);
//        addLabel(strLabel);
        PopupHintPanel.assignHintTo(strLabel, "Strength: <base>/<total>.");

        agiLabel = new Label("", Assets.labelStyle14orange);
//        addLabel(agiLabel);
        PopupHintPanel.assignHintTo(agiLabel, "Agility: <base>/<total>.");

        vitLabel = new Label("", Assets.labelStyle14orange);
//        addLabel(vitLabel);
        PopupHintPanel.assignHintTo(vitLabel, "Vitality: <base>/<total>.");

        if (showSoul) {
            soulLbl = addLabel("", Assets.labelStyle14orange).getActor();
        }

        if (showBloodline) {
            bloodlineLbl = addLabel("", Assets.labelStyle14orange).getActor();
        }

        unitEffectsPanel = new UnitEffectsPanel();
        add(unitEffectsPanel);
    }

    public void load(AbstractSquad squad) {
        unitStatusPanel.load(squad);
        teamLabel.setText(squad.getTeam().getName());
        strLabel.setText("STR: " + squad.getMainParams().get(UnitParameterType.STRENGTH)
                + "/" + squad.getTotalParam(UnitParameterType.STRENGTH));
        agiLabel.setText("AGI: " + squad.getMainParams().get(UnitParameterType.AGILITY)
                + "/" + squad.getTotalParam(UnitParameterType.AGILITY));
        vitLabel.setText("VIT: " + squad.getMainParams().get(UnitParameterType.VITALITY)
                + "/" + squad.getTotalParam(UnitParameterType.VITALITY));

        unitEffectsPanel.load(squad);
        unitPowerPanel.load(squad);
        unitActionPointsFullPanel.load(squad);
        ultProgressBarPanel.load(squad);
        if (showExp) {
            unitExperiencePanel.load(squad);
        }
        if (showAge) {
            unitAgePanel.load(squad);
        }
        if (showUnitLevel) {
            unitLevelPanel.load(squad);
        }

        unitDamagePanel.load(squad);

        if (showHitChance) {
            unitHitChancePanel.load(squad);
        }

        manaPanel.load(squad);
        if (showSoul) {
            soulLbl.clearListeners();
            soulLbl.setText(squad.soul.getDescription());
            PopupHintPanel.assignHintTo(soulLbl, squad.soul.getMoreDescription());
        }
        if (showBloodline) {
            bloodlineLbl.clearListeners();
            bloodlineLbl.setText("Race founders in bloodline: " + squad.getBloodlines().size);
            PopupHintPanel.assignHintTo(bloodlineLbl, squad.getBloodlineDescription());
        }
        pack();
        show();
    }
}
