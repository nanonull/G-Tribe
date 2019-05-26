package conversion7.engine.custom2d.tree;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class TreeWidget extends WidgetGroup {

    private Array<AbstractTreeNode> treeNodes;
    private Array<AbstractTreeNode> rootNodes;

    public TreeWidget(Array<AbstractTreeNode> treeNodes, Array<AbstractTreeNode> rootNodes) {
        this.treeNodes = treeNodes;
        this.rootNodes = rootNodes;
        for (AbstractTreeNode treeNode : treeNodes) {
            addActor(treeNode.getNodeTable());
        }
    }

    public Array<AbstractTreeNode> getRootNodes() {
        return rootNodes;
    }

    @Override
    public void layout() {
        for (AbstractTreeNode treeNode : treeNodes) {
            treeNode.layout(this);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (AbstractTreeNode rootNode : rootNodes) {
            rootNode.drawHierarchy(batch);
        }
        super.draw(batch, parentAlpha);
    }

}
