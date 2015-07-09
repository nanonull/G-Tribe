package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.WorldThreadLocalSort;
import org.slf4j.Logger;
import org.fest.assertions.api.Fail;

import java.util.Comparator;

public class Soil {

    private static final Logger LOG = Utils.getLoggerForClass();

    /** The biggest value - 1st, the smallest id - 1st */
    private static final Comparator<Parameter> PARAMETER_COMPARATOR = new Comparator<Parameter>() {
        @Override
        public int compare(Parameter o1, Parameter o2) {
            if (o1.value > o2.value) {
                return -1;
            } else if (o1.value < o2.value) {
                return 1;
            } else {
                return o1.id > o2.id ? 1 : -1;
            }
        }
    };

    /** Sorted */
    public Array<Parameter> parameterArraySorted = new Array<>();

    public int getSoilTypeValue(int typeId) {
        for (Parameter parameter : parameterArraySorted) {
            if (parameter.id == typeId) {
                return parameter.value;
            }
        }
        Fail.fail("no parameter with typeId=" + typeId);
        return 0;
    }

    /**
     * dirt + sand + stone == 100
     */
    public Soil(int dirt, int sand, int stone) {
        parameterArraySorted.add(new Parameter(TypeId.DIRT, dirt));
        parameterArraySorted.add(new Parameter(TypeId.SAND, sand));
        parameterArraySorted.add(new Parameter(TypeId.STONE, stone));
        WorldThreadLocalSort.instance().sort(parameterArraySorted, PARAMETER_COMPARATOR);
    }

    public Soil(AverageCellParams averageCellParams) {
        this(averageCellParams.dirtSum, averageCellParams.sandSum,
                averageCellParams.stoneSum);
    }

    @Override
    public String toString() {
        StringBuilder sb =
                new StringBuilder(GdxgConstants.HINT_SPLITTER);

        sb.append(" values:").append(GdxgConstants.HINT_SPLITTER);
        for (Parameter parameter : parameterArraySorted) {
            sb.append(parameter).append(GdxgConstants.HINT_SPLITTER);
        }

        return sb.toString();
    }

    public static class TypeId {
        public static final int DIRT = 0;
        public static final int SAND = 1;
        public static final int STONE = 2;
    }

    public static class Parameter {
        public int id;
        public int value;

        public Parameter(int id, int value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringBuilder("parameter id=").append(id).append("; value=").append(value).toString();
        }
    }

}
