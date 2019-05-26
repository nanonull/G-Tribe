package conversion7.engine.custom2d.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.geometry.Drawer2d;

public abstract class AbstractTreeNode {

    public static final int BASE_SIZE = 42;
    private static final float MAGIC_NODES_LAYOUT_OFFSET = BASE_SIZE / 2 + 22;

    protected DefaultTable nodeTable = new DefaultTable();

    protected final int row;
    protected final int column;
    protected float layoutX;
    protected float layoutY;

    protected final ObjectSet<AbstractTreeNode> childNodes = new ObjectSet<>();
    protected final ObjectSet<AbstractTreeNode> parentNodes = new ObjectSet<>();

    protected long hierarchyDrawnOnFrame;
    protected long nodeStateUpdatedOnCounter;

    protected int columnWidth = (int) (BASE_SIZE * 3);
    protected int rowHeight = (int) (BASE_SIZE * 1.5f);

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

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public void layout(TreeWidget treeWidget) {
        nodeTable.setPosition(
                column * columnWidth,
                treeWidget.getHeight() - nodeTable.getPrefHeight()
                        - row * rowHeight);

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
