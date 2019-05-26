package conversion7.engine.dialog.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.TableWithScrollPane;
import conversion7.engine.dialog.AbstractDialog;
import conversion7.engine.dialog.DialogConfig;
import conversion7.engine.dialog.QuestTextPhrase;
import org.testng.Assert;

public class DialogWindow extends AnimatedWindow {

    TableWithScrollPane descriptionTable;
    DefaultTable pictureTable;
    Cell<DefaultTable> pictureTableCell;

    QuestChoicesBox questChoicesBox;
    private AbstractDialog activeDialog;
    private boolean pictureViewEnabled = false;

    public DialogWindow(Stage stage, Skin skin) {
        super(stage, DialogWindow.class.getSimpleName(), skin, Direction.right);
        setSize(DialogConfig.WINDOW_WIDTH, DialogConfig.WINDOW_HEIGHT);

        DefaultTable textTable = new DefaultTable().applyDefaultPaddings();
        add(textTable).expand().fill();
        buildTextTable(textTable);

        pictureTable = new DefaultTable().applyDefaultPaddings();
        pictureTable.center();
        pictureTableCell = add(pictureTable).size(DialogConfig.PICTURE_VIEW_WIDTH, getHeight());

        addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F4) {
                    activeDialog.complete();
                    return true;
                }
                return super.keyUp(event, keycode);
            }
        });
    }

    public boolean isPictureViewEnabled() {
        return pictureViewEnabled;
    }

    public void setPictureViewEnabled(boolean enabled) {
        pictureViewEnabled = enabled;
    }

    private void buildTextTable(DefaultTable textTable) {
        descriptionTable = new TableWithScrollPane();
        descriptionTable.defaults().pad(DialogConfig.DEFAULT_PAD);
        ScrollPane scroll1 = descriptionTable.getScrollPane();
        textTable.add(scroll1).expand().fill().pad(DialogConfig.DEFAULT_PAD);
        scroll1.setFadeScrollBars(false);
        scroll1.setScrollingDisabled(true, false);

        //
        textTable.row();
        questChoicesBox = new QuestChoicesBox(getSkin());
        TableWithScrollPane defaultTableWithScrollPane2 = new TableWithScrollPane();
        defaultTableWithScrollPane2.add(questChoicesBox).expand().fill().pad(DialogConfig.DEFAULT_PAD);

        ScrollPane scroll2 = defaultTableWithScrollPane2.getScrollPane();
        textTable.add(scroll2).expandX().fill().height(QuestChoicesBox.QUEST_CHOICES_BOX_HEIGHT).pad(DialogConfig.DEFAULT_PAD);
        scroll2.setFadeScrollBars(false);
        scroll2.setScrollingDisabled(true, false);

        questChoicesBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (event instanceof QuestChoiceEvent) {
                    activeDialog.selected((QuestChoiceEvent) event);
                }
            }
        });
    }

    public void showFor(AbstractDialog quest) {
        activeDialog = quest;
        reload();

        updateAnimationBounds();
        show();
    }

    public void validateWidth() {
        if (pictureViewEnabled) {
            setWidth(DialogConfig.WINDOW_WIDTH);
            pictureTableCell.width(DialogConfig.PICTURE_VIEW_WIDTH);

        } else {
            pictureTableCell.width(0);
            setWidth(DialogConfig.WINDOW_WIDTH_WITHOUT_PICTURE_VIEW);
        }
    }

    public void append() {
        refreshDescription(activeDialog.getDescriptionRows());
    }

    public void reload() {
        Assert.assertNotNull(activeDialog);
        validateWidth();
        refreshDescription(activeDialog.getDescriptionRows());
        questChoicesBox.refreshItems(activeDialog.getChoiceItems());
        refreshPicture(activeDialog.getPicture());
    }

    private void refreshPicture(Texture picture) {
        if (picture != null) {
            pictureTable.clear();
            Image image = new Image(picture);
            image.setScaling(Scaling.fit);
            pictureTable.add(image);
        }
    }

    private void refreshDescription(Array<QuestTextPhrase> phrases) {
        for (QuestTextPhrase phrase : phrases) {
            Label label = new Label(phrase.getText(), phrase.getLabelStyle());
            label.setWrap(true);
            descriptionTable.add(label).width(DialogConfig.LABEL_WIDTH);
            descriptionTable.row(); // required for correct layout?..
            descriptionTable.add(); // required for correct layout?..
            descriptionTable.row();
        }

        if (descriptionTable.getChildren().size > 0) {
            descriptionTable.row().height(15);
            descriptionTable.add();
            descriptionTable.row();
            descriptionTable.layout(); // required for correct layout?..
            descriptionTable.layout(); // required for correct layout?..
            descriptionTable.getScrollPane().layout(); // required for correct layout?..
            descriptionTable.getScrollPane().setScrollPercentY(100);

        }
    }

    public void close() {
        activeDialog = null;
        hide();
    }
}
