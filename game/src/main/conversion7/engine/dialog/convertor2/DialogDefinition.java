package conversion7.engine.dialog.convertor2;

import conversion7.engine.dialog.convertor.model.drawio.DrawioCell;
import conversion7.engine.dialog.convertor.model.drawio.DrawioSrc;
import conversion7.engine.dialog.convertor.utils.ConvUtils;
import conversion7.engine.utils.Utils;
import org.fest.assertions.api.Assertions;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogDefinition {
    private static final Logger LOG = Utils.getLoggerForClass();
    public static final String START_STATE_CODE = DialogState.STATE_BODY_SEPARATOR + "start";

    public String dialogCodeName;
    public DialogState initialState;
    public List<DialogState> states = new ArrayList<>();
    public Map<String, String> textResources = new HashMap<>();
    public String dialogClassName;

    public void collectStatesAndArrows(DrawioSrc drawioSrc, List<DrawioCell> arrows) {
        for (DrawioCell cell : drawioSrc.mxGraphModel.root.drawioCells) {
            String cellValue = cell.value;

            if (cellValue == null) {
                LOG.info("arrow: {}", cell);
                if (cell.source != null || cell.target != null) {
                    if (cell.source == null) {
                        throw new AssertionError("Broken cell.source in dialog cfg! \n" + cell);
                    }
                    if (cell.target == null) {
                        throw new AssertionError("Broken cell.target in dialog cfg! \n" + cell);
                    }
                    arrows.add(cell);
                }

            } else {
                LOG.info("state: {}", cell);
                String lcaseValue = cellValue.toLowerCase();

                DialogState state;
                if (lcaseValue.startsWith(START_STATE_CODE)) {
                    String stateBodyText = cellValue.split(" ", 2)[1];
                    state = new DialogState(stateBodyText, cell);
                    initialState = state;
                    state.initialState = true;
                } else {
                    state = new DialogState(cellValue, cell);
                }
                state.setDialog(this);

                if (initialState == null) {
                    initialState = state;
                }

                states.add(state);
            }
        }
    }

    public void setTargets(DrawioSrc drawioSrc, List<DrawioCell> arrows) {
        for (DrawioCell arrow : arrows) {
            List<DialogState> targetStates = states.stream()
                    .filter(dialogState -> dialogState.getCell().id.equals(arrow.target))
                    .collect(Collectors.toList());

            List<DialogState> sourceStates = states.stream()
                    .filter(dialogState -> dialogState.getCell().id.equals(arrow.source))
                    .collect(Collectors.toList());

            for (DialogState sourceState : sourceStates) {
                targetStates.forEach(sourceState::addTargetState);
            }
        }
    }

    public void buildStates() {
        states.forEach(DialogState::preBuild);
        states.forEach(DialogState::postBuild);
        Assertions.assertThat(initialState).as("no initialState!").isNotNull();
        dialogCodeName = initialState.getNameCode();
        dialogClassName = ConvUtils.toCamelCase(dialogCodeName);
    }
}
