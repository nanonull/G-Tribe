package conversion7.engine.dialog.convertor2;

import conversion7.engine.dialog.convertor.utils.ConvUtils;

public class DialogClosure {
    private String name;
    private String returnTypeName;

    public DialogClosure(Class returnType, String name) {
        this.returnTypeName = returnType == null ? "void" : returnType.getSimpleName();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getReturnTypeName() {
        return returnTypeName;
    }

    @Override
    public String toString() {
        return getName();
    }

    public DialogClosure toCamel() {
        this.name = ConvUtils.toCamelCase(name);
        return this;
    }
}
