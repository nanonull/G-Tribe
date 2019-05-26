package conversion7.engine.validators;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.tree_structure.TreeNode;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public abstract class NodeValidator {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int MAX_NODE_VALIDATIONS_PER_TREE_VALIDATION = 2;
    private static final int MAX_TREE_VALIDATIONS = 10;

    private boolean valid = true;
    protected Array<NodeValidator> childValidators = new Array<>();
    protected Array<NodeAppendedValidator> appendedValidators = new Array<>();
    private boolean invalidationMet;
    private boolean treeValidationInProgress;
    private boolean treeValidationEnabled = true;
    private boolean validationEnabled = true;
    private NodeValidator parent;

    public NodeValidator() {
        this(true);
    }

    public NodeValidator(boolean valid) {
        this.valid = valid;
        init();
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    /** They will be validated when runTreeValidation walks through this object */
    public Array<NodeValidator> getChildValidators() {
        return childValidators;
    }

    /** They will be validated when runTreeValidation walks through this object AND object is invalidated */
    public Array<NodeAppendedValidator> getAppendedValidators() {
        return appendedValidators;
    }

    public boolean isValid() {
        return valid;
    }

    public void setTreeValidationEnabled(boolean treeValidationEnabled) {
        this.treeValidationEnabled = treeValidationEnabled;
    }

    private void setInvalidationMet(boolean invalidationMet) {
        this.invalidationMet = invalidationMet;
    }

    protected void init() {

    }

    public void runTreeValidation() {
        if (!treeValidationEnabled || treeValidationInProgress) {
            return;
        }
        treeValidationInProgress = true;
        int validationIndex = 0;
        do {
            invalidationMet = false;
            innerValidationCycle(this);
            validationIndex++;
            if (validationIndex == MAX_TREE_VALIDATIONS) {
                LOG.error("Tree still invalidated after " + validationIndex + " validation attempts!");
            }
        } while (invalidationMet);
        treeValidationInProgress = false;
    }

    protected void innerValidationCycle(NodeValidator root) {
        for (int i = 0; validationEnabled && !valid && i < MAX_NODE_VALIDATIONS_PER_TREE_VALIDATION; i++) {
            root.setInvalidationMet(true);
            valid = true;
            validate();
            for (NodeAppendedValidator appendedValidator : appendedValidators) {
                appendedValidator.validation();
            }
        }
        if (!valid) {
//            LOG.warn("Still invalidated: {}", getClass().getSimpleName());
//            throw new NodeValidationException("Self invalidate!");
            return;
        }
        for (NodeValidator validator : childValidators) {
            validator.innerValidationCycle(root);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " valid " + valid;
    }

    public abstract void validate();

    public NodeValidator invalidate() {
        valid = false;
        return this;
    }

    public void refresh() {
        invalidate();
        validate();
    }

    public void registerChildValidator(NodeValidator childValidator) {
        if (childValidators.contains(childValidator, true)) {
            throw new RuntimeException("ChildValidator already exist: " + childValidator);
        }
        childValidators.add(childValidator);
        childValidator.parent = this;
    }

    public void registerAppendedValidator(NodeAppendedValidator validator) {
        if (appendedValidators.contains(validator, true)) {
            throw new RuntimeException("AppendedValidator already exist: " + validator);
        }
        appendedValidators.add(validator);
    }

    public void printTree() {
        TreeNode<String> myNode = new TreeNode<>(this.toString());
        collectTree(myNode);
        LOG.info("\n\nValidators Tree: ");
        for (TreeNode<String> stringTreeNode : myNode.getElementsIndex()) {
            LOG.info(stringTreeNode.getData());
        }
    }

    private void collectTree(TreeNode<String> myNode) {
        for (NodeValidator childValidator : childValidators) {
            TreeNode<String> stringTreeNode = myNode.addChild(childValidator.toString());
            childValidator.collectTree(stringTreeNode);
        }
    }

    public void disableChildRecursive() {
        setValidationEnabled(false);
        for (NodeValidator childValidator : childValidators) {
            childValidator.disableChildRecursive();
        }
    }

    public void invalidateChildRecursive() {
        invalidate();
        for (NodeValidator childValidator : childValidators) {
            childValidator.invalidateChildRecursive();
        }
    }

    public void forceTreeValidationFromRootNode() {
        if (parent == null) {
            forceTreeValidationFromThisNode();
        } else {
            parent.forceTreeValidationFromRootNode();
        }
    }

    public void forceTreeValidationFromThisNode() {
        invalidateChildRecursive();
        runTreeValidation();
    }
}
