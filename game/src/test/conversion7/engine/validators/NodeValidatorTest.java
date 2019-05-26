package conversion7.engine.validators;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class NodeValidatorTest {

    @Test
    public void testValidateTree() {
        final int[] counter1 = {0};
        final int[] counter2 = {0};
        final int[] counter3 = {0};

        NodeValidator rootValidatorV2 = new NodeValidator(false) {
            @Override
            public void validate() {
                counter1[0]++;
            }
        };

        NodeValidator nodeValidatorV2_2 = new NodeValidator(false) {
            @Override
            public void validate() {
                counter2[0]++;
            }
        };
        rootValidatorV2.registerChildValidator(nodeValidatorV2_2);

        NodeValidator nodeValidatorV2_3 = new NodeValidator(false) {
            @Override
            public void validate() {
                counter3[0]++;
            }
        };
        nodeValidatorV2_2.registerChildValidator(nodeValidatorV2_3);

        rootValidatorV2.runTreeValidation();

        assertThat(counter1[0]).isEqualTo(1);
        assertThat(counter2[0]).isEqualTo(1);
        assertThat(counter3[0]).isEqualTo(1);
    }

    @Test
    public void testTreeValidationCycle() {
        final int[] counter1 = {0};
        final int[] counter2 = {0};
        final int[] counter3 = {0};

        NodeValidator validator = new NodeValidator(false) {
            @Override
            public void validate() {
                counter1[0]++;
            }
        };

        NodeValidator nodeValidator2 = new NodeValidator(false) {
            @Override
            public void validate() {
                if (counter2[0] == 0) {
                    validator.invalidate();
                }
                counter2[0]++;
            }
        };
        validator.registerChildValidator(nodeValidator2);

        NodeValidator nodeValidator3 = new NodeValidator(false) {
            @Override
            public void validate() {
                if (counter3[0] == 0) {
                    nodeValidator2.invalidate();
                }
                counter3[0]++;
            }
        };
        nodeValidator2.registerChildValidator(nodeValidator3);

        validator.runTreeValidation();

        assertThat(counter1[0]).isEqualTo(2);
        assertThat(counter2[0]).isEqualTo(2);
        assertThat(counter3[0]).isEqualTo(1);
    }

    @Test
    public void testAppendedValidator() {
        final int[] counter1 = {0};

        NodeValidator rootValidator = new NodeValidator(false) {
            @Override
            public void validate() {
                counter1[0]++;
            }
        };

        NodeAppendedValidator validator1 = new NodeAppendedValidator() {
            @Override
            public void validation() {
                counter1[0]++;
            }
        };

        rootValidator.registerAppendedValidator(validator1);
        rootValidator.runTreeValidation();

        assertThat(counter1[0]).isEqualTo(2);
    }

}