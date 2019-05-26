package conversion7.engine.dialog.convertor2;

import conversion7.engine.dialog.convertor.Translit;
import conversion7.engine.dialog.convertor.model.drawio.DrawioCell;
import conversion7.engine.dialog.convertor.utils.ConvUtils;
import org.apache.commons.lang3.StringUtils;
import org.fest.assertions.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DialogState {

    public static final String STATE_BODY_SEPARATOR = "@";
    public static final String CLOSURE_OPEN_TAG = "${";
    public static final String CLOSURE_OPEN_TAG_SAFE_FOR_INPUT = "\\${";
    public static final String CLOSURE_OPEN_TAG_ESCAPED = Pattern.quote(CLOSURE_OPEN_TAG);
    public static final String CLOSURE_CLOSE_TAG = "}";
    public static final String CLOSURE_CLOSE_TAG_ESCAPED = Pattern.quote(CLOSURE_CLOSE_TAG);

    private String nameText;
    private String nameCode;
    StringBuilder scriptCodeBuilder;
    List<DialogClosure> calledClosures = new ArrayList<>();
    private DrawioCell cell;
    private String stateBodyText;
    private List<DialogState> targetStates = new ArrayList<>();
    public boolean initialState;
    private String stateNameCode;
    private DialogDefinition dialog;
    /**Comes from @if in diagram*/
    private String transitionConditionClosureName;
    /** For custom code for state */
    private String stateClosureName;
    private String DBL_SPACE_FOR_LIBGDX_LBL = " ";

    public DialogState(String stateBodyText, DrawioCell cell) {
        this.stateBodyText = stateBodyText;
        this.cell = cell;
    }

    public DrawioCell getCell() {
        return cell;
    }

    public String getNameText() {
        return nameText;
    }

    public String getNameCode() {
        return nameCode;
    }

    public String getScriptCode() {
        return scriptCodeBuilder.toString();
    }

    public void setDialog(DialogDefinition dialog) {
        this.dialog = dialog;
    }

    public void preBuild() {
        String[] bodySplit = stateBodyText.split(STATE_BODY_SEPARATOR);
        for (int i = 0; i < bodySplit.length - 1; i++) {
            bodySplit[i] = bodySplit[i].trim();
        }
        nameText = bodySplit[0];
        nameText = ConvUtils.removeHtml(nameText);
        nameText = Translit.toTranslit(nameText);

        nameCode = ConvUtils.getConstName(nameText);
        stateNameCode = nameCode + "_STATE";
        stateClosureName = nameCode + "_STATE_CLOSURE";

        scriptCodeBuilder = new StringBuilder();
        scriptCodeBuilder.append("(").append(initialState ? "INIT_STATE" : stateNameCode).append("):\n")
                .append(" [\n")
                .append(" {\n");
        for (int i = 1; i < bodySplit.length; i++) {
            parseScriptChunk(bodySplit[i]);
        }
        addStateClosureCall(scriptCodeBuilder);
    }

    public void postBuild() {
        for (DialogState targetState : targetStates) {
            dialog.textResources.put(targetState.nameCode, targetState.getNameText());
            String transitionCallText = "option(Assets.textResources.get(\"" + targetState.nameCode + "\"), {\n"
                    + "newState(" + targetState.stateNameCode + ")\n})\n";
            String conditionClosureName = targetState.transitionConditionClosureName;
            if (conditionClosureName == null) {
                conditionClosureName = "can" + targetState.stateClosureName;
            }
            // wrap option call in default 'if'
            scriptCodeBuilder.append("if (").append(conditionClosureName).append("()){\n")
                    .append(transitionCallText).append("}\n");
            calledClosures.add(new DialogClosure(Boolean.class, conditionClosureName).toCamel());

        }
        scriptCodeBuilder.append(" }\n" +
                "],\n");
    }

    @Override
    public String toString() {
        return scriptCodeBuilder.toString();
    }

    private void parseScriptChunk(String scriptChunk) {
        if (scriptChunk.startsWith("speakOff")) {
            scriptCodeBuilder.append("disableSpeaker()").append("\n");

        } else if (scriptChunk.startsWith("if")) {
            transitionConditionClosureName = scriptChunk.replaceFirst("if[\n]?", "");
            transitionConditionClosureName = ConvUtils.toCamelCase(transitionConditionClosureName);

        } else if (scriptChunk.startsWith("message")) {
            String messageInput = scriptChunk.replaceFirst("message[\n]?", "");
            String msgWip = messageInput;

            // find closures
            while (true) {
                String closureName = StringUtils.substringBetween(msgWip, CLOSURE_OPEN_TAG, CLOSURE_CLOSE_TAG);
                if (closureName == null) {
                    break;
                }
                String closureNameCamel = ConvUtils.toCamelCase(closureName);
                Assertions.assertThat(closureNameCamel).as("Possibly wrong closure definition - only single var closure supported!").isEqualTo(closureName);
                DialogClosure dialogClosure = new DialogClosure(String.class, closureNameCamel).toCamel();
                calledClosures.add(dialogClosure);
                msgWip = msgWip.replaceFirst(CLOSURE_OPEN_TAG_ESCAPED + dialogClosure.getName() + CLOSURE_CLOSE_TAG_ESCAPED, "~");
            }

            String msgWithClosures = messageInput;
            for (DialogClosure closure : calledClosures) {
//                msgWithClosures = msgWithClosures.replaceFirst(closure + CLOSURE_CLOSE_TAG_ESCAPED,
//                        closure.getName() + "()" + CLOSURE_CLOSE_TAG);

                msgWithClosures = msgWithClosures.replaceFirst(
                        CLOSURE_OPEN_TAG_ESCAPED + closure.getName() + CLOSURE_CLOSE_TAG_ESCAPED,
                        DBL_SPACE_FOR_LIBGDX_LBL + CLOSURE_OPEN_TAG_SAFE_FOR_INPUT
                                + closure.getName() + "()"
                                + CLOSURE_CLOSE_TAG + DBL_SPACE_FOR_LIBGDX_LBL);
            }

            scriptCodeBuilder.append("text(\"").append(msgWithClosures).append("\")\n");

        } else {
            String closureNameCamel = ConvUtils.toCamelCase(scriptChunk);
            Assertions.assertThat(closureNameCamel).isEqualTo(scriptChunk)
                    .as("Possibly wrong closure definition - only single var closure supported!");
            DialogClosure dialogClosure = new DialogClosure(String.class, closureNameCamel).toCamel();
            calledClosures.add(dialogClosure);
            scriptCodeBuilder.append(dialogClosure.getName()).append("()\n");
        }
    }

    private void addStateClosureCall(StringBuilder scriptCodeBuilder) {
        scriptCodeBuilder.append(stateClosureName).append("()\n\n");
        calledClosures.add(new DialogClosure(null, stateClosureName));
    }

    public void addTargetState(DialogState targetState) {
        targetStates.add(targetState);
    }

    public String getStateIdFieldCode(int i) {
        return "static int " + stateNameCode + " = " + i + " ";
    }
}
