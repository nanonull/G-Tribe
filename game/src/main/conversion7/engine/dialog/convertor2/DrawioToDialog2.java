package conversion7.engine.dialog.convertor2;

import conversion7.engine.dialog.DialogConfig;
import conversion7.engine.dialog.convertor.DrawioXmlParser;
import conversion7.engine.dialog.convertor.model.drawio.DrawioCell;
import conversion7.engine.dialog.convertor.model.drawio.DrawioSrc;
import conversion7.engine.dialog.convertor.utils.ConvUtils;
import conversion7.engine.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawioToDialog2 {

    static File convertorDir = new File(new File("").getAbsoluteFile(), "dialogs_convertor");
    private static final Logger LOG = Utils.getLoggerForClass();

    public static void main(String[] args) throws IOException {
        convert("dialog-wip.xml");
    }

    public static DialogDefinition convert(String xmlFileName) throws IOException {
        File xmlFile = new File(convertorDir, xmlFileName);
        DrawioSrc drawioSrc = loadFromXml(xmlFile);

        DialogDefinition dialogDefinition = new DialogDefinition();

        List<DrawioCell> arrows = new ArrayList<>();
        dialogDefinition.collectStatesAndArrows(drawioSrc, arrows);
        dialogDefinition.setTargets(drawioSrc, arrows);
        dialogDefinition.buildStates();
        LOG.info(dialogDefinition.dialogClassName);
        printWriteQuestSrcHistoryLog(dialogDefinition.dialogCodeName, getQuestSources(dialogDefinition));
        LOG.info("\n\n=============== \n" + dialogDefinition.dialogCodeName + " was generated!");
        LOG.info("\n\n=============== \nHow to use:");
        LOG.info("\n1) if first time dialog - just copy and impl closures" +
                "\n2) if update dialog - try full copy and return implemented closures from prev version");

        return dialogDefinition;
    }

    static void printWriteQuestSrcHistoryLog(String name, String text) throws IOException {
        File histiryFolder = new File(convertorDir, "history");
        histiryFolder.mkdirs();
        FileUtils.writeStringToFile(new File(histiryFolder
                , ConvUtils.getSafeFileName(name + '_' + Instant.now().toString().replaceAll("\\..*$", ""))), text);
    }

    static String getQuestSources(DialogDefinition dialog) {
        StringBuilder fullTextToConsoleBuilder = new StringBuilder();

        fullTextToConsoleBuilder.append("\n===== DIALOG CLASS:\n");
        fullTextToConsoleBuilder.append(dialog.dialogClassName).append("\n\n");

        StringBuilder classInnerCodeBuilder = new StringBuilder();
        classInnerCodeBuilder.append("    {\n // ===== STATES:");
        classInnerCodeBuilder.append("\n");
        for (int i = 0; i < dialog.states.size(); i++) {
            DialogState state = dialog.states.get(i);
            if (!state.initialState) {
                classInnerCodeBuilder.append(state.getStateIdFieldCode(i));
                classInnerCodeBuilder.append("\n");
            }
        }


        classInnerCodeBuilder.append("\n\n    /** ===== DIALOG STATES DEFINITION MAP ===== */\n");
        classInnerCodeBuilder.append("@Override\n" +
                "    protected Map<Object, List> getQuestStateMap() {\n" +
                "        return [");
        dialog.states.forEach(s -> {
            classInnerCodeBuilder.append(s.toString());
            classInnerCodeBuilder.append("\n");
        });
        classInnerCodeBuilder.append("]\n");
        classInnerCodeBuilder.append("}\n");

        printClosuresCode(classInnerCodeBuilder, dialog);
        classInnerCodeBuilder.append("}\n");
        copyToClipboard(classInnerCodeBuilder.toString());
        fullTextToConsoleBuilder.append(classInnerCodeBuilder.toString());

        fullTextToConsoleBuilder.append("\n\n ===== text.properties TEXT RESOURCES:\n\n");
        for (Map.Entry<String, String> entry : dialog.textResources.entrySet()) {
            fullTextToConsoleBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }

        String string = fullTextToConsoleBuilder.toString();
        LOG.info(string);
        return string;
    }

    private static void copyToClipboard(String myString) {
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    private static void printClosuresCode(StringBuilder builder, DialogDefinition dialogDefinition) {
        builder.append("\n\n    // ===== CLOSURES:\n");
        for (DialogState state : dialogDefinition.states) {
            state.calledClosures.forEach(dialogClosure -> {
                builder.append(dialogClosure.getReturnTypeName()).append(" ")
                        .append(dialogClosure.getName()).append("() {\n")
                        .append("        return ;\n")
                        .append("    }\n");
            });
        }

        builder.append("\n");
    }


    public static DrawioSrc loadFromXml(File xmlFile) throws IOException {
        String xml = DrawioXmlParser.read(xmlFile);
        JSONObject jSONObject = XML.toJSONObject(xml);
        String json = jSONObject.toString();
        FileUtils.writeStringToFile(new File(xmlFile.getPath() + ".json"), json);
        DrawioSrc drawioSrc = DialogConfig.GSON.fromJson(json, DrawioSrc.class);
        prepareGraph(drawioSrc);
        return drawioSrc;
    }

    static void prepareGraph(DrawioSrc drawioSrc) {
        drawioSrc.mxGraphModel.root.drawioCells.forEach((cell) -> {


            if (cell.value != null)
                cell.value = ConvUtils.removeHtml(cell.value);
        });
    }
}
