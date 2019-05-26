package conversion7.game.ui.world.inventory.panels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitEquipment;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.world.inventory.InventoryWindow;

public class EquipmentPanel extends Panel {

    Label meleeWeapLbl = new Label("Melee weapon", Assets.labelStyle14orange);
    Label rangeWeaponLbl = new Label("Range weapon", Assets.labelStyle14orange);
    Label rangeBullLbl = new Label("..bullet", Assets.labelStyle14orange);
    Label clothesLbl = new Label("Clothes", Assets.labelStyle14orange);
    private InventoryWindow inventoryWindow;

    public EquipmentPanel(InventoryWindow inventoryWindow) {
        this.inventoryWindow = inventoryWindow;
    }

    public void load(AbstractSquad squad) {
        clear();
        defaults().height(ClientUi.EXPANDED_BUTTON_HEIGHT);

        Label.LabelStyle valueStyle = Assets.labelStyle14_lightGreen;
        UnitEquipment equipment = squad.getEquipment();

        add(meleeWeapLbl);
        if (equipment.getMeleeWeaponItem() == null) {
            add(new Label("-", valueStyle));
        } else {
            TextButton button = new TextButton(equipment.getMeleeWeaponItem().getName(), Assets.uiSkin);
            add(button);
            PopupHintPanel.assignHintTo(button, equipment.getMeleeWeaponItem().getDescription());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    equipment.dropMeleeWeapon();
                    inventoryWindow.refresh(squad);
                }
            });
        }
        row();

        add(rangeWeaponLbl);
        if (equipment.getRangeWeaponItem() == null) {
            add(new Label("-", valueStyle));
        } else {
            TextButton button = new TextButton(equipment.getRangeWeaponItem().getName(), Assets.uiSkin);
            add(button);
            PopupHintPanel.assignHintTo(button, equipment.getRangeWeaponItem().getDescription());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    equipment.dropRangeWeapon();
                    inventoryWindow.refresh(squad);
                }
            });

            row();
            add(rangeBullLbl);
            add(new Label(equipment.getBulletCostAndActualAmount(), valueStyle));
        }
        row();

        add(clothesLbl);
        if (equipment.getClothesItem() == null) {
            add(new Label("-", valueStyle));
        } else {
            TextButton button = new TextButton(equipment.getClothesItem().getName(), Assets.uiSkin);
            add(button);
            PopupHintPanel.assignHintTo(button, equipment.getClothesItem().getDescription());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    equipment.dropClothes();
                    inventoryWindow.refresh(squad);
                }
            });
        }
        row();

        pack();
    }
}
