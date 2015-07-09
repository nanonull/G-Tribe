package conversion7.engine.custom2d.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.geometry.Drawer2d;
import conversion7.game.ui.world.team_classes.UnitClassTreeNode;

public abstract class AbstractTreeNode {

    public static final int BUTTON_SIZE = 40;
    private static final float MAGIC_NODES_LAYOUT_OFFSET = BUTTON_SIZE / 2 + 22;

    protected Table nodeTable = new DefaultTable();

    protected final int row;
    protected final int column;
    protected float layoutX;
    protected float layoutY;

    protected final ObjectSet<AbstractTreeNode> childNodes = new ObjectSet<>();
    protected final ObjectSet<AbstractTreeNode> parentNodes = new ObjectSet<>();

    protected long hierarchyDrawnOnFrame;
    protected long nodeStateUpdatedOnCounter;

    public AbstractTreeNode(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public ObjectSet<AbstractTreeNode> getChildNodes() {
        return childNodes;
    }

    public ObjectSet<AbstractTreeNode> getParentNodes() {
        return parentNodes;
    }

    public Table getNodeTable() {
        return nodeTable;
    }

    public void layout(TreeWidget treeWidget) {
        nodeTable.setPosition(
                column * UnitClassTreeNode.BUTTON_WIDTH_WITH_PADDING,
                treeWidget.getHeight() - nodeTable.getPrefHeight()
                        - row * UnitClassTreeNode.BUTTON_HEIGHT_WITH_PADDING);

        layoutX = nodeTable.getX() + MAGIC_NODES_LAYOUT_OFFSET;
        layoutY = nodeTable.getY();
    }

    public void drawHierarchy(Batch batch) {
        if (Gdx.graphics.getFrameId() == hierarchyDrawnOnFrame) {
            return;
        }
        hierarchyDrawnOnFrame = Gdx.graphics.getFrameId();

        for (AbstractTreeNode childNode : childNodes) {
            Drawer2d.drawLine(layoutX, layoutY,
                    childNode.layoutX, childNode.layoutY,
                    4, Color.LIGHT_GRAY, batch);
            childNode.drawHierarchy(batch);
        }
    }

    public void addChildNode(AbstractTreeNode abstractTreeNode) {
        childNodes.add(abstractTreeNode);
        abstractTreeNode.addParentNode(this);
    }

    private void addParentNode(AbstractTreeNode abstractTreeNode) {
        parentNodes.add(abstractTreeNode);
    }
}
