package conversion7.game.ui.quest;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import conversion7.engine.quest_old.AbstractQuest;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;
import org.testng.Assert;

@Deprecated
public class QuestWindow extends AnimatedWindow {

    private static float WINDOW_WIDTH = GdxgConstants.SCREEN_WIDTH_IN_PX;
    private static float PICTURE_VIEW_WIDTH = WINDOW_WIDTH * 0.4f;
    private static float WINDOW_WIDTH_WITHOUT_PICTURE_VIEW = WINDOW_WIDTH - PICTURE_VIEW_WIDTH;
    TableWithScrollPane descriptionTable;
    DefaultTable pictureTable;
    Cell<DefaultTable> pictureTableCell;

    QuestChoicesBox questChoicesBox;
    private AbstractQuest activeQuest;
    private boolean pictureViewEnabled = true;

    public QuestWindow(Stage stage, Skin skin) {
        super(stage, QuestWindow.class.getSimpleName(), skin, Direction.right);
        setSize(WINDOW_WIDTH, GdxgConstants.SCREEN_HEIGHT_IN_PX);

        DefaultTable textTable = new DefaultTable().applyDefaultPaddings();
        add(textTable).expand().fill();
        buildTextTable(textTable);

        pictureTable = new DefaultTable().applyDefaultPaddings();
        pictureTable.center();
        pictureTableCell = add(pictureTable).size(PICTURE_VIEW_WIDTH, getHeight());
    }

    public boolean isPictureViewEnabled() {
        return pictureViewEnabled;
    }

    public void setPictureViewEnabled(boolean enabled) {
        pictureViewEnabled = enabled;
    }

    private void buildTextTable(DefaultTable textTable) {
        descriptionTable = new TableWithScrollPane();
        descriptionTable.defaults().pad(ClientUi.SPACING);
        ScrollPane scroll1 = descriptionTable.getScrollPane();
        textTable.add(scroll1).expand().fill().pad(ClientUi.SPACING);
        scroll1.setFadeScrollBars(false);
        scroll1.setScrollingDisabled(false, false);

        //
        textTable.row();
        questChoicesBox = new QuestChoicesBox(getSkin());
        TableWithScrollPane defaultTableWithScrollPane2 = new TableWithScrollPane();
        defaultTableWithScrollPane2.add(questChoicesBox).expand().fill().pad(ClientUi.SPACING);

        ScrollPane scroll2 = defaultTableWithScrollPane2.getScrollPane();
        textTable.add(scroll2).expandX().fill().height(QuestChoicesBox.QUEST_CHOICES_BOX_HEIGHT).pad(ClientUi.SPACING);
        scroll2.setFadeScrollBars(false);
        scroll2.setScrollingDisabled(true, false);

        questChoicesBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (event instanceof QuestChoiceEvent) {
                    activeQuest.selected((QuestChoiceEvent) event);
                }
            }
        });
    }

    public void showFor(AbstractQuest quest) {
        activeQuest = quest;
        refreshActiveQuest();
//        pack();

        updateAnimationBounds();
        show();
    }

    private void validateWidth() {
        if (pictureViewEnabled) {
            setWidth(WINDOW_WIDTH);
            pictureTableCell.width(PICTURE_VIEW_WIDTH);

        } else {
            pictureTableCell.width(0);
            setWidth(WINDOW_WIDTH_WITHOUT_PICTURE_VIEW);
        }
    }

    public void refreshActiveQuest() {
        Assert.assertNotNull(activeQuest);
        validateWidth();
        refreshDescription(activeQuest.getDescriptionRows());
        refreshPicture(activeQuest.getPicture());
        questChoicesBox.refreshItems(activeQuest.getChoiceItems());
    }

    private void refreshPicture(Texture picture) {
        if (picture != null) {
            pictureTable.clear();
            Image image = new Image(picture);
            image.setScaling(Scaling.fit);
            pictureTable.add(image);
        }
    }

    private void refreshDescription(Array<String> strings) {
        descriptionTable.clear();
        for (String string : strings) {
            Label label = new Label(string, Assets.labelStyle14orange);
            label.setWrap(true);
            descriptionTable.add(label).width(WINDOW_WIDTH_WITHOUT_PICTURE_VIEW * 0.9f);
            descriptionTable.row();
        }

    }

    public void close() {
        activeQuest = null;
        hide();
    }
}
